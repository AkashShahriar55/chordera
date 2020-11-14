package com.cookietech.chordera.Landing;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Util.ViewUtils;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentLandingBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandingFragment extends ChorderaFragment {

    private FragmentLandingBinding binding;
    private NewItemAdapter newItemAdapter;
    private CollectionItemAdapter collectionItemAdapter;

    public LandingFragment() {
        // Required empty public constructor
    }


    public static LandingFragment newInstance() {
        return new LandingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLandingBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        adJustViews();
        initializeClickEvents();


    }

    private void initializeClickEvents() {
        binding.cvChordlibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("akash_debug", "onClick: ");
                mainViewModel.setNavigation(NavigatorTags.CHORD_LIBRARY_FRAGMENT);
            }
        });

        binding.cvTop10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sohan debug","top ten pressed");
                mainViewModel.setNavigation(NavigatorTags.TOP_SONG_LIST_FRAGMENT);
            }
        });
    }

    private void initializeViews() {
        initializeNewRecyclerView();
        initializeCollectionRecyclerView();
    }

    private void initializeCollectionRecyclerView() {
        collectionItemAdapter = new CollectionItemAdapter(binding.rvCollectionItems);
        binding.rvCollectionItems.setHasFixedSize(true);
        binding.rvCollectionItems.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvCollectionItems.setAdapter(collectionItemAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvCollectionItems, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
    }

    private void initializeNewRecyclerView() {
        newItemAdapter = new NewItemAdapter(binding.rvNewItems);
        binding.rvNewItems.setHasFixedSize(true);
        binding.rvNewItems.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvNewItems.setAdapter(newItemAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvNewItems, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
    }

    private void adJustViews() {

        binding.clBottomLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("akash_debug", "onGlobalLayout: " + binding.clBottomLayout.getHeight() + " " + binding.clBottomLayout.getWidth());

                int size = (int) Math.min(binding.clBottomLayout.getWidth()/3,binding.clBottomLayout.getHeight()/3) ;

                Log.d("akash_debug", "onGlobalLayout: "+ size);

                int margin = (int) (size * 0.125);


                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.cvTop10Button.getLayoutParams();
                params.height = size;
                params.width = size;
                params.rightMargin = margin;
                params.bottomMargin = margin;
                binding.cvTop10Button.setLayoutParams(params);

                params = (ConstraintLayout.LayoutParams) binding.cvSavedButton.getLayoutParams();
                params.height = size;
                params.width = size;
                params.leftMargin = margin;
                params.bottomMargin = margin;
                binding.cvSavedButton.setLayoutParams(params);

                params = (ConstraintLayout.LayoutParams) binding.cvMetronomeButton.getLayoutParams();
                params.height = size;
                params.width = size;
                params.rightMargin = margin;
                params.topMargin = margin;
                binding.cvMetronomeButton.setLayoutParams(params);

                params = (ConstraintLayout.LayoutParams) binding.cvChordlibraryButton.getLayoutParams();
                params.height = size;
                params.width = size;
                params.leftMargin = margin;
                params.topMargin = margin;
                binding.cvChordlibraryButton.setLayoutParams(params);

                binding.clBottomLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });



    }
}