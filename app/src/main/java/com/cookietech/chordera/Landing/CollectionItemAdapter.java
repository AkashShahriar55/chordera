package com.cookietech.chordera.Landing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;

public class CollectionItemAdapter extends RecyclerView.Adapter<CollectionItemAdapter.CollectionItemViewHolder> {
    RecyclerView collectionItemRecyclerView;

    public CollectionItemAdapter(RecyclerView collectionItemRecyclerView) {
        this.collectionItemRecyclerView = collectionItemRecyclerView;
    }

    @NonNull
    @Override
    public CollectionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_collection_item, parent, false);
        int size = collectionItemRecyclerView.getHeight();
        return new CollectionItemViewHolder(listItem,size);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class CollectionItemViewHolder extends RecyclerView.ViewHolder{

        public CardView newItemHolder;

        public CollectionItemViewHolder(@NonNull View itemView,int size) {
            super(itemView);
            newItemHolder = itemView.findViewById(R.id.cv_new_item_holder);

//            newItemHolder.getLayoutParams().width = (int) (size*1.4);
//            newItemHolder.getLayoutParams().height= size;
        }
    }
}
