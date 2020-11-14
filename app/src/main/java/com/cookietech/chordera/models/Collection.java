package com.cookietech.chordera.models;

/***
 * this is the collection model/pojo
 * it stores the all type of collection artists/bands/genre
 */

public class Collection implements Comparable<Collection> {
    private String name, view;

    public Collection(){};

    public Collection(String name, String view)
    {
        this.name = name;
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    public int compareTo(Collection collection) {
        if(collection.getName().equals(this.name) && collection.getView().equals(this.view))
        {
            return 0;
        }
        return 1;
    }
}
