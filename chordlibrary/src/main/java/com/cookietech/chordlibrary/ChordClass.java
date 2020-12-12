package com.cookietech.chordlibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ChordClass implements Parcelable {
    private String class_name;
    private String name;
    protected ArrayList<Variation> variations;

    public ChordClass() {
    }

    public ChordClass(String class_name, String name, ArrayList<Variation> variations) {
        this.class_name = class_name;
        this.name = name;
        this.variations = variations;
    }

    protected ChordClass(Parcel in) {
        class_name = in.readString();
        name = in.readString();
        variations = in.createTypedArrayList(Variation.CREATOR);
    }

    public static final Creator<ChordClass> CREATOR = new Creator<ChordClass>() {
        @Override
        public ChordClass createFromParcel(Parcel in) {
            return new ChordClass(in);
        }

        @Override
        public ChordClass[] newArray(int size) {
            return new ChordClass[size];
        }
    };

    public ArrayList<Variation> getVariations() {
        return variations;
    }

    public void setVariations(ArrayList<Variation> variations) {
        this.variations = variations;
    }


    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Creator<ChordClass> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(class_name);
        dest.writeString(name);
        dest.writeTypedList(variations);
    }
}
