package com.cookietech.chordera.Landing;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookietech.chordera.SearchSuggestion.SearchSongCommunicator;
import com.cookietech.chordera.SearchSuggestion.SearchSuggestionFragment;
import com.cookietech.chordera.Util.ViewUtils;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentLandingBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.CollectionsPOJO;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandingFragment extends ChorderaFragment {

    private FragmentLandingBinding binding;
    private NewItemAdapter newItemAdapter;
    private CollectionItemAdapter collectionItemAdapter;
    private boolean isCollectionLoaded = false;
    private boolean isNewLoaded = false;

    public LandingFragment() {
        // Required empty public constructor
    }


    public static LandingFragment newInstance() {
        return new LandingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLandingBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_ONLINE);
        initializeViews();
        adJustViews();
        initializeClickEvents();
        fetchDataAndObserve();
        mainViewModel.bindSearch(binding.edtSearchBox);
    }




    private void fetchDataAndObserve() {
        fetchCollectionDataAndObserve();
        fetchNewDataAndObserve();

        ConnectionManager.getObservableNetworkAvailability().observe(fragmentLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean && !isCollectionLoaded){
                    fetchCollectionDataAndObserve();
                }

                if(!aBoolean && !isNewLoaded){
                    fetchNewDataAndObserve();
                }
            }
        });
    }

    Observer< DatabaseResponse > newDataResponseObserver= new Observer<DatabaseResponse>() {
        @Override
        public void onChanged(DatabaseResponse databaseResponse) {
            DatabaseResponse.Response response = databaseResponse.getResponse();
            switch (response){
                case Error:
                case No_internet:
                case Invalid_data:
                    isNewLoaded = false;
                    binding.newErrorMessage.constraintLayout.setVisibility(View.VISIBLE);
                    break;
                case Fetched:
                    isNewLoaded = true;
                    binding.newErrorMessage.constraintLayout.setVisibility(View.GONE);
                    break;
                case Fetching:
                    binding.newErrorMessage.constraintLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
    Observer< ArrayList<SongsPOJO> > newDataObserver= new Observer<ArrayList<SongsPOJO>>() {
        @Override
        public void onChanged(ArrayList<SongsPOJO> songsPOJOS) {
            newItemAdapter.setNewSongsData(songsPOJOS);
        }
    };
    private void fetchNewDataAndObserve() {
        mainViewModel.fetchNewSongsData().observe(fragmentLifecycleOwner, newDataResponseObserver);
        mainViewModel.getNewSongsData().observe(fragmentLifecycleOwner, newDataObserver);
    }


    Observer< DatabaseResponse > collectionDataResponseObserver = new Observer<DatabaseResponse>() {
        @Override
        public void onChanged(DatabaseResponse databaseResponse) {
            DatabaseResponse.Response response = databaseResponse.getResponse();
            Log.d("collection_debug", "fetchCollectionsData: "+ response);
            switch (response){
                case Error:
                case No_internet:
                case Invalid_data:
                    binding.collectionErrorMessage.constraintLayout.setVisibility(View.VISIBLE);
                    isCollectionLoaded = false;
                    break;
                case Fetched:
                    isCollectionLoaded = true;
                    binding.collectionErrorMessage.constraintLayout.setVisibility(View.GONE);
                    break;
                case Fetching:
                    binding.collectionErrorMessage.constraintLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    Observer< ArrayList<CollectionsPOJO> > collectionDataObserver = new Observer<ArrayList<CollectionsPOJO>>() {
        @Override
        public void onChanged(ArrayList<CollectionsPOJO> collections) {
            Log.d("collection_debug", "onChanged: "+ collections.size());
            collectionItemAdapter.setCollections(collections);
        }
    };

    private void fetchCollectionDataAndObserve() {
        mainViewModel.fetchCollectionsData().observe(fragmentLifecycleOwner, collectionDataResponseObserver);
        mainViewModel.getObservableCollectionsData().observe(fragmentLifecycleOwner, collectionDataObserver);
    }

    private void initializeClickEvents() {
        binding.newExploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ConnectionManager.isOnline(requireContext())){
                    Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                mainViewModel.setSongListShowingCalledFrom(Constants.FROM_ONLINE);
                mainViewModel.setNavigation(NavigatorTags.NEW_EXPLORE_LIST_FRAGMENT);
            }
        });

        binding.collectionExploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ConnectionManager.isOnline(requireContext())){
                    Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                mainViewModel.setSongListShowingCalledFrom(Constants.FROM_ONLINE);
                mainViewModel.setNavigation(NavigatorTags.COLLECTION_EXPLORE_LIST_FRAGMENT);
            }
        });

        binding.cvChordlibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("akash_debug", "onClick: ");

                mainViewModel.setNavigation(NavigatorTags.CHORD_LIBRARY_FRAGMENT,((ViewGroup)getView().getParent()).getId());
            }
        });

        binding.cvMetronomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("akash_debug", "onClick: ");
                mainViewModel.setNavigation(NavigatorTags.METRONOME_FRAGMENT,((ViewGroup)getView().getParent()).getId());
            }
        });

       binding.edtSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {

               if(hasFocus){
                   if(!ConnectionManager.isOnline(requireContext())){
                       Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                       binding.edtSearchBox.clearFocus();
                       return;
                   }
                   Log.d("flow_debug", "onSearchedSongSelected: ");
                   binding.ivCancelSearchButton.setVisibility(View.VISIBLE);
                   SearchSongCommunicator songCommunicator = new SearchSongCommunicator() {
                       @Override
                       public void onSearchedSongSelected() {
                           Log.d("flow_debug", "onSearchedSongSelected: ");
                           ViewUtils.hideKeyboardFrom(requireContext(),binding.edtSearchBox);
                       }

                       @Override
                       public void onBackButtonClicked() {
                           Log.d("flow_debug", "onBackButtonClicked: ");
                           binding.edtSearchBox.setText("");
                           binding.edtSearchBox.clearFocus();
                           binding.ivCancelSearchButton.setVisibility(View.GONE);
                           ViewUtils.hideKeyboardFrom(requireContext(),binding.edtSearchBox);
                       }
                   };
                   mainViewModel.setNavigation(NavigatorTags.SEARCH_VIEW_FRAGMENT,binding.searchFragmentContainer.getId(),SearchSuggestionFragment.createBundle(songCommunicator));
               }else{
                   binding.ivCancelSearchButton.setVisibility(View.GONE);
               }
           }
       });
       



       binding.ivCancelSearchButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("flow_debug", "ivCancelSearchButton: ");
               binding.edtSearchBox.setText("");
               binding.edtSearchBox.clearFocus();
               binding.ivCancelSearchButton.setVisibility(View.GONE);
               ViewUtils.hideKeyboardFrom(requireContext(),binding.edtSearchBox);
               mainViewModel.setNavigation(NavigatorTags.LANDING_FRAGMENT,binding.searchFragmentContainer.getId() );
           }
       });


        binding.edtSearchBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i("akash_debug","Enter pressed");
                    ViewUtils.hideKeyboardFrom(requireContext(),binding.edtSearchBox);
                    //next version
