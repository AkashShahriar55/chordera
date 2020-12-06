package com.cookietech.chordera.Room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cookietech.chordera.appcomponents.SingleLiveEvent;

import java.util.List;

@Dao
public interface SongsDao {
    @Insert
    void roomInsertSong(SongsEntity entity);

    @Delete
    void roomDeleteSong(SongsEntity entity);

    @Query("SELECT * FROM songs")
    List<SongsEntity> roomFetchAllSongs();
}
