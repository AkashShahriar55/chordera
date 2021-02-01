package com.cookietech.chordera.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.cookietech.chordera.models.SongsPOJO;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Entity(tableName = "songs")
public class SongsEntity {
    @PrimaryKey
    @NonNull
    private String song_id;
    private String artist_name;
    private String song_name;
    private String genre;
    private String image_url;
    private int song_duration;
    private String youtube_id;

    @TypeConverters(StringMapConverter.class)
    private Map<String,String> song_data;

    public SongsEntity(@NotNull String song_id, String artist_name, String song_name, String genre, String image_url, int song_duration, Map<String,String> song_data, String youtube_id) {
        this.song_id = song_id;
        this.artist_name = artist_name;
        this.song_name = song_name;

        this.genre = genre;
        this.image_url = image_url;
        this.song_duration = song_duration;
        this.song_data = song_data;
        this.youtube_id = youtube_id;
    }

    @NotNull
    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getSong_duration() {
        return song_duration;
    }

    public void setSong_duration(int song_duration) {
        this.song_duration = song_duration;
    }

    public Map<String, String> getSong_data() {
        return song_data;
    }

    public void setSong_data(Map<String, String> song_data) {
        this.song_data = null;
        this.song_data = song_data;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public SongsPOJO convertToSongsPOJO(){
        SongsPOJO songsPOJO = new SongsPOJO();
        songsPOJO.setArtist_name(this.getArtist_name());
        songsPOJO.setGenre(this.getGenre());
        songsPOJO.setId(this.getSong_id());
        songsPOJO.setImage_url(this.getImage_url());
        songsPOJO.setSong_data(this.getSong_data());
        songsPOJO.setSong_name(this.getSong_name());
        //songsPOJO.setViews(this.getViews());
        songsPOJO.setYoutube_id(this.youtube_id);
        return songsPOJO;
    }
}
