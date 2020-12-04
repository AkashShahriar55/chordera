package com.cookietech.chordlibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Root implements Parcelable {
    private String name;
    private ArrayList<ChordClass> chordClasses;

    public Root() {
    }

    public Root(String name, ArrayList<ChordClass> chordClasses) {
        this.name = name;
        this.chordClasses = chordClasses;
    }

    protected Root(Parcel in) {
        name = in.readString();
        chordClasses = in.createTypedArrayList(ChordClass.CREATOR);
    }

    public static final Creator<Root> CREATOR = new Creator<Root>() {
        @Override
        public Root createFromParcel(Parcel in) {
            return new Root(in);
        }

        @Override
        public Root[] newArray(int size) {
            return new Root[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ChordClass> getChordClasses() {
        return chordClasses;
    }

    public void setChordClasses(ArrayList<ChordClass> chordClasses) {
        this.chordClasses = chordClasses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(chordClasses);
    }
}
