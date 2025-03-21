package com.cookietech.chordlibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Variation implements Parcelable {
    private ArrayList<Integer> notes = new ArrayList<Integer>();
    private ArrayList<Integer> fingers = new ArrayList<Integer>();

    public Variation() {
    }

    public Variation( ArrayList<Integer> notes, ArrayList<Integer> fingers) {
        this.notes = notes;
        this.fingers = fingers;
    }


    protected Variation(Parcel in) {
        in.readList(notes,Integer.class.getClassLoader());
        in.readList(fingers,Integer.class.getClassLoader());
    }

    public static final Creator<Variation> CREATOR = new Creator<Variation>() {
        @Override
        public Variation createFromParcel(Parcel in) {
            return new Variation(in);
        }

        @Override
        public Variation[] newArray(int size) {
            return new Variation[size];
        }
    };

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
        dest.writeList(notes);
        dest.writeList(fingers);
    }
}
