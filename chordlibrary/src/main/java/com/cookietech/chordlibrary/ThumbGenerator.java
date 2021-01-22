package com.cookietech.chordlibrary;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class ThumbGenerator {
    private float gap;
    private Bitmap thumbBitmap;
    private Canvas mCanvas;
    private Variation chord;
    float step;
    private ArrayList<Integer> fingers;
    private boolean hasInfo;

    public ThumbGenerator(int sizeInPx) {
        thumbBitmap = Bitmap.createBitmap(sizeInPx,sizeInPx, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        gap = (float) sizeInPx/22;
        step = (sizeInPx-gap*2)/5f;
        mCanvas = new Canvas(thumbBitmap);

        for (int i = 0; i < 6; i++) {
            float y = step * i;
            mCanvas.drawLine(gap,y+gap,sizeInPx-gap,y+gap,paint);
            float x = step* i;
            mCanvas.drawLine(x+gap,gap,x+gap,sizeInPx-gap,paint);
        }

    }


    public Bitmap getThumbBitmap(Variation chord,boolean hasInfo) {
        Bitmap mainThumbBitmap = thumbBitmap.copy(thumbBitmap.getConfig(),true);
        mCanvas = new Canvas(mainThumbBitmap);
        ArrayList notes = chord.getNotes();
        fingers = chord.getFingers();
        int firstFret = chord.getFirstFret();
        this.hasInfo = hasInfo;
        if(firstFret > 1){
            if(hasInfo ){
                for (int i = 0; i < notes.size()-1; i++) {
                    addFretNo(i,chord.getFirstFret()+i-1);
                }
            }
            for (int i = 0; i < notes.size(); i++) {
                int note = (int) notes.get(i);

                if(note == -1){
                    if(hasInfo)
                        addCross(i);
                    continue;
                }

                addDot(i,note-firstFret+1);
                if(hasInfo)
                    addFinger(i,note-firstFret+1);
            }
        }else{
            if(hasInfo ){
                for (int i = 0; i < notes.size()-1; i++) {
                    addFretNo(i,chord.getFirstFret()+i);
                }
            }
            for (int i = 0; i < notes.size(); i++) {
                int note = (int) notes.get(i);
                if(note == -1){
                    if(hasInfo)
                        addCross(i);
                    continue;
                }else if(note == 0){
                    continue;
                }

                addDot(i,note-1);
                if(hasInfo)
                    addFinger(i,note-1);

            }
        }


        return mainThumbBitmap;
    }

    private void addFretNo(int grid, int fret) {
        float left = 0;
        float top = step*grid + gap + step/2;
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(step/4);
        Rect textBoundRect = new Rect();
        String fretNo = String.valueOf(fret);
        textPaint.getTextBounds(fretNo,0,fretNo.length(),textBoundRect);
        textPaint.setColor(Color.WHITE);
        mCanvas.drawText(fretNo,left,top+textBoundRect.height()/2f,textPaint);
    }

    private void addCross(int string) {
        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        float left = (step* string ) + gap;
        float top = gap;
        mCanvas.drawLine(left-step/4,top-step/4,left+step/4,top+step/4,paint);
        mCanvas.drawLine(left+step/4,top-step/4,left-step/4,top+step/4,paint);
    }

    private void addDot(int string ,int fret) {
        Paint paint = new Paint();
        paint.setColor(0xff1089ff);
        float left = (step* string ) + gap - (step/4);
        float top =  (step* fret) +gap - (step/4) + step/2;
        mCanvas.drawRoundRect(left,top,left+step/2,top+step/2,step/4,step/4,paint);

    }


    private void addFinger(int string,int fret){
        float left = (step* string ) + gap;
        float top =  (step* fret) +gap + step/2;
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(step/4);
        Rect textBoundRect = new Rect();
        String finger = String.valueOf(fingers.get(string));
        textPaint.getTextBounds(finger,0,finger.length(),textBoundRect);
        textPaint.setColor(Color.WHITE);
        mCanvas.drawText(finger,left-textBoundRect.width()/2f,top+textBoundRect.height()/2f,textPaint);
    }
}
