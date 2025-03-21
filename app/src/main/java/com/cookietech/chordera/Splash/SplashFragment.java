package com.cookietech.chordera.Splash;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.appcomponents.RemoteConfigManager;
import com.cookietech.chordera.application.ChorderaApplication;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.databinding.FragmentSplashBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends ChorderaFragment {

    // starting of the project //

    FragmentSplashBinding binding;
    MainViewModel mainViewModel;
    ConnectionManager connectionManager;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
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
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSentFriendRequestEvent () {
        AppEventsLogger.newLogger(requireContext()).logEvent("sentFriendRequest");
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    public SplashFragment() {
    }

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logSentFriendRequestEvent();
        Uri video = Uri.parse("android.resource://" + getContext().getPackageName() + "/"
                + R.raw.chordera_splash);

        binding.splashVideoView.setBackgroundColor(Color.WHITE);
        binding.splashVideoView.setZOrderOnTop(true);
        binding.splashVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                            binding.splashVideoView.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                        return false;
                    }
                });

            }
            });


        binding.splashVideoView.setVideoURI(video);

    }


    @Override
    public void onResume() {
        super.onResume();

        if(!binding.splashVideoView.isPlaying()){
            binding.splashVideoView.start();
            binding.splashVideoView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (ConnectionManager.isOnline(ChorderaApplication.getContext())) {
            try {
                RemoteConfigManager.fetchRemoteConfigValues(new RemoteConfigManager.RemoteConfigFetchListener() {
                    @Override
                    public void onCompletion(boolean isFetchSuccessful) {
                        SplashFragment.this.launchMainActivityWithDelay(2000);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        } else {
            launchMainActivityWithDelay(1000);
        }
    }

    private void launchMainActivityWithDelay(int delayInSeconds) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                     if (getActivity() != null && getActivity().getWindow() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                // Clear the systemUiVisibility flag
                getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
            }
            show();
                mainViewModel.setNavigation(NavigatorTags.LANDING_FRAGMENT,NavigatorTags.CONTAINER_ID_DEFAULT);
            }
        }, delayInSeconds);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(binding.splashVideoView.isPlaying()){
            binding.splashVideoView.pause();
            binding.splashVideoView.stopPlayback();
            binding.splashVideoView.setBackgroundColor(requireActivity().getResources().getColor(R.color.splashBackgroundColor));
        }



    }

    @SuppressLint("InlinedApi")
    private void show() {

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
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

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }



}