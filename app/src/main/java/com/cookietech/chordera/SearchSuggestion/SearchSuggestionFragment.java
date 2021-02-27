package com.cookietech.chordera.SearchSuggestion;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cookietech.chordera.Landing.CollectionItemAdapter;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSearchSuggestionBinding;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordera.models.SearchData;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class SearchSuggestionFragment extends ChorderaFragment {

    FragmentSearchSuggestionBinding binding;
    Observer<String> searchKeyWordObservable;
    private SearchResultAdapter searchResultAdapter;
    SearchSongCommunicator communicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("flow_debug", "onCreate: ");
        if(getArguments()!=null){
            Log.d("flow_debug", "onCreate: mal aasche");
            communicator = (SearchSongCommunicator) getArguments().getSerializable("callback");
        }
    }

    public static Bundle createBundle(SearchSongCommunicator communicator){
        Bundle args = new Bundle();
        args.putSerializable("callback",communicator);
        return args;
    }

    public static SearchSuggestionFragment newInstance(Bundle bundle) {
        SearchSuggestionFragment fragment = new SearchSuggestionFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_TOP_SONG);
        searchKeyWordObservable =  new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("akash_debug", "onChanged: "+ s);
            }
        };
        mainViewModel.getObservableSearchKeyword().observe(fragmentLifecycleOwner,searchKeyWordObservable);
        Spannable wordtoSpan = new SpannableString("No data found");
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#EA4F4F")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.errorMessage.errorMessageTv.setText(wordtoSpan);
        searchResultAdapter = new SearchResultAdapter(requireContext(),mainViewModel);
        binding.searchResultRv.setHasFixedSize(true);
        binding.searchResultRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.searchResultRv.setAdapter(searchResultAdapter);
        mainViewModel.getObservableSearchResult().observe(fragmentLifecycleOwner, new Observer<ArrayList<SearchData>>() {
            @Override
            public void onChanged(ArrayList<SearchData> searchData) {
                Log.d("search_result", "onChanged: ");
                searchResultAdapter.setSearchData(searchData);
                binding.searchResultRv.post(new Runnable() {
                    @Override
                    public void run() {
                        searchResultAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        mainViewModel.getObservableSearchResponses().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                switch (databaseResponse.getResponse()){
                    case Fetched:
                        binding.searchLoading.setVisibility(View.INVISIBLE);
                        binding.errorMessage.errorMessageHolder.setVisibility(View.INVISIBLE);
                        break;
                    case Error:
                    case Invalid_data:
                        Log.d("search_result", "onChanged: error ");
                        binding.searchLoading.setVisibility(View.INVISIBLE);
                        binding.errorMessage.errorMessageHolder.setVisibility(View.VISIBLE);
                        break;
                    case Fetching:
                        resetAdapter();
                        binding.searchLoading.setVisibility(View.VISIBLE);
                        binding.errorMessage.errorMessageHolder.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

        mainViewModel.getObservableSearchSelectedSong().observe(fragmentLifecycleOwner, new Observer<SongsPOJO>() {
            @Override
            public void onChanged(SongsPOJO songsPOJO) {
                mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(songsPOJO));
                mainViewModel.setSelectedSong(songsPOJO);
                communicator.onSearchedSongSelected();
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
        communicator.onBackButtonClicked();
        return false;
    }
}
