package com.blz.cookietech.cookietechmetronomelibrary.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.blz.cookietech.Listener.TimePickerListener;

import java.util.ArrayList;
import java.util.List;

public class TimePicker extends View {
    private static final String TAG = "TimePicker";
    private final double offset = 360f/12;


    private int width, height;
    private int middlePointX;
    private int middlePointY;
    private int timerSize = 0;
    private int timerPadding = 0;
    private Paint paintCircle;
    private int outerRadius;
    private Paint paintCenter;
    private int centerRadius;


    private int timeTextSize = 0;
    private Paint timeTextColor;
    private Rect timeValueTextBound;

    private Paint painLine;
    GestureDetector gestureDetector;
    private float touchedCircleRadiusMax = 0;

    /** positionOfTimeValue changes the time and it is used to set the time (minutes/5 - 5) **/
    private int positionOfTimeValue;

    private double innerRadius;
    private final List<Double> angleList = new ArrayList<>();

    private TimePickerListener timePickerListener;

    private int minutes = 5;





    public TimePicker(Context context) {
        super(context);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs,context);
    }


    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, Context context) {
        gestureDetector = new GestureDetector(context, new TimePickerGestureListener());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(getWidth() < getHeight()){
                    width = getWidth();
                    height = getWidth();
                }else{
                    width = getHeight();
                    height = getHeight();
                }


                middlePointX = width/2;
                middlePointY = height/2;
                middlePointX = width/2;
                middlePointY = height/2;

                timerPadding = width/10;
                timerSize = width - timerPadding;
                outerRadius = timerSize/2;
                centerRadius =  timerSize/80;

                paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintCircle.setColor(Color.parseColor("#15273B"));
                paintCenter = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintCenter.setColor(Color.parseColor("#00D49A"));


                timeTextSize = timerSize/16;
                timeTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
                timeTextColor.setColor(Color.WHITE);
                timeTextColor.setTextSize(timeTextSize);
                timeValueTextBound = new Rect();


                painLine = new Paint(Paint.ANTI_ALIAS_FLAG);
                painLine.setColor(Color.parseColor("#00D49A"));
                painLine.setStrokeWidth(5);
                painLine.setStyle(Paint.Style.STROKE);

                int positionX = (int) (middlePointX + Math.cos((-60) * (Math.PI/180)) * (outerRadius-(width/6)) );
                int positionY = (int) (middlePointY + Math.sin((-60) * (Math.PI/180)) * (outerRadius-(width/6)));
                innerRadius = Math.sqrt(Math.pow((positionX - middlePointX),2) + Math.pow((positionY - middlePointY),2));




            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);
        drawCenter(canvas);

        drawNumbers(canvas);
        drawTouchedCircle(canvas);
        drawNumber(canvas);
        drawLine(canvas);

    }

    private void drawTouchedCircle(Canvas canvas) {

        if (paintCenter != null){

            int positionX = (int) ((middlePointX + Math.cos((offset*positionOfTimeValue-60) * (Math.PI/180)) * (outerRadius-width/10) ) - timeValueTextBound.width()/50);
            int positionY = (int) ((middlePointY + Math.sin((offset*positionOfTimeValue-60) * (Math.PI/180)) * (outerRadius-height/10)) + timeValueTextBound.height()/50);
            canvas.drawCircle(positionX,positionY,touchedCircleRadiusMax,paintCenter);
            //canvas.drawCircle(positionX,positionY,20,paintCircle);

        }

    }

    private void drawLine(Canvas canvas) {

        if (painLine != null){
            int positionX = (int) (middlePointX + Math.cos((offset*positionOfTimeValue-60) * (Math.PI/180)) * (outerRadius-(width/6)) );
            int positionY = (int) (middlePointY + Math.sin((offset*positionOfTimeValue-60) * (Math.PI/180)) * (outerRadius-(width/6)));

            canvas.drawLine(middlePointX,middlePointY,positionX,positionY,painLine);
        }

    }



    private void drawNumber(Canvas canvas){
        if (timeTextColor != null){
            String num = String.valueOf(5 + (5*positionOfTimeValue));
            timeTextColor.getTextBounds(num, 0, num.length(), timeValueTextBound);
            int positionX = (int) ((middlePointX + Math.cos((offset*positionOfTimeValue-60) * (Math.PI/180)) * (outerRadius-width/10) ) - timeValueTextBound.width()/2);
            int positionY = (int) ((middlePointY + Math.sin((offset*positionOfTimeValue-60) * (Math.PI/180)) * (outerRadius-height/10)) + timeValueTextBound.height()/2);
            canvas.drawText(num, positionX, positionY, timeTextColor);
        }

    }



    private void drawNumbers(Canvas canvas) {

        if (timeTextColor != null){
            for (int i = 0; i < 12; i++) {
                String num = String.valueOf(5 + (5*i));
                timeTextColor.getTextBounds(num, 0, num.length(), timeValueTextBound);
                int positionX = (int) ((middlePointX + Math.cos((offset*i-60) * (Math.PI/180)) * (outerRadius-width/10) ) - timeValueTextBound.width()/2);
                int positionY = (int) ((middlePointY + Math.sin((offset*i-60) * (Math.PI/180)) * (outerRadius-height/10)) + timeValueTextBound.height()/2);
                canvas.drawText(num, positionX, positionY, timeTextColor);
                angleList.add((offset*i-60));

                touchedCircleRadiusMax = Math.max(touchedCircleRadiusMax,(float) timeValueTextBound.width());
                touchedCircleRadiusMax = Math.max(touchedCircleRadiusMax,(float)timeValueTextBound.height());


            }

        }
    }

    private void drawCenter(Canvas canvas) {
        if (paintCenter != null){
            canvas.drawCircle(middlePointX,middlePointY,centerRadius,paintCenter);
        }
    }

    private void drawCircle(Canvas canvas) {
        if (paintCircle != null){
            canvas.drawCircle(middlePointX,middlePointY,outerRadius,paintCircle);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    class TimePickerGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            float touchedX = e.getX();
            float touchedY = e.getY();
            double posX = e.getX() - middlePointX;
            double posY = e.getY() - middlePointY;
            double touchedAngle = Math.toDegrees(Math.atan2(touchedY - middlePointY, touchedX - middlePointX));
            Log.d(TAG, "onSingleTapUp: touchedAngle prev: " + touchedAngle);

            if(touchedAngle>-180 && touchedAngle<-75){
                Log.d(TAG, "onSingleTapUp: both negative");
                touchedAngle = touchedAngle + 360;
            }


            double touchedDistance = Math.sqrt(Math.pow((touchedX - middlePointX),2) + Math.pow((touchedY - middlePointY),2));


            if (touchedDistance < outerRadius && touchedDistance > innerRadius){
                Log.d(TAG, "onSingleTapUp: touchedAngle final: " + touchedAngle);
                positionOfTimeValue = findClosest(touchedAngle);

                minutes = positionOfTimeValue*5 + 5;

                Log.d(TAG, "onSingleTapUp: minutes : " + minutes);
                timePickerListener.onTimeSet(minutes);
                invalidate();
            }

            return true;


        }


        public int findClosest(double target) {
            int idx = 0;
            double dist = Math.abs(angleList.get(0)- target);

            for (int i = 1; i< angleList.size(); i++) {
                double cdist = Math.abs(angleList.get(i) - target);

                if (cdist < dist) {
                    idx = i;
                    dist = cdist;
                }
            }

            return idx;
        }
    }

    public void setTimeSetListener(TimePickerListener listener){
        timePickerListener = listener;

    }

    public int getDefaultMinutes(){
        return minutes;
    }

    public void setTime(int positionOfTimeValue){
        this.positionOfTimeValue = positionOfTimeValue;
        invalidate();
    }


}
