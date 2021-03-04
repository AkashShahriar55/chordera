package com.cookietech.chordera.featureSongList.saved;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.databinding.FragmentSavedSongBinding;
import com.cookietech.chordera.featureSelectionType.SelectionTypeFragment;
import com.cookietech.chordera.fragments.ChorderaFragment;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.repositories.DatabaseResponse;

import java.util.ArrayList;

public class SavedSongListFragment extends ChorderaFragment implements SwipeRefreshLayout.OnRefreshListener{
    FragmentSavedSongBinding binding;
    RecyclerView recyclerView;
    AllSavedSongPagedAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    /*int currentPage = PAGE_START;
    boolean isLastPage = false;
    int totalPage = 10;*/
    boolean isLoading = false;
    //int itemCount = 0;
    LinearLayoutManager layoutManager;
    //ArrayList<SongsPOJO> songsList = new ArrayList<>();
    private String selectedSongIdForDelete = null;
    private int songPositionForDelete = -1;

    public SavedSongListFragment(){}

    public static SavedSongListFragment newInstance(){return new SavedSongListFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedSongBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariable();
        initialize();
        initializeObservers();
    }

    private void initializeObservers() {

        mainViewModel.getObservableAllSavedSongs().observe(fragmentLifecycleOwner, songsEntities -> {
            Log.d("download_debug", "onChanged: " + songsEntities.size());

            //swipeRefreshLayout.setRefreshing(false);
            //adapter.clear();
            adapter.submitList(songsEntities);
            swipeRefreshLayout.setRefreshing(false);
        });

        mainViewModel.getObservableDeleteSongDataResponse().observe(fragmentLifecycleOwner, databaseResponse -> {
            switch (databaseResponse.getResponse()){
                case Deleting:
                    Log.d("delete_debug", " Song Data Deleting: ");
                    break;
                case Deleted:

                    if (selectedSongIdForDelete != null){
                        mainViewModel.roomDeleteSong(selectedSongIdForDelete);
                        selectedSongIdForDelete = null;
                    }
                    else {
                        Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("delete_debug", " Song Data Deleted: ");

                    break;
                case Error:
                    Toast.makeText(requireContext(), "Something Went wrong", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        });

        mainViewModel.getObservableDeleteSongResponse().observe(fragmentLifecycleOwner, new Observer<DatabaseResponse>() {
            @Override
            public void onChanged(DatabaseResponse databaseResponse) {

                switch (databaseResponse.getResponse()){
                    case Deleting:
                        Log.d("delete_debug", " Song Deleting: ");
                        break;
                    case Deleted:
                        Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Log.d("delete_debug", " Song Deleted: ");
                        if (songPositionForDelete >= 0){
                            adapter.notifyItemRemoved(songPositionForDelete);
                        }
                        else {
                            Toast.makeText(requireContext(), "Something Went wrong", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case Error:
                        Toast.makeText(requireContext(), "Something Went wrong", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }



            }
        });
    }

    private void initializeVariable() {
        isLoading = false;
        //itemCount = 0;
        getData();
    }

    private void getData() {
        isLoading = true;
        mainViewModel.fetchAllSavedSongs();
    }

    private void initialize() {
        binding.headerTittle.setText(R.string.saved_song);
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefresh;

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllSavedSongPagedAdapter(requireContext(),new AllSavedSongPagedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SongsPOJO song) {
                mainViewModel.setNavigation(NavigatorTags.SELECTION_TYPE_FRAGMENT, SelectionTypeFragment.createBundle(song));
                mainViewModel.setSelectedSong(song);
            }
        });
        recyclerView.setAdapter(adapter);
        //getData();
        mainViewModel.setSongListShowingCalledFrom(Constants.FROM_OFFLINE);
        adapter.setOnItemLongClickListener(new AllSavedSongPagedAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLogClick(SongsEntity songsEntity, int position) {
                //Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show();
                showDeleteDialog(songsEntity,position);
            }
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

    }


    @Override
    public void onRefresh() {
        //adapter.clear();
        //getData();
        mainViewModel.refreshSavedSong();
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(SongsEntity songsEntity, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setTitle(songsEntity.getSong_name());
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d("delete_debug", "song_data: " + songsEntity.getSong_data());
                selectedSongIdForDelete = songsEntity.getSong_id();
                songPositionForDelete = position;
                ArrayList<String> song_data_ids = new ArrayList<>(songsEntity.getSong_data().values());
                //Log.d("delete_debug", "song_data: " + song_data_ids);
                mainViewModel.roomDeleteSongData(song_data_ids);
                dialog.dismiss();

            }
        });

        builder.show();
    }
}
