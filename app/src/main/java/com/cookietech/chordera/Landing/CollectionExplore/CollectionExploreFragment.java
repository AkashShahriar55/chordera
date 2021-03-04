package com.cookietech.chordera.Landing.CollectionExplore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.cookietech.chordera.Landing.Collection.CollectionFragment;
import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentTopSongListBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.CollectionsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;
import java.util.ArrayList;


public class CollectionExploreFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentTopSongListBinding binding;
    CollectionExploreAdapter adapter;
    boolean isLoading = true;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "top_ten_debug";
    public boolean databaseFetched = false;

    public CollectionExploreFragment(){}



    public static CollectionExploreFragment newInstance(){return new CollectionExploreFragment();}

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
        mainViewModel.resetLastSongCollectionDocument();
        initializeVariable();
        initialize();
        initializeObservers();
    }

    private void initializeObservers() {


        mainViewModel.getObservableAllCollectionDataLiveData().observe(fragmentLifecycleOwner, collectionsPOJOS -> {
            Log.d("new_explore_debug", "onChanged: " + collectionsPOJOS.size());
            swipeRefreshLayout.setRefreshing(false);
            adapter.onNewData(collectionsPOJOS);

           /* if(adapter.getData().size()<=0)
            {
                adapter.onNewData(collectionsPOJOS);
            }
            else {
                Log.d("data_debug", "onChanged: " + adapter.getData().size());
                ArrayList<CollectionsPOJO> allData = new ArrayList<>(adapter.getData());
                allData.addAll(collectionsPOJOS);
                adapter.onNewData(allData);
            }*/
        });


        ConnectionManager.getObservableNetworkAvailability().observe(fragmentLifecycleOwner, aBoolean -> {
            if(aBoolean){
                Log.d(TAG, "onChanged: net available");
               /* if(!databaseFetched){
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
        binding.headerTittle.setText(R.string.collections);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.tabSelectorRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        binding.tabSelectorRv.setLayoutManager(layoutManager);
        swipeRefreshLayout = binding.swipeRefresh;
        adapter = new CollectionExploreAdapter(new ArrayList<>(), collectionsPOJO -> {
            if(!ConnectionManager.isOnline(requireActivity())){
                Toast.makeText(requireActivity(),"No internet connection",Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("collection_debug", "onClick: "+ collectionsPOJO.getSong_id().size());
            mainViewModel.setNavigation(NavigatorTags.COLLECTION_FRAGMENT, CollectionFragment.createArgs(collectionsPOJO));
        },binding.tabSelectorRv);
        binding.tabSelectorRv.setAdapter(adapter);
        adapter.setLastCollectionVisibilityListener(this::getData);
        getData();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_TOP_SONG);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

    }
    private void getData() {
        isLoading = true;
        mainViewModel.fetchAllCollectionData().observe(fragmentLifecycleOwner, databaseResponse -> {

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
                    adapter.setLastCollectionFetched(true);
                    break;
                default:
                    break;
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainViewModel.stopListeningAllCollectionData();
    }

    @Override
    public void onRefresh() {
        mainViewModel.resetLastSongCollectionDocument();
        adapter.clear();
        adapter.setLastCollectionFetched(false);
        getData();
    }
}
