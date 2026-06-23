package com.example.musicvault.model;

public class SavedAlbum {
    private int id;
    private long collectionId;
    private String collectionName;
    private String artistName;
    private String artworkUrl;
    private String genre;
    private String releaseDate;
    private String status;
    private double rating;
    private String review;

    public SavedAlbum() {
    }

    public SavedAlbum(int id, long collectionId, String collectionName, String artistName,
                      String artworkUrl, String genre, String releaseDate, String status,
                      double rating, String review) {
        this.id = id;
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.artistName = artistName;
        this.artworkUrl = artworkUrl;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.status = status;
        this.rating = rating;
        this.review = review;
    }

    public int getId() { return id; }
    public long getCollectionId() { return collectionId; }
    public String getCollectionName() { return collectionName; }
    public String getArtistName() { return artistName; }
    public String getArtworkUrl() { return artworkUrl; }
    public String getGenre() { return genre; }
    public String getReleaseDate() { return releaseDate; }
    public String getStatus() { return status; }
    public double getRating() { return rating; }
    public String getReview() { return review; }

    public void setId(int id) { this.id = id; }
    public void setCollectionId(long collectionId) { this.collectionId = collectionId; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public void setArtworkUrl(String artworkUrl) { this.artworkUrl = artworkUrl; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setStatus(String status) { this.status = status; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReview(String review) { this.review = review; }
}
