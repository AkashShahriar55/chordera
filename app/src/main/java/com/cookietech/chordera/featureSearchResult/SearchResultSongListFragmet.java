package com.cookietech.chordera.featureSearchResult;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.databinding.FragmentSearchResultRecyclerViewBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSearchResult.utilities.song.SearchedSongListShowingAdapter;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class SearchResultSongListFragmet extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FragmentSearchResultRecyclerViewBinding binding;
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    RecyclerView recyclerView;
    SearchedSongListShowingAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;
    boolean isLoading = false;
    int itemCount = 0;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       binding = FragmentSearchResultRecyclerViewBinding.inflate(getLayoutInflater(),container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariable();
        initializeView();
    }
    private void initializeVariable() {
        currentPage = PAGE_START;
        isLastPage = false;
        totalPage = 10;
        isLoading = false;
        itemCount = 0;
    }
    private void initializeView() {
        //binding.tabId.setText("Song List Tab");
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchedSongListShowingAdapter(new ArrayList<SongsPOJO>(), binding);
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
       /* ArrayList<Song> items = new ArrayList<>();
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
        adapter.clear();
        getData();
    }
}
