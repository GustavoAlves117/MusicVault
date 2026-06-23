package com.example.musicvault.model;

import java.util.List;

public class ItunesResponse {
    private int resultCount;
    private List<Album> results;

    public int getResultCount() {
        return resultCount;
    }

    public List<Album> getResults() {
        return results;
    }
}
