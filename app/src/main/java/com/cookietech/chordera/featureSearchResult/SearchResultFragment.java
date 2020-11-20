package com.cookietech.chordera.featureSearchResult;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSearchResultBinding;
import com.cookietech.chordera.featureSearchResult.utilities.TabAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Navigator;
import com.google.android.material.tabs.TabLayout;

/***
 * This is the search result showing fragment
 * It will handle tab pager and 2 fragments({@link SearchResultSongListFragmet}, {@link SearchResultCollectionFragment})
 */

public class SearchResultFragment extends ChorderaFragment {
    private FragmentSearchResultBinding binding;
    SearchResultSongListFragmet searchResultSongListFragmet = new SearchResultSongListFragmet();
    SearchResultCollectionFragment searchResultCollectionListFragment = new SearchResultCollectionFragment();
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
        ViewPager viewPager = binding.pager;
        TabLayout tabLayout = binding.tabLayout;
        TabAdapter adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(searchResultSongListFragmet, "Songs");
        adapter.addFragment(searchResultCollectionListFragment, "Collections");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onBackPressed(Navigator topNavigation) {
        boolean handled = false;
        if(topNavigation.getNavigatorTag().equalsIgnoreCase(NavigatorTags.SEARCH_RESULT_FRAGMENT)){
            Log.d("akash_debug", "result onBackPressed: ");
            mainViewModel.setNavigation(NavigatorTags.LANDING_FRAGMENT,1 );
            handled = true;
        }
        Log.d("akash_debug", "result onBackPressed: " + handled + " " + topNavigation.getNavigatorTag());
        return handled;
    }
}
