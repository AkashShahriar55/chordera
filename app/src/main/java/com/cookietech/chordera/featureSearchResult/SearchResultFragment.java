package com.cookietech.chordera.featureSearchResult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.cookietech.chordera.databinding.FragmentSearchResultBinding;
import com.cookietech.chordera.featureSearchResult.utilities.TabAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.google.android.material.tabs.TabLayout;

/***
 * This is the search result showing fragment
 * It will handle tab pager and 2 fragments({@link SearchResultSongListFragmet}, {@link SearchResultCollectionFragment})
 */

public class SearchResultFragment extends ChorderaFragment {
    private FragmentSearchResultBinding binding;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FragmentManager fragmentManager;

    public SearchResultFragment(){};

    public static SearchResultFragment newInstance(){return new SearchResultFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchResultBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    private void initialize() {
        viewPager = binding.pager;
        tabLayout = binding.tabLayout;
        adapter = new TabAdapter(getChildFragmentManager());
        SearchResultSongListFragmet searchResultSongListFragmet = new SearchResultSongListFragmet();
        SearchResultCollectionFragment searchResultCollectionListFragment = new SearchResultCollectionFragment();
        adapter.addFragment(searchResultSongListFragmet, "Song");
        adapter.addFragment(searchResultCollectionListFragment, "Collection");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
