package com.example.musicplayer.controller.model;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Music {
    private UUID mUuid;
    private String mNameMusic;
    private String mNameSinger;
    private String mNameAlbum;
    private String mAlbumPath;
    private long mDuration;
    private Long albumId;
    private Long artistId;
    private int musicId;

    public Music() {
    }

    public Music(String mNameMusic, int musicId) {
        this.mNameMusic = mNameMusic;
        this.musicId = musicId;
    }

    public Music(String mNameMusic, String mAlbumPath, long mDuration, int musicId , long albumId,long artistId) {
        this.mNameMusic = mNameMusic;
        this.mAlbumPath = mAlbumPath;
        this.mDuration = mDuration;
        this.musicId = musicId;
        this.albumId = albumId;
        this.artistId =artistId;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public long getMusicId() {
        return musicId;
    }

    public UUID getUuid() {
        return mUuid;
    }

    public String getNameMusic() {
        return mNameMusic;
    }

    public void setName(String name) {
        this.mNameMusic = name;
    }

    public String getNameSinger() {
        return mNameSinger;
    }

    public void setNameSinger(String nameSinger) {
        this.mNameSinger = nameSinger;
    }

    public String getNameAlbum() {
        return mNameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.mNameAlbum = nameAlbum;
    }

    public String getmAlbumPath() {
        return mAlbumPath;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setDuration(long mDuration) {
//        String time = String.format("%02d min, %02d sec",
//                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(mDuration)),
//                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(mDuration)),
//                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(mDuration))));

        this.mDuration = mDuration;
    }

    public void setmAlbumPath(String mAlbumPath) {
        this.mAlbumPath = mAlbumPath;
    }

    public Music(String name, String nameSinger, String nameAlbum) {
        this.mUuid = UUID.randomUUID();
        this.mNameMusic = name;
        this.mNameSinger = nameSinger;
        this.mNameAlbum = nameAlbum;
    }



}
