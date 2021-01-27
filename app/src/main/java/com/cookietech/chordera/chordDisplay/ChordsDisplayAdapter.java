package com.cookietech.chordera.chordDisplay;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordlibrary.AppComponent.ThumbGeneratorListener;
import com.cookietech.chordlibrary.ChordClass;
import com.cookietech.chordlibrary.ThumbGenerator;
import com.cookietech.chordlibrary.Variation;

import java.util.ArrayList;
import java.util.List;

public class ChordsDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ThumbGeneratorListener {
    private Context context;
    private ArrayList<ChordClass> chords;
    private Communicator communicator;
    private RecyclerView recyclerView;
    private int thumbSize;


    public ChordsDisplayAdapter(Context context, ArrayList<ChordClass> chords, Communicator communicator, RecyclerView recyclerView) {
        this.context = context;
        this.chords = chords;
        this.communicator = communicator;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.chords_display_item_layout,parent,false);
        thumbSize = recyclerView.getWidth()/5-dpToPx(2);
        return new ChordViewHolder(root);
    }

    public ArrayList<ChordClass> getChords() {
        return chords;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() > 0) {
            for(Object data : payloads){
                if(data != null){
                    Bitmap thumb = (Bitmap) data;
                    ((ChordViewHolder)holder).iv_thumb.setImageBitmap(thumb);
                }
            }
        }else{
            super.onBindViewHolder(holder, position, payloads);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ChordViewHolder chordViewHolder = (ChordViewHolder) holder;
        final ChordClass chordClass = chords.get(position);
        Variation variation = chordClass.getVariations().get(0);
        String chordName = chordClass.getName().substring(0,1).toUpperCase() + chordClass.getName().substring(1).toLowerCase();
        chordViewHolder.tv_fret_no.setText(chordName);
        new Thread(new ThumbGeneratorRunnable(position,variation,this)).start();

        chordViewHolder.cl_chord_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.onChordSelected(chordClass);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(chords ==null){
            return 0;
        }
        return chords.size();
    }

    public void setChords(ArrayList<ChordClass> chords) {
        this.chords = chords;
        notifyDataSetChanged();
    }

    @Override
    public void onThumbGenerated(final int index, final Bitmap thumb, Variation chord) {
        if(chords.get(index).getVariations().contains(chord)){
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
            cl_chord_holder = itemView.findViewById(com.cookietech.chordlibrary.R.id.chord_holder);
            iv_thumb = itemView.findViewById(com.cookietech.chordlibrary.R.id.chord_thumb);
            tv_fret_no = itemView.findViewById(com.cookietech.chordlibrary.R.id.fret_number);
            iv_thumb.getLayoutParams().width = thumbSize;
            iv_thumb.getLayoutParams().height = thumbSize;
        }



    }

    public interface Communicator{
        void onChordSelected(ChordClass chord);
    }

    class ThumbGeneratorRunnable implements Runnable {
        int index;
        Variation chord;
        ThumbGeneratorListener listener;
        private final ThumbGenerator thumbGenerator = new ThumbGenerator(thumbSize);

        public ThumbGeneratorRunnable(int index, Variation chord, ThumbGeneratorListener listener) {
            this.index = index;
            this.chord = chord;
            this.listener = listener;

        }

        @Override
        public void run() {
            Bitmap bitmap = thumbGenerator.getThumbBitmap(chord);
            if(bitmap!=null)
                listener.onThumbGenerated(index,bitmap,chord);
        }
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}

