package com.cookietech.chordlibrary;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import java.util.ArrayList;

public class ThumbGenerator {
    private Bitmap thumbBitmap;
    private Canvas mCanvas;
    private Variation chord;
    float step;

    public ThumbGenerator(int sizeInPx) {
        thumbBitmap = Bitmap.createBitmap(sizeInPx,sizeInPx, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        step = (sizeInPx-10)/5f;
        mCanvas = new Canvas(thumbBitmap);

        for (int i = 0; i < 6; i++) {
            float y = step * i;
            mCanvas.drawLine(5,y+5,sizeInPx-5,y+5,paint);
            float x = step* i;
            mCanvas.drawLine(x+5,5,x+5,sizeInPx-5,paint);
        }

    }


    public Bitmap getThumbBitmap(Variation chord) {
        Bitmap mainThumbBitmap = thumbBitmap.copy(thumbBitmap.getConfig(),true);
        mCanvas = new Canvas(mainThumbBitmap);
        ArrayList notes = chord.getNotes();
        int firstFret = chord.getFirstFret();

        if(firstFret > 2){
            for (int i = 0; i < notes.size(); i++) {
                int note = (int) notes.get(i);
                if(note == -1){
                    continue;
                }

                addDot(i,note-firstFret);

            }
        }else{
            for (int i = 0; i < notes.size(); i++) {
                int note = (int) notes.get(i);
                if(note == -1){
                    continue;
                }

                addDot(i,note-1);

            }
        }


        return mainThumbBitmap;
    }

    private void addDot(int string ,int fret) {
        Paint paint = new Paint();
        paint.setColor(0xff1089ff);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float left = step* string;
            float top = step* fret + step/2;
            mCanvas.drawOval(left,top,left+step/2,top+step/2,paint);
        }
    }
}
