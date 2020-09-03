package com.cookietech.chordera.Splash;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphamovie.lib.AlphaMovieView;
import com.cookietech.chordera.R;
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

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(getLayoutInflater(),container,false);
        View root = binding.getRoot();


        return root;
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
    public void onPause() {
        super.onPause();
        if(binding.splashVideoView.isPlaying()){
            binding.splashVideoView.pause();
            binding.splashVideoView.stopPlayback();
            binding.splashVideoView.setBackgroundColor(requireActivity().getResources().getColor(R.color.splashBackgroundColor));
        }

    }


}