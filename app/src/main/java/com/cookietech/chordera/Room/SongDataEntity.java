package com.cookietech.chordera.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "song_data")
public class SongDataEntity {
    @PrimaryKey()
    @NonNull
    private String song_data_id;
    private String data;
    private String key;
    private String tuning;
    private String data_type;

    public SongDataEntity(@NotNull String song_data_id, String data, String key, String tuning, String data_type) {
        this.song_data_id = song_data_id;
        this.data = data;
        this.key = key;
        this.tuning = tuning;
        this.data_type = data_type;
    }

    @NonNull
    public String getSong_data_id() {
        return song_data_id;
    }

    public void setSong_data_id(@NonNull String song_data_id) {
        this.song_data_id = song_data_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTuning() {
        return tuning;
    }

    public void setTuning(String tuning) {
        this.tuning = tuning;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public TabPOJO convertToSongDataPOJO(){
        TabPOJO tabPOJO = new TabPOJO();

        tabPOJO.setId(this.getSong_data_id());
        tabPOJO.setData(this.data);
        tabPOJO.setKey(this.key);
        tabPOJO.setTuning(this.tuning);
        tabPOJO.setData_type(this.data_type);
        return tabPOJO;
    }
}
