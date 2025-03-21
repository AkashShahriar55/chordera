package com.cookietech.chordera.chordDisplay.chordDetails;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cookietech.chordera.Util.ViewUtils;
import com.cookietech.chordera.databinding.DialogChordDetailsBinding;
import com.cookietech.chordlibrary.ChordClass;

public class ChordDetailsDialogFragment extends DialogFragment {
    DialogChordDetailsBinding binding;
    ChordClass chordClass;
    ChordVariationAdapter chordVariationAdapter;
    private ChordDetailsDialogFragment(){

    }

    public static ChordDetailsDialogFragment newInstance(ChordClass chordClass) {

        Bundle args = new Bundle();

        ChordDetailsDialogFragment fragment = new ChordDetailsDialogFragment();
        args.putParcelable("chord_classes",chordClass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        int size = (int) (ViewUtils.getDisplayWidth(requireActivity())*0.8);
        getDialog().getWindow().setLayout(size, size);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogChordDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title

        // Show soft keyboard automatically and request focus to field







        if(getArguments()!= null){
            chordClass = getArguments().getParcelable("chord_classes");
        }
        if(chordClass!=null){
            chordVariationAdapter = new ChordVariationAdapter(this,chordClass);
            binding.variationPager.setAdapter(chordVariationAdapter);
            binding.slideLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nextPos = binding.variationPager.getCurrentItem()-1;
                    if(nextPos >= 0)
                        binding.variationPager.setCurrentItem(nextPos);
                }
            });

            binding.slideRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nextPos = binding.variationPager.getCurrentItem()+1;
                    if(nextPos < chordVariationAdapter.getItemCount())
                        binding.variationPager.setCurrentItem(nextPos);
                }
            });
        }
        else{
            Toast.makeText(requireContext(),"Something was wrong",Toast.LENGTH_SHORT).show();
            dismiss();
        }

    }
}
