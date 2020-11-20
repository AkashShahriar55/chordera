package com.cookietech.chordera.featureSongList.top10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.databinding.FragmentSongListAnythingBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Song;

import java.util.ArrayList;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class TopSongListFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentSongListAnythingBinding binding;
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    SongListShowingAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;
    boolean isLoading = false;
    int itemCount = 0;
    LinearLayoutManager layoutManager;

    public TopSongListFragment(){};

    public static TopSongListFragment newInstance(){return new TopSongListFragment();};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongListAnythingBinding.inflate(getLayoutInflater(),container,false);
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
        totalPage = 5;
        isLoading = false;
        itemCount = 0;
    }

    private void initialize() {
        binding.headerTittle.setText("Top 10");
        binding.collectionName.setVisibility(View.GONE);
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        binding.recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new SongListShowingAdapter(new ArrayList<Song>(), binding);
        getData();
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
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


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonPressed();
            }
        });

    }
    private void getData() {
        ArrayList<Song> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            itemCount++;
            Song song = new Song();
            song.setTittle("Koshto" + itemCount);
            song.setBandName("Avoid Rafa");
            song.setTotalView("120");
            items.add(song);
        }
        /**
         * manage progress view
         */
        if (currentPage != PAGE_START) adapter.removeLoading();
        //adapter.addItems(items);
        ArrayList<Song> allData = new ArrayList<Song>(adapter.getData());
        allData.addAll(items);
        adapter.onNewData(allData);
        swipeRefreshLayout.setRefreshing(false);

        // check weather is last page or not
        if (currentPage < totalPage) {
            adapter.addLoading();
        } else {
            isLastPage = true;
        }
        isLoading = false;
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        getData();
    }

    private void backButtonPressed() {
        requireActivity().onBackPressed();
    }
}
