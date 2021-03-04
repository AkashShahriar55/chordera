package com.cookietech.chordera.Landing.Collection;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.databinding.FragmentTopSongListBinding;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.CollectionsPOJO;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

public class CollectionFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentTopSongListBinding binding;
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    SongListShowingAdapter adapter;
    boolean isLoading = true;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "top_ten_debug";
    public boolean databaseFetched = false;
    private CollectionsPOJO collectionsPOJO;

    public CollectionFragment(){};

    public static Bundle createArgs(CollectionsPOJO collectionsPOJO){
        Bundle bundle = new Bundle();
        bundle.putParcelable("collection",collectionsPOJO);
        return bundle;
    }

    public static CollectionFragment newInstance( Bundle args) {
        CollectionFragment fragment = new CollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            collectionsPOJO = getArguments().getParcelable("collection");
            Log.d("collection_debug", "onCreate: " + collectionsPOJO.getCollection_name()+ collectionsPOJO.getId());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTopSongListBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.resetLastCollectionSongDocument();
        initializeVariable();
        initialize();
        initializeObservers();
    }

    private void initializeObservers() {

        mainViewModel.getObservableCollectionSongsData().observe(fragmentLifecycleOwner, new Observer<ArrayList<SongsPOJO>>() {
            @Override
            public void onChanged(ArrayList<SongsPOJO> songsPOJOS) {
                Log.d("collection_song_debug", "onChanged: " + songsPOJOS.size());
                swipeRefreshLayout.setRefreshing(false);
//                adapter.removeLoading();
                adapter.onNewData(songsPOJOS);
/*
                if(adapter.getData().size()<=0)
                {
                    adapter.onNewData(songsPOJOS);
                }
                else {
                    Log.d("data_debug", "onChanged: " + adapter.getData().size());
                    ArrayList<SongsPOJO> allData = new ArrayList<>(adapter.getData());
                    allData.addAll(songsPOJOS);
                    adapter.onNewData(allData);
                }*/

            }
        });



        ConnectionManager.getObservableNetworkAvailability().observe(fragmentLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Log.d(TAG, "onChanged: net available");
                    /*if(!databaseFetched){
                        //getData();
                    }*/
                }else{
                    Log.d(TAG, "onChanged: net not available");
                    Toast.makeText(getContext(),"No internet connectoin",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeVariable() {

        isLoading = true;
    }

    private void initialize() {
        binding.headerTittle.setText(collectionsPOJO.getCollection_name());
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.tabSelectorRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        binding.tabSelectorRv.setLayoutManager(layoutManager);
        swipeRefreshLayout = binding.swipeRefresh;
        adapter = new SongListShowingAdapter(new ArrayList<SongsPOJO>(), binding.tabSelectorRv, mainViewModel,fragmentLifecycleOwner, requireContext());
        binding.tabSelectorRv.setAdapter(adapter);
        adapter.setLastSongVisibilityListener(this::getData);

        getData();
//        adapter.addLoading();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_ONLINE);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

    }
    private void getData() {
        //adapter.addLoading();
        isLoading = true;
        Log.d("collection_songs_data", "getData: ");
        mainViewModel.fetchCollectionSongs(collectionsPOJO.getId()).observe(fragmentLifecycleOwner, databaseResponse -> {
            Log.d("collection_songs_data", "onChanged: ");
            DatabaseResponse.Response response = databaseResponse.getResponse();
            switch (response){
                case Error:
                    adapter.removeLoading();
                    Log.d("collection_song_debug", "Error Fetching All New Song: ");
                    break;
                case Fetched:
                    Log.d("collection_song_debug", "All New Song Fetched: ");
                    databaseFetched = true;
                    break;
                case Fetching:
                    Log.d("collection_song_debug", "All New Song Fetching: ");
                    break;
                case No_internet:
                    adapter.removeLoading();
                    Log.d("collection_song_debug", "No Internet fetching all new songs: ");
                    break;
                case Invalid_data:
                    adapter.removeLoading();
                    Log.d("collection_song_debug", "Invalid Data: ");
                    break;
                case LastSongFetched:
                    Log.d("collection_song_debug", "LastSongFetched: ");
                    adapter.removeLoading();
                    adapter.setLastSongFetched(true);
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainViewModel.stopListeningCollectionSongsData();
    }

    @Override
    public void onRefresh() {
        mainViewModel.resetLastCollectionSongDocument();
        adapter.clear();
        adapter.setLastSongFetched(false);
        getData();
    }
}
