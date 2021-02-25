package com.cookietech.chordera.featureSongList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.featureSearchResult.utilities.BaseViewHolder;
import com.cookietech.chordera.featureSearchResult.utilities.song.SongDiffUtilCallback;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SongListShowingAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    RecyclerView recyclerView;
    private ArrayList<SongsPOJO> songList;
    private final MainViewModel mainViewModel;
    private final LifecycleOwner lifecycleOwner;
    private LastSongVisibilityListener lastSongVisibilityListener;
    private Boolean lastSongFetched = false;

    public SongListShowingAdapter(ArrayList<SongsPOJO> songList, RecyclerView recyclerView, MainViewModel mainViewModel, LifecycleOwner lifecycleOwner) {
        this.recyclerView = recyclerView;
        this.songList = songList;
        this.mainViewModel = mainViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setLastSongVisibilityListener(LastSongVisibilityListener lastSongVisibilityListener) {
        this.lastSongVisibilityListener = lastSongVisibilityListener;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //Log.d("pg_debug", "onViewAttachedToWindow: " + holder.getCurrentPosition() + " " + songList.size());
        if (holder.getCurrentPosition() == songList.size()-1 && !lastSongFetched){
            lastSongVisibilityListener.onLastSongVisible();
        }

    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_view, parent, false));
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
            holder.onBind(position);
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

    public void addItems(List<SongsPOJO> songs) {
        songList.addAll(songs);
        notifyDataSetChanged();
    }

    SongsPOJO songsPOJO = new SongsPOJO();
    public void addLoading() {
        Log.d("data_debug", "addLoading: called");
        isLoaderVisible = true;
        songList.add(songsPOJO);
        if(songList.size()<=0) notifyItemInserted(0);
        else notifyItemInserted(songList.size() - 1);
    }

    public void removeLoading() {
        Log.d("data_debug", "removeLoading: called");
        isLoaderVisible = false;
        songList.remove(songsPOJO);
        notifyItemRemoved(songList.indexOf(songsPOJO));
    }

    public void clear() {
        songList.clear();
        notifyDataSetChanged();
    }

    SongsPOJO getItem(int position) {
        return songList.get(position);
    }

    public ArrayList<SongsPOJO> getData() {
        Log.d("data_debug", "getData: " + songList.size());
        return (ArrayList<SongsPOJO>) songList;
    }

    public void setLastSongFetched(Boolean bool) {
        lastSongFetched = bool;
    }

    public class ViewHolder extends BaseViewHolder {
        public TextView tittle, band, view;
        public ConstraintLayout rowLayout;
        private int position;
        public ViewHolder(View v) {
            super(v);
            tittle = v.findViewById(R.id.txt_song_tittle);
            band = v.findViewById(R.id.txt_artist);
            rowLayout = v.findViewById(R.id.rowLayout);
            view = v.findViewById(R.id.views_count);
            rowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("sohan_debug","one song clicked");
                    mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(songList.get(position)));
                    mainViewModel.setSelectedSong(songList.get(position));
                }
            });
        }

        protected void clear() {
            if(songList != null)
            songList.clear();
            //notifyDataSetChanged();
        }

        public void onBind(int position) {
            super.onBind(position);

            this.position = position;
            Log.e("sohan debug", String.valueOf(songList.size()));
            SongsPOJO item = songList.get(position);

            tittle.setText(item.getSong_name());
            band.setText(item.getArtist_name());
            view.setText(String.valueOf(item.getViews()));
        }
        public void onBind(int position, List<Object> payloads)
        {
            this.position = position;
            if (payloads.isEmpty()){
                //
                super.onBind(position, payloads);
                SongsPOJO item = songList.get(position);

                tittle.setText(item.getSong_name());
                band.setText(item.getArtist_name());
                view.setText(item.getViews());
            }
            else {

                Bundle o = (Bundle) payloads.get(0);
                for (String key : o.keySet()) {
                    if(key.equals("tittle")){
                        //Toast.makeText(tittle.getContext(), "Song "+position+" : Tittle Changed", Toast.LENGTH_SHORT).show();;
                        tittle.setText(songList.get(position).getSong_name());
                    }
                    if(key.equals("band")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        band.setText(songList.get(position).getArtist_name());
                    }
                    if(key.equals("view")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        view.setText(String.valueOf(songList.get(position).getViews()));
                    }
                }
            }
        }
    }


    public static class ProgressHolder extends BaseViewHolder {


        public ProgressHolder(View v) {
            super(v);
        }
    }

    public void onNewData(ArrayList<SongsPOJO> newData) {
        if(songList.size() <= 0)
        {
            songList.addAll(newData);
            Log.d("data_debug", "onNewData: " + getItemCount());
        }
        else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SongDiffUtilCallback(newData, (ArrayList<SongsPOJO>) this.songList));
            diffResult.dispatchUpdatesTo(this);
            songList.clear();
            songList.addAll(newData);

        }
        notifyDataSetChanged();

    }

    public interface LastSongVisibilityListener{
        void onLastSongVisible();
    }
}
