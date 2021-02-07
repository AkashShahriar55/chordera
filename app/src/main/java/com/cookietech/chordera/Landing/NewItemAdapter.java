package com.cookietech.chordera.Landing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookietech.chordera.R;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;

public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.NewItemViewHolder> {
    RecyclerView newItemRecyclerView;
    ArrayList<SongsPOJO> newSongsData= new ArrayList<>();

    public NewItemAdapter(RecyclerView newItemRecyclerView) {
        this.newItemRecyclerView = newItemRecyclerView;
    }


    public void setNewSongsData(ArrayList<SongsPOJO> newSongsData) {
        this.newSongsData = newSongsData;
        notifyDataSetChanged();
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
        SongsPOJO songsPOJO = newSongsData.get(position);
        holder.tvViews.setText(""+songsPOJO.getViews());
        holder.tvArtistName.setText(songsPOJO.getArtist_name());
        holder.tvSongName.setText(songsPOJO.getSong_name());
        Glide.with(holder.itemView).load(songsPOJO.getImage_url()).thumbnail(0.5f).centerCrop().into(holder.ivBackground);
    }

    @Override
    public int getItemCount() {
        return newSongsData.size();
    }

    public static class NewItemViewHolder extends RecyclerView.ViewHolder{

        public CardView newItemHolder;
        public TextView tvViews;
        public TextView tvSongName;
        public TextView tvArtistName;
        public ImageView ivBackground;

        public NewItemViewHolder(@NonNull View itemView,int size) {
            super(itemView);
            newItemHolder = itemView.findViewById(R.id.cv_new_item_holder);
            tvViews = itemView.findViewById(R.id.tv_views);
            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvArtistName = itemView.findViewById(R.id.tv_artist);
            ivBackground = itemView.findViewById(R.id.iv_background);
            newItemHolder.getLayoutParams().width = (int) (size*0.8);
            newItemHolder.getLayoutParams().height= size;
            newItemHolder.setRadius(10);
        }
    }
}
