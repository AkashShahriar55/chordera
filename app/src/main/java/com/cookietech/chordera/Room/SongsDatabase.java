package com.cookietech.chordera.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {SongsEntity.class, SongDataEntity.class}, version = 1)
@TypeConverters({StringMapConverter.class})
public abstract class SongsDatabase extends RoomDatabase {
    public static SongsDatabase instance;
    public abstract SongsDao songsDao();
    public abstract SongDataDao songDataDao();

    public static synchronized SongsDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SongsDatabase.class,"songs_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
