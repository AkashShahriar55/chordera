package com.cookietech.chordera.models;

public class TabulatorChordStructure {
    boolean isInline;
    String chord;

    public boolean isInline() {
        return isInline;
    }

    public void setInline(boolean inline) {
        isInline = inline;
    }

    public String getChord() {
        return chord;
    }

    public void setChord(String chord) {
        this.chord = chord;
    }

    public TabulatorChordStructure(boolean isInline, String chord) {
        this.isInline = isInline;
        this.chord = chord;
    }
}
