package com.blz.cookietech.cookietechmetronomelibrary.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.blz.cookietech.Helpers.Constants;
import com.blz.cookietech.Listener.BPMListener;
import com.blz.cookietech.cookietechmetronomelibrary.R;

public class ChordEraRoundWheeler extends View {

    private static final String TAG = "bishal_wheeler";
    private int BPM;
    Drawable wheeler;
    GestureDetector gestureDetector;
    private float angle = (float) 0.0;
    private int width;
    private int height;
    private float percent;
    private int wheelerSize = 0;
    private int wheelerPadding = 0;
    private int bpmTextSize;
    private Paint bpmTextColor;

    private Drawable  outerRing;
    private Drawable wave;
    private int waveSize;
    private int middlePointX;
    private int middlePointY;
    private float bpmValueTextSize;
    private Paint bpmValueTextColor;
    private Rect bpmValueTextBound,bpmTextBound,waveBound;
    private Paint outerRingProgressPaint;

    final RectF oval = new RectF();

    private BPMListener bpmListener;

    /** Increment Decrement BPM section **/
    private Paint inc_dec_paint;
    private String inc_dec_text = "";
    private ValueAnimator animator;



    public ChordEraRoundWheeler(Context context) {
        super(context);
        /*bpmListener = (BPMListener) get*/

    }



