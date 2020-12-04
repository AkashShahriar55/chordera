package com.cookietech.chordera.Splash;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.appcomponents.RemoteConfigManager;
import com.cookietech.chordera.application.ChorderaApplication;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.databinding.FragmentSplashBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;

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
                mainViewModel.setNavigation(NavigatorTags.LANDING_FRAGMENT,((ViewGroup)getView().getParent()).getId());
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



}