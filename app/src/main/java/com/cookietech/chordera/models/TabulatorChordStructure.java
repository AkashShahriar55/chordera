package com.cookietech.chordera.models;

public class TabulatorChordStructure {
    boolean isInline;
    String chord;
    String transposed_chord;

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

    public String getTransposed_chord() {
        return transposed_chord;
    }

    public void setTransposed_chord(String transposed_chord) {
        this.transposed_chord = transposed_chord;
    }

    public TabulatorChordStructure(boolean isInline, String chord, String transposed_chord) {
        this.isInline = isInline;
        this.chord = chord;
        this.transposed_chord = transposed_chord;
    }
}
