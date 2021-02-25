package com.cookietech.chordera.featureSongList.top10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;

public class TopTenSongsAdapter extends RecyclerView.Adapter<TopTenSongsAdapter.TopTenSongsViewHolder> {
    private RecyclerView recyclerView;
    private ArrayList<SongsPOJO> topTenSongsList;
    private final OnItemClickListener onItemClickListener;

    public TopTenSongsAdapter(ArrayList<SongsPOJO> topTenSongsList, RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
        this.recyclerView = recyclerView;
        this.topTenSongsList = topTenSongsList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TopTenSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopTenSongsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_view,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopTenSongsViewHolder holder, int position) {

        SongsPOJO song = topTenSongsList.get(position);
        holder.song_name.setText(song.getSong_name());
        holder.artist_name.setText(song.getArtist_name());
        holder.view.setText(String.valueOf(song.getViews()));
        holder.bind(topTenSongsList.get(position),onItemClickListener);

    }

    @Override
    public int getItemCount() {
        return topTenSongsList.size();
    }

    public class TopTenSongsViewHolder extends RecyclerView.ViewHolder{
        public TextView song_name, artist_name, view;
        public ConstraintLayout rowLayout;
        public ImageView view_icon;
        public TopTenSongsViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.txt_song_tittle);
            artist_name = itemView.findViewById(R.id.txt_artist);
            rowLayout = itemView.findViewById(R.id.rowLayout);
            view = itemView.findViewById(R.id.views_count);
            view_icon = itemView.findViewById(R.id.view_icon);
        }

        private void bind(final SongsPOJO sonng, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(sonng);
                }
            });
        }
    }

    public void clearData() {
        topTenSongsList.clear();
        notifyDataSetChanged();
    }

    public void addNewQueryData(ArrayList<SongsPOJO> songsPOJOS){
        clearData();
        topTenSongsList.addAll(songsPOJOS);
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onItemClick(SongsPOJO song);
    }
}
