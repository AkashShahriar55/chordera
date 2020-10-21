package com.cookietech.chordera.Landing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;

public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.NewItemViewHolder> {
    RecyclerView newItemRecyclerView;

    public NewItemAdapter(RecyclerView newItemRecyclerView) {
        this.newItemRecyclerView = newItemRecyclerView;
    }

    @NonNull
    @Override
    public NewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_new_item, parent, false);
        int size = newItemRecyclerView.getHeight();
        return new NewItemViewHolder(listItem,size);
    }

    @Override
    public void onBindViewHolder(@NonNull NewItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class NewItemViewHolder extends RecyclerView.ViewHolder{

        public CardView newItemHolder;

        public NewItemViewHolder(@NonNull View itemView,int size) {
            super(itemView);
            newItemHolder = itemView.findViewById(R.id.cv_new_item_holder);

            newItemHolder.getLayoutParams().width = (int) (size*0.8);
            newItemHolder.getLayoutParams().height= size;
        }
    }
}
