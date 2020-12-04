package com.cookietech.chordlibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ChordClass implements Parcelable {
    private String name;
    protected ArrayList<Chord> chords;

    public ChordClass() {
    }

    public ChordClass(String name, ArrayList<Chord> chords) {
        this.name = name;
        this.chords = chords;
    }

    protected ChordClass(Parcel in) {
        name = in.readString();
        chords = in.createTypedArrayList(Chord.CREATOR);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Chord> getChords() {
        return chords;
    }

    public void setChords(ArrayList<Chord> chords) {
        this.chords = chords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(chords);
    }
}
