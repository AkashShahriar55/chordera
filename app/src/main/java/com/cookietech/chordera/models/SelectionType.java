package com.cookietech.chordera.models;

public class SelectionType {
    String selectionName, selectionId;
    public SelectionType(String name,String id){
        this.selectionName = name;
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

}
