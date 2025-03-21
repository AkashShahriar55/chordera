package com.cookietech.chordera.chordDisplay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Util.StringManipulationHelper;
import com.cookietech.chordera.databinding.FragmentChordDisplayTransposeModalBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.security.Key;

import static com.cookietech.chordera.application.AppSharedComponents.major_key_circle;
import static com.cookietech.chordera.application.AppSharedComponents.minor_key_circle;


public class ChordDisplayTransposeModal extends BottomSheetDialogFragment {
    String key = "C";
    TransposeCallback callback;
    FragmentChordDisplayTransposeModalBinding binding;
    int transposeValue;
    public ChordDisplayTransposeModal() {
        // Required empty public constructor
    }

    public static ChordDisplayTransposeModal newInstance(String key,int lastTranspose) {
        ChordDisplayTransposeModal fragment = new ChordDisplayTransposeModal();
        Bundle args = new Bundle();
        args.putInt("last_transpose",lastTranspose);
        args.putString("key",key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transposeValue = getArguments().getInt("last_transpose");
            key = getArguments().getString("key");
        }
    }

    public void setCallback(TransposeCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordDisplayTransposeModalBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.transposeValue.setText(String.valueOf(transposeValue));
        binding.transposePlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transposeValue++;
                if(callback!=null)
                    callback.onTranspose(transposeValue);
                binding.transposeValue.setText(String.valueOf(transposeValue));

                setUpUiForKeyTransposeChange();
            }
        });

        binding.transposeMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transposeValue--;
                if(callback!=null)
                    callback.onTranspose(transposeValue);
                binding.transposeValue.setText(String.valueOf(transposeValue));
                setUpUiForKeyTransposeChange();
            }
        });

        binding.transposeKeyBtn.setSelected(true);



        setUpUiForKeyTransposeChange();


        binding.moreCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    void setUpUiForKeyTransposeChange(){
        binding.transposeKeyValue.setText(StringManipulationHelper.getTransposedChord(key,transposeValue));
        binding.transposeValue.setText(StringManipulationHelper.getTransposedChord(key,transposeValue));
    }


    public interface TransposeCallback{
        void onTranspose(int transpose);
    }
}