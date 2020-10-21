package com.cookietech.chordera.repositories;

import com.cookietech.chordera.models.Song;

import java.util.ArrayList;

/**
 * Repositorie for storing all type of  {@link Song} list
 * create only one instance of this class.
 * just call the getInstance to get this class
 */


public class SongRepositories {

    private static SongRepositories songRepositories;
    private ArrayList<Song> songList;

    private SongRepositories()
    {

    }

    public static SongRepositories getInstance()
    {
        if(songRepositories == null)
        {
            songRepositories = new SongRepositories();
        }
        return songRepositories;
    }

    private void addSong(Song song)
    {
        songList.add(song);
    }

    private void addSong(ArrayList<Song> songList)
    {

    }

}
