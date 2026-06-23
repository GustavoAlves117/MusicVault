package com.example.musicvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicvault.adapter.SavedAlbumAdapter;
import com.example.musicvault.database.DatabaseHelper;
import com.example.musicvault.model.SavedAlbum;

import java.util.List;

public class MyVaultActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_VAULT";

    private Spinner spinnerFilter;
    private Button btnApplyFilter;
    private Button btnGoSearch;
    private Button btnBack;
    private TextView txtEmpty;
    private RecyclerView recyclerSavedAlbums;

    private DatabaseHelper databaseHelper;
    private SavedAlbumAdapter adapter;

    private final String[] filterOptions = {"Todos", "Quero ouvir", "Ouvindo", "Já ouvi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vault);

        Log.d(TAG, "onCreate chamado");

        databaseHelper = new DatabaseHelper(this);

        spinnerFilter = findViewById(R.id.spinnerFilter);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnGoSearch = findViewById(R.id.btnGoSearch);
        btnBack = findViewById(R.id.btnBack);
        txtEmpty = findViewById(R.id.txtEmpty);
        recyclerSavedAlbums = findViewById(R.id.recyclerSavedAlbums);

        setupSpinner();

        adapter = new SavedAlbumAdapter(album -> openSavedAlbum(album));

        recyclerSavedAlbums.setLayoutManager(new LinearLayoutManager(this));
        recyclerSavedAlbums.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnApplyFilter.setOnClickListener(v -> loadAlbums());

        btnGoSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MyVaultActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner_selected,
                filterOptions
        );

        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerFilter.setAdapter(spinnerAdapter);
        spinnerFilter.setSelection(0);
    }

    private void loadAlbums() {
        String selectedFilter = spinnerFilter.getSelectedItem().toString();

        List<SavedAlbum> albums;

        if ("Todos".equals(selectedFilter)) {
            albums = databaseHelper.getAllAlbums();
        } else {
            albums = databaseHelper.getAlbumsByStatus(statusParaBanco(selectedFilter));
        }

        adapter.setAlbums(albums);

        if (albums.isEmpty()) {
            txtEmpty.setText("Nenhum álbum salvo nesse filtro.");
        } else {
            txtEmpty.setText("");
        }
    }

    private String statusParaBanco(String status) {
        if ("Já ouvi".equalsIgnoreCase(status)) {
            return "Ouvido";
        }

        return status;
    }

    private void openSavedAlbum(SavedAlbum album) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra("savedAlbumId", album.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume chamado");
        loadAlbums();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart chamado");
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
