package com.cookietech.chordera.Landing.NewExplore;

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

import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.databinding.FragmentTopSongListBinding;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

public class NewSongsExploreFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentTopSongListBinding binding;
    //private ArrayList<Song> songArrayList = new ArrayList<Song>();
    SongListShowingAdapter adapter;
    boolean isLoading = true;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "top_ten_debug";
    public boolean databaseFetched = false;

    public NewSongsExploreFragment(){}

    public static NewSongsExploreFragment newInstance(){return new NewSongsExploreFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mainViewModel.resetLastNewSongDocument();
        initializeVariable();
        initialize();
        initializeObservers();
    }

    private void initializeObservers() {

        mainViewModel.getObservableAllNewSongsLiveData().observe(fragmentLifecycleOwner, songsPOJOS -> {
            Log.d("akash_loading_debug", "initializeObservers: " + songsPOJOS.size());
            swipeRefreshLayout.setRefreshing(false);
//            adapter.removeLoading();
            adapter.onNewData(songsPOJOS);
           /* if(adapter.getData().size()<=0)
            {
                adapter.onNewData(songsPOJOS);
            }
            else {
                Log.d("data_debug", "onChanged: " + adapter.getData().size());
                ArrayList<SongsPOJO> allData = new ArrayList<>(adapter.getData());
                allData.addAll(songsPOJOS);
                adapter.onNewData(allData);
            }*/

        });



        ConnectionManager.getObservableNetworkAvailability().observe(fragmentLifecycleOwner, aBoolean -> {
            if(aBoolean){
                Log.d(TAG, "onChanged: net available");
                /*if(!databaseFetched){
                    //getData();
                }*/
            }else{
                Log.d(TAG, "onChanged: net not available");
                Toast.makeText(getContext(),"No internet connectoin",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeVariable() {
        isLoading = true;
    }

    private void initialize() {
        binding.headerTittle.setText(R.string.new_explore);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.tabSelectorRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        binding.tabSelectorRv.setLayoutManager(layoutManager);
        swipeRefreshLayout = binding.swipeRefresh;
        adapter = new SongListShowingAdapter(new ArrayList<>(), binding.tabSelectorRv, mainViewModel,fragmentLifecycleOwner);
        binding.tabSelectorRv.setAdapter(adapter);
        adapter.setLastSongVisibilityListener(this::getData);
        getData();
//        adapter.addLoading();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_TOP_SONG);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

    }
    private void getData() {
        isLoading = true;
        //Log.d("new_explore_debug", "getData: ");
        mainViewModel.fetchAllNewSongsData().observe(fragmentLifecycleOwner, databaseResponse -> {
            DatabaseResponse.Response response = databaseResponse.getResponse();
            //Log.d("new_explore_debug", "getData: "+ response);
            switch (response) {
                case Error:
                    adapter.removeLoading();
                    Log.d("new_explore_debug", "Error Fetching All New Song: ");
                    break;
                case Fetched:
                    Log.d("new_explore_debug", "All New Song Fetched: ");
                    databaseFetched = true;
                    break;
                case Fetching:

                    Log.d("new_explore_debug", "All New Song Fetching: ");
                    break;
                case No_internet:
                    adapter.removeLoading();
                    Log.d("new_explore_debug", "No Internet fetching all new songs: ");
                    break;
                case Invalid_data:
                    adapter.removeLoading();
                    Log.d("new_explore_debug", "Invalid Data: ");
                    break;
                case LastSongFetched:
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
        mainViewModel.stopListeningNewSongs();
    }

    @Override
    public void onRefresh() {
        mainViewModel.resetLastNewSongDocument();
        adapter.clear();
        adapter.setLastSongFetched(false);
        getData();
    }


}
