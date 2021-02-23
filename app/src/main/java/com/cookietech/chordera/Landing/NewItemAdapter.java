package com.cookietech.chordera.Landing;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;

public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.NewItemViewHolder> {
    private final MainViewModel mainViewModel;
    RecyclerView newItemRecyclerView;
    Context context;
    ArrayList<SongsPOJO> newSongsData= new ArrayList<>();

    public NewItemAdapter(Context context, RecyclerView newItemRecyclerView, MainViewModel mainViewModel) {
        this.newItemRecyclerView = newItemRecyclerView;
        this.mainViewModel = mainViewModel;
        this.context = context;
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
        holder.newItemHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ConnectionManager.isOnline(context)){
                    Toast.makeText(context,"No internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(songsPOJO));
                mainViewModel.setSelectedSong(songsPOJO);
            }
        });

        Glide.with(holder.itemView)
                .load(songsPOJO.getImage_url())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .thumbnail(0.5f)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.ivBackground);
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
//            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    newItemHolder.getLayoutParams().width = (int) (size*0.8);
//                    newItemHolder.getLayoutParams().height= size;
//                    newItemHolder.setRadius(10);
//                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });


        }
    }
}
