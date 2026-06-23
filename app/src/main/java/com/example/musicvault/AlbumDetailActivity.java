package com.example.musicvault;

import android.content.Intent;
import android.content.ClipData;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.adapter.TrackAdapter;
import com.example.musicvault.database.DatabaseHelper;
import com.example.musicvault.model.SavedAlbum;
import com.example.musicvault.model.Track;
import com.example.musicvault.model.TracksResponse;
import com.example.musicvault.service.ItunesApiService;
import com.example.musicvault.service.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumDetailActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_DETAIL";

    private ImageView imgCover;
    private TextView txtAlbumName;
    private TextView txtArtistName;
    private TextView txtGenre;
    private TextView txtReleaseDate;
    private TextView txtTrackMessage;

    private Spinner spinnerStatus;
    private EditText edtRating;
    private EditText edtReview;
    private View layoutRatingReview;

    private Button btnSave;
    private Button btnShareImage;
    private Button btnDelete;
    private Button btnBack;

    private RecyclerView recyclerTracks;
    private TrackAdapter trackAdapter;

    private DatabaseHelper databaseHelper;
    private ItunesApiService apiService;
    private Call<TracksResponse> tracksCall;

    private int savedAlbumId = -1;
    private long collectionId;
    private String collectionName;
    private String artistName;
    private String artworkUrl;
    private String genre;
    private String releaseDate;

    private final String[] statusOptions = {"Quero ouvir", "Ouvindo", "Já ouvi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        Log.d(TAG, "onCreate chamado");

        databaseHelper = new DatabaseHelper(this);
        apiService = RetrofitClient.getClient().create(ItunesApiService.class);

        initViews();
        setupSpinner();
        setupRecycler();

        savedAlbumId = getIntent().getIntExtra("savedAlbumId", -1);

        if (savedAlbumId != -1) {
            loadSavedAlbum();
        } else {
            loadAlbumFromIntent();
        }

        fillAlbumInfo();
        loadTracks();
        updateRatingReviewVisibility();

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveAlbum());
        btnShareImage.setOnClickListener(v -> compartilharReviewComoImagem());
        btnDelete.setOnClickListener(v -> deleteAlbum());
    }

    private void initViews() {
        imgCover = findViewById(R.id.imgCover);
        txtAlbumName = findViewById(R.id.txtAlbumName);
        txtArtistName = findViewById(R.id.txtArtistName);
        txtGenre = findViewById(R.id.txtGenre);
        txtReleaseDate = findViewById(R.id.txtReleaseDate);
        txtTrackMessage = findViewById(R.id.txtTrackMessage);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        edtRating = findViewById(R.id.edtRating);
        edtReview = findViewById(R.id.edtReview);
        layoutRatingReview = findViewById(R.id.layoutRatingReview);

        btnSave = findViewById(R.id.btnSave);
        btnShareImage = findViewById(R.id.btnShareImage);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        recyclerTracks = findViewById(R.id.recyclerTracks);
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner_selected,
                statusOptions
        );

        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerStatus.setAdapter(spinnerAdapter);
        spinnerStatus.setSelection(0);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRatingReviewVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateRatingReviewVisibility() {
        if (spinnerStatus == null || layoutRatingReview == null) {
            return;
        }

        String status = spinnerStatus.getSelectedItem().toString();
        boolean isWantToListen = "Quero ouvir".equalsIgnoreCase(status);

        if (isWantToListen) {
            layoutRatingReview.setVisibility(View.GONE);
        } else {
            layoutRatingReview.setVisibility(View.VISIBLE);
        }

        if (btnShareImage != null) {
            btnShareImage.setVisibility(isWantToListen ? View.GONE : View.VISIBLE);
        }
    }

    private void setupRecycler() {
        trackAdapter = new TrackAdapter();

        recyclerTracks.setLayoutManager(new LinearLayoutManager(this));
        recyclerTracks.setAdapter(trackAdapter);
        recyclerTracks.setNestedScrollingEnabled(false);
    }

    private void loadSavedAlbum() {
        SavedAlbum album = databaseHelper.getAlbumById(savedAlbumId);

        if (album == null) {
            Toast.makeText(this, "Álbum não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        collectionId = album.getCollectionId();
        collectionName = album.getCollectionName();
        artistName = album.getArtistName();
        artworkUrl = album.getArtworkUrl();
        genre = album.getGenre();
        releaseDate = album.getReleaseDate();

        edtRating.setText(String.valueOf(album.getRating()));
        edtReview.setText(album.getReview());

        setSpinnerSelection(album.getStatus());

        btnSave.setText("Atualizar");
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void loadAlbumFromIntent() {
        collectionId = getIntent().getLongExtra("collectionId", 0);
        collectionName = getIntent().getStringExtra("collectionName");
        artistName = getIntent().getStringExtra("artistName");
        artworkUrl = getIntent().getStringExtra("artworkUrl");
        genre = getIntent().getStringExtra("genre");
        releaseDate = getIntent().getStringExtra("releaseDate");

        btnSave.setText("Salvar no Vault");
        btnDelete.setVisibility(View.GONE);
    }

    private void fillAlbumInfo() {
        txtAlbumName.setText(nullToDefault(collectionName, "Álbum sem nome"));
        txtArtistName.setText(nullToDefault(artistName, "Artista não informado"));
        txtGenre.setText("Gênero: " + nullToDefault(genre, "Não informado"));
        txtReleaseDate.setText("Lançamento: " + formatDate(releaseDate));

        Picasso.get()
                .load(artworkUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(imgCover);
    }

    private void loadTracks() {
        if (collectionId == 0) {
            txtTrackMessage.setText("Não foi possível carregar as músicas.");
            return;
        }

        txtTrackMessage.setText("Carregando músicas...");

        tracksCall = apiService.getAlbumTracks(collectionId, "song", "BR");

        tracksCall.enqueue(new Callback<TracksResponse>() {
            @Override
            public void onResponse(Call<TracksResponse> call, Response<TracksResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResults() == null) {
                    txtTrackMessage.setText("Não foi possível carregar as músicas.");
                    return;
                }

                List<Track> tracks = new ArrayList<>();

                for (Track track : response.body().getResults()) {
                    if ("track".equalsIgnoreCase(track.getWrapperType()) && track.getTrackName() != null) {
                        tracks.add(track);
                    }
                }

                if (tracks.isEmpty()) {
                    txtTrackMessage.setText("Nenhuma música encontrada para este álbum.");
                } else {
                    txtTrackMessage.setText("Músicas do álbum");
                    trackAdapter.setTracks(tracks);
                }
            }

            @Override
            public void onFailure(Call<TracksResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                txtTrackMessage.setText("Falha ao carregar músicas.");
            }
        });
    }

    private void saveAlbum() {
        String selectedStatus = spinnerStatus.getSelectedItem().toString();
        String status = statusParaBanco(selectedStatus);
        String review;
        double rating;

        if ("Quero ouvir".equalsIgnoreCase(selectedStatus)) {
            rating = 0;
            review = "";
        } else {
            review = edtReview.getText().toString().trim();

            try {
                String ratingText = edtRating.getText().toString().trim();

                if (ratingText.isEmpty()) {
                    rating = 0;
                } else {
                    rating = Double.parseDouble(ratingText.replace(",", "."));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Digite uma nota válida.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating < 0 || rating > 10) {
                Toast.makeText(this, "A nota deve ser entre 0 e 10.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SavedAlbum album = new SavedAlbum(
                savedAlbumId,
                collectionId,
                collectionName,
                artistName,
                artworkUrl,
                genre,
                releaseDate,
                status,
                rating,
                review
        );

        if (savedAlbumId != -1) {
            databaseHelper.updateAlbum(album);
            Toast.makeText(this, "Álbum atualizado com sucesso.", Toast.LENGTH_SHORT).show();
        } else {
            if (databaseHelper.isAlbumSaved(collectionId)) {
                int existingId = databaseHelper.getSavedIdByCollectionId(collectionId);
                album.setId(existingId);
                databaseHelper.updateAlbum(album);
                Toast.makeText(this, "Álbum já existia. Dados atualizados.", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.insertAlbum(album);
                Toast.makeText(this, "Álbum salvo no Vault.", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void deleteAlbum() {
        if (savedAlbumId == -1) {
            return;
        }

        databaseHelper.deleteAlbum(savedAlbumId);
        Toast.makeText(this, "Álbum removido.", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void compartilharReviewComoImagem() {
        String status = spinnerStatus.getSelectedItem().toString();

        if ("Quero ouvir".equalsIgnoreCase(status)) {
            Toast.makeText(this, "Para compartilhar, selecione Ouvindo ou Já ouvi e escreva uma review.", Toast.LENGTH_SHORT).show();
            return;
        }

        String review = edtReview.getText().toString().trim();
        String ratingText = edtRating.getText().toString().trim();

        if (review.isEmpty()) {
            Toast.makeText(this, "Escreva uma review antes de compartilhar.", Toast.LENGTH_SHORT).show();
            return;
        }

        double rating = 0;
        if (!ratingText.isEmpty()) {
            try {
                rating = Double.parseDouble(ratingText.replace(",", "."));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Digite uma nota válida antes de compartilhar.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Bitmap shareBitmap = criarImagemDaReview(status, rating, review);
        Uri imageUri = salvarBitmapParaCompartilhar(shareBitmap);

        if (imageUri == null) {
            Toast.makeText(this, "Não foi possível gerar a imagem da review.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.setDataAndType(imageUri, "image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setClipData(ClipData.newRawUri("Music Vault Review", imageUri));
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Review Music Vault");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Review Music Vault");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(shareIntent, "Compartilhar review como imagem");
        startActivity(chooser);
    }

    private Bitmap criarImagemDaReview(String status, double rating, String review) {
        int width = 1080;
        int height = 1350;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        LinearGradient background = new LinearGradient(
                0, 0, width, height,
                Color.rgb(8, 8, 12),
                Color.rgb(22, 19, 30),
                Shader.TileMode.CLAMP
        );
        paint.setShader(background);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);

        // Detalhes visuais no fundo, para o card não ficar chapado.
        paint.setColor(Color.argb(42, 29, 185, 84));
        canvas.drawCircle(90, 140, 250, paint);
        paint.setColor(Color.argb(34, 120, 80, 255));
        canvas.drawCircle(990, 250, 300, paint);
        paint.setColor(Color.argb(30, 255, 255, 255));
        canvas.drawCircle(850, 1160, 230, paint);

        RectF mainCard = new RectF(46, 46, width - 46, height - 46);
        paint.setColor(Color.rgb(18, 18, 24));
        canvas.drawRoundRect(mainCard, 54, 54, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setColor(Color.argb(70, 255, 255, 255));
        canvas.drawRoundRect(mainCard, 54, 54, paint);
        paint.setStyle(Paint.Style.FILL);

        TextPaint brandPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        brandPaint.setColor(Color.WHITE);
        brandPaint.setTextSize(42);
        brandPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.rgb(166, 166, 176));
        labelPaint.setTextSize(25);

        TextPaint albumPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        albumPaint.setColor(Color.WHITE);
        albumPaint.setTextSize(52);
        albumPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        TextPaint artistPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        artistPaint.setColor(Color.rgb(205, 205, 215));
        artistPaint.setTextSize(31);

        TextPaint pillPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        pillPaint.setTextSize(27);
        pillPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        TextPaint reviewPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        reviewPaint.setColor(Color.WHITE);
        reviewPaint.setTextSize(38);
        reviewPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

        TextPaint footerPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        footerPaint.setColor(Color.rgb(150, 150, 160));
        footerPaint.setTextSize(25);

        int left = 94;
        int right = width - 94;

        drawTextMaxLines(canvas, "Music Vault", left, 88, brandPaint, right - left, 1);
        drawTextMaxLines(canvas, "Minha review de álbum", left, 138, labelPaint, right - left, 1);

        // Marca pequena no topo direito.
        pillPaint.setColor(Color.rgb(29, 185, 84));
        canvas.drawText("●", right - 158, 115, pillPaint);
        labelPaint.setColor(Color.rgb(190, 190, 198));
        canvas.drawText("review", right - 126, 115, labelPaint);
        labelPaint.setColor(Color.rgb(166, 166, 176));

        int coverSize = 420;
        int coverLeft = (width - coverSize) / 2;
        int coverTop = 205;
        RectF coverShadow = new RectF(coverLeft + 14, coverTop + 18, coverLeft + coverSize + 14, coverTop + coverSize + 18);
        paint.setColor(Color.argb(110, 0, 0, 0));
        canvas.drawRoundRect(coverShadow, 34, 34, paint);

        RectF coverRect = new RectF(coverLeft, coverTop, coverLeft + coverSize, coverTop + coverSize);
        Bitmap cover = getBitmapFromImageView(imgCover);
        if (cover != null) {
            drawRoundedBitmap(canvas, cover, coverRect, 34);
        } else {
            paint.setColor(Color.rgb(52, 52, 60));
            canvas.drawRoundRect(coverRect, 34, 34, paint);
        }

        int titleY = coverTop + coverSize + 58;
        int titleHeight = drawCenteredTextMaxLines(
                canvas,
                nullToDefault(collectionName, "Álbum sem nome"),
                left,
                titleY,
                albumPaint,
                right - left,
                2
        );

        int artistY = titleY + titleHeight + 20;
        int artistHeight = drawCenteredTextMaxLines(
                canvas,
                nullToDefault(artistName, "Artista não informado"),
                left,
                artistY,
                artistPaint,
                right - left,
                1
        );

        String statusText = statusParaCompartilhar(status);
        String ratingText = "Nota " + formatRating(rating) + "/10";

        float statusWidth = pillPaint.measureText(statusText) + 56;
        float ratingWidth = pillPaint.measureText(ratingText) + 56;
        float gap = 20;
        float totalPillsWidth = statusWidth + gap + ratingWidth;
        float pillX = (width - totalPillsWidth) / 2f;
        float pillY = artistY + artistHeight + 34;

        drawPill(canvas, statusText, pillX, pillY, pillPaint, Color.argb(34, 29, 185, 84), Color.rgb(29, 230, 105));
        drawPill(canvas, ratingText, pillX + statusWidth + gap, pillY, pillPaint, Color.rgb(34, 34, 44), Color.WHITE);

        RectF reviewBox = new RectF(left, 895, right, 1168);
        paint.setColor(Color.rgb(25, 25, 32));
        canvas.drawRoundRect(reviewBox, 32, 32, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.rgb(46, 46, 56));
        canvas.drawRoundRect(reviewBox, 32, 32, paint);
        paint.setStyle(Paint.Style.FILL);

        TextPaint quotePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        quotePaint.setColor(Color.argb(45, 255, 255, 255));
        quotePaint.setTextSize(96);
        quotePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("“", left + 28, 975, quotePaint);

        String reviewPreview = review.trim();
        if (reviewPreview.length() > 230) {
            reviewPreview = reviewPreview.substring(0, 230).trim() + "...";
        }

        drawTextMaxLines(
                canvas,
                reviewPreview,
                left + 58,
                954,
                reviewPaint,
                right - left - 116,
                5
        );

        paint.setColor(Color.rgb(29, 185, 84));
        canvas.drawRoundRect(new RectF(left, 1205, right, 1211), 4, 4, paint);

        String footer = "Criado no app Music Vault";
        int footerWidth = (int) footerPaint.measureText(footer);
        canvas.drawText(footer, (width - footerWidth) / 2f, 1264, footerPaint);

        return bitmap;
    }

    private int drawCenteredTextMaxLines(Canvas canvas, String text, int x, int y, TextPaint paint, int width, int maxLines) {
        if (text == null) {
            text = "";
        }

        StaticLayout staticLayout = StaticLayout.Builder
                .obtain(text, 0, text.length(), paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0, 1.0f)
                .setIncludePad(false)
                .setMaxLines(maxLines)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build();

        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();

        return staticLayout.getHeight();
    }

    private float drawPill(Canvas canvas, String text, float x, float y, TextPaint textPaint, int backgroundColor, int textColor) {
        float height = 58;
        float horizontalPadding = 28;
        float width = textPaint.measureText(text) + horizontalPadding * 2;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        RectF rect = new RectF(x, y, x + width, y + height);
        canvas.drawRoundRect(rect, height / 2f, height / 2f, paint);

        int oldColor = textPaint.getColor();
        textPaint.setColor(textColor);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = y + height / 2f - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(text, x + horizontalPadding, textY, textPaint);
        textPaint.setColor(oldColor);

        return width;
    }

    private Uri salvarBitmapParaCompartilhar(Bitmap bitmap) {
        String fileName = "music_vault_review_" + System.currentTimeMillis() + ".jpg";

        // Salva como IMAGEM real na galeria/cache do Android via MediaStore.
        // Isso evita o WhatsApp/compartilhador tratar o arquivo como PDF/documento.
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MusicVault");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri == null) {
                return salvarBitmapEmCacheComoFallback(bitmap, fileName);
            }

            try (OutputStream outputStream = getContentResolver().openOutputStream(imageUri)) {
                if (outputStream == null) {
                    return salvarBitmapEmCacheComoFallback(bitmap, fileName);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 96, outputStream);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                getContentResolver().update(imageUri, values, null, null);
            }

            return imageUri;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao salvar imagem via MediaStore. Tentando cache.", e);
            return salvarBitmapEmCacheComoFallback(bitmap, fileName);
        }
    }

    private Uri salvarBitmapEmCacheComoFallback(Bitmap bitmap, String fileName) {
        try {
            File dir = new File(getCacheDir(), "shared_reviews");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 96, outputStream);
            outputStream.flush();
            outputStream.close();

            return FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );
        } catch (IOException e) {
            Log.e(TAG, "Erro ao salvar imagem da review", e);
            return null;
        }
    }

    private Bitmap getBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                Math.max(1, drawable.getIntrinsicWidth()),
                Math.max(1, drawable.getIntrinsicHeight()),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void drawRoundedBitmap(Canvas canvas, Bitmap bitmap, RectF dst, float radius) {
        Rect src = getCenterCropSourceRect(bitmap, dst);

        Path path = new Path();
        path.addRoundRect(dst, radius, radius, Path.Direction.CW);

        canvas.save();
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, src, dst, null);
        canvas.restore();
    }

    private Rect getCenterCropSourceRect(Bitmap bitmap, RectF dst) {
        int sourceWidth = bitmap.getWidth();
        int sourceHeight = bitmap.getHeight();

        float sourceRatio = sourceWidth / (float) sourceHeight;
        float destinationRatio = dst.width() / dst.height();

        if (sourceRatio > destinationRatio) {
            int newWidth = (int) (sourceHeight * destinationRatio);
            int left = (sourceWidth - newWidth) / 2;
            return new Rect(left, 0, left + newWidth, sourceHeight);
        } else {
            int newHeight = (int) (sourceWidth / destinationRatio);
            int top = (sourceHeight - newHeight) / 2;
            return new Rect(0, top, sourceWidth, top + newHeight);
        }
    }

    private int drawTextMaxLines(Canvas canvas, String text, int x, int y, TextPaint paint, int width, int maxLines) {
        if (text == null) {
            text = "";
        }

        StaticLayout staticLayout = StaticLayout.Builder
                .obtain(text, 0, text.length(), paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0, 1.0f)
                .setIncludePad(false)
                .setMaxLines(maxLines)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build();

        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();

        return staticLayout.getHeight();
    }

    private int drawText(Canvas canvas, String text, int x, int y, TextPaint paint, int width) {
        if (text == null) {
            text = "";
        }

        StaticLayout staticLayout = StaticLayout.Builder
                .obtain(text, 0, text.length(), paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0, 1.0f)
                .setIncludePad(false)
                .build();

        canvas.save();
        canvas.translate(x, y);
        staticLayout.draw(canvas);
        canvas.restore();

        return staticLayout.getHeight();
    }

    private String statusParaCompartilhar(String status) {
        if ("Ouvido".equalsIgnoreCase(status) || "Já ouvi".equalsIgnoreCase(status)) {
            return "Já ouvi";
        }

        return status;
    }

    private String statusParaBanco(String status) {
        if ("Já ouvi".equalsIgnoreCase(status)) {
            return "Ouvido";
        }

        return status;
    }

    private String formatRating(double rating) {
        if (rating == (long) rating) {
            return String.format(Locale.US, "%d", (long) rating);
        }

        return String.format(Locale.US, "%.1f", rating);
    }

    private void setSpinnerSelection(String status) {
        for (int i = 0; i < statusOptions.length; i++) {
            boolean sameStatus = statusOptions[i].equalsIgnoreCase(status);
            boolean listenedStatus = "Já ouvi".equalsIgnoreCase(statusOptions[i]) && "Ouvido".equalsIgnoreCase(status);

            if (sameStatus || listenedStatus) {
                spinnerStatus.setSelection(i);
                updateRatingReviewVisibility();
                return;
            }
        }
    }

    private String nullToDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    private String formatDate(String date) {
        if (date == null || date.length() < 10) {
            return "Não informado";
        }

        return date.substring(8, 10) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart chamado");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume chamado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause chamado");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop chamado");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy chamado");

        if (tracksCall != null) {
            tracksCall.cancel();
        }
    }
}
