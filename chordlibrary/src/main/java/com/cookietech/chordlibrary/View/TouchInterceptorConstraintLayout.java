package com.cookietech.chordlibrary.View;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TouchInterceptorConstraintLayout extends ConstraintLayout {
    List<View> touchBoundViews = new ArrayList<>();
    HashMap<View, TouchBoundListener> touchBoundListenerHashMap = new HashMap<>();
    public TouchInterceptorConstraintLayout(@NonNull Context context) {
        super(context);
    }

    public TouchInterceptorConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchInterceptorConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchInterceptorConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for(View view:touchBoundViews){
            TouchBoundListener touchBoundListener = touchBoundListenerHashMap.get(view);
            Rect viewRect = new Rect();
            view.getGlobalVisibleRect(viewRect);
            if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                if(touchBoundListener!= null)
                touchBoundListener.onTouchOutSide();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void bindTouch(View view,TouchBoundListener touchBoundListener){
        touchBoundViews.add(view);
        touchBoundListenerHashMap.put(view,touchBoundListener);
    }



    public interface TouchBoundListener{
        void onTouchOutSide();
    }
}
