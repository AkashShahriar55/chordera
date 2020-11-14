package com.cookietech.chordera.featureSearchResult.utilities.collection;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cookietech.chordera.models.Collection;
import com.cookietech.chordera.models.Song;

import java.util.ArrayList;

public class CollectionDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<Collection> newList;
    ArrayList<Collection> oldList;

    public CollectionDiffUtilCallback(ArrayList<Collection> newList, ArrayList<Collection> oldList) {
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
        Collection newCollection = newList.get(newItemPosition);
        Collection oldCollection = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();
        if(!newCollection.getName().equals(oldCollection.getName())){
            diff.putString("name", newCollection.getName());
        }
        if(!newCollection.getView().equals (oldCollection.getView())){
            diff.putString("view", newCollection.getView());
        }
        if (diff.size()==0){
            return null;
        }
        return diff;
    }
}