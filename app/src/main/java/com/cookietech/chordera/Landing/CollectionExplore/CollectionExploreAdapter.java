package com.cookietech.chordera.Landing.CollectionExplore;

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
import com.cookietech.chordera.models.Collection;
import com.cookietech.chordera.models.CollectionsPOJO;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;
import java.util.List;

public class CollectionExploreAdapter extends  RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    RecyclerView recyclerView;
    private final ArrayList<CollectionsPOJO> collectionsList;
    private final MainViewModel mainViewModel;
    private final LifecycleOwner lifecycleOwner;
    private String fromWhere = "";

    public CollectionExploreAdapter(ArrayList<CollectionsPOJO> collectionsList, RecyclerView recyclerView, MainViewModel mainViewModel, LifecycleOwner lifecycleOwner) {
        this.recyclerView = recyclerView;
        this.collectionsList = collectionsList;
        this.mainViewModel = mainViewModel;
        this.lifecycleOwner = lifecycleOwner;
        initializeObserver();
    }

    private void initializeObserver() {
        mainViewModel.getObservableSongListShowingCalledFrom().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                fromWhere = s;
            }
        });
    }


    // Create new views (invoked by the layout manager)
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new CollectionExploreAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_row_view, parent, false));
            case VIEW_TYPE_LOADING:
                return new CollectionExploreAdapter.ProgressHolder(
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
            return position == collectionsList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return collectionsList == null ? 0 : collectionsList.size();
    }

    public void addItems(List<CollectionsPOJO> songs) {
        collectionsList.addAll(songs);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        collectionsList.add(new CollectionsPOJO());
        if(collectionsList.size()<=0) notifyItemChanged(0);
        else notifyItemInserted(collectionsList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = collectionsList.size() - 1;
        CollectionsPOJO item = getItem(position);
        if (item != null) {
            collectionsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        collectionsList.clear();
        notifyDataSetChanged();
    }

    CollectionsPOJO getItem(int position) {
        return collectionsList.get(position);
    }

    public ArrayList<CollectionsPOJO> getData() {
        return (ArrayList<CollectionsPOJO>) this.collectionsList;
    }

    public class ViewHolder extends BaseViewHolder {
        public TextView tittle, band, view;
        public ConstraintLayout rowLayout;
        public ImageView view_icon;
        private int position;
        public ViewHolder(View v) {
            super(v);
            tittle = v.findViewById(R.id.txt_song_tittle);
            band = v.findViewById(R.id.txt_artist);
            rowLayout = v.findViewById(R.id.rowLayout);
            view = v.findViewById(R.id.views_count);
            view_icon = v.findViewById(R.id.view_icon);
           /* ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rowLayout.getLayoutParams();
            //Log.e("ratio h/w", String.valueOf(binding.recyclerView.getWidth()/params.height));
            params.height = (int) (recyclerView.getWidth()/7.2);
            rowLayout.setLayoutParams(params);*/
            //width/height = 7.2    ratio was calculated from xd design

            rowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("sohan_debug","one song clicked");
//                    mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(collectionsList.get(position)));
//                    mainViewModel.setSelectedSong(collectionsList.get(position));
                }
            });
        }

        protected void clear() {
            if(collectionsList != null)
                collectionsList.clear();
            //notifyDataSetChanged();
        }

        public void onBind(int position) {
            super.onBind(position);

            this.position = position;
            Log.e("sohan debug", String.valueOf(collectionsList.size()));
            CollectionsPOJO item = collectionsList.get(position);

          /*  tittle.setText(item.getSong_name());
            band.setText(item.getArtist_name());*/
            view.setText(String.valueOf(item.getViews()));
            if(fromWhere.equalsIgnoreCase(Constants.FROM_SAVED)){
                view.setVisibility(View.GONE);
                view_icon.setVisibility(View.GONE);
            }else{
                view.setVisibility(View.VISIBLE);
                view_icon.setVisibility(View.VISIBLE);
            }
        }
        public void onBind(int position, List<Object> payloads)
        {
            if(fromWhere.equalsIgnoreCase(Constants.FROM_SAVED)){
                view.setVisibility(View.GONE);
                view_icon.setVisibility(View.GONE);
            }else{
                view.setVisibility(View.VISIBLE);
                view_icon.setVisibility(View.VISIBLE);
            }
            this.position = position;
            if (payloads.isEmpty()){
                //
                super.onBind(position, payloads);
                CollectionsPOJO item = collectionsList.get(position);
/*
                tittle.setText(item.getSong_name());
                band.setText(item.getArtist_name());*/
                view.setText(item.getViews());
            }
            else {

                Bundle o = (Bundle) payloads.get(0);
                for (String key : o.keySet()) {
                    /*if(key.equals("tittle")){
                        //Toast.makeText(tittle.getContext(), "Song "+position+" : Tittle Changed", Toast.LENGTH_SHORT).show();;
                        tittle.setText(collectionsList.get(position).getSong_name());
                    }
                    if(key.equals("band")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        band.setText(collectionsList.get(position).getArtist_name());
                    }*/
                    if(key.equals("view")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        view.setText(String.valueOf(collectionsList.get(position).getViews()));
                    }
                }
            }
        }
    }


    public class ProgressHolder extends BaseViewHolder {


        public ProgressHolder(View v) {
            super(v);
        }
    }

    public void onNewData(ArrayList<CollectionsPOJO> newData) {
        if(this.collectionsList.size() <= 0)
        {
            this.collectionsList.addAll(newData);
            notifyDataSetChanged();
        }
        else {
//            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SongDiffUtilCallback(newData, (ArrayList<CollectionsPOJO>) collectionsList));
            this.collectionsList.clear();
            this.collectionsList.addAll(newData);
            notifyDataSetChanged();
//            diffResult.dispatchUpdatesTo(this);

        }

    }
}
