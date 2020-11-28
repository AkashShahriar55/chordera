package com.cookietech.chordlibrary.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.cookietech.chordlibrary.AppComponent.CacheFactory;
import com.cookietech.chordlibrary.Model.Constants;
import com.cookietech.chordlibrary.R;

import java.util.ArrayList;

public class Fretboard extends View {
    private int canvasWidth;
    private int canvasHeight;
    private int distanceBetweenFrets ;
    private Canvas mCanvas;




    private ArrayList<Integer> allFretMiddlePosition = new ArrayList<>();


    private float scale = 1;
    private int leftOffset;

    public Fretboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Log.d("akash_fretboard_inspect", "Fretboard: background");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        mCanvas.translate(leftOffset,0);
        generateFretboard();
        Constants.setAllFretMiddlePosition(allFretMiddlePosition);
        Log.d("akash_fretboard_inspect", "onDraw: background");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
        canvasWidth = (int) (w * Constants.fretboard_width_ratio);
        leftOffset = (w - canvasWidth)/2;
        canvasHeight = (int) (canvasWidth * Constants.fretboard_height_ratio_with_width);
        scale = (float) canvasHeight/Constants.original_fretboard_height;
        setMeasuredDimension(w, canvasHeight);
    }


    public void generateFretboard(){
        this.distanceBetweenFrets = (int) (Constants.original_fret_distance*scale);
        Bitmap fretboard = BitmapManager.decodeSampledBitmapFromResource(getResources(),R.drawable.final_fretboard,canvasWidth,canvasHeight);
        fretboard = Bitmap.createScaledBitmap(fretboard,canvasWidth,canvasHeight,false);
        mCanvas.drawBitmap(fretboard,0,0,null);
        addFrets();
        addDots();;
        fretboard.recycle();
    }



    private void addDots() {
        int dotWidth = (int) (Constants.original_fretboard_dot_width * scale);
        int dotOffset = dotWidth /2;


        Bitmap dot = BitmapManager.decodeSampledBitmapFromResource(getResources(),R.drawable.dot,dotWidth,dotWidth);
        dot = Bitmap.createScaledBitmap(dot,dotWidth, dotWidth,false);

        int leftForSingleDot = (canvasWidth/2) - dotOffset;
        int leftForDoubleDotOne = (canvasWidth/3) - dotOffset;
        int leftForDoubleDotTwo = (canvasWidth*2/3) - dotOffset;


        for (int i = 1; i <= allFretMiddlePosition.size(); i++) {
            int top = allFretMiddlePosition.get(i-1);
            if(i == 12 || i == 24){
                mCanvas.drawBitmap(dot, leftForDoubleDotOne,top - dotOffset,null);
                mCanvas.drawBitmap(dot, leftForDoubleDotTwo,top - dotOffset,null);
            }
            else if(i == 3 || i == 5 || i == 7 || i == 9 || i == 15 || i == 17 || i == 19 || i == 21){
                mCanvas.drawBitmap(dot, leftForSingleDot,top - dotOffset,null);
            }


        }

        dot.recycle();
    }

    private void addFrets() {
        int fretWidth = canvasWidth;
        int fretHeight = (int) (Constants.original_fret_height * scale);

        int startOffset = (int) (Constants.original_neck_bar_height * scale);
        int fretOffset = (int) (Constants.original_fret_offset * scale);

        Log.d("akash_debug", "addFrets: " +startOffset+" "+fretOffset);

        Bitmap fret = BitmapManager.decodeSampledBitmapFromResource(getResources(),R.drawable.final_fret,fretWidth,fretHeight);
        fret = Bitmap.createScaledBitmap(fret,fretWidth,fretHeight,false);

        int prevTop = 0;

        for (int i = 1; i <= 24; i++) {
            int top = 0;
            if(i==1){
                top = distanceBetweenFrets + startOffset;
                Log.d("akash_debug", "addFrets: " +startOffset +" "+  top);
                mCanvas.drawBitmap(fret, 0,top ,null);
            }else{
                top = (int) (prevTop + fretOffset + (distanceBetweenFrets - (2.5 * (i-1))));
                Log.d("akash_debug", "addFrets: " +fretOffset +" "+  top );
                mCanvas.drawBitmap(fret, 0,top,null);
            }

            int middle = prevTop + fretOffset +  (top - (prevTop + fretOffset))/2;
            allFretMiddlePosition.add(middle);




            prevTop = top;

        }

        fret.recycle();


    }

    public ArrayList<Integer> getAllFretMiddlePosition() {
        Log.d("akash_fretboard_inspect", "getAllFretMiddlePosition: " + allFretMiddlePosition.size());
        return allFretMiddlePosition;
    }


}
