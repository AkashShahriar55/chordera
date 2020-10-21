package com.cookietech.chordera.featureSongList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.utilities.MyDiffUtilCallback;

import java.util.ArrayList;
import java.util.List;

public class SongListShowingAdapter extends RecyclerView.Adapter<SongListShowingAdapter.MyViewHolder> {
    private ArrayList<Song> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tittle, band;
        public MyViewHolder(View v) {
            super(v);
            tittle = v.findViewById(R.id.tittle);
            band = v.findViewById(R.id.band);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SongListShowingAdapter(ArrayList<Song> mDataset) {
        mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SongListShowingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card_view, parent, false);
        return new SongListShowingAdapter.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Song song = mDataset.get(position);
        holder.tittle.setText(song.getTittle());
        holder.band.setText(song.getBandName());
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, List<Object> payloads) {

        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads);
        }
        else {
            Bundle o = (Bundle) payloads.get(0);
            for (String key : o.keySet()) {
                if(key.equals("tittle")){
                    Toast.makeText(holder.tittle.getContext(), "Song "+position+" : Tittle Changed", Toast.LENGTH_SHORT).show();;
                    holder.tittle.setText(mDataset.get(position).getTittle());
                }
                if(key.equals("band")){
                    Toast.makeText(holder.itemView.getContext(), "Song "+position+" : Band Name Changed", Toast.LENGTH_SHORT).show();;
                    holder.band.setText(mDataset.get(position).getBandName());
                }
            }
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void onNewData(ArrayList<Song> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(newData, mDataset));
        diffResult.dispatchUpdatesTo(this);
        this.mDataset.clear();
        this.mDataset.addAll(newData);
    }
}
