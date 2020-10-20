package com.cookietech.chordlibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Chord implements Parcelable {
    private String name;
    private ArrayList<Integer> notes = new ArrayList<Integer>();
    private ArrayList<Integer> fingers = new ArrayList<Integer>();

    public Chord() {
    }

    public Chord(String name, ArrayList<Integer> notes, ArrayList<Integer> fingers) {
        this.name = name;
        this.notes = notes;
        this.fingers = fingers;
    }


    protected Chord(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Chord> CREATOR = new Creator<Chord>() {
        @Override
        public Chord createFromParcel(Parcel in) {
            return new Chord(in);
        }

        @Override
        public Chord[] newArray(int size) {
            return new Chord[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Integer> notes) {
        this.notes = notes;
    }

    public ArrayList<Integer> getFingers() {
        return fingers;
    }

    public void setFingers(ArrayList<Integer> fingers) {
        this.fingers = fingers;
    }

    public int getFirstFret(){
        int firstFret = 100000;
        for(int fret:notes){
            if(fret < firstFret && fret != -1 && fret != 0){
                firstFret = fret;
            }
        }
        return firstFret;
    }

    public int getLastFret(){
        int lastFret = -100000;
        for(int fret:notes){
            if(fret > lastFret && fret != -1 && fret != 0){
                lastFret = fret;
            }
        }
        return lastFret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
