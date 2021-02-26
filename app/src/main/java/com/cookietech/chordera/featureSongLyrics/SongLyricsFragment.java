package com.cookietech.chordera.featureSongLyrics;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.Util.NativeAdsFragment;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.appcomponents.RemoteConfigManager;
import com.cookietech.chordera.databinding.FragmentSongLyricsBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongLyricsFragment extends ChorderaFragment {

    private SongsPOJO selectedSong;
    private boolean isDarkModeActivated = false;
    private SelectionType selectedTab;
    private TabPOJO lyricsData;
    private boolean adsFragmentSetup = false;

    public SongLyricsFragment(){};

    FragmentSongLyricsBinding binding;
    public static SongLyricsFragment newInstance(){return new SongLyricsFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSongLyricsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.displayScrollView.setVisibility(View.GONE);
        adsFragmentSetup = false;
        if(savedInstanceState!=null){
            Log.d("akash_lyrics_debug", "onViewCreated: saved instance is not null");
            selectedSong = savedInstanceState.getParcelable("selectedSong");
            selectedTab = savedInstanceState.getParcelable("selectedTab");
            if(selectedSong!=null)
                mainViewModel.getObservableSelectedSong().setValue(selectedSong);
            if(selectedTab != null)
                mainViewModel.getObservableSelectedTab().setValue(selectedTab);
            adsFragmentSetup = savedInstanceState.getBoolean("adsFragmentSetup",false);
        }else{
            Log.d("akash_lyrics_debug", "onViewCreated: saved instance is null");
        }

        Log.d("akash_lyric_debug", "onViewCreated: ");
        setUpViews();
        initializeClicks();
        initializeObserver();
        setupMenuSelector();
        if(RemoteConfigManager.shouldShowChordDisplayNativeAds() && !adsFragmentSetup)
            setUpNativeAdFragment();
    }

    private void setUpViews() {
        isDarkModeActivated = mainViewModel.getObservableIsDarkModeActivated().getValue();
        //mainViewModel.setIsDarkModeActivated(isDarkModeActivated);
        binding.modeSwitch.setChecked(isDarkModeActivated);
        toggleMode();
    }

    private void setUpNativeAdFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Fragment adFragment = NativeAdsFragment.newInstance();
        transaction.add(binding.nativeAdContainer.getId(),adFragment);
        transaction.commitAllowingStateLoss();
        adsFragmentSetup = true;
    }

    private void initializeObserver() {
        selectedSong = mainViewModel.getObservableSelectedSong().getValue();
        mainViewModel.getObservableSelectedSong().observe(fragmentLifecycleOwner, new Observer<SongsPOJO>() {
            @Override
            public void onChanged(SongsPOJO songsPOJO) {
                Log.d("tab_debug", "onChanged: " + songsPOJO.getSong_name());
                selectedSong = songsPOJO;
                updateView();
            }
        });

        mainViewModel.getObservableSelectedTab().observe(fragmentLifecycleOwner, new Observer<SelectionType>() {
            @Override
            public void onChanged(SelectionType selectionType) {
                selectedTab = selectionType;
                mainViewModel.loadTab(selectedTab);

            }
        });

        mainViewModel.getObservableSelectedTabLiveData().observe(fragmentLifecycleOwner, new Observer<TabPOJO>() {
            @Override
            public void onChanged(TabPOJO lyricsPOJO) {
                lyricsData = lyricsPOJO;
                updateView();
                binding.lyricLoader.setVisibility(View.GONE);
                binding.displayScrollView.setVisibility(View.VISIBLE);

            }
        });

        /** Observer to get downloadSongDataResponse**/
        mainViewModel.getObservableDownloadSongDataResponse().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                switch (databaseResponse.getResponse()){

                    case Storing:
                        Log.d("download_debug", "onChanged: song data storing");
                        binding.downloadBtn.setVisibility(View.INVISIBLE);
                        binding.downloadProgress.setVisibility(View.VISIBLE);
                        break;
                    case Stored:
                        Log.d("download_debug", "onChanged: song data stored");
                        Map<String,String> songDataMap = new HashMap<>();
                        songDataMap.put(lyricsData.getData_type(),lyricsData.getId());
                        mainViewModel.roomInsertSong(new SongsEntity(selectedSong.getId(),selectedSong.getArtist_name(),selectedSong.getSong_name(), selectedSong.getGenre(),selectedSong.getImage_url(),selectedSong.getSong_duration(),songDataMap,selectedSong.getYoutube_id()));

                        break;
                    case Already_exist:
                        Log.d("download_debug", "onChanged: song data already exist");
                        Toast.makeText(requireContext(), "You Already downloaded this chord", Toast.LENGTH_SHORT).show();
                        binding.downloadBtn.setVisibility(View.VISIBLE);
                        binding.downloadBtn.setImageResource(R.drawable.downloaded_icon);
                        binding.downloadProgress.setVisibility(View.INVISIBLE);
                        binding.downloadBtn.setOnClickListener(null);
                        break;
                }
            }
        });

        mainViewModel.getObservableDownloadSongResponse().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                switch (databaseResponse.getResponse()){
                    case Storing:
                        Log.d("download_debug", "onChanged: song storing");
                        break;
                    case Stored:
                        Log.d("download_debug", "onChanged: song stored");
                        Toast.makeText(requireContext(), "Downloaded ", Toast.LENGTH_SHORT).show();
                        binding.downloadBtn.setVisibility(View.VISIBLE);
                        binding.downloadBtn.setImageResource(R.drawable.downloaded_icon);
                        binding.downloadProgress.setVisibility(View.INVISIBLE);
                        binding.downloadBtn.setOnClickListener(null);
                        break;
                    case Already_exist:
                        Log.d("download_debug", "onChanged: song already exist");
                        mainViewModel.roomFetchASong(selectedSong.getId());

                }
            }
        });

        mainViewModel.getObservableRoomFetchedSongResponse().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                switch (databaseResponse.getResponse()){
                    case Fetching:
                        Log.d("download_debug", "onChanged: Song Fetching For update");
                        break;
                    case Fetched:
                        Log.d("download_debug", "onChanged: song Fetched to update");
                        SongsEntity fetchedSong = mainViewModel.getObservableRoomFetchedSong().getValue();
                        Map<String,String> fetchedSongData = fetchedSong.getSong_data();
                        fetchedSongData.put(lyricsData.getData_type(),lyricsData.getId());
                        fetchedSong.setSong_data(fetchedSongData);
                        mainViewModel.roomUpdateExistingSongData(fetchedSong);

                        break;
                    case Error:
                        /**Implement Toast Here to show error message to user**/
                        Log.d("download_debug", "onChanged: song fetching error. Not able to update");
                        Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        binding.downloadBtn.setVisibility(View.VISIBLE);
                        binding.downloadProgress.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

        /*mainViewModel.getObservableRoomFetchedSong().observe(fragmentLifecycleOwner, new Observer<SongsEntity>() {
        });*/

        mainViewModel.getObservableRoomUpdateSongResponse().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {
                switch (databaseResponse.getResponse()){
                    case Updating:
                        Log.d("download_debug", "onChanged: song updating");
                        break;
                    case Updated:
                        Log.d("download_debug", "onChanged: Update Completed");
                        Toast.makeText(requireContext(), "Downloaded ", Toast.LENGTH_SHORT).show();
                        binding.downloadBtn.setVisibility(View.VISIBLE);
                        binding.downloadBtn.setImageResource(R.drawable.downloaded_icon);
                        binding.downloadProgress.setVisibility(View.INVISIBLE);
                        binding.downloadBtn.setOnClickListener(null);
                        break;
                    case Error:
                        Log.d("download_debug", "onChanged: Error in update");
                        Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        binding.downloadBtn.setVisibility(View.VISIBLE);
                        binding.downloadProgress.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

        Log.d("from_debug", "initializeObserver: " + mainViewModel.getObservableSongListShowingCalledFrom().getValue());
        mainViewModel.getObservableSongListShowingCalledFrom().observe(fragmentLifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String fromWhere) {
                if(fromWhere.equalsIgnoreCase(Constants.FROM_SAVED)){
                    binding.downloadBtn.setVisibility(View.GONE);
                }else{
                    binding.downloadBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        /** View_Mode Observer for dark and light mode**/

        mainViewModel.getObservableIsDarkModeActivated().observe(fragmentLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d("bishal_debug", "onChanged: called");
                Log.d("bishal_debug", "onChanged: " + isDarkModeActivated);
                isDarkModeActivated = aBoolean;
                Log.d("bishal_debug", "onChanged: " + isDarkModeActivated);
                toggleMode();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("akash_lyrics_debug", "onSaveInstanceState: ");
        outState.putParcelable("selectedTab",selectedTab);
        outState.putParcelable("selectedSong",selectedSong);
        outState.putBoolean("adsFragmentSetup",adsFragmentSetup);
    }

    private void initializeClicks() {
        binding.modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainViewModel.setIsDarkModeActivated(isChecked);
            }
        });


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });


        /*** Download section **/
        binding.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Download clicked", Toast.LENGTH_SHORT).show();
                downloadSongData();
            }
        });
    }
    private void setupMenuSelector() {
        final ArrayList<SelectionType> selectionTypeArrayList = new ArrayList<>();
        binding.menuSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewGroup root = (ViewGroup) requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                String[] limits = new String[]{"Guitar Chord", "Lyrics"};
                final View view = new View(requireContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
                view.setBackgroundColor(Color.TRANSPARENT);

                root.addView(view);
                float toolbarheight = binding.toolbar.getHeight();
                view.setX(0);
                view.setY(toolbarheight);
                Context wrapper = new ContextThemeWrapper(requireContext(), R.style.PopupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, view, Gravity.CENTER);

                if(selectedSong != null){
                    Map<String, String> map = selectedSong.getSong_data();
                    Log.d("sohan_debug","map_size:"+String.valueOf(map.size()));
                    int i = 0;
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        // here entry.getKey() returns like 'guitar_chord'
                        // SelectionType.displaySelectionNameMap.get(entry.getKey()) returns Guitar Chord
                        if(SelectionType.displaySelectionNameMap.containsKey(entry.getKey()) && !entry.getKey().equals("lyrics"))
                        {
                            popupMenu.getMenu().add(1,i,i,SelectionType.displaySelectionNameMap.get(entry.getKey()));  //here assigning i as temporary item id
                            selectionTypeArrayList.add(new SelectionType(entry.getKey(), SelectionType.displaySelectionNameMap.get(entry.getKey()),String.valueOf(entry.getValue())));
                            i++;
                        }
                        else
                        {
                            Log.d("sohan_debug","key not found");
                            //TODO need to handle key not found
                        }
                    }

                }else{
                    Log.d("tab_debug", "getData: no data found");
                }
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
                {
                    @Override
                    public void onDismiss(PopupMenu menu)
                    {
                        root.removeView(view);
                    }
                });

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO if you have new selection type you should add here logic for that
                        Log.d("sohan_debug", (String) item.getTitle());
                        if(!ConnectionManager.isOnline(requireContext())){
                            Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        SelectionType selectionType = selectionTypeArrayList.get(item.getItemId());
                        if(((String) item.getTitle()).equals(SelectionType.displaySelectionNameMap.get("guitar_chord")))
                        {
                            mainViewModel.setSelectedTab(selectionType);
                            mainViewModel.setNavigation(NavigatorTags.CHORD_DISPLAY_FRAGMENT,1);
                        }
                        return true;
                    }
                });
            }
        });

    }


    private void updateView() {
        if(selectedSong != null){
            binding.tvSongName.setText(selectedSong.getSong_name());
            binding.tvBandName.setText(selectedSong.getArtist_name());
            binding.tvGenre.setText("Genre: "+ selectedSong.getGenre());
        }

        if(lyricsData != null){
            String data = lyricsData.getData();
            data = data.replace("\\n","\n");
            binding.tvSongLyrics.setText(data);
        }


    }


    private void activateLightMode(){
        binding.rootLayout.setBackgroundColor(getResources().getColor(R.color.white));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongLyrics.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvLyrics.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvGenre.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.modeAnimationView.setText(Constants.LIGHT_MODE);
        binding.modeAnimationView.setTextColor(Color.parseColor("#22374C"));
        ObjectAnimator animation = ObjectAnimator.ofFloat(binding.modeAnimationView, View.ALPHA, 0f,1f,0f);
        ObjectAnimator zoomAnimationX = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_X, 0.5f,1f);
        ObjectAnimator zoomAnimationY = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_Y, 0.5f,1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250);
        animatorSet.playTogether(animation,zoomAnimationX,zoomAnimationY);
        animatorSet.start();
    }

    private void activateDarkMode(){
        binding.rootLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.white));
        binding.tvSongLyrics.setTextColor(getResources().getColor(R.color.white));
        binding.tvLyrics.setTextColor(getResources().getColor(R.color.white));
        binding.tvGenre.setTextColor(getResources().getColor(R.color.white));
        binding.modeAnimationView.setText(Constants.DARK_MODE);
        binding.modeAnimationView.setTextColor(Color.WHITE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(binding.modeAnimationView, View.ALPHA, 0f,1f,0f);
        ObjectAnimator zoomAnimationX = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_X, 0.5f,1f);
        ObjectAnimator zoomAnimationY = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_Y, 0.5f,1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250);
        animatorSet.playTogether(animation,zoomAnimationX,zoomAnimationY);
        animatorSet.start();
    }

    private void toggleMode() {

        if (!isDarkModeActivated){
            activateLightMode();
        }
        else {
            activateDarkMode();
        }
    }

    private void downloadSongData() {
        mainViewModel.roomInsertSongData(new SongDataEntity(lyricsData.getId(),lyricsData.getData(),lyricsData.getKey(),lyricsData.getTuning(),lyricsData.getData_type()));
    }

}