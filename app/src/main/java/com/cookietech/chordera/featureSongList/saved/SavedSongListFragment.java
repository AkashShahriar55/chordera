package com.cookietech.chordera.featureSongList.saved;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSavedSongBinding;
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SongsPOJO;

import java.util.ArrayList;
import java.util.List;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class SavedSongListFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentSavedSongBinding binding;
    RecyclerView recyclerView;
    AllSavedSongPagedAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    /*int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;*/
    boolean isLoading = false;
    //int itemCount = 0;
    LinearLayoutManager layoutManager;
    //ArrayList<SongsPOJO> songsList = new ArrayList<>();

    public SavedSongListFragment(){}

    public static SavedSongListFragment newInstance(){return new SavedSongListFragment();}

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

        mainViewModel.getObservableAllSavedSongs().observe(fragmentLifecycleOwner, songsEntities -> {
            Log.d("download_debug", "onChanged: " + songsEntities.size());

            //swipeRefreshLayout.setRefreshing(false);
            //adapter.clear();
            adapter.submitList(songsEntities);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initializeVariable() {
        isLoading = false;
        //itemCount = 0;
        getData();
    }

    private void getData() {
        isLoading = true;
        mainViewModel.fetchAllSavedSongs();
    }

    private void initialize() {
        binding.headerTittle.setText(R.string.saved_song);
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllSavedSongPagedAdapter(requireContext(),new AllSavedSongPagedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SongsPOJO song) {
                mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(song));
                mainViewModel.setSelectedSong(song);
            }
        });
        recyclerView.setAdapter(adapter);
        //getData();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_SAVED);
        adapter.setOnItemLongClickListener(new AllSavedSongPagedAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLogClick(SongsEntity songsEntity) {
                //Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

    }


    @Override
    public void onRefresh() {
        //adapter.clear();
        //getData();
        mainViewModel.refreshSavedSong();
        adapter.notifyDataSetChanged();
    }
}
