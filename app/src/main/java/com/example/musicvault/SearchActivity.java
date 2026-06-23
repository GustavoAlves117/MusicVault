package com.example.musicvault;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.adapter.AlbumSearchAdapter;
import com.example.musicvault.model.Album;
import com.example.musicvault.model.ItunesResponse;
import com.example.musicvault.service.ItunesApiService;
import com.example.musicvault.service.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_SEARCH";

    private Button btnBack;
    private EditText edtSearch;
    private Button btnSearch;
    private ProgressBar progressBar;
    private TextView txtMessage;
    private RecyclerView recyclerAlbums;

    private AlbumSearchAdapter adapter;
    private ItunesApiService apiService;
    private Call<ItunesResponse> currentCall;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d(TAG, "onCreate chamado");

        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        txtMessage = findViewById(R.id.txtMessage);
        recyclerAlbums = findViewById(R.id.recyclerAlbums);

        apiService = RetrofitClient.getClient().create(ItunesApiService.class);

        adapter = new AlbumSearchAdapter(album -> openAlbumDetail(album));

        recyclerAlbums.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlbums.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        txtMessage.setText("Digite pelo menos 2 letras para pesquisar.");

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> searchAlbums());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                String term = s.toString().trim();

                if (term.length() < 2) {
                    adapter.setAlbums(new ArrayList<>());
                    txtMessage.setText("Digite pelo menos 2 letras para pesquisar.");
                    progressBar.setVisibility(View.GONE);

                    if (currentCall != null) {
                        currentCall.cancel();
                    }

                    return;
                }

                searchRunnable = () -> searchAlbums();
                searchHandler.postDelayed(searchRunnable, 600);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchAlbums() {
        String term = edtSearch.getText().toString().trim();

        if (term.isEmpty()) {
            txtMessage.setText("Digite o nome de um álbum ou artista.");
            adapter.setAlbums(new ArrayList<>());
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (term.length() < 2) {
            txtMessage.setText("Digite pelo menos 2 letras para pesquisar.");
            adapter.setAlbums(new ArrayList<>());
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (currentCall != null) {
            currentCall.cancel();
        }

        progressBar.setVisibility(View.VISIBLE);
        txtMessage.setText("");

        currentCall = apiService.searchAlbums(term, "album", "BR");

        currentCall.enqueue(new Callback<ItunesResponse>() {
            @Override
            public void onResponse(Call<ItunesResponse> call, Response<ItunesResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    txtMessage.setText("Erro ao buscar álbuns.");
                    adapter.setAlbums(new ArrayList<>());
                    return;
                }

                if (response.body().getResults() == null || response.body().getResults().isEmpty()) {
                    txtMessage.setText("Nenhum álbum encontrado.");
                    adapter.setAlbums(new ArrayList<>());
                    return;
                }

                txtMessage.setText("");
                adapter.setAlbums(response.body().getResults());
            }

            @Override
            public void onFailure(Call<ItunesResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                progressBar.setVisibility(View.GONE);
                txtMessage.setText("Falha de conexão. Verifique sua internet.");
                adapter.setAlbums(new ArrayList<>());
            }
        });
    }

    private void openAlbumDetail(Album album) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);

        intent.putExtra("mode", "api");
        intent.putExtra("collectionId", album.getCollectionId());
        intent.putExtra("collectionName", album.getCollectionName());
        intent.putExtra("artistName", album.getArtistName());
        intent.putExtra("artworkUrl", album.getHighQualityArtworkUrl());
        intent.putExtra("genre", album.getPrimaryGenreName());
        intent.putExtra("releaseDate", album.getReleaseDate());

        startActivity(intent);
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

        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}
