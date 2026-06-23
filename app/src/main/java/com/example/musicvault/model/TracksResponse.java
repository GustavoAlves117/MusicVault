package com.example.musicvault.model;

import java.util.List;

public class TracksResponse {
    private int resultCount;
    private List<Track> results;

    public int getResultCount() {
        return resultCount;
    }

    public List<Track> getResults() {
        return results;
    }
}
