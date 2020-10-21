package com.cookietech.chordlibrary;

import java.util.ArrayList;

public class ChordClass {
    private String name;
    protected ArrayList<Chord> chords;

    public ChordClass() {
    }

    public ChordClass(String name, ArrayList<Chord> chords) {
        this.name = name;
        this.chords = chords;
    }

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
}
