package com.cookietech.chordera.chordDisplay;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ScrollSpeedController;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.databinding.FragmentChordDisplayFullscreenBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordlibrary.ChordClass;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChordDisplayFullscreenFragment extends ChorderaFragment implements ChordsDisplayAdapter.Communicator {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private FragmentChordDisplayFullscreenBinding binding;
    private  ChordsDisplayAdapter chordsDisplayAdapter;
    private int song_duration;
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    private int scrollViewHeight = 0;
    private long scrollDelayPerPixel = 0;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

            ObjectAnimator hideAnimation = ObjectAnimator.ofFloat(binding.fullscreenContentControls, "translationY", binding.fullscreenContentControls.getHeight(),0f);
            hideAnimation.setDuration(100);
            hideAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    binding.fullscreenContentControls.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            hideAnimation.start();

        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private boolean theViewIsDestroyed = false;
    private double scrollSpeed = 1;

    public ChordDisplayFullscreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arg = getArguments();
            scrollSpeed = arg.getDouble("auto_scroll_speed");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        theViewIsDestroyed = true;
    }

    public static ChordDisplayFullscreenFragment newInstance(Bundle args) {
        ChordDisplayFullscreenFragment fragment = new ChordDisplayFullscreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChordDisplayFullscreenBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVisible = true;
        theViewIsDestroyed = false;


        // Set up the user interaction to manually show or hide the system UI.

        GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("bishal", "onClick: toggle hocche");
                toggle();
                return super.onSingleTapUp(e);

            }
        });
        binding.fullscreenScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        binding.fullscreenContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bishal", "onClick: toggle cancel" + mainViewModel.getObservableTransposedTabDisplayChords().getValue());
                toggle();
            }
        });
        initializeChordsRecyclerView();
        setUpViews();
        initializeClicks();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //view.findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    private void initializeClicks() {
        binding.fullscreenCancelBtn.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        theViewIsDestroyed = false;
        binding.fullscreenScrollView.post(new ScrollRunnable());

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            // Clear the systemUiVisibility flag
            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        }
        show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void toggle() {
        if (mVisible) {

            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ObjectAnimator hideAnimation = ObjectAnimator.ofFloat(binding.fullscreenContentControls, "translationY", 0f,binding.fullscreenContentControls.getHeight());
        hideAnimation.setDuration(100);
        hideAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.fullscreenContentControls.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        hideAnimation.start();

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        binding.fullscreenContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        delayedHide(4000);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Nullable
    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }

    private void initializeChordsRecyclerView() {
        binding.fullscreenRvChords.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),5);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.fullscreenRvChords.setLayoutManager(layoutManager);
        chordsDisplayAdapter = new ChordsDisplayAdapter(requireContext(),new ArrayList<ChordClass>(),this,binding.fullscreenRvChords);
        binding.fullscreenRvChords.setAdapter(chordsDisplayAdapter);
        binding.fullscreenRvChords.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                chordsDisplayAdapter.setChords(mainViewModel.getObservableTransposedTabDisplayChords().getValue());
                binding.fullscreenRvChords.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    @Override
    public void onChordSelected(ChordClass chord) {

    }

    private void setUpViews(){
        //Log.d("bishal_debug", "setUpViews: called");
        binding.fullscreenTvSongChords.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mainViewModel.getObservableSelectedTabLiveData().getValue() != null){
                    binding.fullscreenTvSongChords.setFormattedText(mainViewModel.getObservableSelectedTabLiveData().getValue().getData());
                   // binding.fullscreenTvSongChords.setTextColor(getResources().getColor(R.color.white));
                }

                if (mainViewModel.getObservableIsDarkModeActivated().getValue()){
                    activateDarkMode();
                }

                if(mainViewModel.getObservableTransposeValue().getValue() != null){
                    binding.fullscreenTvSongChords.setTranspose(mainViewModel.getObservableSelectedTabLiveData().getValue().getData(),mainViewModel.getObservableTransposeValue().getValue());
                }

                else if (!mainViewModel.getObservableIsDarkModeActivated().getValue()){
                    activateLightMode();
                }



                if (mainViewModel.getObservableSelectedSong().getValue() != null){
                    song_duration = mainViewModel.getObservableSelectedSong().getValue().getSong_duration();
                   // Log.d("bishal_debug", "onGlobalLayout: duration" + song_duration);
                }

                //binding.fullscreenScrollView.fling(0);
                Log.d("bishal_debug", "onGlobalLayout: " + binding.fullscreenScrollView.getBottom());
                /*binding.fullscreenScrollView.fullScroll(View.FOCUS_DOWN);
                binding.fullscreenScrollView.smoothScrollTo(0,binding.fullscreenScrollView.getBottom());*/

                binding.fullscreenTvSongChords.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }

        });




        binding.fullscreenScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              /*  binding.fullscreenScrollView.postDelayed(new Runnable() {
                    public void run() {
                        Log.d("bishal_debug", "run: called");
                        int bottom = binding.fullscreenScrollView.getHeight();
                        if ( binding.fullscreenScrollView.getChildCount() > 0) {
                            View view = binding.fullscreenScrollView.getChildAt(binding.fullscreenScrollView.getChildCount() - 1);
                            NestedScrollView.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
                            bottom = binding.fullscreenScrollView.getBottom() + lp.bottomMargin + binding.fullscreenScrollView.getPaddingBottom();
                        }
                        binding.fullscreenScrollView.smoothScrollTo(0,bottom);
                        binding.fullscreenScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                },2000);*/


                //binding.fullscreenScrollView.post(new ScrollRunnable());

                //binding.fullscreenScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                Log.d("auto_speed_debug", "onGlobalLayout: child: " + binding.fullscreenScrollView.getChildAt(0).getHeight());
                Log.d("auto_speed_debug", "onGlobalLayout: scroll: " + binding.fullscreenScrollView.getHeight());
                Log.d("auto_speed_debug", "onGlobalLayout: text: " + binding.fullscreenTvSongChords.getHeight());


                if(scrollViewHeight >= binding.fullscreenScrollView.getChildAt(0).getHeight()){
                    //Log.d("auto_speed_debug", "onGlobalLayout: delay : " + ScrollSpeedController.getDelayForScroll(1,song_duration,scrollViewHeight));
                    scrollDelayPerPixel = (long) (50 + (80*(1-scrollSpeed)));

                    binding.fullscreenScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    scrollViewHeight =  binding.fullscreenScrollView.getChildAt(0).getHeight();
                }

            }
        });

        /*binding.fullscreenScrollView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {

                binding.fullscreenScrollView.post(new Runnable() {
                    public void run() {
                        Log.d("bishal_debug", "run: called");
                         binding.fullscreenScrollView.smoothScrollTo(0,binding.fullscreenScrollView.getBottom(), 30000);
                    }
                });
                binding.fullscreenScrollView.getViewTreeObserver().removeOnDrawListener(this);
            }
        });*/


    }


    private class ScrollRunnable implements Runnable{

        @Override
        public void run() {
            try{
                binding.fullscreenScrollView.smoothScrollBy(0,1);
                if(!theViewIsDestroyed)
                    binding.fullscreenScrollView.postDelayed(this,scrollDelayPerPixel);
            }catch (Exception e){
                Log.d("akash_scroll_debug", "run: " + e);
            }

        }
    }

    private void activateLightMode(){
        binding.fullscreenRoot.setBackgroundColor(getResources().getColor(R.color.white));
        binding.fullscreenTvSongChords.setMode(TabulatorTextView.Mode.Light);
    }

    private void activateDarkMode (){
        binding.fullscreenRoot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        binding.fullscreenTvSongChords.setMode(TabulatorTextView.Mode.Dark);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        theViewIsDestroyed = true;
    }
}