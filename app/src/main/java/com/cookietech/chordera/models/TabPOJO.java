package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TabPOJO implements Parcelable {
    String data;
    String tuning;
    String key;

    public TabPOJO(String data, String tuning, String key) {
        this.data = data;
        this.tuning = tuning;
        this.key = key;
    }

    public TabPOJO() {
    }

    protected TabPOJO(Parcel in) {
        data = in.readString();
        tuning = in.readString();
        key = in.readString();
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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(tuning);
        dest.writeString(key);
    }
}
