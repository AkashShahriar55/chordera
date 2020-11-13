package com.blz.cookietech.cookietechmetronomelibrary.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import java.util.logging.Handler;

public class LightsView extends View {

    private static final String TAG = "LightsView";

    private int height,width,middlePointX,getMiddlePointY;
    private Paint lightOffPaint,lightOnPaint;
    private int lightNumber;
    private float radius;
    private float distanceBtnTwoCenter;
    private boolean isToggling = false;

    private int bpm = 80;
    private int subdivision = 1;
    private long togglingDelay = 0;

    public LightsView(Context context) {
        super(context);
    }
    private int lightPointer = 0;

    public LightsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                width = getWidth();
                height = getHeight();
                middlePointX = width/2;
                getMiddlePointY = height/2;
                float radiusX,radiusY;
                radiusY = (float) (height * 0.1); // circle height will be 40% off parent. 40%/2 = radiusY

                Log.d(TAG, "onGlobalLayout: width : " + width);
                Log.d(TAG, "onGlobalLayout: height : " + height);
                Log.d(TAG, "onGlobalLayout: lightNumber : " + lightNumber);

                distanceBtnTwoCenter = (float) width/(lightNumber + 1);
                radiusX = (distanceBtnTwoCenter/2) - (distanceBtnTwoCenter/10);

                radius = Math.min(radiusX,radiusY);

                Log.d(TAG, "onGlobalLayout: radiusX : " + radiusX);
                Log.d(TAG, "onGlobalLayout: radiusY : " + radiusY);
                Log.d(TAG, "onGlobalLayout: radius : " + radius);


                lightOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                lightOffPaint.setStyle(Paint.Style.STROKE);
                lightOffPaint.setStrokeWidth(2);
                lightOffPaint.setColor(Color.parseColor("#80848484")); //OFF color;
                lightOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                lightOnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                lightOnPaint.setStrokeWidth(2);
                lightOnPaint.setColor(Color.parseColor("#3495FF")); //ON color;

                //lightPaint.stroke
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;

            }
        });

    }



    public LightsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LightsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: called");
        long drawStart = SystemClock.currentThreadTimeMillis();
        drawLight(canvas);
        long drawEnd = SystemClock.currentThreadTimeMillis();

        long timeLoss = drawEnd - drawStart;

        if(isToggling){
            toggleLight();
            postInvalidateDelayed(togglingDelay - timeLoss);
        }

    }


    private void drawLight(Canvas canvas) {
        if (lightOffPaint != null){
           for(int i = 1;i <= lightNumber;i++){

               if(lightPointer == i){
                   canvas.drawCircle(distanceBtnTwoCenter*i, getMiddlePointY, radius, lightOnPaint);
               }else {
                   canvas.drawCircle(distanceBtnTwoCenter*i, getMiddlePointY, radius, lightOffPaint);
               }


           }
        }
    }

    public void toggleLight(){
        if(lightPointer < lightNumber){

            lightPointer++;
        }else{
            lightPointer = 1;
        }

    }


    public void setLightNumber(int lightNumber){

        this.lightNumber = lightNumber;
        float radiusX,radiusY;
        radiusY = (float) (height * 0.1); // circle height will be 40% off parent. 40%/2 = radiusY

        Log.d(TAG, "onGlobalLayout: width : " + width);
        Log.d(TAG, "onGlobalLayout: height : " + height);
        Log.d(TAG, "onGlobalLayout: lightNumber : " + lightNumber);

        distanceBtnTwoCenter = (float) width/(lightNumber + 1);
        radiusX = (distanceBtnTwoCenter/2) - (distanceBtnTwoCenter/10);

        radius = Math.min(radiusX,radiusY);

        if(!isToggling){
            invalidate();
        }

    }

    public void startToggling() {
        isToggling = true;
        toggleLight();
        invalidate();
    }

    public void stopToggling() {
        isToggling = false;
        lightPointer = 0;
        invalidate();
    }


    public void setBpm(int bpm) {
        this.bpm = bpm;
        togglingDelay = (60000/(bpm));
    }
}
