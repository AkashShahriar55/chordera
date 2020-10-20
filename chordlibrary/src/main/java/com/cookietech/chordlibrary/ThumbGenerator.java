package com.cookietech.chordlibrary;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import java.util.ArrayList;

public class ThumbGenerator {
    private Bitmap thumbBitmap;
    private Canvas mCanvas;
    private Chord chord;

    public ThumbGenerator() {
        thumbBitmap = Bitmap.createBitmap(110,110, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(0xffffffff);

        mCanvas = new Canvas(thumbBitmap);

        for (int i = 0; i < 6; i++) {
            int y = 20 * i;
            mCanvas.drawLine(5,y+5,105,y+5,paint);
            int x = 20* i;
            mCanvas.drawLine(x+5,5,x+5,105,paint);
        }

    }


    public Bitmap getThumbBitmap(Chord chord) {
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
            int left = 20* string;
            int top = 20* fret + 10;
            mCanvas.drawOval(left,top,left+10,top+10,paint);
        }
    }
}
