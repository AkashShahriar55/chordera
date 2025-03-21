package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class TabPOJO implements Parcelable {
    String data;
    String tuning;
    String key;
    String data_type;
    @Exclude
    String id;

    public TabPOJO(String data, String tuning, String key,String id,String data_type) {
        this.data = data;
        this.tuning = tuning;
        this.key = key;
        this.id = id;
        this.data_type = data_type;
    }

    public TabPOJO() {
    }

    protected TabPOJO(Parcel in) {
        data = in.readString();
        tuning = in.readString();
        key = in.readString();
        id = in.readString();
        data = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
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
        dest.writeString(id);
        dest.writeString(data_type);
    }
}
