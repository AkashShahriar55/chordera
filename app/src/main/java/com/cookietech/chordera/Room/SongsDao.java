package com.cookietech.chordera.Room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cookietech.chordera.appcomponents.SingleLiveEvent;

import java.util.List;
import java.util.Map;

@Dao
public interface SongsDao {
    @Insert
    void roomInsertSong(SongsEntity entity);

    @Delete
    void roomDeleteSong(SongsEntity entity);

    @Query("SELECT * FROM songs")
    DataSource.Factory<Integer, SongsEntity> roomFetchAllSongs();

    @Query("SELECT * FROM songs WHERE song_id = :id")
    SongsEntity roomFetchASong(String id);

    @Query("UPDATE songs SET song_data = :song_data WHERE song_id = :id")
    void roomUpdateExistingSongData(String id, Map<String,String> song_data);
}
