package com.cookietech.chordera.chordDisplay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentChordDisplaySettingModalBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordDisplaySettingModal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordDisplaySettingModal extends BottomSheetDialogFragment {

    FragmentChordDisplaySettingModalBinding binding;
    MoreCallback callback;


    public ChordDisplaySettingModal() {
        // Required empty public constructor
    }

    public static ChordDisplaySettingModal newInstance() {
        ChordDisplaySettingModal fragment = new ChordDisplaySettingModal();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public void setCallback(MoreCallback callback) {
        this.callback = callback;
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
        binding = FragmentChordDisplaySettingModalBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.moreBackToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                    callback.onBackToHomeSelected();
                dismiss();
            }
        });
        binding.moreSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                    callback.onSettingSelected();
                dismiss();
            }
        });
        binding.moreShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                    callback.onShareSelected();
                dismiss();
            }
        });
        binding.smorePrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                    callback.onPrintSelected();
                dismiss();
            }
        });
        binding.moreTransposeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                    callback.onTransposeSelected();
                dismiss();
            }
        });



        binding.moreCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface MoreCallback{
        void onTransposeSelected();
        void onPrintSelected();
        void onShareSelected();
        void onSettingSelected();
        void onBackToHomeSelected();
    }
}