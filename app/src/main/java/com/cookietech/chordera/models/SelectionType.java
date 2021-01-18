package com.cookietech.chordera.models;

import java.util.HashMap;
import java.util.Map;

public class SelectionType {

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
}
