package com.example.musicvault.service;

import com.example.musicvault.model.ItunesResponse;
import com.example.musicvault.model.TracksResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItunesApiService {

    @GET("search")
    Call<ItunesResponse> searchAlbums(
            @Query("term") String term,
            @Query("entity") String entity,
            @Query("country") String country
    );

    @GET("lookup")
    Call<TracksResponse> getAlbumTracks(
            @Query("id") long collectionId,
            @Query("entity") String entity,
            @Query("country") String country
    );
}
