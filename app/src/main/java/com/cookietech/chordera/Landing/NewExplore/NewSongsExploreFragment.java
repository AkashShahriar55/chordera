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
import com.cookietech.chordera.featureSearchResult.utilities.PaginationListener;
import com.cookietech.chordera.featureSongList.SongListShowingAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Song;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

import static com.cookietech.chordera.featureSearchResult.utilities.PaginationListener.PAGE_START;

public class NewSongsExploreFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentTopSongListBinding binding;
    private ArrayList<Song> songArrayList = new ArrayList<Song>();
    SongListShowingAdapter adapter;
    int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;
    boolean isLoading = true;
    int itemCount = 0;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "top_ten_debug";
    public boolean databaseFetched = false;

    public NewSongsExploreFragment(){};

    public static NewSongsExploreFragment newInstance(){return new NewSongsExploreFragment();};

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

        mainViewModel.getObservableAllNewSongsLiveData().observe(fragmentLifecycleOwner, new Observer<ArrayList<SongsPOJO>>() {
            @Override
            public void onChanged(ArrayList<SongsPOJO> songsPOJOS) {
                //adapter.onNewData(songsPOJOS);
                Log.d("new_explore_debug", "onChanged: " + songsPOJOS.size());
                swipeRefreshLayout.setRefreshing(false);
                if(isLoading)
                {
                    adapter.removeLoading();
                    isLoading = false;
                }

                if(adapter.getData().size()<=0)
                {
                    adapter.onNewData(songsPOJOS);
                }
                else {
                    ArrayList<SongsPOJO> allData = new ArrayList<SongsPOJO>(adapter.getData());
                    allData.addAll(songsPOJOS);
                    adapter.onNewData(allData);
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
        currentPage = PAGE_START;
        isLastPage = false;
        totalPage = 5;
        isLoading = true;
        itemCount = 0;
    }

    private void initialize() {
        binding.headerTittle.setText(R.string.new_explore);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.tabSelectorRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        binding.tabSelectorRv.setLayoutManager(layoutManager);
        swipeRefreshLayout = binding.swipeRefresh;
        adapter = new SongListShowingAdapter(new ArrayList<SongsPOJO>(), binding.tabSelectorRv, mainViewModel,fragmentLifecycleOwner);
        binding.tabSelectorRv.setAdapter(adapter);
        getData();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_TOP_SONG);

        binding.tabSelectorRv.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getData();
                if (currentPage <= totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
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
    private void getData() {
        adapter.addLoading();
        isLoading = true;
        Log.d("new_explore_debug", "getData: ");
        mainViewModel.fetchAllNewSongsData().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                DatabaseResponse.Response response = databaseResponse.getResponse();
                Log.d("new_explore_debug", "getData: "+ response);
                switch (response){
                    case Error:
                        break;
                    case Fetched:
                        databaseFetched = true;
                        break;
                    case Fetching:
                        break;
                    case No_internet:
                        break;
                    case Invalid_data:
                        break;
                    default:
                        break;
                }
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
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        getData();
    }
}
