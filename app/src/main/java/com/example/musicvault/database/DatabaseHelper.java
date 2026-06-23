package com.example.musicvault.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.musicvault.model.SavedAlbum;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_vault.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ALBUMS = "albums";

    private static final String COL_ID = "id";
    private static final String COL_COLLECTION_ID = "collection_id";
    private static final String COL_COLLECTION_NAME = "collection_name";
    private static final String COL_ARTIST_NAME = "artist_name";
    private static final String COL_ARTWORK_URL = "artwork_url";
    private static final String COL_GENRE = "genre";
    private static final String COL_RELEASE_DATE = "release_date";
    private static final String COL_STATUS = "status";
    private static final String COL_RATING = "rating";
    private static final String COL_REVIEW = "review";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_ALBUMS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COLLECTION_ID + " INTEGER UNIQUE, " +
                COL_COLLECTION_NAME + " TEXT, " +
                COL_ARTIST_NAME + " TEXT, " +
                COL_ARTWORK_URL + " TEXT, " +
                COL_GENRE + " TEXT, " +
                COL_RELEASE_DATE + " TEXT, " +
                COL_STATUS + " TEXT, " +
                COL_RATING + " REAL, " +
                COL_REVIEW + " TEXT" +
                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        onCreate(db);
    }

    public long insertAlbum(SavedAlbum album) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getContentValues(album);
        return db.insert(TABLE_ALBUMS, null, values);
    }

    public int updateAlbum(SavedAlbum album) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getContentValues(album);

        return db.update(
                TABLE_ALBUMS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(album.getId())}
        );
    }

    public int deleteAlbum(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_ALBUMS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public List<SavedAlbum> getAllAlbums() {
        List<SavedAlbum> albums = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ALBUMS,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    albums.add(cursorToAlbum(cursor));
                }
            } finally {
                cursor.close();
            }
        }

        return albums;
    }

    public List<SavedAlbum> getAlbumsByStatus(String status) {
        List<SavedAlbum> albums = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ALBUMS,
                null,
                COL_STATUS + " = ?",
                new String[]{status},
                null,
                null,
                COL_ID + " DESC"
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    albums.add(cursorToAlbum(cursor));
                }
            } finally {
                cursor.close();
            }
        }

        return albums;
    }

    public SavedAlbum getAlbumById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ALBUMS,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursorToAlbum(cursor);
                }
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    public boolean isAlbumSaved(long collectionId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ALBUMS,
                new String[]{COL_ID},
                COL_COLLECTION_ID + " = ?",
                new String[]{String.valueOf(collectionId)},
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                return cursor.moveToFirst();
            } finally {
                cursor.close();
            }
        }

        return false;
    }

    public int getSavedIdByCollectionId(long collectionId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ALBUMS,
                new String[]{COL_ID},
                COL_COLLECTION_ID + " = ?",
                new String[]{String.valueOf(collectionId)},
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                }
            } finally {
                cursor.close();
            }
        }

        return -1;
    }

    private ContentValues getContentValues(SavedAlbum album) {
        ContentValues values = new ContentValues();

        values.put(COL_COLLECTION_ID, album.getCollectionId());
        values.put(COL_COLLECTION_NAME, album.getCollectionName());
        values.put(COL_ARTIST_NAME, album.getArtistName());
        values.put(COL_ARTWORK_URL, album.getArtworkUrl());
        values.put(COL_GENRE, album.getGenre());
        values.put(COL_RELEASE_DATE, album.getReleaseDate());
        values.put(COL_STATUS, album.getStatus());
        values.put(COL_RATING, album.getRating());
        values.put(COL_REVIEW, album.getReview());

        return values;
    }

    private SavedAlbum cursorToAlbum(Cursor cursor) {
        SavedAlbum album = new SavedAlbum();

        album.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        album.setCollectionId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_COLLECTION_ID)));
        album.setCollectionName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COLLECTION_NAME)));
        album.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ARTIST_NAME)));
        album.setArtworkUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_ARTWORK_URL)));
        album.setGenre(cursor.getString(cursor.getColumnIndexOrThrow(COL_GENRE)));
        album.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_RELEASE_DATE)));
        album.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
        album.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RATING)));
        album.setReview(cursor.getString(cursor.getColumnIndexOrThrow(COL_REVIEW)));

        return album;
    }
}
