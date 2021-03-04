package com.cookietech.chordera.featureSongList.saved;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cookietech.chordera.R;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.models.SongsPOJO;


public class AllSavedSongPagedAdapter extends PagedListAdapter<SongsEntity,AllSavedSongPagedAdapter.AllSavedSongViewHolder> {

    private final AllSavedSongPagedAdapter.OnItemClickListener onItemClickListener;
    private  AllSavedSongPagedAdapter.OnItemLongClickListener onItemLongClickListener;
    private Context context;

    protected AllSavedSongPagedAdapter(Context context,OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        this.context = context;

    }


    @NonNull
    @Override
    public AllSavedSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new AllSavedSongViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllSavedSongViewHolder holder, int position) {
        SongsEntity songsEntity = getItem(position);

        if (songsEntity != null) {
            holder.bindTo(songsEntity,position);
        } else {
            holder.clear();
        }

    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }


    public class AllSavedSongViewHolder extends RecyclerView.ViewHolder {
        public TextView song_name, artist_name, view;
        public ConstraintLayout rowLayout;
        public ImageView view_icon;
        public AllSavedSongViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.txt_song_tittle);
            artist_name = itemView.findViewById(R.id.txt_artist);
            rowLayout = itemView.findViewById(R.id.rowLayout);
            view = itemView.findViewById(R.id.views_count);
            view_icon = itemView.findViewById(R.id.view_icon);
            view.setVisibility(View.GONE);
            view_icon.setVisibility(View.GONE);
        }

        public void clear() {
            itemView.invalidate();
            song_name.invalidate();
            artist_name.invalidate();

        }

        public void bindTo(SongsEntity songsEntity,int position) {
            song_name.setText(songsEntity.getSong_name());
            artist_name.setText(songsEntity.getArtist_name());
            GestureDetector gestureDetector = new GestureDetector(context, new SavedSongGestureListener(songsEntity,position));

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
    }


    private static final DiffUtil.ItemCallback<SongsEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SongsEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull SongsEntity oldItem, @NonNull SongsEntity newItem) {
                    return oldItem.getSong_id().equals(newItem.getSong_id());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SongsEntity oldItem, @NonNull SongsEntity newItem) {
                    return oldItem.getSong_id().equals(newItem.getSong_id());
                }
    };

    interface OnItemClickListener {
        void onItemClick(SongsPOJO song);
    }
    interface OnItemLongClickListener{
        void onItemLogClick(SongsEntity songsEntity, int position);
    }

     class SavedSongGestureListener extends GestureDetector.SimpleOnGestureListener{
         SongsEntity songsEntity;
         int position;

         public SavedSongGestureListener(SongsEntity songsEntity,int position) {
             this.songsEntity = songsEntity;
             this.position = position;
         }

         @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            onItemClickListener.onItemClick(songsEntity.convertToSongsPOJO());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onItemLongClickListener.onItemLogClick(songsEntity, position);

        }
    }
}
