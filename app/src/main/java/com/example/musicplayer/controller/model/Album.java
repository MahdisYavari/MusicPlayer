package com.example.musicplayer.controller.model;

public class Album {
    private long albumId;
    private long artistId;
    private String albumName;
    private String artistName;
    private String path;
    private int numberOfSongs;

    public long getAlbumId() {
        return albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public Album() {
    }

    public Album(long albumId, long artistId, String albumName, String artistName, String path, int numberOfSongs) {
        this.albumId = albumId;
        this.artistId = artistId;
        this.albumName = albumName;
        this.artistName = artistName;
        this.path = path;
        this.numberOfSongs = numberOfSongs;

    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }
}
