package com.cookietech.chordera.featureSongList.top10;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentSearchResultRecyclerViewBinding;
import com.cookietech.chordera.databinding.FragmentTopSongListBinding;
import com.cookietech.chordera.featureSearchResult.utilities.BaseViewHolder;
import com.cookietech.chordera.featureSearchResult.utilities.song.SongDiffUtilCallback;
import com.cookietech.chordera.models.Collection;
import com.cookietech.chordera.models.Song;

import java.util.ArrayList;
import java.util.List;

public class TopSongListShowingAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private ArrayList<Song> mDataset;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    FragmentTopSongListBinding binding;
    private List<Song> songList;

    public TopSongListShowingAdapter(ArrayList<Song> songList, FragmentTopSongListBinding fragmentTopSongListBinding) {
        this.binding = fragmentTopSongListBinding;
        this.songList = songList;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_song_row_view, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(!payloads.isEmpty()){
            holder.onBind(position, payloads);
        }else{
            super.onBindViewHolder(holder,position,payloads);
        }


    }



    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == songList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return songList == null ? 0 : songList.size();
    }

    public void addItems(List<Song> songs) {
        songList.addAll(songs);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        songList.add(new Song());
        notifyItemInserted(songList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = songList.size() - 1;
        Song item = getItem(position);
        if (item != null) {
            songList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        songList.clear();
        notifyDataSetChanged();
    }

    Song getItem(int position) {
        return songList.get(position);
    }

    public ArrayList<Song> getData() {
        return (ArrayList<Song>) this.songList;
    }

    public class ViewHolder extends BaseViewHolder {
        public TextView tittle, band;
        public ConstraintLayout rowLayout;
        public ViewHolder(View v) {
            super(v);
            tittle = v.findViewById(R.id.txt_song_tittle);
            band = v.findViewById(R.id.txt_artist);
            rowLayout = v.findViewById(R.id.rowLayout);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rowLayout.getLayoutParams();
            //Log.e("ratio h/w", String.valueOf(binding.recyclerView.getWidth()/params.height));
            params.height = (int) (binding.recyclerView.getWidth()/7.2);
            rowLayout.setLayoutParams(params);
            //width/height = 7.2    ratio was calculated from xd design

        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            Song item = songList.get(position);

            tittle.setText(item.getTittle());
            band.setText(item.getBandName());
        }
        public void onBind(int position, List<Object> payloads)
        {
            if (payloads.isEmpty()){
                //
                super.onBind(position, payloads);
                Song item = songList.get(position);

                tittle.setText(item.getTittle());
                band.setText(item.getBandName());
            }
            else {

                Bundle o = (Bundle) payloads.get(0);
                for (String key : o.keySet()) {
                    if(key.equals("tittle")){
                        //Toast.makeText(tittle.getContext(), "Song "+position+" : Tittle Changed", Toast.LENGTH_SHORT).show();;
                        tittle.setText(songList.get(position).getTittle());
                    }
                    if(key.equals("band")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        band.setText(songList.get(position).getBandName());
                    }
                }
            }
        }
    }


    public class ProgressHolder extends BaseViewHolder {

        @Override
        protected void clear() {
        }
        public ProgressHolder(View v) {
            super(v);
        }
    }

    public void onNewData(ArrayList<Song> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SongDiffUtilCallback(newData, (ArrayList<Song>) songList));
        diffResult.dispatchUpdatesTo(this);
        this.songList.clear();
        this.songList.addAll(newData);
    }
}
