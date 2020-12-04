package com.cookietech.chordera.chordDisplay;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cookietech.chordera.R;
import com.cookietech.chordera.databinding.FragmentChordDisplayBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordlibrary.Chord;
import com.cookietech.chordlibrary.ChordsAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordDisplayFragment extends ChorderaFragment implements ChordsAdapter.Communicator {


    FragmentChordDisplayBinding binding;
    private  ChordsAdapter chordsAdapter;
    ArrayList<Chord> chords =new ArrayList<>();
    private boolean isDarkModeActivated = false;
    private int lastSelectedTranspose;
    private SongsPOJO selectedSong;
    private SelectionType selectedTab;
    private TabPOJO tabData;
    private ImageView auto_scroll_btn;

    public ChordDisplayFragment() {
        // Required empty public constructor
    }


    public static ChordDisplayFragment newInstance() {
        ChordDisplayFragment fragment = new ChordDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordDisplayBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDummyChords();
        initializeObserver();
        binding.rvChords.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),5);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvChords.setLayoutManager(layoutManager);
        chordsAdapter = new ChordsAdapter(requireContext(),chords,this,binding.rvChords);
        binding.rvChords.setAdapter(chordsAdapter);

        toggleMode();
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
                    animatorSet.setDuration(200);
                    animatorSet.playTogether(animation,zoomAnimationX,zoomAnimationY);
                    animatorSet.start();

                }else{
                    binding.modeAnimationView.setText("Light");
                    binding.modeAnimationView.setTextColor(Color.parseColor("#22374C"));
                    ObjectAnimator animation = ObjectAnimator.ofFloat(binding.modeAnimationView, View.ALPHA, 0f,1f,0f);
                    ObjectAnimator zoomAnimationX = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_X, 0.5f,1f);
                    ObjectAnimator zoomAnimationY = ObjectAnimator.ofFloat(binding.modeAnimationView, View.SCALE_Y, 0.5f,1f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(200);
                    animatorSet.playTogether(animation,zoomAnimationX,zoomAnimationY);
                    animatorSet.start();
                }

            }
        });

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChordDisplaySettingModal moreFragmentDialog = ChordDisplaySettingModal.newInstance(lastSelectedTranspose);
                moreFragmentDialog.setCallback(new ChordDisplaySettingModal.MoreCallback() {
                    @Override
                    public void onTranspose(int transpose) {
                        lastSelectedTranspose = transpose;
                    }

                    @Override
                    public void onPrintSelected() {
                        Log.d("more_debug", "onPrintSelected: ");
                    }

                    @Override
                    public void onShareSelected() {
                        Log.d("more_debug", "onShareSelected: ");
                    }

                    @Override
                    public void onSettingSelected() {
                        Log.d("more_debug", "onSettingSelected: ");
                    }

                    @Override
                    public void onBackToHomeSelected() {
                        Log.d("more_debug", "onBackToHomeSelected: ");
                    }
                });
                moreFragmentDialog.show(requireFragmentManager(),"dialog");
            }
        });

        View bottomSheet = binding.rootLayout.findViewById(R.id.chord_display_bottom_sheet);
        final BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);



        binding.displayScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        /**Go to Auto Scroll fragment**/
        auto_scroll_btn = binding.rootLayout.findViewById(R.id.auto_scroll_btn);

        auto_scroll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Hey", Toast.LENGTH_SHORT).show();
            }
        });

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
           public void onChanged(TabPOJO tabPOJO) {
                tabData = tabPOJO;
                updateView();
           }
       });

    }

    private void updateView() {
        if(selectedSong != null){
            binding.tvSongName.setText(selectedSong.getSong_name());
            binding.tvBandName.setText(selectedSong.getArtist_name());
        }

        if(tabData != null){
            binding.tvTuning.setText("Tuning: "+ tabData.getTuning());
            binding.tvKey.setText("Key: "+ tabData.getKey());
            binding.tvGenre.setText("Genre: "+ tabData.getGenre());
            binding.tvSongChords.setText(tabData.getData());
        }
    }

    private void setDummyChords(){
        chords.clear();
        chords.add(new Chord("A maj", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(2);
                add(0);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(4);
                add(0);
            }
        }));

        chords.add(new Chord("A min", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(1);
                add(0);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(1);
                add(0);
            }
        }));

        chords.add(new Chord("A5", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(-1);
                add(-1);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(0);
                add(0);
            }
        }));

        chords.add(new Chord("A maj", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(2);
                add(0);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(4);
                add(0);
            }
        }));

        chords.add(new Chord("A min", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(1);
                add(0);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(1);
                add(0);
            }
        }));

        chords.add(new Chord("A5", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(-1);
                add(-1);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(0);
                add(0);
            }
        }));

        chords.add(new Chord("A min", new ArrayList<Integer>(){
            {
                add(-1);
                add(0);
                add(2);
                add(2);
                add(1);
                add(0);
            }
        },new ArrayList<Integer>(){
            {
                add(0);
                add(0);
                add(2);
                add(3);
                add(1);
                add(0);
            }
        }));
    }

    @Override
    public void onChordSelected(int position) {

    }

    private void activateLightMode(){
        binding.rootContainer.setBackgroundColor(getResources().getColor(R.color.white));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvTuning.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvKey.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvChords.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongChords.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void activateDarkMode(){
        binding.rootContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.white));
        binding.tvTuning.setTextColor(getResources().getColor(R.color.white));
        binding.tvKey.setTextColor(getResources().getColor(R.color.white));
        binding.tvChords.setTextColor(getResources().getColor(R.color.white));
        binding.tvSongChords.setTextColor(getResources().getColor(R.color.white));
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