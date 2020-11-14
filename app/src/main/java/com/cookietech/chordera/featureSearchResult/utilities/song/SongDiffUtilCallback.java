package com.cookietech.chordera.featureSearchResult.utilities.song;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cookietech.chordera.models.Song;

import java.util.ArrayList;

public class SongDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<Song> newList;
    ArrayList<Song> oldList;

    public SongDiffUtilCallback(ArrayList<Song> newList, ArrayList<Song> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0 ;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0 ;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newList.get(newItemPosition).compareTo (oldList.get(oldItemPosition));
        if (result==0){
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Song newSong = newList.get(newItemPosition);
        Song oldSong = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();
        if(!newSong.getTittle().equals(oldSong.getTittle())){
            diff.putString("tittle", newSong.getTittle());
        }
        if(!newSong.getBandName().equals (oldSong.getBandName())){
            diff.putString("band", newSong.getBandName());
        }
        if(!newSong.getTotalView().equals (oldSong.getTotalView())){
            diff.putString("view", newSong.getTotalView());
        }
        if (diff.size()==0){
            return null;
        }
        return diff;
    }
}