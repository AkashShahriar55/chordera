package com.blz.cookietech.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blz.cookietech.cookietechmetronomelibrary.Metronome;
import com.blz.cookietech.cookietechmetronomelibrary.MetronomeFragment;
import com.blz.cookietech.cookietechmetronomelibrary.R;

import java.util.List;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MetronomeService extends Service {
    public static final String PENDING_INTENT = "pending_intent";
    public static final String TICK_VALUE = "tick_value";
    public static final String TOCK_VALUE = "tock_value";

    public static final String CHANNEL_ID = "metronomeServiceChannel";
    public static final String INITIAL_BPM = "initial_bpm";
    public static final String INITIAL_SUBDIVISION = "initial_subdivision";
    public static final String INITIAL_TIME_SIGNATURE = "initial_time_signature";

    private static final String TAG = "MetronomeService";

    private Metronome metronomeThread;
    private PlayPauseBroadcastReceiver playPauseBroadcastReceiver = new PlayPauseBroadcastReceiver();

    // Binder given to clients
    private final int notificationId = 1;
    private boolean isPlaying = false;

    private double[] tick;
    private double[] tock;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */




    /** onCreate will be called at the first time we create our service**/

    @Override
    public void onCreate() {
        Log.d("akash_debug", "onCreate: ");
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(playPauseBroadcastReceiver);
        Log.d("akash_debug", "onDestroy: ");
        //metronomeThread.stopMetronome();
        metronomeThread.destroyAndReleaseResource();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void pause(){
        metronomeThread.stopMetronome();
    }

    public void play(){
        metronomeThread.playMetronome();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PendingIntent pendingIntent = intent.getParcelableExtra(PENDING_INTENT);
        tick = (double[]) intent.getExtras().getSerializable(TICK_VALUE);
        tock = (double[]) intent.getExtras().getSerializable(TOCK_VALUE);
        int bpm = intent.getIntExtra(INITIAL_BPM,80);
        int subdivision = intent.getIntExtra(INITIAL_SUBDIVISION,1);
        int timeSignature = intent.getIntExtra(INITIAL_TIME_SIGNATURE,4);

        metronomeThread = new Metronome(tick,tock,bpm,subdivision,timeSignature);


        Log.d("akash_debug", "onStartCommand: " + tick[500] + " " + tock[500]);

        Log.d("akash_debug", "onStartCommand: ");
        Intent quitIntent = new Intent(PlayPauseBroadcastReceiver.ACTION_QUIT);
        quitIntent.putExtra(EXTRA_NOTIFICATION_ID, 5);
        PendingIntent quitPendingIntent =
                PendingIntent.getBroadcast(this, 0, quitIntent, 0);


        Intent toggleIntent = new Intent(PlayPauseBroadcastReceiver.ACTION_TOGGLE);
        toggleIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent playPausePendingIntent =
                PendingIntent.getBroadcast(this, 0, toggleIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("The Metronome by Chordera")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setVibrate(new long[]{0})
                .addAction(R.drawable.ic_play,"Quit",quitPendingIntent)
                .addAction(R.drawable.ic_play,"Play & Pause",playPausePendingIntent)
                .setAllowSystemGeneratedContextualActions(false);
        builder.setDefaults(0);


// notificationId is a unique int for each notification that you must define
        startForeground(notificationId,builder.build());

        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayPauseBroadcastReceiver.ACTION_PLAY_PAUSE);
        filter.addAction(PlayPauseBroadcastReceiver.ACTION_QUIT);
        filter.addAction(PlayPauseBroadcastReceiver.ACTION_TOGGLE);
        filter.addAction(PlayPauseBroadcastReceiver.TICK_TOCK_VALUE);
        filter.addAction(PlayPauseBroadcastReceiver.BPM_CHANGE);
        filter.addAction(PlayPauseBroadcastReceiver.TIME_SIGNATURE_CHANGE);
        filter.addAction(PlayPauseBroadcastReceiver.SUB_DIVISION_CHANGE);
        registerReceiver(playPauseBroadcastReceiver, filter);


        metronomeThread.start();
        return START_NOT_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("akash_debug", "onUnbind: ");

        return super.onUnbind(intent);
    }


    public class PlayPauseBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_PLAY_PAUSE = "com.blz.cookietech.PLAY_PAUSE";
        public static final String ACTION_TOGGLE = "com.blz.cookietech.TOGGLE";
        public static final String ACTION_QUIT = "com.blz.cookietech.QUIT";
        public static final String PLAY_PAUSE_EXTRA = "play_pause_extra";
        public static final String TICK_TOCK_VALUE = "tick_tock_value";
        public static final String BPM_VALUE = "bpm_value";
        public static final String BPM_CHANGE = "com.blz.cookietech.BPM_CHANGE";
        public static final String TIME_SIGNATURE_CHANGE = "com.blz.cookietech.TIME_SIGNATURE_CHANGE";
        public static final String TIME_SIGNATURE_VALUE = "time_signature_value";
        public static final String SUB_DIVISION_CHANGE = "com.blz.cookietech.SUB_DIVISION_CHANGE";
        public static final String SUB_DIVISION_VALUE = "sub_division_value";


        @Override
        public void onReceive(Context context, Intent intent) {


            Log.d("akash_debug", "onReceive: " + intent.getAction());

            Log.d("akash_debug", "onReceive: " + context.getPackageName());

            Log.d("akash_debug", "onReceive: " + intent.getIntExtra(EXTRA_NOTIFICATION_ID,-1));


            if(intent.getAction() != null){
                switch (intent.getAction()) {
                    case ACTION_TOGGLE:

                        if(isPlaying){
                            isPlaying = false;
                            pause();
                            Log.d("akash_debug", "onReceive: toggle pause");
                        }else{
                            isPlaying = true;
                            play();
                            Log.d("akash_debug", "onReceive: toggle play");
                        }
                        break;
                    case ACTION_QUIT:
                        if (applicationInForeground()) {
                            Log.d("akash_debug", "onReceive: is foreground ");
                        } else {
                            Log.d("akash_debug", "onReceive: is not foreground ");
                            stopSelf();
                        }
                        break;
                    case ACTION_PLAY_PAUSE:
                        boolean play = intent.getBooleanExtra(PLAY_PAUSE_EXTRA,false);

                        if(play){
                            if(!isPlaying){
                                Log.d("akash_debug", "onReceive:  play ");
                                isPlaying = true;
                                play();
                            }
                        }else{
                            if(isPlaying){
                                Log.d("akash_debug", "onReceive:  pause ");
                                isPlaying = false;
                                pause();
                            }
                        }
                        break;
                    case BPM_CHANGE:
                        int bpm = intent.getIntExtra(BPM_VALUE,80);
                        metronomeThread.setBpm(bpm);
                        break;
                    case TIME_SIGNATURE_CHANGE:
                        int timeSignature = intent.getIntExtra(TIME_SIGNATURE_VALUE,4);
                        metronomeThread.setTimeSignature(timeSignature);
                        break;
                    case SUB_DIVISION_CHANGE:
                        int subDivision = intent.getIntExtra(SUB_DIVISION_VALUE,1);
                        metronomeThread.setSubDivision(subDivision);
                        break;
                }
            }




        }
    }


    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName
                .equalsIgnoreCase(getPackageName()) && services.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            isActivityFound = true;
        }

        return isActivityFound;
    }




}
