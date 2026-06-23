package com.example.musicvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.adapter.SavedAlbumAdapter;
import com.example.musicvault.database.DatabaseHelper;
import com.example.musicvault.model.SavedAlbum;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_MAIN";

    private Button btnSearch;
    private Button btnVault;

    private TextView txtEmptyReviews;
    private TextView txtEmptyWantToListen;
    private TextView txtHomeSummary;

    private RecyclerView recyclerReviews;
    private RecyclerView recyclerWantToListen;

    private DatabaseHelper databaseHelper;

    private SavedAlbumAdapter reviewsAdapter;
    private SavedAlbumAdapter wantToListenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate chamado");

        databaseHelper = new DatabaseHelper(this);

        btnSearch = findViewById(R.id.btnSearch);
        btnVault = findViewById(R.id.btnVault);

        txtEmptyReviews = findViewById(R.id.txtEmptyReviews);
        txtEmptyWantToListen = findViewById(R.id.txtEmptyWantToListen);
        txtHomeSummary = findViewById(R.id.txtHomeSummary);

        recyclerReviews = findViewById(R.id.recyclerReviews);
        recyclerWantToListen = findViewById(R.id.recyclerWantToListen);

        reviewsAdapter = new SavedAlbumAdapter(album -> openSavedAlbum(album));
        wantToListenAdapter = new SavedAlbumAdapter(album -> openSavedAlbum(album));

        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(reviewsAdapter);
        recyclerReviews.setNestedScrollingEnabled(false);

        recyclerWantToListen.setLayoutManager(new LinearLayoutManager(this));
        recyclerWantToListen.setAdapter(wantToListenAdapter);
        recyclerWantToListen.setNestedScrollingEnabled(false);

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        btnVault.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyVaultActivity.class);
            startActivity(intent);
        });
    }

    private void loadHomeContent() {
        List<SavedAlbum> allAlbums = databaseHelper.getAllAlbums();

        List<SavedAlbum> reviewedAlbums = new ArrayList<>();
        List<SavedAlbum> wantToListenAlbums = new ArrayList<>();

        for (SavedAlbum album : allAlbums) {
            String review = album.getReview();
            String status = album.getStatus();

            if (review != null && !review.trim().isEmpty()) {
                reviewedAlbums.add(album);
            }

            if ("Quero ouvir".equalsIgnoreCase(status)) {
                wantToListenAlbums.add(album);
            }
        }

        reviewsAdapter.setAlbums(reviewedAlbums);
        wantToListenAdapter.setAlbums(wantToListenAlbums);

        int totalReviews = reviewedAlbums.size();
        int totalWant = wantToListenAlbums.size();

        txtHomeSummary.setText(
                totalReviews + " review(s) salva(s) e " +
                        totalWant + " álbum(ns) na lista Quero ouvir."
        );

        if (reviewedAlbums.isEmpty()) {
            recyclerReviews.setVisibility(View.GONE);
            txtEmptyReviews.setVisibility(View.VISIBLE);
        } else {
            recyclerReviews.setVisibility(View.VISIBLE);
            txtEmptyReviews.setVisibility(View.GONE);
        }

        if (wantToListenAlbums.isEmpty()) {
            recyclerWantToListen.setVisibility(View.GONE);
            txtEmptyWantToListen.setVisibility(View.VISIBLE);
        } else {
            recyclerWantToListen.setVisibility(View.VISIBLE);
            txtEmptyWantToListen.setVisibility(View.GONE);
        }
    }

    private void openSavedAlbum(SavedAlbum album) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra("savedAlbumId", album.getId());
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
        loadHomeContent();
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
    }
}
