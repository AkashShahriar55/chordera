package com.cookietech.chordera.chordDisplay;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.Util.NativeAdsFragment;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.appcomponents.RemoteConfigManager;
import com.cookietech.chordera.appcomponents.ViewsManager;
import com.cookietech.chordera.chordDisplay.chordDetails.ChordDetailsDialogFragment;
import com.cookietech.chordera.databinding.FragmentChordDisplayBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;

import com.cookietech.chordlibrary.ChordClass;
import com.cookietech.chordlibrary.Variation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChordDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChordDisplayFragment extends ChorderaFragment implements ChordsDisplayAdapter.Communicator {


    FragmentChordDisplayBinding binding;
    private  ChordsDisplayAdapter chordsDisplayAdapter;
    ArrayList<Variation> chords =new ArrayList<>();
    private boolean isDarkModeActivated = false;
    private int lastSelectedTranspose;
    private SongsPOJO selectedSong;
    private SelectionType selectedTab;
    private TabPOJO tabData;
    private ImageView auto_scroll_btn;
    private ImageView play_youtube_btn;
    private TabulatorGenerator tabulatorGenerator = new TabulatorGenerator();
    private double autoScrollSpeed = 1;
    private ArrayList<ChordClass> initialChordList;
    private BottomSheetBehavior<View> behavior ;



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
            /** get argument from bundle**/

        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChordDisplayBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lastSelectedTranspose = 0;
        binding.displayScrollView.setVisibility(View.GONE);
        setUpViews();
        initializeObserver();
        initializeAutoScrollSpeedUi();
        setupMenuSelector();


        if(RemoteConfigManager.shouldShowChordDisplayNativeAds())
            setUpNativeAdFragment();



        binding.modeSwitchIv.setOnClickListener(v -> {
            if (isDarkModeActivated){
                mainViewModel.setIsDarkModeActivated(false);
            }
            else {
                mainViewModel.setIsDarkModeActivated(true);
            }

        });

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChordDisplaySettingModal moreFragmentDialog = ChordDisplaySettingModal.newInstance();
                moreFragmentDialog.setCallback(new ChordDisplaySettingModal.MoreCallback() {

                    @Override
                    public void onTransposeSelected() {
                        ChordDisplayTransposeModal transposeModalDialog = ChordDisplayTransposeModal.newInstance(tabData.getKey(), lastSelectedTranspose);
                        transposeModalDialog.setCallback(new ChordDisplayTransposeModal.TransposeCallback() {
                            @Override
                            public void onTranspose(int transpose) {
                                Log.d("transpose_debug", "onTranspose: " + transpose);
                                lastSelectedTranspose = transpose;
                                binding.tvSongChords.setTranspose(tabData.getData(), transpose);
                                mainViewModel.transposeChords(initialChordList, transpose);
                                mainViewModel.setTransposeValue(transpose);
                            }
                        });
                        transposeModalDialog.show(requireFragmentManager(), "transpose_dialog");
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
                        mainViewModel.setNavigation(NavigatorTags.LANDING_FRAGMENT, NavigatorTags.CONTAINER_ID_DEFAULT);
                    }
                });
                moreFragmentDialog.show(requireFragmentManager(), "more_dialog");
            }
        });

        behavior = BottomSheetBehavior.from((View) binding.bottomSheet.chordDisplayBottomSheet);



        binding.displayScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> behavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        /**Go to Auto Scroll fragment**/
        auto_scroll_btn = binding.rootLayout.findViewById(R.id.auto_scroll_btn);
        play_youtube_btn = binding.rootLayout.findViewById(R.id.play_youtube_btn);

        auto_scroll_btn.setOnClickListener(v -> {
            //Toast.makeText(requireContext(), "Hey", Toast.LENGTH_SHORT).show();
            Log.d("test_debug", "onClick: " + autoScrollSpeed);
            Bundle bundle = new Bundle();
            bundle.putDouble("auto_scroll_speed", autoScrollSpeed);
            mainViewModel.setNavigation(NavigatorTags.CHORD_DISPLAY_FULLSCREEN_FRAGMENT,1, bundle);
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        play_youtube_btn.setOnClickListener(v -> {
            //Log.d("youtube", "onClick: " + selectedSong.getYoutube_id());
            watchYoutubeVideo(requireContext(),selectedSong.getYoutube_id());
        });

        /**Download Section**/
        binding.downloadBtn.setOnClickListener(v -> {
            //Toast.makeText(requireContext(), "Hey Baby", Toast.LENGTH_SHORT).show();
            downloadSongData();
        });

        ChordTouchListener chordTouchListener = new ChordTouchListener(new ChordTouchListener.chordSelectionListener() {
            @Override
            public void onChordSelected(ChordClass chordClass) {
                ChordDetailsDialogFragment.newInstance(chordClass).show(requireFragmentManager(),"chord_selection");
            }
        });
        binding.tvSongChords.setOnTouchListener(chordTouchListener);



    }

    @Override
    public void onStart() {
        super.onStart();
        if (ViewsManager.ViewedSongIds.get(selectedSong.getId()) == null){
            Log.d("views_debug", "onStart: not contain");
            mainViewModel.updateSongViews(selectedSong.getId(),selectedSong.getViews() + 1);

        }
        else{
            Log.d("views_debug", "onStart: contain");
        }
    }

    private void setUpNativeAdFragment() {
        FragmentTransaction transaction =   getChildFragmentManager().beginTransaction();
        Fragment adFragment = NativeAdsFragment.newInstance();
        transaction.add(binding.chordNativeAdHolder.getId(), adFragment);
        transaction.commitAllowingStateLoss();
    }
    private void setUpViews() {
        isDarkModeActivated = mainViewModel.getObservableIsDarkModeActivated().getValue();
        toggleMode();
    }

    private void initializeAutoScrollSpeedUi() {
        String speed = String.format("%.1f", autoScrollSpeed);
        binding.bottomSheet.autoscrollSpeedTv.setText("Speed "+speed+"x");
        binding.bottomSheet.autoscrollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                autoScrollSpeed = (progress / 100f) + 0.5;
                String speed = String.format("%.1f", autoScrollSpeed);
                binding.bottomSheet.autoscrollSpeedTv.setText("Speed " + speed + "x");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupMenuSelector() {
        final ArrayList<SelectionType> selectionTypeArrayList = new ArrayList<>();
        binding.menuSelector.setOnClickListener(v -> {

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
                    if(SelectionType.displaySelectionNameMap.containsKey(entry.getKey()) && !entry.getKey().equals("guitar_chord"))
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

            popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) item -> {
                //TODO if you have new selection type you should add here logic for that
                Log.d("sohan_debug", (String) item.getTitle());
                SelectionType selectionType = selectionTypeArrayList.get(item.getItemId());
                if(((String) item.getTitle()).equals(SelectionType.displaySelectionNameMap.get("lyrics")))
                {
                    mainViewModel.setSelectedTab(selectionType);
                    mainViewModel.setNavigation(NavigatorTags.SONG_DETAIL_FRAGMENT,1);
                }
                return true;
            });
        });

    }

    private void downloadSongData() {
        mainViewModel.roomInsertSongData(new SongDataEntity(tabData.getId(),tabData.getData(),tabData.getKey(),tabData.getTuning(),tabData.getData_type()));
    }

    private void initializeChordsRecyclerView() {
        chordsDisplayAdapter = new ChordsDisplayAdapter(requireContext(),new ArrayList<ChordClass>(),ChordDisplayFragment.this,binding.rvChords);
        binding.rvChords.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.rvChords.setHasFixedSize(true);
                GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),5);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                binding.rvChords.setLayoutManager(layoutManager);
                binding.rvChords.setAdapter(chordsDisplayAdapter);
                binding.rvChords.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void initializeObserver() {
        selectedSong = mainViewModel.getObservableSelectedSong().getValue();
        mainViewModel.getObservableSelectedSong().observe(fragmentLifecycleOwner, songsPOJO -> {
            Log.d("tab_debug", "onChanged: " + songsPOJO.getSong_name());
            selectedSong = songsPOJO;
            updateView();
        });

        mainViewModel.getObservableSelectedTab().observe(fragmentLifecycleOwner, selectionType -> {
            selectedTab = selectionType;
            mainViewModel.loadTab(selectedTab);

        });

       mainViewModel.getObservableSelectedTabLiveData().observe(fragmentLifecycleOwner, tabPOJO -> {
            tabData = tabPOJO;
            mainViewModel.decodeChordsFromData(tabPOJO.getData());
            updateView();
       });


        Log.d("from_debug", "initializeObserver: " + mainViewModel.getObservableSongListShowingCalledFrom().getValue());
        mainViewModel.getObservableSongListShowingCalledFrom().observe(fragmentLifecycleOwner, fromWhere -> {
            if(fromWhere.equalsIgnoreCase(Constants.FROM_OFFLINE)){
                binding.downloadBtn.setVisibility(View.GONE);
            }else{
                binding.downloadBtn.setVisibility(View.VISIBLE);
            }
        });

        /** Observer to get downloadSongDataResponse**/
       mainViewModel.getObservableDownloadSongDataResponse().observe(fragmentLifecycleOwner, databaseResponse -> {
           switch (databaseResponse.getResponse()){
               case Storing:
                   Log.d("download_debug", "onChanged: song data storing");
                   binding.downloadBtn.setVisibility(View.INVISIBLE);
                   binding.downloadProgress.setVisibility(View.VISIBLE);
                    break;
               case Stored:
                   Log.d("download_debug", "onChanged: song data stored");
                   Map<String,String> songDataMap = new HashMap<>();
                   songDataMap.put(tabData.getData_type(),tabData.getId());
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

       });

       mainViewModel.getObservableDownloadSongResponse().observe(fragmentLifecycleOwner, databaseResponse -> {
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
       });

        mainViewModel.getObservableRoomFetchedSongResponse().observe(fragmentLifecycleOwner, databaseResponse -> {
            switch (databaseResponse.getResponse()){
                case Fetching:
                    Log.d("download_debug", "onChanged: Song Fetching For update");
                    break;
                case Fetched:
                    Log.d("download_debug", "onChanged: song Fetched to update");
                    SongsEntity fetchedSong = mainViewModel.getObservableRoomFetchedSong().getValue();
                    Map<String,String> fetchedSongData = fetchedSong.getSong_data();
                    fetchedSongData.put(tabData.getData_type(),tabData.getId());
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
        });

        mainViewModel.getObservableRoomUpdateSongResponse().observe(fragmentLifecycleOwner, databaseResponse -> {
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
        });


       mainViewModel.getObservableTabDataResponse().observe(fragmentLifecycleOwner, databaseResponse -> {
           switch (databaseResponse.getResponse()){
               case Invalid_data:
                   Log.d("callback_debug", "onChanged: Invalid_data");
                   break;
               case No_internet:
                   Log.d("callback_debug", "onChanged: No_internet");
                   break;
               case Fetching:
                   Log.d("callback_debug", "onChanged: Fetching");
                   break;
               case Fetched:
                   Log.d("callback_debug", "onChanged: Fetched");
                   break;
               case Error:
                   Log.d("callback_debug", "onChanged: Error");
                   break;
               default:
                   break;
           }
       });

       mainViewModel.getObservableTabDisplayChords().observe(fragmentLifecycleOwner, chordClasses -> {
           initialChordList = chordClasses;
           mainViewModel.transposeChords(initialChordList,0);
       });


       mainViewModel.getObservableTransposedTabDisplayChords().observe(fragmentLifecycleOwner, chordClassArrayList -> chordsDisplayAdapter.setChords(chordClassArrayList));

       mainViewModel.getObservableIsDarkModeActivated().observe(fragmentLifecycleOwner, aBoolean -> {
           Log.d("bishal_debug", "onChanged: called");
           Log.d("bishal_debug", "onChanged: " + isDarkModeActivated);
           isDarkModeActivated = aBoolean;
           Log.d("bishal_debug", "onChanged: " + isDarkModeActivated);
           toggleMode();
       });

       /** Observer For Update Views**/
        mainViewModel.getObservableUpdateViewsResponse().observe(fragmentLifecycleOwner, databaseResponse -> {

            switch (databaseResponse.getResponse()){
                case Updated:
                    Log.d("views_debug", "onChanged: Views updated");
                    ViewsManager.ViewedSongIds.put(selectedSong.getId(),true);
                    break;
                case Error:
                    Log.d("views_debug", "onChanged: Views Update Error");
                    break;
                default:
                    break;

            }

        });

    }

    private void updateView() {
        if(selectedSong != null){
            binding.tvSongName.setText(selectedSong.getSong_name());
            binding.tvBandName.setText(selectedSong.getArtist_name());
            binding.tvGenre.setText("Genre: "+ selectedSong.getGenre());
        }

        if(tabData != null){
            binding.tvTuning.setText("Tuning: "+ tabData.getTuning());
            binding.tvKey.setText("Key: "+ tabData.getKey());
            //binding.tvSongChords.setText(tabData.getData());
        }

/*        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String lyricWithChord = "[Dm]এই অ[Am]বেলাই [Gm]তোমারি আকা[Bdim]শে  নিরব[Gaug] আপোসে ভেসে [G]যাই[Tab]";
        //Rect bounds = new Rect();
        //TextPaint textPaint = binding.tvChords.getPaint();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager  = requireActivity().getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int marginPadding = 50;
        int rootWidth = displayMetrics.widthPixels - marginPadding;
        ChordFormater chordFormater = new ChordFormater(tabData.getData(),rootWidth);
        //chordFormater.processChord(0)
        spannableStringBuilder = chordFormater.getProcessedChord(0);*/
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        },2000);
        binding.chordsLoader.setVisibility(View.GONE);
        binding.displayScrollView.setVisibility(View.VISIBLE);
        initializeChordsRecyclerView();
        if (tabData != null) {
/*            binding.tvSongChords.setFormattedText("{Intro}\n" +
                    "([Em],[C],[Am],[D]) " +
                    "(x4)\n" +
                    "{Verse 1}\n" +
                    "[Em]   Nona [C]Shopne Gora[Am]a To[D]mar Swmriti\n");*/
            binding.tvSongChords.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(binding.tvSongChords.getWidth()>0){
                        Log.d("akash_chords_debug", "onGlobalLayout: ");
                        binding.tvSongChords.setFormattedText(tabData.getData());
                        binding.tvSongChords.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                    }

                }
            });

        }
    }



    @Override
    public void onChordSelected(ChordClass chordClass) {
        ChordDetailsDialogFragment.newInstance(chordClass).show(requireFragmentManager(),"chord_selection");
    }

    private void activateLightMode(){
        binding.modeSwitchIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.switch_light,null));
        binding.rootLayout.setBackgroundColor(getResources().getColor(R.color.white));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvTuning.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvKey.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvChords.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongChords.setMode(TabulatorTextView.Mode.Light);
        binding.tvGenre.setTextColor(getResources().getColor(R.color.colorPrimary));
        binding.tvCapo.setTextColor(getResources().getColor(R.color.colorPrimary));
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
        binding.modeSwitchIv.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.switch_dark,null));
        binding.rootLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        binding.tvSongName.setTextColor(getResources().getColor(R.color.white));
        binding.tvTuning.setTextColor(getResources().getColor(R.color.white));
        binding.tvKey.setTextColor(getResources().getColor(R.color.white));
        binding.tvChords.setTextColor(getResources().getColor(R.color.white));
        binding.tvSongChords.setMode(TabulatorTextView.Mode.Dark);
        binding.tvGenre.setTextColor(getResources().getColor(R.color.white));
        binding.tvCapo.setTextColor(getResources().getColor(R.color.white));
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
        Log.d("bishal_bedug", "toggleMode: called");

        if (!isDarkModeActivated){
            activateLightMode();
        }
        else {
            activateDarkMode();
        }
    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}