package com.blz.cookietech.cookietechmetronomelibrary;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.NumberPicker;
import com.blz.cookietech.Adapter.SubdivisionAdapter;
import com.blz.cookietech.Dialogs.TimerDialog;
import com.blz.cookietech.Helpers.Constants;
import com.blz.cookietech.Listener.BPMListener;
import com.blz.cookietech.Listener.StopTimerListener;
import com.blz.cookietech.Services.MetronomeService;
import com.blz.cookietech.cookietechmetronomelibrary.View.AdaptiveBannerFragment;
import com.blz.cookietech.cookietechmetronomelibrary.View.ChordEraRoundWheeler;
import com.blz.cookietech.cookietechmetronomelibrary.View.LightsView;
import com.blz.cookietech.cookietechmetronomelibrary.View.TimerWheeler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 * sample -> 6000
 * sample_01 -> 4500
 * sample_02 -> 3000
 * sample_03 -> 1500
 * sample_04 -> 1050
 */
public class MetronomeFragment extends Fragment implements BPMListener, StopTimerListener, NumberPicker.OnValueChangeListener, NumberPicker.OnScrollListener, TimerDialog.TimerDialogListener {




    private static final String TAG = "MetronomeFragment";
    private static final String ARG_BANNER_AD = "banner_ad";
    private double [] tick;
    private double [] tock;

    public static final String ARG_TICK = "tick_sound";
    public static final String ARG_TOCK = "tock_sound";
    public static final String ARG_PENDING_INTENT = "pending_intent";


    private ChordEraRoundWheeler bpmWheel;

    private ServiceConnection serviceConnection;
    private ImageView play_pause_btn;


    private AudioGenerator audio;


    private TimerWheeler timerWheel;
    private NumberPicker leftTimeSignaturePicker, rightTimeSignaturePicker;

    /** This Value may be handled with sharedPreference Or ViewModel on savedInstanceState**/
    private int leftTimeSignature = 4;
    private int rightTimeSignature = 1;
    private boolean isPlaying = false;
    private int subdivisionPosition = 1;
    private int BPM = 80;
    private boolean isTimerEnabled = false;
    private int minutes =10;


   /* *//** Lights View **//*
    LightsView lightsView;*/


    /** Subdivision RecyclerView **/
    private RecyclerView subdivisionRecyclerView;

    /** Subdivision Container **/
    private ConstraintLayout subdivisionContainer;
    private int subdivisionContainerWidth;
    private int subdivisionContainerHeight;

    /** Wheeler Container **/
    private ConstraintLayout wheelerContainer;
    private int wheelerContainerHeight;
    private int wheelerContainerWidth;


    boolean mBound = false;
    private MetronomeService mService;
    Intent metronomeServiceIntent;
    PendingIntent pendingIntent;
    private SubdivisionAdapter adapter;
    private MetronomeFragmentBroadcastReceiver broadcastReceiver = new MetronomeFragmentBroadcastReceiver();
    private ImageView backButton;
    private boolean isBannerAdActivated = true;


    public static MetronomeFragment newInstance(boolean isBannerAdActivated,PendingIntent pendingIntent,double[] tick,double[] tock) {

        Bundle args = new Bundle();

        MetronomeFragment fragment = new MetronomeFragment();
        args.putDoubleArray(ARG_TICK,tick);
        args.putDoubleArray(ARG_TOCK,tock);
        args.putBoolean(ARG_BANNER_AD,isBannerAdActivated);
        args.putParcelable(ARG_PENDING_INTENT,pendingIntent);
        fragment.setArguments(args);
        return fragment;
    }


    public MetronomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("akash_test_debug", "onCreate: ");
        Log.d("akash_debug", "onCreate: "+ com.blz.cookietech.cookietechmetronomelibrary.Model.Constants.getTick()[500]);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MetronomeFragmentBroadcastReceiver.ACTION_QUIT);
        filter.addAction(MetronomeFragmentBroadcastReceiver.ACTION_TOGGLE);
        filter.addAction(MetronomeFragmentBroadcastReceiver.ACTION_LIGHT);
        requireContext().registerReceiver(broadcastReceiver, filter);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metronome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("akash_test_debug", "onViewCreated: ");
        if(getArguments() != null){
            Bundle args = getArguments();
            tick = args.getDoubleArray(ARG_TICK);
            tock = args.getDoubleArray(ARG_TOCK);
            pendingIntent = args.getParcelable(ARG_PENDING_INTENT);
            isBannerAdActivated = args.getBoolean(ARG_BANNER_AD);
        }


        /** Initialize play pause button **/
        play_pause_btn = view.findViewById(R.id.play_pause_btn);

        /** Initialize Time Signature Picker **/
        leftTimeSignaturePicker  = view.findViewById(R.id.leftTimeSignaturePicker);
        rightTimeSignaturePicker = view.findViewById(R.id.rightTimeSignaturePicker);

        /** Initialize BPM Wheel Section**/
        bpmWheel = view.findViewById(R.id.bpmWheel);
        bpmWheel.setBPM(BPM);
        bpmWheel.setBPMListener(this);

      /*  *//** Initialize Lights View **//*
        lightsView = view.findViewById(R.id.lightsView);
*/

        /**Initialize  Subdivision RecyclerView **/
        subdivisionRecyclerView = view.findViewById(R.id.subdivisionRecyclerView);

        /**Initialize  Subdivision Container **/
        subdivisionContainer = view.findViewById(R.id.subdivisionContainer);

        /**Initialize Timer Wheel **/
        timerWheel = view.findViewById(R.id.timerWheel);

        /** Initialize wheelerContainer **/
        wheelerContainer = view.findViewById(R.id.wheelerContainer);

        /** initialize back button **/
        backButton = view.findViewById(R.id.back_btn);



