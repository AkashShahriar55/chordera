package com.cookietech.chordera.Util;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import com.cookietech.chordera.models.CollectionsPOJO;
import java.util.ArrayList;

public class CollectionDiffUtilCallback extends DiffUtil.Callback {

    ArrayList<CollectionsPOJO> newList;
    ArrayList<CollectionsPOJO> oldList;

    public CollectionDiffUtilCallback(ArrayList<CollectionsPOJO> newList, ArrayList<CollectionsPOJO> oldList) {
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
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d("pagination_debug", "oldListSize: " + getOldListSize());
        Log.d("pagination_debug", "newListSize: " + getNewListSize());
        Log.d("pagination_debug", "oldItemPosition: " + oldItemPosition);
        Log.d("pagination_debug", "newItemPosition: " + newItemPosition);
        int result = newList.get(newItemPosition).compareTo (oldList.get(oldItemPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        CollectionsPOJO newCollection = newList.get(newItemPosition);
        CollectionsPOJO oldCollection = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();

        if(!newCollection.getCollection_name().equals (oldCollection.getCollection_name())){
            diff.putString("collectionName", newCollection.getCollection_name());
        }
        if(newCollection.getViews() != oldCollection.getViews()){
            diff.putInt("view", newCollection.getViews());
        }
        if (diff.size()==0){
            return null;
        }
        return diff;
    }
}
