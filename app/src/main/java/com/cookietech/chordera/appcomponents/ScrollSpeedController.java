package com.cookietech.chordera.appcomponents;

import android.util.Log;

public class ScrollSpeedController {

    public static long getDelayForScroll(double speed , int songDuration , int scrollViewHeight){
        Log.d("auto_speed_debug", "getDelayForScroll: speed: " + speed);
        Log.d("auto_speed_debug", "getDelayForScroll: songDuration: " + songDuration);
        Log.d("auto_speed_debug", "getDelayForScroll: songDuration: " + scrollViewHeight);
        double delayPerPixelInSec = (double) songDuration /scrollViewHeight;
        Log.d("auto_speed_debug", "getDelayForScroll: delayPerPixelInSec: " + delayPerPixelInSec);
        Log.d("auto_speed_debug", "getDelayForScroll: actual_delay: " + (long) (delayPerPixelInSec * speed * 1000));
        return (long) ((delayPerPixelInSec / speed) * 1000);
    }
}
