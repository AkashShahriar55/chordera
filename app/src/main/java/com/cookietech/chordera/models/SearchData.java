package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchData implements Parcelable {
    private String id;
    private String song_name;
    private String artist_name;
    private int views;


    public SearchData() {
    }

    public SearchData(String id, String song_name, String artist_name, int views) {
        this.id = id;
        this.song_name = song_name;
        this.artist_name = artist_name;
        this.views = views;
    }

    protected SearchData(Parcel in) {
        id = in.readString();
        song_name = in.readString();
        artist_name = in.readString();
        views = in.readInt();
    }

    public static final Creator<SearchData> CREATOR = new Creator<SearchData>() {
        @Override
        public SearchData createFromParcel(Parcel in) {
            return new SearchData(in);
        }

        @Override
        public SearchData[] newArray(int size) {
            return new SearchData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
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
        dest.writeString(id);
        dest.writeString(song_name);
        dest.writeString(artist_name);
        dest.writeInt(views);
    }

    @Override
    public String toString() {
        return "SearchData{" +
                "id='" + id + '\'' +
                ", song_name='" + song_name + '\'' +
                ", artist_name='" + artist_name + '\'' +
                ", views=" + views +
                '}';
    }

    public static SearchData fromJson(JSONObject jsonObject){
        SearchData searchData = new SearchData();
        try {
            searchData.id = jsonObject.getString("id");
            searchData.song_name = jsonObject.getString("song_name");
            searchData.artist_name = jsonObject.getString("artist_name");
            searchData.views = jsonObject.getInt("views");
            return searchData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
