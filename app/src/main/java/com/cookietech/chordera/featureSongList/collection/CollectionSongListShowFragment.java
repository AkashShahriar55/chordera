package com.cookietech.chordera.featureSongList.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.databinding.FragmentSavedSongBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;

import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class CollectionSongListShowFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentSavedSongBinding binding;
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    RecyclerView recyclerView;
    SongListShowingAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;
    boolean isLoading = false;
    int itemCount = 0;
    LinearLayoutManager layoutManager;

    public CollectionSongListShowFragment(){};

    public static CollectionSongListShowFragment newInstance(){return new CollectionSongListShowFragment();};

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
    }

    private void initializeVariable() {
        currentPage = PAGE_START;
        isLastPage = false;
        totalPage = 10;
        isLoading = false;
        itemCount = 0;
    }

    private void initialize() {
        binding.headerTittle.setText("Collection");
        //binding.collectionName.setText("'Avoid Rafa'");
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SongListShowingAdapter(new ArrayList<SongsPOJO>(), binding.recyclerView, mainViewModel,fragmentLifecycleOwner);
        getData();
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getData();
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

    }
    private void getData() {
        /*ArrayList<Song> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            itemCount++;
            Song song = new Song();
            song.setTittle("Koshto" + itemCount);
            song.setBandName("Avoid Rafa");
            song.setTotalView("120");
            items.add(song);
        }
        *//**
         * manage progress view
         *//*
        if (currentPage != PAGE_START) adapter.removeLoading();
        //adapter.addItems(items);
        ArrayList<SongsPOJO> allData = new ArrayList<SongsPOJO>(adapter.getData());
        allData.addAll(items);
        adapter.onNewData(allData);
        swipeRefreshLayout.setRefreshing(false);

        // check weather is last page or not
        if (currentPage < totalPage) {
            adapter.addLoading();
        } else {
            isLastPage = true;
        }
        isLoading = false;*/
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        getData();
    }

}
