package com.example.musicvault.model;

public class Track {
    private String wrapperType;
    private String kind;
    private String trackName;
    private int trackNumber;
    private long trackTimeMillis;

    public String getWrapperType() {
        return wrapperType;
    }

    public String getKind() {
        return kind;
    }

    public String getTrackName() {
        return trackName;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public long getTrackTimeMillis() {
        return trackTimeMillis;
    }

    public String getDurationFormatted() {
        if (trackTimeMillis <= 0) {
            return "--:--";
        }

        long totalSeconds = trackTimeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }
}
