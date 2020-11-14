package com.cookietech.chordera.featureSearchResult;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSearchResultRecyclerViewBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSearchResult.utilities.collection.CollectionListShowingAdapter;
import com.cookietech.chordera.featureSearchResult.utilities.song.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Collection;
import com.cookietech.chordera.models.Song;

import java.util.ArrayList;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class SearchResultCollectionFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener, CollectionListShowingAdapter.FragmentListener {
    FragmentSearchResultRecyclerViewBinding binding;
    private ArrayList<Collection> collectionsList = new ArrayList<Collection>();
    RecyclerView recyclerView;
    CollectionListShowingAdapter adapter;
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
        initializeView();
    }

    private void initializeView() {
        //binding.tabId.setText("Song List Tab");
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getData();
        adapter = new CollectionListShowingAdapter(new ArrayList<Collection>(),binding);
        adapter.setOnclickItemListener(this);
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
        final ArrayList<Collection> items = new ArrayList<>();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    itemCount++;
                    Collection collection = new Collection();
                    collection.setName("Avoid Rafa" + itemCount);
                    collection.setView("100");
                    items.add(collection);
                }
                /**
                 * manage progress view
                 */
                if (currentPage != PAGE_START) adapter.removeLoading();
                //adapter.addItems(items);
                ArrayList<Collection> allData = new ArrayList<Collection>(adapter.getData());
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
        }, 1500);
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        getData();
    }

    @Override
    public void onItemClick(String id) {
        Log.d("sohan_debug", "onItemClick: " + id);
        mainViewModel.setNavigation(NavigatorTags.SONG_LIST_FRAGMENT);
    }
}
