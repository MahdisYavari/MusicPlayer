package com.example.musicplayer.controller.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import com.example.musicplayer.controller.model.Album;
import com.example.musicplayer.controller.model.Artist;
import com.example.musicplayer.controller.model.Music;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
public class BeatBox {

    private List<Music> mMusic;
    private List<Album> mAlbumList;
    private List<Artist> mArtist;
    ContentResolver contentResolver;
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private static BeatBox instance;
    private Uri uri;
    public Music currentMusic;
    private Cursor cursor;
    private BitBoxCallBacks mCallBacks;

    public void setCallBacks(BitBoxCallBacks mCallBacks) {
        this.mCallBacks = mCallBacks;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static BeatBox getInstance(Context context) {
        if (instance == null) {
            instance = new BeatBox(context);
        }
        return instance;
    }


    public List<Album> getAlbum() {
        return mAlbumList;
    }

    public List<Music> getMusic() {
        return mMusic;
    }

    public List<Artist> getArtist() {
        return mArtist;
    }

    private BeatBox(Context context) {

        mediaPlayer = new MediaPlayer();
        mContext = context.getApplicationContext();
        mMusic = loadMusic();
        mAlbumList = loadAlbum();
        mArtist = loadArtist();

    }

    public String getAlbumMusic(String songAlbum) {
        String path = " ";
        contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.AlbumColumns.ALBUM_ART},
                MediaStore.Audio.AlbumColumns.ALBUM + "=?",
                new String[]{songAlbum},
                null);

        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

        }
        return path;
    }

    public Uri getMusicUri(Music music) {
        long musicId = music.getMusicId();
        Uri musicUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicId);
        return musicUri;
    }

    public void play(Music music) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(mContext, getMusicUri(music));
        mediaPlayer.start();
        currentMusic = music;
        music.getNameMusic();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                int played =1;
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(played<20){
                        played++;
                        mediaPlayer.start();
                        mediaPlayer.seekTo(0);
                        nextMusic();
                        mCallBacks.setUi(currentMusic);
                    }
                }
            });
    }

    public Music nextMusic() {
        int index = mMusic.indexOf(currentMusic);
        Music nextMusic = mMusic.get((index + 1) % mMusic.size());
        currentMusic = nextMusic;
        play(nextMusic);
        mCallBacks.setUi(currentMusic);
        return nextMusic;
    }

    public Music prevMusic() {
        int index = mMusic.indexOf(currentMusic);
        Music prevMusic = mMusic.get((mMusic.size() + (index - 1)) % mMusic.size());
        currentMusic = prevMusic;
        play(prevMusic);
        mCallBacks.setUi(currentMusic);
        return prevMusic;
    }

    public Music repeatOne() {
        int index = mMusic.indexOf(currentMusic);
        Music repeatOne = mMusic.get(index);
        currentMusic = repeatOne;
        play(repeatOne);
        return repeatOne;
    }

    public Music shuffleMusic(){
        Random rand = new Random();
         int currentMusicIndex = rand.nextInt((mMusic.size() - 1)  + 1);
         Music shuffle = mMusic.get(currentMusicIndex);
         currentMusic = shuffle;
         mCallBacks.setUi(currentMusic);
         play(shuffle);
         return shuffle;
    }

    public List<Music> loadMusic() {

        List<Music> musicList = new ArrayList<>();
        contentResolver = mContext.getContentResolver();
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(mContext, "No Music Found on SD Card.", Toast.LENGTH_LONG);
        } else {
            do {
                String songSinger = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                int SongId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long musicDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String songAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Music music = new Music(songSinger, getAlbumMusic(songAlbum), musicDuration, SongId,albumId,artistId);
                musicList.add(music);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return musicList;
    }

    public List<Album> loadAlbum() {
        List<Album> albumList = new ArrayList<>();
        contentResolver = mContext.getContentResolver();
        uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST_ID));
                int numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String path = cursor.getString(cursor.getColumnIndex("album_art"));
                Album album = new Album(albumId, artistId, albumName, artist, path, numberOfSongs);
                albumList.add(album);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumList;
    }
    public List<Artist> loadArtist(){

        List<Artist> artistList = new ArrayList<>();
        contentResolver = mContext.getContentResolver();
        uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        );
        if(cursor != null && cursor.moveToFirst()){
            do{
                long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                Artist artist  = new Artist(artistName,artistId,numberOfSongs);
                artistList.add(artist);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return artistList;
    }

    public String formatDuration(long duration) {
        int seconds = (int) duration / 1000;
        int minutes = seconds / 60;
        seconds%=60;
        return String.format(Locale.ENGLISH,"%02d",minutes)+
                ":"+String.format(Locale.ENGLISH,"%02d",seconds);

    }

    public List<Music> getMusicOfAlbum(Long albumId) {
        List<Music> musicListAlbum = new ArrayList<>();
        for (Music music : loadMusic()){
            if(music.getAlbumId()==albumId)
                musicListAlbum.add(music);
        }
        return musicListAlbum;
    }

    public List<Music> getMusicOfArtist(Long artistId){
        List<Music> musicListArtist = new ArrayList<>();
        for(Music music : loadMusic()){
            if(music.getArtistId()== artistId)
                musicListArtist.add(music);
        }
        return musicListArtist;
    }

    public interface BitBoxCallBacks{
        void setUi(Music music);
    }


}



