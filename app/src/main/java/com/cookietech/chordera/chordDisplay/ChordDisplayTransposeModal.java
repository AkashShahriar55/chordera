package com.cookietech.chordera.chordDisplay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentChordDisplayTransposeModalBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.security.Key;

import static com.cookietech.chordera.application.AppSharedComponents.major_key_circle;
import static com.cookietech.chordera.application.AppSharedComponents.minor_key_circle;


public class ChordDisplayTransposeModal extends BottomSheetDialogFragment {
    public static final String TRANSPOSE_CAPO = "transpose_capo";
    public static final String TRANSPOSE_KEY = "transpose_key";
    String key = "C";
    private String transposeType = TRANSPOSE_CAPO;
    TransposeCallback callback;
    FragmentChordDisplayTransposeModalBinding binding;
    int transposeValue;
    public ChordDisplayTransposeModal() {
        // Required empty public constructor
    }

    public static ChordDisplayTransposeModal newInstance(String key,int lastTranspose,String transposeType) {
        ChordDisplayTransposeModal fragment = new ChordDisplayTransposeModal();
        Bundle args = new Bundle();
        args.putInt("last_transpose",lastTranspose);
        args.putString("last_type_selected",transposeType);
        args.putString("key",key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transposeValue = getArguments().getInt("last_transpose");
            transposeType = getArguments().getString("last_type_selected");
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
                    callback.onTranspose(transposeValue,transposeType);
                binding.transposeValue.setText(String.valueOf(transposeValue));

                if(transposeType.equalsIgnoreCase(TRANSPOSE_KEY)){
                   setUpUiForKeyTransposeChange();
                }
            }
        });

        binding.transposeMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transposeValue--;
                if(callback!=null)
                    callback.onTranspose(transposeValue,transposeType);
                binding.transposeValue.setText(String.valueOf(transposeValue));
                if(transposeType.equalsIgnoreCase(TRANSPOSE_KEY)){
                   setUpUiForKeyTransposeChange();
                }
            }
        });

        if(transposeType.equalsIgnoreCase(TRANSPOSE_CAPO)){
            binding.tranposeCapoBtn.setSelected(true);
            binding.transposeKeyBtn.setSelected(false);
        }else{
            binding.tranposeCapoBtn.setSelected(false);
            binding.transposeKeyBtn.setSelected(true);
        }

        binding.transposeKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transposeType = TRANSPOSE_KEY;
                binding.tranposeCapoBtn.setSelected(false);
                binding.transposeKeyBtn.setSelected(true);
                transposeValue = 0;
                if(callback != null)
                    callback.onTranspose(transposeValue,transposeType);
                binding.transposeKeyValue.setText(key);
                binding.transposeValue.setText(key);
            }
        });

        binding.tranposeCapoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transposeType = TRANSPOSE_CAPO;
                binding.tranposeCapoBtn.setSelected(true);
                binding.transposeKeyBtn.setSelected(false);
                transposeValue = 0;
                if(callback != null)
                    callback.onTranspose(transposeValue,transposeType);
                binding.transposeKeyValue.setText(getTheActualKeyValue());
                binding.transposeValue.setText(String.valueOf(0));
            }
        });

        if(transposeType.equalsIgnoreCase(TRANSPOSE_KEY)){
           setUpUiForKeyTransposeChange();
        }else{
            binding.transposeKeyValue.setText(getTheActualKeyValue());
            binding.transposeValue.setText(String.valueOf(transposeValue));
        }


        binding.moreCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    void setUpUiForKeyTransposeChange(){
        binding.transposeKeyValue.setText(getTheActualKeyValue());
        binding.transposeValue.setText(getTheActualKeyValue());
    }

    String getTheActualKeyValue(){
        String actualKey = "";
        int currentKeyPosition;
        int transposedPosition;
        int key_value_for_transpose;
        for (String value:major_key_circle){
            if(value.equalsIgnoreCase(key)){
                currentKeyPosition = major_key_circle.indexOf(value);
                transposedPosition = currentKeyPosition+transposeValue;
                key_value_for_transpose = transposedPosition>=0? (Math.abs(transposedPosition)%12):(12-Math.abs(transposedPosition)%12);
                actualKey = major_key_circle.get(key_value_for_transpose);
            }

        }
        for (String value:minor_key_circle){
            if(value.equalsIgnoreCase(key)){
                currentKeyPosition = minor_key_circle.indexOf(value);
                transposedPosition = currentKeyPosition+transposeValue;
                key_value_for_transpose = transposedPosition>=0? (Math.abs(transposedPosition)%12):(12-Math.abs(transposedPosition)%12);
                actualKey = minor_key_circle.get(key_value_for_transpose);
            }
        }

        return actualKey;
    }


    public interface TransposeCallback{
        void onTranspose(int transpose,String transposeType);
    }
}