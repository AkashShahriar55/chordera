package com.cookietech.chordera.chordDisplay.chordDetails;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Half;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentChordVariationBinding;
import com.cookietech.chordlibrary.ThumbGenerator;
import com.cookietech.chordlibrary.Variation;
import com.google.errorprone.annotations.Var;

import static android.view.ViewTreeObserver.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordVariationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordVariationFragment extends Fragment {


    Variation variation;
    String name;
    int position;
    FragmentChordVariationBinding binding;
    Bitmap generatedThumb;
    private int size;

    public ChordVariationFragment() {
        // Required empty public constructor
    }


    public static ChordVariationFragment newInstance(Variation variation,String name,int position) {
        ChordVariationFragment fragment = new ChordVariationFragment();
        Bundle args = new Bundle();
        Log.d("variation_debug", "newInstance: " + variation.getFingers().size());
        args.putParcelable("variation",variation);
        args.putString("name",name);
        args.putInt("position",position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           variation = getArguments().getParcelable("variation");
           position = getArguments().getInt("position");
           name = getArguments().getString("name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordVariationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("variation_debug", "onViewCreated: variation fragment created");
        if(variation != null){
            TextView textView = view.findViewById(R.id.variation_name);
            binding.variationName.setText("Fret " +variation.getFirstFret());
            binding.chordName.setText(name);

        }




    }

    @Override
    public void onResume() {
        super.onResume();
        if(generatedThumb!=null){
            binding.imageView2.setImageBitmap(generatedThumb);
            return;
        }
        binding.progressBar2.setVisibility(View.VISIBLE);
        binding.imageView2.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(binding.imageView2.getWidth() > 0){
                    if(variation != null)
                        new Thread(new ThumbGeneratorRunnable(variation,binding.imageView2.getWidth())).start();
                    binding.imageView2.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }

        });

    }

    private class ThumbGeneratorRunnable implements Runnable {
        Variation variation;
        int size;

        public ThumbGeneratorRunnable(Variation variation,int size) {
            this.variation = variation;
            this.size = size;
        }

        @Override
        public void run() {
            ThumbGenerator thumbGenerator = new ThumbGenerator(size);
            Bitmap bitmap = thumbGenerator.getThumbBitmap(variation,true);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    generatedThumb = bitmap;
                    binding.imageView2.setImageBitmap(bitmap);
                    binding.progressBar2.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

}