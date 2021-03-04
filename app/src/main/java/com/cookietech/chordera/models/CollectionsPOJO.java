package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectionsPOJO implements Parcelable, Comparable<CollectionsPOJO> {
    private String collection_name;
    @Exclude
    private String id;
    private String image_url;
    private ArrayList<String> song_id = new ArrayList<>();
    private int views;


    public CollectionsPOJO() {
    }

    public CollectionsPOJO(String id) {
        this.id = id;
    }

    public CollectionsPOJO(String collection_name, String image_url, ArrayList<String> song_id, int views) {
        this.collection_name = collection_name;
        this.image_url = image_url;
        this.song_id = song_id;
        this.views = views;
    }


    protected CollectionsPOJO(Parcel in) {
        collection_name = in.readString();
        id = in.readString();
        image_url = in.readString();
        song_id = in.createStringArrayList();
        views = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(collection_name);
        dest.writeString(id);
        dest.writeString(image_url);
        dest.writeStringList(song_id);
        dest.writeInt(views);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CollectionsPOJO> CREATOR = new Creator<CollectionsPOJO>() {
        @Override
        public CollectionsPOJO createFromParcel(Parcel in) {
            return new CollectionsPOJO(in);
        }

        @Override
        public CollectionsPOJO[] newArray(int size) {
            return new CollectionsPOJO[size];
        }
    };

    public String getCollection_name() {
        return collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public ArrayList<String> getSong_id() {
        return song_id;
    }

    public void setSong_id(ArrayList<String> songIds) {
        this.song_id = songIds;
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

    public static Creator<CollectionsPOJO> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int compareTo(CollectionsPOJO collection) {
        if(collection.getCollection_name().equals(this.collection_name))
        {
            return 0;
        }
        return 1;
    }
}
