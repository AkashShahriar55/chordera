package com.cookietech.chordlibrary;

import java.util.ArrayList;

public class Root {
    private String name;
    private ArrayList<ChordClass> chordClasses;

    public Root() {
    }

    public Root(String name, ArrayList<ChordClass> chordClasses) {
        this.name = name;
        this.chordClasses = chordClasses;
    }

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
}
