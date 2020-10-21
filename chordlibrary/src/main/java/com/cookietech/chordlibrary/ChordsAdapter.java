package com.cookietech.chordlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Chord> chords;
    private Communicator communicator;
    private ThumbGenerator thumbGenerator = new ThumbGenerator();

    public ChordsAdapter(Context context,ArrayList<Chord> chords,Communicator communicator) {
        this.context = context;
        this.chords = chords;
        this.communicator = communicator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.chords_item_layout,parent,false);
        return new ChordViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ChordViewHolder chordViewHolder = (ChordViewHolder) holder;
        Chord chord = chords.get(position);
        //chordViewHolder.tv_fret_no.setText("fret "+chord.getStartFret());
        chordViewHolder.iv_thumb.setImageBitmap(thumbGenerator.getThumbBitmap(chord));

        chordViewHolder.cl_chord_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.onChordSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chords.size();
    }

    public void setChords(ArrayList<Chord> chords) {
        this.chords = chords;
        notifyDataSetChanged();
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
        }



    }

    public interface Communicator{
        void onChordSelected(int position);
    }
}
