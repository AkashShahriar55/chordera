package com.cookietech.chordera.chordDisplay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentChordDisplayDetailDialogBinding;
import com.cookietech.chordlibrary.ChordClass;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordDisplayDetailDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordDisplayDetailDialog extends Fragment {

    ChordClass chordClass;
    FragmentChordDisplayDetailDialogBinding binding;
    public ChordDisplayDetailDialog() {
        // Required empty public constructor
    }

    public static ChordDisplayDetailDialog newInstance(ChordClass chordClass) {
        ChordDisplayDetailDialog fragment = new ChordDisplayDetailDialog();
        Bundle args = new Bundle();
        args.putParcelable("chord_class",chordClass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chordClass = getArguments().getParcelable("chord_class");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordDisplayDetailDialogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}