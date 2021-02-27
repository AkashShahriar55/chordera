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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Util.CollectionDiffUtilCallback;
import com.cookietech.chordera.featureSearchResult.utilities.BaseViewHolder;
import com.cookietech.chordera.models.CollectionsPOJO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CollectionExploreAdapter extends  RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private final ArrayList<CollectionsPOJO> collectionsList;
    private LastCollectionVisibilityListener lastCollectionVisibilityListener;
    private Boolean lastCollectionFetched = false;
    private OnCollectionItemListener onCollectionItemListener;


    public CollectionExploreAdapter(ArrayList<CollectionsPOJO> collectionsList, OnCollectionItemListener onCollectionItemListener) {
        this.collectionsList = collectionsList;
        this.onCollectionItemListener = onCollectionItemListener;
    }

    public void setLastCollectionVisibilityListener(CollectionExploreAdapter.LastCollectionVisibilityListener lastCollectionVisibilityListener) {
        this.lastCollectionVisibilityListener = lastCollectionVisibilityListener;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //Log.d("pg_debug", "onViewAttachedToWindow: " + holder.getCurrentPosition() + " " + songList.size());
        if (holder.getCurrentPosition() == collectionsList.size()-1 && !lastCollectionFetched){
            lastCollectionVisibilityListener.onLastCollectionVisible();
        }

    }



    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new CollectionExploreAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_row_view, parent, false));
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

    CollectionsPOJO collectionsPOJO = new CollectionsPOJO();
    public void addLoading() {
        isLoaderVisible = true;
        collectionsList.add(collectionsPOJO);
        if(collectionsList.size()<=0) notifyItemChanged(0);
        else notifyItemInserted(collectionsList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        collectionsList.remove(collectionsPOJO);
        notifyItemRemoved(collectionsList.indexOf(collectionsPOJO));
    }

    public void clear() {
        collectionsList.clear();
        notifyDataSetChanged();
    }

    CollectionsPOJO getItem(int position) {
        return collectionsList.get(position);
    }

    public ArrayList<CollectionsPOJO> getData() {
        return (ArrayList<CollectionsPOJO>) collectionsList;
    }

    public void setLastCollectionFetched(Boolean bool) {
        lastCollectionFetched = bool;
    }



    public class ViewHolder extends BaseViewHolder {
        public TextView collectionName, view;
        public ConstraintLayout rowLayout;
        public ImageView view_icon;
        private int position;
        public ViewHolder(View v) {
            super(v);
            collectionName = v.findViewById(R.id.txt_collection_name);
            rowLayout = v.findViewById(R.id.rowLayout);
            view = v.findViewById(R.id.views_count);
            rowLayout.setOnClickListener(v1 -> {
                Log.e("sohan_debug","one song clicked");

                onCollectionItemListener.onItemClicked(collectionsList.get(position));
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

            collectionName.setText(item.getCollection_name());
            view.setText(String.valueOf(item.getViews()));
        }
        public void onBind(int position, List<Object> payloads)
        {
            this.position = position;
            if (payloads.isEmpty()){
                //
                super.onBind(position, payloads);
                CollectionsPOJO item = collectionsList.get(position);
                collectionName.setText(item.getCollection_name());
                view.setText(item.getViews());
            }
            else {

                Bundle o = (Bundle) payloads.get(0);
                for (String key : o.keySet()) {
                    if(key.equals("collectionName")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        collectionName.setText(collectionsList.get(position).getCollection_name());
                    }
                    if(key.equals("view")){
                        //Toast.makeText(itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                        view.setText(String.valueOf(collectionsList.get(position).getViews()));
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

    public void onNewData(ArrayList<CollectionsPOJO> newData) {
        if(collectionsList.size() <= 0)
        {
            collectionsList.addAll(newData);
        }
        else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CollectionDiffUtilCallback(newData, collectionsList));
            collectionsList.clear();
            collectionsList.addAll(newData);
            diffResult.dispatchUpdatesTo(this);

        }
        notifyDataSetChanged();

    }

    public interface LastCollectionVisibilityListener {
        void onLastCollectionVisible();
    }

    public interface OnCollectionItemListener{
        void onItemClicked(CollectionsPOJO collectionsPOJO);
    }
}
