package com.cookietech.chordera.chordDisplay;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordlibrary.ChordClass;

import java.util.ArrayList;
import java.util.HashMap;

public class ChordTouchListener implements View.OnTouchListener {
    ArrayList<Rect> chordTouchableRect = new ArrayList<>();
    HashMap<Rect,String> chordTouchableMap = new HashMap<>();
    chordSelectionListener chordSelectionListener;

    public ChordTouchListener(chordSelectionListener chordSelectionListener) {
        this.chordSelectionListener = chordSelectionListener;
    }

    boolean isClicked = true;
    Rect touchedRect;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof TabulatorTextView){
            TabulatorTextView tabulatorTextView = (TabulatorTextView) v;
            chordTouchableMap = tabulatorTextView.getChordTouchableMap();
            chordTouchableRect = tabulatorTextView.getChordTouchableRect();
            Log.d("touch_debug", "onTouch: " + event.getX() + " " + event.getY());
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isClicked = true;
                    for(Rect rect:chordTouchableRect){
                        if(rect.contains((int)event.getX(),(int)event.getY())){
                            Log.d("touch_debug", "onTouch: ");
                            touchedRect = rect;
                        }
                    }
                    Log.d("touch_debug", "ACTION_DOWN: " + event.getX() + " " + event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    isClicked = false;
                    Log.d("touch_debug", "ACTION_MOVE: " + event.getX() + " " + event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    if(isClicked && touchedRect!= null){
                        String chord = chordTouchableMap.get(touchedRect);
                        Log.d("akash_transpose_debug", "onTouch: "+chord);
                        ChordClass selectedChord = AppSharedComponents.getAllChords().get(chord.toLowerCase());
                        if(chordSelectionListener != null)
                            chordSelectionListener.onChordSelected(selectedChord);
                    }
                    isClicked = false;
                    touchedRect = null;
                    Log.d("touch_debug", "ACTION_UP: " + event.getX() + " " + event.getY());
                    break;
            }
        }else {
            return false;
        }
        return false;
    }


    public interface chordSelectionListener{
        void onChordSelected(ChordClass chordClass);
    }

}
