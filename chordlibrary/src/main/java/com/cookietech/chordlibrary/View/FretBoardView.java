package com.cookietech.chordlibrary.View;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.cookietech.chordlibrary.Variation;
import com.cookietech.chordlibrary.databinding.LayoutFretBoardViewBinding;

import java.util.ArrayList;

public class FretBoardView extends ScrollView {
    LayoutFretBoardViewBinding binding;
    ArrayList<Integer> allMiddlePosition = new ArrayList<>();

    public FretBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        binding = LayoutFretBoardViewBinding.inflate(layoutInflater,this,true);
        setVerticalScrollBarEnabled(false);

        Log.d("akash_fretboard_inspect", "FretBoardView: background");

        binding.fretboardBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                allMiddlePosition = binding.fretboardBackground.getAllFretMiddlePosition();
            }
        });

    }



    public void setChord(Variation chord){
        binding.fretboardForeground.setChord(chord);
        int startFret = chord.getFirstFret();
        int scrollIngDistance = 0;
        if(startFret > 3){
            scrollIngDistance = allMiddlePosition.get(startFret - 3);
        }
        smoothScrollTo(0,scrollIngDistance);
    }

    public void setNotesVisible(Boolean bool){
        binding.fretboardForeground.setNotesVisible(bool);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("akash_fretboard_inspect", "onDraw: wrapper");
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
