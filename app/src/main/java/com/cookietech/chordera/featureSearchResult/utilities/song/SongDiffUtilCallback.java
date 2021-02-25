package com.cookietech.chordera.featureSearchResult.utilities.song;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;

public class SongDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<SongsPOJO> newList;
    ArrayList<SongsPOJO> oldList;

    public SongDiffUtilCallback(ArrayList<SongsPOJO> newList, ArrayList<SongsPOJO> oldList) {
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
        /** d To change**/
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d("pagination_debug", "oldListSize: " + getOldListSize());
        Log.d("pagination_debug", "newListSize: " + getNewListSize());
        Log.d("pagination_debug", "oldItemPosition: " + oldItemPosition);
        Log.d("pagination_debug", "newItemPosition: " + newItemPosition);
        int result = newList.get(newItemPosition).compareTo (oldList.get(oldItemPosition));
        if (result==0){
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        SongsPOJO newSong = newList.get(newItemPosition);
        SongsPOJO oldSong = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();
        if(!newSong.getSong_name().equals(oldSong.getSong_name())){
            diff.putString("tittle", newSong.getSong_name());
        }
        if(!newSong.getArtist_name().equals (oldSong.getArtist_name())){
            diff.putString("band", newSong.getArtist_name());
        }
        if(newSong.getViews() != oldSong.getViews()){
            diff.putInt("view", newSong.getViews());
        }
        if (diff.size()==0){
            return null;
        }
        return diff;
    }
}