package com.cookietech.chordera.chordDisplay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentChordDisplayBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordDisplayFragment extends ChorderaFragment {

    FragmentChordDisplayBinding binding;


    public ChordDisplayFragment() {
        // Required empty public constructor
    }


    public static ChordDisplayFragment newInstance() {
        ChordDisplayFragment fragment = new ChordDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordDisplayBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}