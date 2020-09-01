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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {

    // starting of the project //

    FragmentSplashBinding binding;

    public static SplashFragment newInstance() {
        SplashFragment fragment = new SplashFragment();
        return fragment;
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
                + R.raw.chordera_animation);

        binding.videoView.setLooping(true);
        binding.videoView.setVideoFromUri(getContext(),video);
        binding.videoView.setOnVideoStartedListener(new AlphaMovieView.OnVideoStartedListener() {
            @Override
            public void onVideoStarted() {
                Log.d("akash_debug", "onVideoStarted: ");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.hideScreen.setVisibility(View.INVISIBLE);
                    }
                },1000);

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.videoView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.videoView.onPause();
    }
}