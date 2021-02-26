package com.cookietech.chordera.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class SelectionType implements Parcelable {

    public final static Map<String, String> displaySelectionNameMap;
    static {
        displaySelectionNameMap = new HashMap<>();

        displaySelectionNameMap.put("guitar_chord", "Guitar Chords");
        displaySelectionNameMap.put("lyrics", "Lyrics");
        displaySelectionNameMap.put("ukulele_chord", "Ukulele Chords");
    }

    String selectionName, displaySelectionName, selectionId;
    public SelectionType(String name,String displayName, String id){
        this.selectionName = name;
        this.displaySelectionName = displayName;
        this.selectionId = id;
    }

    protected SelectionType(Parcel in) {
        selectionName = in.readString();
        displaySelectionName = in.readString();
        selectionId = in.readString();
    }

    public static final Creator<SelectionType> CREATOR = new Creator<SelectionType>() {
        @Override
        public SelectionType createFromParcel(Parcel in) {
            return new SelectionType(in);
        }

        @Override
        public SelectionType[] newArray(int size) {
            return new SelectionType[size];
        }
    };

    public String getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(String selectionId) {
        this.selectionId = selectionId;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selectionName) {
        this.selectionName = selectionName;
    }

    public String getDisplaySelectionName() {
        return displaySelectionName;
    }

    public void setDisplaySelectionName(String displaySelectionName) {
        this.displaySelectionName = displaySelectionName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(selectionName);
        dest.writeString(displaySelectionName);
        dest.writeString(selectionId);
    }
}
