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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Util.NativeAdsFragment;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.appcomponents.RemoteConfigManager;
import com.cookietech.chordera.databinding.FragmentSongLyricsBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;

import java.util.ArrayList;
import java.util.Map;

public class SongLyricsFragment extends ChorderaFragment {

    private SongsPOJO selectedSong;
    private boolean isDarkModeActivated = false;
    private SelectionType selectedTab;
    private TabPOJO lyricsData;

    public SongLyricsFragment(){};

    FragmentSongLyricsBinding binding;
    public static SongLyricsFragment newInstance(){return new SongLyricsFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        initialize();
        initializeObserver();
        setupMenuSelector();
        if(RemoteConfigManager.shouldShowChordDisplayNativeAds())
            setUpNativeAdFragment();
    }

    private void setUpNativeAdFragment() {
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        Fragment adFragment = NativeAdsFragment.newInstance();
        transaction.add(binding.nativeAdContainer.getId(),adFragment);
        transaction.commitAllowingStateLoss();
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
            }
        });
    }

    private void initialize() {
        binding.modeSwitch.setChecked(isDarkModeActivated);
        binding.modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isDarkModeActivated = isChecked;
                toggleMode();

                if(isDarkModeActivated){
                    binding.modeAnimationView.setText("Dark");
                    binding.modeAnimationView.setTextColor(Color.WHITE);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(binding.modeAnimationView, View.ALPHA, 0f,1f,0f);
                    ObjectAnimator zoomAnimationX = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_X, 0.5f,1f);
                    ObjectAnimator zoomAnimationY = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_Y, 0.5f,1f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(250);
                    animatorSet.playTogether(animation,zoomAnimationX,zoomAnimationY);
                    animatorSet.start();

                }else{
                    binding.modeAnimationView.setText("Light");
                    binding.modeAnimationView.setTextColor(Color.parseColor("#22374C"));
                    ObjectAnimator animation = ObjectAnimator.ofFloat(binding.modeAnimationView, View.ALPHA, 0f,1f,0f);
                    ObjectAnimator zoomAnimationX = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_X, 0.5f,1f);
                    ObjectAnimator zoomAnimationY = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_Y, 0.5f,1f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(250);
                    animatorSet.playTogether(animation,zoomAnimationX,zoomAnimationY);
                    animatorSet.start();
                }

            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
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
    }

    private void activateDarkMode(){
        binding.rootLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.white));
        binding.tvSongLyrics.setTextColor(getResources().getColor(R.color.white));
        binding.tvLyrics.setTextColor(getResources().getColor(R.color.white));
        binding.tvGenre.setTextColor(getResources().getColor(R.color.white));
    }

    private void toggleMode() {

        if (!isDarkModeActivated){
            activateLightMode();
        }
        else {
            activateDarkMode();
        }
    }

}