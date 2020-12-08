package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongsPOJO implements Parcelable,Comparable<SongsPOJO> {

    private String artist_name;
    private ArrayList<String> collections;
    private int download_count;
    private String image_url;
    private Map<String,String> song_data = new HashMap<>();
    private String song_name;
    private int views;
    private String genre;
    private int song_duration;
    private String youtube_id;
    @Exclude
    private String id;

    public SongsPOJO(String artist_name, ArrayList<String> collections, int download_count, String image_url, Map<String, String> song_data, String song_name, int views, String genre, int durationInSecond, String songId, String youtube_id) {
        this.artist_name = artist_name;
        this.collections = collections;
        this.download_count = download_count;
        this.image_url = image_url;
        this.song_data = song_data;
        this.song_name = song_name;
        this.views = views;
        this.genre = genre;
        this.song_duration = durationInSecond;
        this.id = songId;
        this.youtube_id = youtube_id;
    }


    public SongsPOJO() {
    }


    protected SongsPOJO(Parcel in) {
        artist_name = in.readString();
        collections = in.createStringArrayList();
        download_count = in.readInt();
        image_url = in.readString();
        song_name = in.readString();
        views = in.readInt();
        genre = in.readString();
        song_duration = in.readInt();
        id = in.readString();
        youtube_id = in.readString();
        int songDataSize = in.readInt();
        for (int i = 0; i < songDataSize; i++) {
            String key = in.readString();
            String value = in.readString();
            song_data.put(key,value);
        }
    }

    public static final Creator<SongsPOJO> CREATOR = new Creator<SongsPOJO>() {
        @Override
        public SongsPOJO createFromParcel(Parcel in) {
            return new SongsPOJO(in);
        }

        @Override
        public SongsPOJO[] newArray(int size) {
            return new SongsPOJO[size];
        }
    };

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public ArrayList<String> getCollections() {
        return collections;
    }

    public void setCollections(ArrayList<String> collections) {
        this.collections = collections;
    }

    public int getDownload_count() {
        return download_count;
    }

    public void setDownload_count(int download_count) {
        this.download_count = download_count;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Map<String, String> getSong_data() {
        return song_data;
    }

    public void setSong_data(Map<String, String> song_data) {
        this.song_data = song_data;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getSong_duration() {
        return song_duration;
    }

    public void setSong_duration(int song_duration) {
        this.song_duration = song_duration;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    @Override
    public int compareTo(SongsPOJO song) {
        if(song.getSong_name().equals(this.song_name) && song.getArtist_name().equals(this.artist_name))
        {
            return 0;
        }
        return 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist_name);
        dest.writeStringList(collections);
        dest.writeInt(download_count);
        dest.writeString(image_url);
        dest.writeString(song_name);
        dest.writeInt(views);
        dest.writeString(genre);
        dest.writeInt(song_duration);
        dest.writeString(id);
        dest.writeString(youtube_id);
        int songDataSize = song_data.size();
        dest.writeInt(songDataSize);
        for (Map.Entry<String, String> entry : song_data.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }
}
