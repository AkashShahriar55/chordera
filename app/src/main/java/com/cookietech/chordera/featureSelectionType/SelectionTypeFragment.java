package com.cookietech.chordera.featureSelectionType;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSelectionTypeBinding;
import com.cookietech.chordera.databinding.FragmentSongListAnythingBinding;
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

    public SelectionTypeFragment(){};

    public static SelectionTypeFragment newInstance(){return new SelectionTypeFragment();};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initializeVariable();
        initialize();
        initializeObservers();
    }

    private void initializeObservers() {
        mainViewModel.getObservableSelectedSong().observe(fragmentLifecycleOwner, new Observer<SongsPOJO>() {
            @Override
            public void onChanged(SongsPOJO songsPOJO) {
                selectedSong = songsPOJO;
                updateView();
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


        adapter = new SelectionTypeShowingAdapter(new ArrayList<SelectionType>(), binding, mainViewModel);
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

    }
    private void getData() {
        ArrayList<SelectionType> items = new ArrayList<>();
        if(selectedSong != null){
            int id = 1;
            Map<String, String> map = selectedSong.getSong_data();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                items.add(new SelectionType(entry.getKey(),String.valueOf(id)));
                id++;
            }

        }

        adapter.addItems(items);
    }


}
