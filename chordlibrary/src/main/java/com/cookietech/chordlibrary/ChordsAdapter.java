package com.cookietech.chordlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordlibrary.AppComponent.ThumbGeneratorListener;

import java.util.ArrayList;
import java.util.List;

public class ChordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ThumbGeneratorListener {
    private Context context;
    private ArrayList<Variation> chords;
    private Communicator communicator;
    private RecyclerView recyclerView;
    private int lastSelectedPosition = 0;


    public ChordsAdapter(Context context, ArrayList<Variation> chords, Communicator communicator, RecyclerView recyclerView) {
        this.context = context;
        this.chords = chords;
        this.communicator = communicator;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.chords_item_layout,parent,false);
        return new ChordViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() > 0) {
            for(Object data : payloads){
                if(data != null){
                    if(data instanceof Bitmap){
                        Bitmap thumb = (Bitmap) data;
                        ((ChordViewHolder)holder).iv_thumb.setImageBitmap(thumb);
                    }else if(data instanceof Boolean){
                        ((ChordViewHolder) holder).setSelected((Boolean) data);
                    }

                }
            }
        }else{
            super.onBindViewHolder(holder, position, payloads);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ChordViewHolder chordViewHolder = (ChordViewHolder) holder;
        Variation chord = chords.get(position);
        //chordViewHolder.tv_fret_no.setText("fret "+chord.getStartFret());
        chordViewHolder.setSelected(lastSelectedPosition == position);
        new Thread(new ThumbGeneratorRunnable(position,chord,this)).start();
        chordViewHolder.tv_fret_no.setText("fret "+chord.getFirstFret());
        chordViewHolder.cl_chord_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.onChordSelected(position);
                setSelectedItemToMiddle(v);
                updateSelection(position);
            }
        });
    }

    private void updateSelection(int position) {
        if (lastSelectedPosition==position)
            return;
        notifyItemChanged(position,true);
        notifyItemChanged(lastSelectedPosition,false);
        lastSelectedPosition = position;
    }

    @Override
    public int getItemCount() {
        return chords.size();
    }

    public void setChords(ArrayList<Variation> chords) {
        this.chords = chords;
        lastSelectedPosition = 0;
        if(recyclerView!=null)
            recyclerView.smoothScrollToPosition(0);
        notifyDataSetChanged();
    }

    @Override
    public void onThumbGenerated(final int index, final Bitmap thumb, Variation chord) {
        if(chords.contains(chord)){
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(index,thumb);
                }
            });

        }
    }

    private class ChordViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_chord_holder;
        ImageView iv_thumb;
        TextView tv_fret_no;


        ChordViewHolder(@NonNull View itemView) {
            super(itemView);
            cl_chord_holder = itemView.findViewById(R.id.chord_holder);
            iv_thumb = itemView.findViewById(R.id.chord_thumb);
            tv_fret_no = itemView.findViewById(R.id.fret_number);
            iv_thumb.getLayoutParams().width = dpToPx(50);
            iv_thumb.getLayoutParams().height = dpToPx(50);
        }


        public void setSelected(boolean value) {
            if(value)
                cl_chord_holder.setBackgroundColor(Color.parseColor("#B3325981"));
            else
                cl_chord_holder.setBackgroundColor(0);
        }
    }

    public interface Communicator{
        void onChordSelected(int position);
    }

    class ThumbGeneratorRunnable implements Runnable {
        int index;
        Variation chord;
        ThumbGeneratorListener listener;
        private ThumbGenerator thumbGenerator = new ThumbGenerator(dpToPx(50));

        public ThumbGeneratorRunnable(int index, Variation chord, ThumbGeneratorListener listener) {
            this.index = index;
            this.chord = chord;
            this.listener = listener;
        }

        @Override
        public void run() {
            Bitmap bitmap = thumbGenerator.getThumbBitmap(chord,false);
            if(bitmap!=null)
                listener.onThumbGenerated(index,bitmap,chord);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void setSelectedItemToMiddle(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int deviceHeight = context.getResources().getDisplayMetrics().heightPixels;
        int center = deviceHeight/2;
        center = center - (view.getHeight()/2);
        final int scroll = center - location[1]+ (deviceHeight-recyclerView.getHeight());
        Log.d("scroll_debug", "setSelectedItemToMiddle: " + scroll);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollBy(0,-scroll);
            }
        });
    }

}
