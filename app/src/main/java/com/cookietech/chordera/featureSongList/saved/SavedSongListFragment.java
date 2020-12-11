package com.cookietech.chordera.featureSongList.saved;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.databinding.FragmentSavedSongBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;
import java.util.List;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class SavedSongListFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentSavedSongBinding binding;
    RecyclerView recyclerView;
    SongListShowingAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;
    boolean isLoading = false;
    int itemCount = 0;
    LinearLayoutManager layoutManager;
    List<SongsPOJO> songsList = new ArrayList<>();

    public SavedSongListFragment(){};

    public static SavedSongListFragment newInstance(){return new SavedSongListFragment();};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedSongBinding.inflate(getLayoutInflater(),container,false);
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

        mainViewModel.getObservableAllSongs().observe(fragmentLifecycleOwner, new Observer<List<SongsEntity>>() {
            @Override
            public void onChanged(List<SongsEntity> songsEntities) {
                Log.d("download_debug", "onChanged: " + songsEntities.size());
                swipeRefreshLayout.setRefreshing(false);
                if(isLoading)
                {
                    adapter.removeLoading();
                    isLoading = false;
                }
                songsList.clear();
                for (SongsEntity entity:songsEntities){
                    songsList.add(entity.convertToSongsPOJO());
                }
                //swipeRefreshLayout.setRefreshing(false);
                //adapter.clear();
                adapter.addItems(songsList);

            }
        });
    }

    private void initializeVariable() {
        currentPage = PAGE_START;
        isLastPage = false;
        totalPage = 5;
        isLoading = false;
        itemCount = 0;
    }

    private void getData() {
        adapter.addLoading();
        isLoading = true;
        mainViewModel.fetchAllSongs();
    }

    private void initialize() {
        binding.headerTittle.setText(R.string.saved_song);
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SongListShowingAdapter(new ArrayList<SongsPOJO>(), binding.recyclerView, mainViewModel,fragmentLifecycleOwner);
        recyclerView.setAdapter(adapter);
        getData();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_SAVED);

        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

    }


    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        getData();
    }
}
