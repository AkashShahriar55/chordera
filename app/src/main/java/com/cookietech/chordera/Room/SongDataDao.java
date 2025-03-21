package com.cookietech.chordera.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;

@Dao
public interface SongDataDao {

    @Insert
    void roomInsertSongData(SongDataEntity entity);

    @Delete
    void roomDeleteSongData(SongDataEntity entity);

    @Query("SELECT * from song_data WHERE song_data_id IN (:song_data_id)")
    SongDataEntity roomFetchSongData(String song_data_id);

    @Query("DELETE FROM song_data WHERE song_data_id IN(:song_data_ids)")
    void deleteSongData(ArrayList<String> song_data_ids);
}
