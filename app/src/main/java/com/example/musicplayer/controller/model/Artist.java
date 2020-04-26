package com.example.musicplayer.controller.model;

public class Artist {
    private String artistName;
    private long idArtist;
    private int numberOfSongs;

    public Artist(String artistName, long idArtist, int numberOfSongs) {
        this.artistName = artistName;
        this.idArtist = idArtist;
        this.numberOfSongs = numberOfSongs;
    }

    public Artist() {
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getIdArtist() {
        return idArtist;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }
}