//        lightsView.setBpm(BPM);


        initializeClickEvents();



        /** Timer Wheel Section **/
        SetUpTimerWheel();

        timerWheel.setOnStopTimerListener(this);
        timerWheel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (isPlaying){
                    stopMetronome();
                }

                TimerDialog timerDialog = new TimerDialog(isTimerEnabled, minutes);
                timerDialog.show(getChildFragmentManager(),"Timer Dialog");
                return true;
            }
        });

        timerWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lightsView.toggleLight();
                shakeItBaby();
            }
        });



        /** Time Signature Picker Section**/
        rightTimeSignaturePicker.setMinValue(1);
        rightTimeSignaturePicker.setMaxValue(4);
        leftTimeSignaturePicker.setMinValue(1);
        leftTimeSignaturePicker.setMaxValue(16);
        leftTimeSignaturePicker.setOnValueChangedListener(this);
        rightTimeSignaturePicker.setOnValueChangedListener(this);
        leftTimeSignaturePicker.setOnScrollListener(this);
        rightTimeSignaturePicker.setOnScrollListener(this);
        leftTimeSignaturePicker.setValue(leftTimeSignature);
        rightTimeSignaturePicker.setValue(rightTimeSignature);
        rightTimeSignaturePicker.setDisplayedValues(new String[] { "1", "2", "4" , "8" });
        rightTimeSignaturePicker.setWrapSelectorWheel(false);
        leftTimeSignaturePicker.setWrapSelectorWheel(false);



