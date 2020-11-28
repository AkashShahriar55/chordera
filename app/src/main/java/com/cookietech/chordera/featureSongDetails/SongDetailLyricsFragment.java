package com.cookietech.chordera.featureSongDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cookietech.chordera.databinding.FragmentSongDetailsAndLyricsBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;

public class SongDetailLyricsFragment extends ChorderaFragment {
    public SongDetailLyricsFragment(){};

    public static SongDetailLyricsFragment newInstance(){return new SongDetailLyricsFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSongDetailsAndLyricsBinding binding = FragmentSongDetailsAndLyricsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    private void initialize() {

    }
}