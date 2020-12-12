package com.cookietech.chordera.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blz.cookietech.cookietechmetronomelibrary.MetronomeFragment;
import com.cookietech.chordera.Landing.LandingFragment;
import com.cookietech.chordera.R;
import com.cookietech.chordera.SearchSuggestion.SearchSuggestionFragment;
import com.cookietech.chordera.Splash.SplashFragment;
import com.cookietech.chordera.appcomponents.CookieTechFragmentManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.chordDisplay.ChordDisplayFragment;
import com.cookietech.chordera.chordDisplay.ChordDisplayFullscreenFragment;
import com.cookietech.chordera.databinding.ActivityMainBinding;
import com.cookietech.chordera.featureSearchResult.SearchResultFragment;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.featureSongLyrics.SongLyricsFragment;
import com.cookietech.chordera.featureSongList.collection.CollectionSongListShowFragment;
import com.cookietech.chordera.featureSongList.saved.SavedSongListFragment;
import com.cookietech.chordera.featureSongList.top10.TopSongListFragment;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordlibrary.Fragment.ChordLibraryFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    SplashFragment splashFragment;
    LandingFragment landingFragment;
    ChordLibraryFragment chordLibraryFragment;
    MetronomeFragment metronomeFragment;
    SearchResultFragment searchResultFragment;
    CollectionSongListShowFragment collectionSongListShowFragment;
    TopSongListFragment topSongListFragment;
    SavedSongListFragment savedSongListFragment;
    SongLyricsFragment songLyricsFragment;
    SelectionTypeFragment selectionTypeFragment;
    ChordDisplayFragment chordDisplayFragment;
    ChordDisplayFullscreenFragment chordDisplayFullscreenFragment;
    ActivityMainBinding binding;
    CookieTechFragmentManager cookieTechFragmentManager;
    MainViewModel mainViewModel;
    Observer<Navigator> navigationObserver;
    long lastBackButtonPressed = 0;
    private SearchSuggestionFragment searchSuggestionFragment;


    //skifjoaisdhjfo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        navigationObserver = new Observer<Navigator>() {
            @Override
            public void onChanged(Navigator navigator) {
                navigateTo(navigator.getNavigatorTag(),navigator.getContainerId());
            }

        };

        mainViewModel.getNavigation().observe(this,navigationObserver);

        cookieTechFragmentManager = CookieTechFragmentManager.getInstance();
        splashFragment = SplashFragment.newInstance();
        landingFragment = LandingFragment.newInstance();
        searchSuggestionFragment = new SearchSuggestionFragment();
        chordLibraryFragment = ChordLibraryFragment.newInstance(AppSharedComponents.getRoots());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        metronomeFragment = MetronomeFragment.newInstance(pendingIntent, AppSharedComponents.getTick(),AppSharedComponents.getTock());
        searchResultFragment = SearchResultFragment.newInstance();
        topSongListFragment = TopSongListFragment.newInstance();
        savedSongListFragment = SavedSongListFragment.newInstance();
        songLyricsFragment = SongLyricsFragment.newInstance();
        selectionTypeFragment = SelectionTypeFragment.newInstance();
        chordDisplayFragment = ChordDisplayFragment.newInstance();
        chordDisplayFullscreenFragment = ChordDisplayFullscreenFragment.newInstance();

        collectionSongListShowFragment = CollectionSongListShowFragment.newInstance();
        cookieTechFragmentManager.initCookieTechFragmentManager(getSupportFragmentManager());
        if(savedInstanceState == null){
            navigateTo(NavigatorTags.LANDING_FRAGMENT,binding.mainFragmentHolder.getId());
            mainViewModel.setNavigation(NavigatorTags.SPLASH_FRAGMENT,binding.mainFragmentHolder.getId());
        }else{
            Log.d("akash_test_debug", "onCreate: restart ");
        }



        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    Log.d("akash_test_debug", "onPermissionsChecked: granted");
                }
                else {
                    Log.d("akash_test_debug", "onPermissionsChecked: denied");
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .onSameThread()
                .check();





    }

    private void navigateTo(String tag,int containerId) {

        if(containerId == 1){
            containerId = binding.mainFragmentHolder.getId();
        }

        if(cookieTechFragmentManager.getIsFragmentAvailable(tag)){
            cookieTechFragmentManager.popFragmentExclusive(tag);
        }else{
            if(tag.equals(NavigatorTags.LANDING_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(landingFragment, NavigatorTags.LANDING_FRAGMENT,containerId, R.anim.enter_zoom_in_fade_in,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_zoom_out_fade_out);
            }else if(tag.equalsIgnoreCase(NavigatorTags.SPLASH_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(splashFragment, NavigatorTags.SPLASH_FRAGMENT,containerId,0,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_left);
            }else if(tag.equalsIgnoreCase(NavigatorTags.CHORD_LIBRARY_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(chordLibraryFragment,NavigatorTags.CHORD_LIBRARY_FRAGMENT,containerId,R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }else if(tag.equalsIgnoreCase(NavigatorTags.METRONOME_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(metronomeFragment,NavigatorTags.METRONOME_FRAGMENT,containerId,R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }else if(tag.equals(NavigatorTags.SEARCH_RESULT_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(searchResultFragment,NavigatorTags.SEARCH_RESULT_FRAGMENT,containerId,R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
            else if(tag.equals(NavigatorTags.COLLECTION_SONG_LIST_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(collectionSongListShowFragment,NavigatorTags.COLLECTION_SONG_LIST_FRAGMENT,containerId,R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
            else if(tag.equals(NavigatorTags.TOP_SONG_LIST_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(topSongListFragment,NavigatorTags.TOP_SONG_LIST_FRAGMENT,containerId,R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }else if(tag.equalsIgnoreCase(NavigatorTags.SEARCH_VIEW_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStack(searchSuggestionFragment,NavigatorTags.SEARCH_VIEW_FRAGMENT,containerId);
            }
            else if(tag.equals(NavigatorTags.SAVED_SONG_LIST_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(savedSongListFragment,NavigatorTags.SAVED_SONG_LIST_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right, R.anim.exit_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
            else if(tag.equals(NavigatorTags.SONG_DETAIL_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(songLyricsFragment,NavigatorTags.SONG_DETAIL_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right, R.anim.exit_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
            else if(tag.equals(NavigatorTags.SELECTION_TYPE_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(selectionTypeFragment,NavigatorTags.SELECTION_TYPE_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right, R.anim.exit_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }else if(tag.equalsIgnoreCase(NavigatorTags.CHORD_DISPLAY_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(chordDisplayFragment,NavigatorTags.CHORD_DISPLAY_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right, R.anim.exit_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
            else if (tag.equalsIgnoreCase(NavigatorTags.CHORD_DISPLAY_FULLSCREEN_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(chordDisplayFullscreenFragment,NavigatorTags.CHORD_DISPLAY_FULLSCREEN_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right, R.anim.exit_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
        }

        Log.d("akash_debug", "navigateTo: " + cookieTechFragmentManager.getFragmentsTagList() + tag);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        if(Objects.requireNonNull(mainViewModel.getNavigation().getValue()).getNavigatorTag().equalsIgnoreCase( NavigatorTags.SPLASH_FRAGMENT)){
            return;
        }
        if(Objects.requireNonNull(mainViewModel.getNavigation().getValue()).getNavigatorTag().equalsIgnoreCase( NavigatorTags.LANDING_FRAGMENT)){
            Toast.makeText(this,"Double tap to exit",Toast.LENGTH_SHORT).show();
            long interval = System.currentTimeMillis() - lastBackButtonPressed;
            if(interval < 1500 && interval > 0){
                finish();
            }else{
                lastBackButtonPressed = System.currentTimeMillis();
            }
            return;
        }

        Fragment topFragment = cookieTechFragmentManager.getTopFragment();
        if(topFragment instanceof ChorderaFragment){

            boolean handled = ((ChorderaFragment) topFragment).onBackPressed(mainViewModel.getNavigation().getValue());
            Log.d("akash_debug", "main onBackPressed: " + handled + " "+topFragment.getTag());
            if(handled){
                return;
            }
        }
        lastBackButtonPressed = System.currentTimeMillis();

        List<String> taglist = cookieTechFragmentManager.getFragmentsTagList();

        mainViewModel.setNavigation(taglist.get(taglist.size() - 2),binding.mainFragmentHolder.getId());
    }
}