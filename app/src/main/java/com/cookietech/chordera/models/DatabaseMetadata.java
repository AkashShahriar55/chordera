package com.cookietech.chordera.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class DatabaseMetadata {
    String database_name;
    String database_url;
    Timestamp update_date;
    @Exclude
    String id;

    public DatabaseMetadata() {
    }

    public DatabaseMetadata(String database_name, String database_url, Timestamp update_date, String id) {
        this.database_name = database_name;
        this.database_url = database_url;
        this.update_date = update_date;
        this.id = id;
    }


    public String getDatabase_name() {
        return database_name;
    }

    public void setDatabase_name(String database_name) {
        this.database_name = database_name;
    }

    public String getDatabase_url() {
        return database_url;
    }

    public void setDatabase_url(String database_url) {
        this.database_url = database_url;
    }

    public Timestamp getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Timestamp update_date) {
        this.update_date = update_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
