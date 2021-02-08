package com.cookietech.chordera.SearchSuggestion;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cookietech.chordera.Landing.CollectionItemAdapter;
import com.cookietech.chordera.databinding.FragmentSearchSuggestionBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordera.models.SearchData;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class SearchSuggestionFragment extends ChorderaFragment {

    FragmentSearchSuggestionBinding binding;
    Observer<String> searchKeyWordObservable;
    private SearchResultAdapter searchResultAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchSuggestionBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchKeyWordObservable =  new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("akash_debug", "onChanged: "+ s);
            }
        };
        mainViewModel.getObservableSearchKeyword().observe(fragmentLifecycleOwner,searchKeyWordObservable);

        searchResultAdapter = new SearchResultAdapter();
        binding.searchResultRv.setHasFixedSize(true);
        binding.searchResultRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.searchResultRv.setAdapter(searchResultAdapter);
        mainViewModel.getObservableSearchResult().observe(fragmentLifecycleOwner, new Observer<ArrayList<SearchData>>() {
            @Override
            public void onChanged(ArrayList<SearchData> searchData) {
                Log.d("search_result", "onChanged: ");
                searchResultAdapter.setSearchData(searchData);
            }
        });

        mainViewModel.getObservableSearchResponses().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                switch (databaseResponse.getResponse()){
                    case Fetched:
                        binding.searchLoading.setVisibility(View.INVISIBLE);
                        binding.errorMessage.getRoot().setVisibility(View.INVISIBLE);
                        break;
                    case Error:
                    case Invalid_data:
                        binding.searchLoading.setVisibility(View.INVISIBLE);
                        binding.errorMessage.getRoot().setVisibility(View.VISIBLE);
                        break;
                    case Fetching:
                        resetAdapter();
                        binding.searchLoading.setVisibility(View.VISIBLE);
                        binding.errorMessage.getRoot().setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
    }

    private void resetAdapter() {
        if(searchResultAdapter != null)
            searchResultAdapter.reset();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onBackPressed(Navigator topNavigation) {
        Log.d("akash_debug", "onBackPressed: ");
        return false;
    }
}
