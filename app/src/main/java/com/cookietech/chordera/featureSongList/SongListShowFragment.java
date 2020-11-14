package com.cookietech.chordera.featureSongList;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSearchResultBinding;
import com.cookietech.chordera.databinding.FragmentSongListOfCollectionBinding;
import com.cookietech.chordera.featureSearchResult.SearchResultCollectionFragment;
import com.cookietech.chordera.featureSearchResult.SearchResultSongListFragmet;
import com.cookietech.chordera.featureSearchResult.utilities.TabAdapter;
import com.cookietech.chordera.fragments.ChorderaFragment;

/***
 * this is the song list showing of any collections
 */


public class SongListShowFragment extends ChorderaFragment {
    FragmentSongListOfCollectionBinding binding;
    FrameLayout frameLayout;

    public SongListShowFragment(){};

    public static SongListShowFragment newInstance(){return new SongListShowFragment();};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongListOfCollectionBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    private void initialize() {
        SearchResultSongListFragmet searchResultSongListFragmet = new SearchResultSongListFragmet();
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.add(binding.songFragmentHolder.getId(), searchResultSongListFragmet, NavigatorTags.SONG_LIST_FRAGMENT);
        fragmentTransaction.addToBackStack(NavigatorTags.SONG_LIST_FRAGMENT);
        fragmentTransaction.commitAllowingStateLoss();

    }
}