//        /** Lights View Section**/
//        lightsView.setLightNumber(leftTimeSignature);

        /** Set BPM Wheel listener**/
        bpmWheel.setBPMListener(this);

        Intent service = new Intent(requireContext(),MetronomeService.class);
        service.putExtra(MetronomeService.PENDING_INTENT,pendingIntent);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(MetronomeService.TICK_VALUE,  tick);
        mBundle.putSerializable(MetronomeService.TOCK_VALUE,  tock);
        mBundle.putInt(MetronomeService.INITIAL_BPM,BPM);
        mBundle.putInt(MetronomeService.INITIAL_SUBDIVISION,subdivisionPosition);
        mBundle.putInt(MetronomeService.INITIAL_TIME_SIGNATURE,leftTimeSignature);
        service.putExtras(mBundle);
        requireActivity().startService(service);








        /** Dynamically change the bpm wheeler and timer wheeler size **/
        wheelerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Log.d(TAG, "onGlobalLayout: called");
                wheelerContainerHeight = wheelerContainer.getHeight();
                wheelerContainerWidth = wheelerContainer.getWidth();
                int topBottomPadding = wheelerContainerHeight/10;
                int leftRightPadding = wheelerContainerWidth/10;
                int marginFromOtherWheeler = wheelerContainerWidth/20;

                Log.d(TAG, "onGlobalLayout: wheelerContainerHeight : " + wheelerContainerHeight);
                Log.d(TAG, "onGlobalLayout: wheelerContainerWidth : " + wheelerContainerWidth);
                Log.d(TAG, "onGlobalLayout: topBottomPadding : " + topBottomPadding);
                Log.d(TAG, "onGlobalLayout: marginFromOtherWheeler : " + marginFromOtherWheeler);



                //int wheelerContainerMiddle = wheelerContainerWidth/2;
                int sixtyPercentOfWidth = (wheelerContainerWidth * 3)/5;
                int fortyPercentOfWidth = wheelerContainerWidth - sixtyPercentOfWidth;

                Log.d(TAG, "onGlobalLayout: sixtyPercentOfWidth : " + sixtyPercentOfWidth);
                Log.d(TAG, "onGlobalLayout: fortyPercentOfWidth : " + fortyPercentOfWidth);


                if (wheelerContainerHeight > 0 && wheelerContainerWidth > 0){

                    wheelerContainer.setPadding(leftRightPadding/2,topBottomPadding/2,leftRightPadding/2,topBottomPadding/2);

                    int bpmWheelWidth = sixtyPercentOfWidth -marginFromOtherWheeler;
                    int bpmWheelHeight = wheelerContainerHeight - topBottomPadding;
                    int bpmWheelSize = Math.min(bpmWheelWidth, bpmWheelHeight);
                    Log.d(TAG, "onGlobalLayout: bpmWheelWidth : " + bpmWheelWidth);
                    Log.d(TAG, "onGlobalLayout: bpmWheelHeight : " + bpmWheelHeight);
                    Log.d(TAG, "onGlobalLayout: bpmWheelSize : " + bpmWheelSize);

                    ViewGroup.LayoutParams params = bpmWheel.getLayoutParams();
                    params.height = bpmWheelSize;
                    params.width = bpmWheelSize;
                    bpmWheel.setLayoutParams(params);

                    int timerWheelWidth = fortyPercentOfWidth - marginFromOtherWheeler;
                    int timerWheelSize = Math.min(timerWheelWidth,bpmWheelHeight);
                    Log.d(TAG, "onGlobalLayout: timerWheelWidth : " + timerWheelWidth);
                    Log.d(TAG, "onGlobalLayout: timerWheelSize : " + timerWheelSize);


                    params = timerWheel.getLayoutParams();
                    params.height = timerWheelSize;
                    params.width = timerWheelSize;
                    timerWheel.setLayoutParams(params);
                    Log.d(TAG, "onGlobalLayout: called");



                    int mainMiddle = leftRightPadding/2 + bpmWheelSize + marginFromOtherWheeler;
                    int buttonMiddle = play_pause_btn.getLeft() + (play_pause_btn.getWidth()/2);

                    play_pause_btn.setTranslationX((float) (mainMiddle-buttonMiddle+ (play_pause_btn.getWidth()*0.04)));
                    play_pause_btn.setTranslationY((float) (play_pause_btn.getWidth()*0.05));



                    wheelerContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }





            }
        });



        /** Setup Adapter and recyclerView Function **/
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        subdivisionRecyclerView.setLayoutManager(layoutManager);
        subdivisionRecyclerView.setHasFixedSize(true);
        adapter = new SubdivisionAdapter(requireContext(),subdivisionRecyclerView,rightTimeSignature);
        subdivisionRecyclerView.setAdapter(adapter);
        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(subdivisionRecyclerView);


        subdivisionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);



                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //Log.d(TAG, "onScrollStateChanged: newState : " + newState);
                    View centerView = snapHelper.findSnapView(layoutManager);
                    int pos = layoutManager.getPosition(centerView);
                    //Log.d(TAG, "onScrollStateChanged: pos : " + pos);
                    subdivisionPosition = pos;
                    Intent subDivisionChangeIntent = new Intent(MetronomeService.PlayPauseBroadcastReceiver.SUB_DIVISION_CHANGE);
                    subDivisionChangeIntent.putExtra(MetronomeService.PlayPauseBroadcastReceiver.SUB_DIVISION_VALUE,subdivisionPosition);
                    requireActivity().sendBroadcast(subDivisionChangeIntent);
                }
            }
        });

       



        play_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isPlaying){

                   playMetronome();

                }
                else{

                    stopMetronome();
                }

            }
        });

        if(isBannerAdActivated){
            Fragment fragment = new AdaptiveBannerFragment();
            getChildFragmentManager().beginTransaction().add(R.id.ad_holder,fragment).commit();
        }
    }

    private void initializeClickEvents() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonPressed();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("akash_test_debug", "onStart: ");

    }

    private void resetPlayPauseBtn() {
        isPlaying = false;
        play_pause_btn.setImageResource(R.drawable.play);
    }

    @Override
    public void onBPMChange(int bpm) {
        BPM = bpm;
        Intent bpmChangeIntent = new Intent(MetronomeService.PlayPauseBroadcastReceiver.BPM_CHANGE);
        bpmChangeIntent.putExtra(MetronomeService.PlayPauseBroadcastReceiver.BPM_VALUE,bpm);
        requireActivity().sendBroadcast(bpmChangeIntent);
        Log.d("akash_debug", String.valueOf(bpm));
//        lightsView.setBpm(BPM);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("akash_test_debug", "onDestroyView: ");
        Intent service = new Intent(requireContext(),MetronomeService.class);
        requireActivity().stopService(service);
        isPlaying = false;
        requireContext().unregisterReceiver(broadcastReceiver);
    }



    @Override
    public void onStopTimer() {
        //resetPlayPauseBtn();
        if(isTimerEnabled) stopMetronome();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("akash_test_debug", "onStop: ");
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        if (picker.getId() == R.id.leftTimeSignaturePicker){
            leftTimeSignature = newVal;

            Log.d(TAG, "onValueChange: called");
            Log.d(TAG, "onValueChange: leftTimeSignature : " + leftTimeSignature);
            Intent bpmChangeIntent = new Intent(MetronomeService.PlayPauseBroadcastReceiver.TIME_SIGNATURE_CHANGE);
            bpmChangeIntent.putExtra(MetronomeService.PlayPauseBroadcastReceiver.TIME_SIGNATURE_VALUE,leftTimeSignature);
            requireActivity().sendBroadcast(bpmChangeIntent);
//            lightsView.setLightNumber(leftTimeSignature);

        }
        else if (picker.getId() == R.id.rightTimeSignaturePicker){
            rightTimeSignature = newVal;
            adapter.setRightTimeSignature(rightTimeSignature);
            Log.d(TAG, "onValueChange: right : " + rightTimeSignature);
        }

    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {

        /*if (scrollState == SCROLL_STATE_IDLE){
            Log.d(TAG, "onScrollStateChange: ");
            if (view.getId() == R.id.leftTimeSignaturePicker){
                //leftTimeSignature = newVal;
                lightsView.setLightNumber(leftTimeSignature);

            }
            else if (view.getId() == R.id.rightTimeSignaturePicker){
                //rightTimeSignature = newVal;
            }

        }*/

    }


    @Override
    public void getTimePickerStatus(int minute, boolean isEnabled) {
        minutes = minute;
        isTimerEnabled = isEnabled;
        SetUpTimerWheel();
        Log.d(TAG, "getTimePickerStatus: minutes : " + minutes + " isTimerEnabled : " + isTimerEnabled);
    }

    private void SetUpTimerWheel(){
        if (isTimerEnabled){
            timerWheel.setUpTimer(minutes);
        }
        else {
            timerWheel.setUpTimer(Constants.ZERO);
        }
    }



    public class MetronomeFragmentBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_TOGGLE = "com.blz.cookietech.TOGGLE";
        public static final String ACTION_QUIT = "com.blz.cookietech.QUIT";
        public static final String ACTION_LIGHT = "com.blz.cookietech.LIGHT";


        @Override
        public void onReceive(Context context, Intent intent) {


            Log.d("akash_debug", "onReceive: " + intent.getAction());

            Log.d("akash_debug", "onReceive: " + context.getPackageName());

            Log.d("akash_debug", "onReceive: " + intent.getIntExtra(EXTRA_NOTIFICATION_ID,-1));


            if(intent.getAction() != null){
                switch (intent.getAction()) {
                    case ACTION_TOGGLE:

                        if(isPlaying){
                            stopMetronome();
                            Log.d("akash_test_debug", "onReceive: toggle pause");
                        }else{
                            playMetronome();
                            Log.d("akash_test_debug", "onReceive: toggle play");
                        }
                        break;
                    case ACTION_QUIT:
                        Log.d("akash_test_debug", "onReceive: quit");
                        stopMetronome();
                        backButtonPressed();
                        Intent service = new Intent(requireContext(),MetronomeService.class);
                        requireActivity().stopService(service);
                        break;
                    case ACTION_LIGHT:
                        Log.d("notification_debug", "fragment onReceive: ");
                        //lightsView.reset();
                        break;
                }
            }




        }
    }

    private void backButtonPressed() {
        requireActivity().onBackPressed();
    }




    private void playMetronome() {
        Intent playPauseIntent = new Intent(MetronomeService.PlayPauseBroadcastReceiver.ACTION_PLAY_PAUSE);
        playPauseIntent.putExtra(MetronomeService.PlayPauseBroadcastReceiver.PLAY_PAUSE_EXTRA,true);
        requireActivity().sendBroadcast(playPauseIntent);
        isPlaying = true;
        play_pause_btn.setImageResource(R.drawable.pause);
        timerWheel.startTimer();

//        lightsView.startToggling();
    }

    private void stopMetronome() {
        Intent playPauseIntent = new Intent(MetronomeService.PlayPauseBroadcastReceiver.ACTION_PLAY_PAUSE);
        playPauseIntent.putExtra(MetronomeService.PlayPauseBroadcastReceiver.PLAY_PAUSE_EXTRA,false);
        try {
            requireContext().sendBroadcast(playPauseIntent);
        }catch (IllegalStateException ignored){

        }

        resetPlayPauseBtn();
        timerWheel.stopTimer();
//        lightsView.stopToggling();
    }

    private void shakeItBaby(){
        Vibrator v = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(100);
        }
    }


}