//                    mainViewModel.SaveSearchKeyWordHistory(binding.edtSearchBox.getText().toString());
//                    binding.edtSearchBox.setText("");
//                    mainViewModel.setNavigation(NavigatorTags.SEARCH_RESULT_FRAGMENT,1);
                }
                return false;
            }
        });

        binding.cvTop10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sohan debug","top ten pressed");
                if(!ConnectionManager.isOnline(requireContext())){
                    Toast.makeText(requireContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                mainViewModel.setNavigation(NavigatorTags.TOP_SONG_LIST_FRAGMENT,((ViewGroup)getView().getParent()).getId());
            }
        });
        binding.cvSavedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sohan_debug", "saved button pressed");
                mainViewModel.setNavigation(NavigatorTags.SAVED_SONG_LIST_FRAGMENT,((ViewGroup)getView().getParent()).getId());
            }
        });

        binding.ivChorderaIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Get Search Result", Toast.LENGTH_SHORT).show(
            }
        });


    }


    private void initializeViews() {
        initializeNewRecyclerView();
        initializeCollectionRecyclerView();

//        mainViewModel.bindSearchBox(binding.edtSearchBox);
    }

    private void initializeCollectionRecyclerView() {
        collectionItemAdapter = new CollectionItemAdapter(requireContext(),binding.rvCollectionItems,mainViewModel);
        binding.rvCollectionItems.setHasFixedSize(true);
        binding.rvCollectionItems.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvCollectionItems.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.rvCollectionItems.setAdapter(collectionItemAdapter);
                binding.rvCollectionItems.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvCollectionItems, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
    }

    private void initializeNewRecyclerView() {
        newItemAdapter = new NewItemAdapter(requireContext(),binding.rvNewItems,mainViewModel);
        binding.rvNewItems.setHasFixedSize(true);
        binding.rvNewItems.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvNewItems.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.rvNewItems.setAdapter(newItemAdapter);
                binding.rvNewItems.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        OverScrollDecoratorHelper.setUpOverScroll(binding.rvNewItems, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);



    }



    private void adJustViews() {

        binding.clBottomLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("akash_debug", "onGlobalLayout: " + binding.clBottomLayout.getHeight() + " " + binding.clBottomLayout.getWidth());

                int size = (int) Math.min(binding.clBottomLayout.getWidth()/3,binding.clBottomLayout.getHeight()/3) ;

                Log.d("akash_debug", "onGlobalLayout: "+ size);

                int margin = (int) (size * 0.125);


                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.cvTop10Button.getLayoutParams();
                params.height = size;
                params.width = size;
                params.rightMargin = margin;
                params.bottomMargin = margin;
                binding.cvTop10Button.setLayoutParams(params);

                params = (ConstraintLayout.LayoutParams) binding.cvSavedButton.getLayoutParams();
                params.height = size;
                params.width = size;
                params.leftMargin = margin;
                params.bottomMargin = margin;
                binding.cvSavedButton.setLayoutParams(params);

                params = (ConstraintLayout.LayoutParams) binding.cvMetronomeButton.getLayoutParams();
                params.height = size;
                params.width = size;
                params.rightMargin = margin;
                params.topMargin = margin;
                binding.cvMetronomeButton.setLayoutParams(params);

                params = (ConstraintLayout.LayoutParams) binding.cvChordlibraryButton.getLayoutParams();
                params.height = size;
                params.width = size;
                params.leftMargin = margin;
                params.topMargin = margin;
                binding.cvChordlibraryButton.setLayoutParams(params);

                binding.clBottomLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("akash_debug", "onResume: ");
    }



    @Override
    public boolean onBackPressed(Navigator topNavigation) {
        boolean handled = false;
        Log.d("akash_debug", "onBackPressed: ");
        if(topNavigation.getNavigatorTag().equalsIgnoreCase(NavigatorTags.SEARCH_VIEW_FRAGMENT)){
            //binding.ivCancelSearchButton.setVisibility(View.GONE);
            Log.d("akash_debug", "onBackPressed: ok ");
            binding.edtSearchBox.clearFocus();
            mainViewModel.setNavigation(NavigatorTags.LANDING_FRAGMENT,binding.searchFragmentContainer.getId() );
            handled = true;
        }
        return handled;
    }
}