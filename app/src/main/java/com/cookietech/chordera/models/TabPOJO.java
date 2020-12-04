package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TabPOJO implements Parcelable {
    String data;
    String tuning;
    String key;
    int durationInSecond;
    String genre;

    public TabPOJO(String data, String tuning, String key, int durationInSecond, String genre) {
        this.data = data;
        this.tuning = tuning;
        this.key = key;
        this.durationInSecond = durationInSecond;
        this.genre = genre;
    }

    public TabPOJO() {
    }

    protected TabPOJO(Parcel in) {
        data = in.readString();
        tuning = in.readString();
        key = in.readString();
        durationInSecond = in.readInt();
        genre = in.readString();
    }

    public static final Creator<TabPOJO> CREATOR = new Creator<TabPOJO>() {
        @Override
        public TabPOJO createFromParcel(Parcel in) {
            return new TabPOJO(in);
        }

        @Override
        public TabPOJO[] newArray(int size) {
            return new TabPOJO[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTuning() {
        return tuning;
    }

    public void setTuning(String tuning) {
        this.tuning = tuning;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDurationInSecond() {
        return durationInSecond;
    }

    public void setDurationInSecond(int durationInSecond) {
        this.durationInSecond = durationInSecond;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(tuning);
        dest.writeString(key);
        dest.writeInt(durationInSecond);
        dest.writeString(genre);
    }
}
