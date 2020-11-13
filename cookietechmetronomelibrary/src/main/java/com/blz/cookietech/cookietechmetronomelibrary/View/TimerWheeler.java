package com.blz.cookietech.cookietechmetronomelibrary.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.blz.cookietech.Helpers.Constants;
import com.blz.cookietech.Listener.StopTimerListener;
import com.blz.cookietech.cookietechmetronomelibrary.R;

import java.util.Locale;

public class TimerWheeler extends View {
    private static final String TAG = "TimerWheeler";
    private float angle = (float) 0.0;
    private String timerValue;
    int width;
    int height;
    private int middlePointX;
    private int middlePointY;

    Drawable wheeler;
    Drawable wave;
    Drawable  outerRing;
    private int waveSize;
    int wheelerSize = 0;
    int wheelerPadding = 0;

    Paint timerTextColor;
    float timerTextSize;
    private float timerValueTextSize;
    private Paint timerValueTextColor;

    private Rect timerValueTextBound;
    final RectF oval = new RectF();
    private Paint outerRingProgressPaint;

    // For timer
    private long TIME_IN_MILLS;
    private long TIME_LEFT_IN_MILLS;
    private long TIME_IN_MINUTES;

    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    private boolean isTimerSet;
    private float percent = 0;

    private StopTimerListener stopTimerListener;







    public TimerWheeler(Context context) {
        super(context);
    }

    public TimerWheeler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        wheeler = ResourcesCompat.getDrawable(getResources(),R.drawable.metronome_controller,null);
        wave  = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_wave,null);
        outerRing = ResourcesCompat.getDrawable(getResources(),R.drawable.wheeler_progress,null);

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

                wheelerPadding = width/10;
                wheelerSize = width - wheelerPadding;
                wheeler.setBounds(wheelerPadding,wheelerPadding,wheelerSize,wheelerSize);
                outerRing.setBounds(0,0,width,height);

                timerTextSize = (float) width/12;
                timerTextColor = new Paint();
                timerTextColor.setColor(Color.WHITE);
                timerTextColor.setTextSize(timerTextSize);

                timerValueTextSize = (float) width/5;
                timerValueTextColor = new Paint();
                timerValueTextColor.setColor(Color.WHITE);
                timerValueTextColor.setTextSize(timerValueTextSize);
                timerValueTextColor.setAlpha(50);


                timerValueTextBound = new Rect();
                //bpmTextBound = new Rect();
                //waveBound = new Rect();

                outerRingProgressPaint = new Paint();
                outerRingProgressPaint.setStrokeWidth(width/20);
                outerRingProgressPaint.setStyle(Paint.Style.STROKE);
                outerRingProgressPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.parseColor("#0072BC"), Color.parseColor("#00D49A"), Shader.TileMode.CLAMP));
                outerRingProgressPaint.setStrokeCap(Paint.Cap.ROUND);


            }

        });
    }

    public TimerWheeler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimerWheeler(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (timerValueTextColor != null && timerTextColor != null && outerRingProgressPaint != null){
            Log.d(TAG, "onDraw: ");
            canvas.save();
            Log.d("akash_wheeler", "onDraw: " + percent + " " + getAngleFromPercent(percent));
            canvas.save();
            angle = getAngleFromPercent(percent);
            canvas.rotate(angle,(float)width/2,(float)height/2);
            wheeler.draw(canvas);
            canvas.restore();
            outerRing.draw(canvas);


            //String bpmValue = String.valueOf((int)percent);
            timerValueTextColor.getTextBounds(timerValue,0,timerValue.length(),timerValueTextBound);
            float timerValueTextWidth = timerValueTextBound.width();
            float timerValueTextHeight = timerValueTextBound.height();
            int bpmValuePositionX = (int) (middlePointX - timerValueTextWidth/2);
            int bpmValuePositionY = (int) (middlePointY + timerValueTextHeight/2);
            canvas.drawText(timerValue,bpmValuePositionX,bpmValuePositionY,timerValueTextColor);


            float timerTextWidth = timerTextColor.measureText("Timer");
            int timerPositionX = middlePointX - (int) timerTextWidth /2;
            int timerPositionY = (int) (middlePointY - timerValueTextHeight);
            canvas.drawText("Timer",timerPositionX,timerPositionY,timerTextColor);

            float radius;

            if (width > height) {
                radius = (float) (height / 2.222) + width/40f;
            } else {
                radius = (float) (width / 2.222) + width/40f;
            }




            float angle = getAngleFromPercent(percent);
            if(angle < 0)
                angle = angle + 360;

            //Log.d("akash_wheeler", "onDraw: degree" + getAngleFromPercent(percent) );

            oval.set(middlePointX - radius,
                    middlePointY - radius,
                    middlePointX + radius,
                    middlePointY + radius);
            canvas.drawArc(oval, -90, angle, false, outerRingProgressPaint);
        }
    }

    public void startTimer(){

        timer = new CountDownTimer(TIME_LEFT_IN_MILLS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT_IN_MILLS = millisUntilFinished;

                updateTimerView();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                stopTimerListener.onStopTimer();
                resetTimer();

            }
        }.start();
        isTimerRunning = true;

    }

    private void updateTimerView() {

        int minutes = (int) (TIME_LEFT_IN_MILLS/Constants.ONE_SECOND) / 60;
        int seconds = (int) (TIME_LEFT_IN_MILLS/Constants.ONE_SECOND) % 60;
        timerValue = String.format(Locale.getDefault(),"%02d : %02d",minutes,seconds);
        if(TIME_IN_MILLS > 0){
            percent = (float) TIME_LEFT_IN_MILLS/TIME_IN_MILLS * 100;
            timerValueTextColor.setAlpha(255);
        }else{
            percent = 0;
            if (timerValueTextColor != null){
                timerValueTextColor.setAlpha(50);
            }

        }

        Log.d(TAG, "updateTimerView: percent : " + percent);

        invalidate();

    }


    public void resetTimer(){
        TIME_LEFT_IN_MILLS = TIME_IN_MILLS;
        percent = 0;

        updateTimerView();

    }

    public void stopTimer(){
        timer.cancel();
        isTimerRunning = false;
        resetTimer();
    }

    public void setUpTimer(long timeInMinutes){

       TIME_IN_MINUTES = timeInMinutes;
       TIME_IN_MILLS = TIME_IN_MINUTES * Constants.ONE_MINUTE;
       TIME_LEFT_IN_MILLS = TIME_IN_MILLS;
        updateTimerView();

    }

    public boolean isTimerRunning(){
        return isTimerRunning;
    }

    private float getAngleFromPercent(float percent) {
        return (360 * percent/100);
    }

    public void setOnStopTimerListener(StopTimerListener listener){
        stopTimerListener = listener;

    }

}
