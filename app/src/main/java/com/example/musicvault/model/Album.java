package com.example.musicvault.model;

public class Album {
    private long collectionId;
    private String collectionName;
    private String artistName;
    private String artworkUrl100;
    private String primaryGenreName;
    private String releaseDate;

    public long getCollectionId() {
        return collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getHighQualityArtworkUrl() {
        if (artworkUrl100 == null) {
            return "";
        }
        return artworkUrl100.replace("100x100bb", "600x600bb");
    }
}
