package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

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

    public SongsPOJO(String artist_name, ArrayList<String> collections, int download_count, String image_url, Map<String, String> song_data, String song_name, int views) {
        this.artist_name = artist_name;
        this.collections = collections;
        this.download_count = download_count;
        this.image_url = image_url;
        this.song_data = song_data;
        this.song_name = song_name;
        this.views = views;
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
        int client_info_size = in.readInt();
        for (int i = 0; i < client_info_size; i++) {
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
        int song_data_size = song_data.size();
        dest.writeInt(song_data_size);
        for(Map.Entry<String,String> map: song_data.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }
    }

    @Override
    public String toString() {
        return "SongsPOJO{" +
                "artist_name='" + artist_name + '\'' +
                ", collections=" + collections +
                ", download_count=" + download_count +
                ", image_url='" + image_url + '\'' +
                ", song_data=" + song_data +
                ", song_name='" + song_name + '\'' +
                ", views=" + views +
                '}';
    }


    @Override
    public int compareTo(SongsPOJO song) {
        if(song.getSong_name().equals(this.song_name) && song.getArtist_name().equals(this.artist_name))
        {
            return 0;
        }
        return 1;
    }
}