    public ChordEraRoundWheeler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context,new WheelerGestureListener());
        wheeler = ResourcesCompat.getDrawable(getResources(),R.drawable.metronome_controller,null);
        wave  = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_wave,null);
        outerRing = ResourcesCompat.getDrawable(getResources(),R.drawable.wheeler_progress,null);

        animator = ValueAnimator.ofInt(255,0);
        animator.setDuration(1000); // 1000 ms

        // Callback that executes on animation steps.
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                Log.d("ValueAnimator", "value=" + value);

                // Here you can now translate or redraw your view
                // You need to map 'value' to your animation in regards to time
                // eg) mDigitY = value; invalidate();
                inc_dec_paint.setAlpha(value);
                invalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
               inc_dec_text = "";
               invalidate();
            }
        });



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
                Log.d("akash_wheeler", "onGlobalLayout: "+wheelerPadding);
                wheelerSize = width - wheelerPadding;

                wheeler.setBounds(wheelerPadding,wheelerPadding,wheelerSize,wheelerSize);
                outerRing.setBounds(0,0,width,height);

                bpmValueTextSize = (float) width/5;
                bpmValueTextColor = new Paint();
                bpmValueTextColor.setColor(Color.WHITE);
                bpmValueTextColor.setTextSize(bpmValueTextSize);


                bpmValueTextBound = new Rect();
                bpmTextBound = new Rect();
                waveBound = new Rect();




                waveSize = (width)/16;
                wave.setBounds(0,0,waveSize,waveSize);

                bpmTextSize = width/12;
                bpmTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
                bpmTextColor.setColor(Color.WHITE);
                bpmTextColor.setTextSize(bpmTextSize);


                outerRingProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                outerRingProgressPaint.setStrokeWidth(width/20f);
                outerRingProgressPaint.setStyle(Paint.Style.STROKE);
                outerRingProgressPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.parseColor("#0072BC"), Color.parseColor("#00D49A"), Shader.TileMode.CLAMP));
                outerRingProgressPaint.setStrokeCap(Paint.Cap.ROUND);

                /** increment decrement section **/
                inc_dec_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                inc_dec_paint.setTextSize(bpmTextSize);
                //inc_dec_paint.setAlpha(0);

            }
        });

    }

    public ChordEraRoundWheeler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChordEraRoundWheeler(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (bpmValueTextColor != null && bpmTextColor != null && outerRingProgressPaint != null && inc_dec_paint != null){
            Log.d("akash_wheeler", "onDraw: ");
            canvas.save();
            Log.d(TAG, "onDraw: percent : " + percent);
            angle = getAngleFromPercent(percent);
            canvas.rotate(angle,(float)width/2,(float)height/2);
            wheeler.draw(canvas);
            canvas.restore();
            outerRing.draw(canvas);



            String bpmValue = String.valueOf(percentToBpm(percent));
            bpmValueTextColor.getTextBounds(bpmValue,0,bpmValue.length(),bpmValueTextBound);
            float bpmValueTextWidth = bpmValueTextBound.width();
            float bpmValueTextHeight = bpmValueTextBound.height();
            int bpmValuePositionX = (int) (middlePointX - bpmValueTextWidth/2);
            int bpmValuePositionY = (int) (middlePointY + bpmValueTextHeight/2);
            canvas.drawText(bpmValue,bpmValuePositionX,bpmValuePositionY,bpmValueTextColor);



            float bpmTextWidth = bpmTextColor.measureText("BPM");
            int bpmPositionX = (int) (middlePointX - bpmTextWidth /2 + waveSize/2);
            int bpmPositionY = (int) (middlePointY - bpmValueTextHeight);
            canvas.drawText("BPM",bpmPositionX,bpmPositionY,bpmTextColor);


            waveBound.left = bpmPositionX - waveSize - 10;
            waveBound.right = bpmPositionX - 10;
            waveBound.top = bpmPositionY - waveSize;
            waveBound.bottom = waveBound.top+waveSize;
            wave.setBounds(waveBound);
            wave.draw(canvas);



            float radius;

            if (width > height) {
                radius = (float) (height / 2.222) + width/40f;
            } else {
                radius = (float) (width / 2.222) + width/40f;
            }



            float angle = getAngleFromPercent(percent);
            if(angle < 0)
                angle = angle + 360;

            Log.d("akash_wheeler", "onDraw: degree" + getAngleFromPercent(percent) );

            oval.set(middlePointX - radius,
                    middlePointY - radius,
                    middlePointX + radius,
                    middlePointY + radius);
            canvas.drawArc(oval, -90, angle, false, outerRingProgressPaint);

            /** increment decrement section **/

            //inc_dec_paint.getTextBounds(inc_dec_text,0,inc_dec_text.length(),inc_dec_text_bound);
            float inc_dec_text_width = inc_dec_paint.measureText(inc_dec_text);
            int inc_dec_PositionX = (int) (middlePointX - inc_dec_text_width /2);
            int inc_dec_PositionY = (int) (middlePointY + bpmValueTextHeight + height/10);
            canvas.drawText(inc_dec_text,inc_dec_PositionX,inc_dec_PositionY,inc_dec_paint);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }




     class WheelerGestureListener extends GestureDetector.SimpleOnGestureListener{
        float prevAngle = 0;
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("akash_wheeler", "onDown: ");
            prevAngle = 0;
            return true;
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("akash_wheeler", "onScroll: " + distanceY);
            float x = e2.getX() / ((float) getWidth());
            float y = e2.getY() / ((float) getHeight());
            float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

            if (! Float.isNaN(rotDegrees)) {
                // instead of getting 0-> 180, -180 0 , we go for 0 -> 360
                float posDegrees = rotDegrees;
                if (rotDegrees < 0) posDegrees = 360 + rotDegrees;

                float change =  posDegrees - prevAngle;

                Log.d("akash_wheeler", "onScroll: " + prevAngle + " "+ posDegrees+ " "+ change + " " + (change * 100 /360))  ;
                if(posDegrees == change || change > 20 || change <-20){
                    prevAngle = posDegrees;
                    return true;
                }
                float tempPercent = percent + (change * 100 /360);
                if((tempPercent >100&&percent ==100) || (tempPercent < 0 && percent ==0)){
                    return true;
                }
                else if(tempPercent > 100 && percent < 100 ){
                    percent = 100;
                }else if(tempPercent < 0 && percent > 0){
                    percent = 0;
                }else{
                    percent =tempPercent;
                }
                Log.d("akash_wheeler", "onScroll: " + percent);
                bpmListener.onBPMChange(percentToBpm(percent));
                
                invalidate();
                BPM = percentToBpm(percent);
                prevAngle = posDegrees;
                return true; //consumed
            } else
                return false; // not consumed
        }

         @Override
         public boolean onSingleTapConfirmed(MotionEvent e) {
             float x = e.getX() / ((float) getWidth());
             float y = e.getY() / ((float) getHeight());
             float mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
             percent = getPercentFromAngle(mAngleDown);
             bpmListener.onBPMChange(percentToBpm(percent));
             BPM = percentToBpm(percent);
             invalidate();
             Log.d("akash_wheeler", "onSingleTapUp: ");
             return true;
         }


         @Override
         public boolean onDoubleTap(MotionEvent e) {

            float touchedX = e.getX();
            if (touchedX > middlePointX){
                if (BPM != 400){
                    inc_dec_text = Constants.plus_five;
                    incrementDecrementBPM(BPM + 5);
                    inc_dec_paint.setColor(Color.parseColor("#00D49A"));
                }
            }
            else {
                if (BPM != 20){
                    inc_dec_text = Constants.minus_five;
                    incrementDecrementBPM(BPM - 5);
                    inc_dec_paint.setColor(Color.parseColor("#ff5252"));
                }
            }
             //animator.start();


             return true;
         }
     }

    private int getPercentFromAngle(float mAngleDown) {
        if(mAngleDown > 0){
            return (int) ((mAngleDown * 100) /360);
        }else{
            return (int) (((mAngleDown + 360)*100)/360);
        }
    }


    private float getAngleFromPercent(float percent) {
        return (360 * percent/100);
    }

    private float cartesianToPolar(float x, float y) {
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }

     public void setBPMListener(BPMListener listener){
         bpmListener = listener;

    }

    private void incrementDecrementBPM(int bpm){

        if (bpm<20){
            bpm = 20;
        }
        else if (bpm>400){
            bpm = 400;
        }

        percent = BPMToPercent(bpm);
        BPM = bpm;
        animator.start();




        bpmListener.onBPMChange(BPM);


    }


    /*private void decrementBPM(int bpm){

    }*/




    private int percentToBpm(double percent){
        return (int) Math.round((380*percent/100) +20);
    }

    public void setBPM(int BPM){

        if (BPM<20){
            BPM = 20;
        }
        else if (BPM>400){
            BPM = 400;
        }

        percent = BPMToPercent(BPM);
        invalidate();
        this.BPM = BPM;

    }
    private float BPMToPercent(int BPM){
        float tmp = ((float)(BPM -20)/380)*100;
        Log.d(TAG, "BPMToPercent: tmp : " + tmp);
        return tmp;
    }








}
