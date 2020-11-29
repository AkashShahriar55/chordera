package com.cookietech.chordera.models;

public class SelectionType {
    String selectionName, selectionId, views;
    public SelectionType(String name,String id,String views){
        this.selectionName = name;
        this.selectionId = id;
        this.views = views;
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

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }
}
