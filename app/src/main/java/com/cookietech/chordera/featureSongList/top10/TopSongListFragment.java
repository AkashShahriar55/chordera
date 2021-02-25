package com.cookietech.chordera.featureSongList.top10;

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
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentTopSongListBinding;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;


public class TopSongListFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentTopSongListBinding binding;
    TopTenSongsAdapter adapter;
    boolean isLoading = true;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "top_ten_debug";
    public boolean databaseFetched = false;

    public TopSongListFragment(){

    }

    public static TopSongListFragment newInstance(){return new TopSongListFragment();}

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
        initializeVariable();
        initialize();
        initializeObservers();
    }

    private void initializeObservers() {

        mainViewModel.getObservableTopTenSongs().observe(fragmentLifecycleOwner, new Observer<ArrayList<SongsPOJO>>() {
            @Override
            public void onChanged(ArrayList<SongsPOJO> songsPOJOS) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.addNewQueryData(songsPOJOS);

            }
        });

        mainViewModel.getObservableTopTenResponse().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                DatabaseResponse.Response response = databaseResponse.getResponse();
                switch (response){
                    case Error:
                        Log.d("top_ten_debug", "onChanged: Error Loading Top Ten Data");
                        Toast.makeText(requireActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        break;
                    case Fetched:
                        databaseFetched = true;
                        Log.d("top_ten_debug", "onChanged: Top ten Fetched Successfully");
                        break;
                    case Fetching:
                        Log.d("top_ten_debug", "onChanged: Top Ten Fetching");
                        break;
                    case No_internet:
                        Log.d("top_ten_debug", "onChanged: No Internet");
                        break;
                    case Invalid_data:
                        Log.d("top_ten_debug", "onChanged: Top Ten Invalid Data");
                        break;
                    default:
                        break;
                }
            }
        });

        ConnectionManager.getObservableNetworkAvailability().observe(fragmentLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Log.d(TAG, "onChanged: net available");
                    if(!databaseFetched){
                        //getData();
                    }
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
        binding.headerTittle.setText(R.string.top_10);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.tabSelectorRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        binding.tabSelectorRv.setLayoutManager(layoutManager);
        swipeRefreshLayout = binding.swipeRefresh;
        adapter = new TopTenSongsAdapter(new ArrayList<SongsPOJO>(), binding.tabSelectorRv, new TopTenSongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SongsPOJO song) {
                //Log.d("click_debug", "onItemClick: " + song.getSong_name());
                mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(song));
                mainViewModel.setSelectedSong(song);

            }
        });
        binding.tabSelectorRv.setAdapter(adapter);
        getData();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

    }
    private void getData() {
        //adapter.addLoading();
        isLoading = true;
        mainViewModel.queryTopTenSongs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainViewModel.stopListeningTopTen();
    }

    @Override
    public void onRefresh() {
        getData();
    }
}
