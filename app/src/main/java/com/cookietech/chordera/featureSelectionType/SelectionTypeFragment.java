package com.cookietech.chordera.featureSelectionType;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.Util.NativeAdsFragment;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.appcomponents.RemoteConfigManager;
import com.cookietech.chordera.databinding.FragmentSelectionTypeBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;
import java.util.Map;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class SelectionTypeFragment extends ChorderaFragment{
    FragmentSelectionTypeBinding binding;
    RecyclerView recyclerView;
    SelectionTypeShowingAdapter adapter;
    SongsPOJO selectedSong;
    public static final String SONGS_POJO = "songs_pojo";
    private boolean adFragmentSetup = false;

    public SelectionTypeFragment(){};

    public static Bundle createBundle(SongsPOJO songsPOJO){
        Bundle bundle = new Bundle();
        bundle.putParcelable(SONGS_POJO,songsPOJO);
        return bundle;
    }

    public static SelectionTypeFragment newInstance(Bundle args){
        SelectionTypeFragment fragment = new SelectionTypeFragment();
        fragment.setArguments(args);
        return fragment;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            if(getArguments().getParcelable(SONGS_POJO) != null){
                selectedSong = getArguments().getParcelable(SONGS_POJO);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectionTypeBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState != null){
            adFragmentSetup = savedInstanceState.getBoolean("adFragmentSetup");
        }else{
            adFragmentSetup = false;
        }
        initializeVariable();
        initialize();
        initializeObservers();
        if(RemoteConfigManager.shouldShowSelectionNativeAds() && !adFragmentSetup)
            setUpNativeAdFragment();
    }

    private void initializeObservers() {
        Log.d("akash_selection_debug", "initializeObservers: " + mainViewModel.getObservableSelectedSong().getValue());
        if(mainViewModel.getObservableSelectedSong().getValue() == null && selectedSong !=null)
            mainViewModel.setSelectedSong(selectedSong);
        mainViewModel.getObservableSelectedSong().observe(fragmentLifecycleOwner, new Observer<SongsPOJO>() {
            @Override
            public void onChanged(SongsPOJO songsPOJO) {
                Log.d("akash_debug_list", "onChanged: "+ songsPOJO.getSong_name());
                selectedSong = songsPOJO;
                updateView();
                getData();
            }
        });
    }

    private void updateView() {
        binding.songTittle.setText(selectedSong.getSong_name());
        binding.band.setText(selectedSong.getArtist_name());
    }

    private void initializeVariable() {
        //
    }

    private void initialize() {
        binding.songTittle.setText("Koshto");
        binding.band.setText("Avoid Rafa");
        recyclerView = binding.recyclerView;


        adapter = new SelectionTypeShowingAdapter(requireContext(),new ArrayList<SelectionType>(), binding, mainViewModel);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getData();
        updateView();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

    }
    private void getData() {
        ArrayList<SelectionType> items = new ArrayList<>();
        if(selectedSong != null){
            Map<String, String> map = selectedSong.getSong_data();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Log.d("tab_debug", "getData: " + entry.getKey());
                // here entry.getKey() returns like 'guitar_chord'
                // SelectionType.displaySelectionNameMap.get(entry.getKey()) returns Guitar Variation
                if(SelectionType.displaySelectionNameMap.containsKey(entry.getKey()))
                {
                    items.add(new SelectionType(entry.getKey(), SelectionType.displaySelectionNameMap.get(entry.getKey()),String.valueOf(entry.getValue())));
                }
                else
                {
                    Log.d("sohan_debug","key not found");
                    //TODO need to handle key not found
                }
            }

        }else{
            Log.d("tab_debug", "getData: no data found");
        }

        adapter.addItems(items);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("akash_selection_debug", "onSaveInstanceState: ");
        outState.putBoolean("adFragmentSetup",adFragmentSetup);
    }

    private void setUpNativeAdFragment() {
        FragmentTransaction transaction =   getChildFragmentManager().beginTransaction();
        Fragment adFragment = NativeAdsFragment.newInstance();
        transaction.add(binding.selectionNativeAdHolder.getId(), adFragment);
        transaction.commit();
        adFragmentSetup = true;
    }




}
