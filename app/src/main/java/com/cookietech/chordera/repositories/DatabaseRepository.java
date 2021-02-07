package com.cookietech.chordera.repositories;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;


import com.cookietech.chordera.Room.SongDataDao;
import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsDao;
import com.cookietech.chordera.Room.SongsDatabase;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.Util.StringManipulationHelper;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordera.application.ChorderaApplication;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordera.models.TabulatorChordStructure;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.cookietech.chordlibrary.ChordClass;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.bind.TreeTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseRepository {
    private static final String TAG = "database_repository";
    private final FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();
    private final SingleLiveEvent<ArrayList<SongsPOJO>> topTenLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<TabPOJO> selectedTabLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<DatabaseResponse> topTenResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<List<SongsEntity>> allSongs = new SingleLiveEvent<>();
    private SongDataEntity songData;
    private SongsDao songsDao;
    private SongDataDao songDataDao;
    private SingleLiveEvent<DatabaseResponse> downloadSongDataResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> downloadSongResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> fetchAllSongsResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> newSongsResponse =new SingleLiveEvent<>();
    private SingleLiveEvent<ArrayList<SongsPOJO>> newSongsLiveData = new SingleLiveEvent<>();

    public DatabaseRepository(){
        SongsDatabase database = SongsDatabase.getInstance(ChorderaApplication.getContext());
        songsDao = database.songsDao();
        songDataDao = database.songDataDao();
    }

    private final SingleLiveEvent<DatabaseResponse> tabDataResponse = new SingleLiveEvent<>();
    private final SingleLiveEvent<ArrayList<ChordClass>> tabDisplayChords = new SingleLiveEvent<>();
    private final SingleLiveEvent<ArrayList<ChordClass>> transposedTabDisplayChords = new SingleLiveEvent<>();
    private ListenerRegistration topTenListenerRegistration;
    private ListenerRegistration tabDataListenerRegistration;
    private ListenerRegistration newSongDataListenerRegistration;

    public void queryTopTenSongs(){
        topTenResponse.setValue(new DatabaseResponse("top_ten_response",null, DatabaseResponse.Response.Fetching));
        if(topTenListenerRegistration != null)
            stopListeningTopTen();
        topTenListenerRegistration = firebaseUtilClass.queryTopTenSongData(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    topTenResponse.setValue(new DatabaseResponse("top_ten_response",error, DatabaseResponse.Response.Error));
                    return;
                }

                ArrayList<SongsPOJO> songs = new ArrayList<>();
                if (snapshots != null) {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        try{
                            SongsPOJO song = doc.toObject(SongsPOJO.class);
                            song.setId(doc.getId());
                            songs.add(song);
                        }catch (Exception e){

                        }

                    }
                    topTenLiveData.setValue(songs);
                    topTenResponse.setValue(new DatabaseResponse("top_ten_response",null, DatabaseResponse.Response.Fetched));
                }else{
                    topTenResponse.setValue(new DatabaseResponse("top_ten_response",null, DatabaseResponse.Response.Invalid_data));
                }
            }
        });
    }

    public SingleLiveEvent<ArrayList<SongsPOJO>> getObservableTopTenSongs() {
        return topTenLiveData;
    }

    public void loadTab(final SelectionType selectionType,String fromWhere) {
        if(!ConnectionManager.isOnline(ChorderaApplication.getContext())){
            tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.No_internet));
            return;
        }
        tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.Fetching));
        Log.d("tab_debug", "loadTab: " + selectionType.getSelectionId());
        if(tabDataListenerRegistration != null)
            stopListeningTabData();
        if(fromWhere.equalsIgnoreCase(Constants.FROM_SAVED)){
            //binding.downloadBtn.setVisibility(View.GONE);
            fetchSongData(selectionType.getSelectionId());

        }else{
            firebaseUtilClass.queryTab(selectionType.getSelectionId(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);

                        return;
                    }

                    String source = value != null && value.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";

                    if (value != null && value.exists()) {
                        Log.d(TAG, source + " data: " + value.getData());

                        TabPOJO tabPOJO = value.toObject(TabPOJO.class);
                        tabPOJO.setId(value.getId());
                        selectedTabLiveData.setValue(tabPOJO);
                        tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.Fetched));
                    } else {
                        tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.Invalid_data));
                        Log.d(TAG, source + " data: null");
                    }

                }
            });
        }
    }

    public SingleLiveEvent<TabPOJO> getObservableSelectedTabLiveData() {
        return selectedTabLiveData;
    }

    public SingleLiveEvent<DatabaseResponse> getObservableTopTenResponse() {
        return topTenResponse;
    }

    public void roomInsertSong(SongsEntity entity){
        new RoomInsertSongAsyncTask().execute(entity);

    }

    public void roomInsertSongData(SongDataEntity entity){
       /* new RoomInsertSongAsyncTask(songsDao).execute(entity);*/
        new RoomInsertSongDataAsyncTask().execute(entity);

    }

    public void roomDeleteSong(SongsEntity entity){

    }

    public SingleLiveEvent<List<SongsEntity>> roomFetchAllSongs(){
        return allSongs;
    }

    public SingleLiveEvent<DatabaseResponse> fetchNewSongsData() {
        newSongsResponse.setValue(new DatabaseResponse("new_song_response",null, DatabaseResponse.Response.Fetching));
        if(newSongDataListenerRegistration != null)
            stopListeningNewSongs();
        newSongDataListenerRegistration = firebaseUtilClass.queryNewSongsData((snapshots, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                newSongsResponse.setValue(new DatabaseResponse("new_song_response",error, DatabaseResponse.Response.Error));
                return;
            }

            ArrayList<SongsPOJO> songs = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    try{
                        SongsPOJO song = doc.toObject(SongsPOJO.class);
                        song.setId(doc.getId());
                        songs.add(song);
                    }catch (Exception e){

                    }

                }
                newSongsLiveData.setValue(songs);
                newSongsResponse.setValue(new DatabaseResponse("top_ten_response",null, DatabaseResponse.Response.Fetched));
            }else{
                newSongsResponse.setValue(new DatabaseResponse("top_ten_response",null, DatabaseResponse.Response.Invalid_data));
            }
        });

        return newSongsResponse;
    }

    public SingleLiveEvent<DatabaseResponse> getNewSongsResponse() {
        return newSongsResponse;
    }

    public SingleLiveEvent<ArrayList<SongsPOJO>> getNewSongsLiveData() {
        return newSongsLiveData;
    }

    private class RoomInsertSongAsyncTask extends AsyncTask<SongsEntity,Void,Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadSongResponse.setValue(new DatabaseResponse("song_response",null, DatabaseResponse.Response.Storing));
        }

        @Override
        protected Boolean doInBackground(SongsEntity... songsEntities) {
            try{
                songsDao.roomInsertSong(songsEntities[0]);
                return true;
            }catch (SQLiteConstraintException exception){
                Log.d(TAG, "doInBackground: " + exception.hashCode() + " " + exception.getCause());
                downloadSongResponse.postValue(new DatabaseResponse("song_response",null, DatabaseResponse.Response.Already_exist));
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if(isSuccess)
                downloadSongResponse.setValue(new DatabaseResponse("song_response",null, DatabaseResponse.Response.Stored));
            super.onPostExecute(isSuccess);
        }
    }


    private class RoomInsertSongDataAsyncTask extends AsyncTask<SongDataEntity,Void,Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadSongDataResponse.setValue(new DatabaseResponse("song_data_download",null, DatabaseResponse.Response.Storing));
        }

        @Override
        protected Boolean doInBackground(SongDataEntity... songDataEntities) {
            try{
                songDataDao.roomInsertSongData(songDataEntities[0]);
                return true;
            }catch (SQLiteConstraintException exception){
                Log.d(TAG, "doInBackground: " + exception.hashCode() + " " + exception.getCause());
                downloadSongDataResponse.postValue(new DatabaseResponse("song_data_download",null, DatabaseResponse.Response.Already_exist));
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if(isSuccess)
                downloadSongDataResponse.setValue(new DatabaseResponse("song_data_download",null, DatabaseResponse.Response.Stored));
            super.onPostExecute(isSuccess);
        }
    }

    public SingleLiveEvent<DatabaseResponse> getObservableDownloadSongDataResponse() {
        return downloadSongDataResponse;
    }

    public SingleLiveEvent<DatabaseResponse> getObservableDownloadSongResponse() {
        return downloadSongResponse;
    }


    public void fetchAllSongs(){
        new RoomFetchAllSongsAsyncTask().execute();
    }

    private class RoomFetchAllSongsAsyncTask extends AsyncTask<SongDataEntity,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Storing));
        }

        @Override
        protected Void doInBackground(SongDataEntity... songDataEntities) {
            allSongs.postValue(songsDao.roomFetchAllSongs());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Stored));
            super.onPostExecute(aVoid);
        }
    }

    public SingleLiveEvent<List<SongsEntity>> getObservableAllSongs() {
        return allSongs;
    }

    public SingleLiveEvent<DatabaseResponse> getObservableFetchAllSongsResponse() {
        return fetchAllSongsResponse;
    }

    private void fetchSongData(String song_data_id){
       new RoomFetchSongDataAsyncTask().execute(song_data_id);
    }


    private class RoomFetchSongDataAsyncTask extends AsyncTask<String,Void,SongDataEntity> {


        @Override
        protected SongDataEntity doInBackground(String... strings) {

            return songDataDao.roomFetchSongData(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch", null, DatabaseResponse.Response.Storing));
        }

        /*@Override
        protected Void doInBackground(SongDataEntity... songDataEntities) {
            allSongs.postValue(songsDao.roomFetchAllSongs());
            return null;
        }*/

        /*@Override
        protected void onPostExecute(Void aVoid) {
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Stored));
            super.onPostExecute(aVoid);
        }*/

        @Override
        protected void onPostExecute(SongDataEntity entity) {
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch", null, DatabaseResponse.Response.Stored));
            TabPOJO tabPOJO = entity.convertToSongDataPOJO();
            selectedTabLiveData.setValue(tabPOJO);
            super.onPostExecute(entity);
        }
    }
    public SingleLiveEvent<DatabaseResponse> getObservableTabDataResponse() {
        return tabDataResponse;
    }

    public void stopListeningTopTen(){
        topTenListenerRegistration.remove();
    }

    public void stopListeningNewSongs(){
        newSongDataListenerRegistration.remove();
    }



    public void stopListeningTabData(){
        tabDataListenerRegistration.remove();
    }

    public void decodeChordsFromData(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> chordsList = new ArrayList<>();
                ArrayList<ChordClass> chordClassArrayList = new ArrayList<>();
                Pattern pattern = Pattern.compile("[\\{\\[](.*?)[\\]\\}]");
                Matcher matcher = pattern.matcher(data);
                while (matcher.find())
                {
                    String chord = matcher.group(1);
                    if(!chordsList.contains(chord.toLowerCase()) && AppSharedComponents.getAllChords().containsKey(chord.toLowerCase())){
                        chordClassArrayList.add(AppSharedComponents.getAllChords().get(chord.toLowerCase()));
                        chordsList.add(chord.toLowerCase());
                    }
                }




                tabDisplayChords.postValue(chordClassArrayList);
            }
        }).start();
    }


    public void transposeChords(final ArrayList<ChordClass> chordClassArrayList,final int transpose){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ChordClass> TransposedChordClassArrayList = new ArrayList<>();
                for (ChordClass chordClass: chordClassArrayList){
                    String chord = chordClass.getName().toLowerCase();
                    Pattern pattern = Pattern.compile("[A-Za-z]#?m?");
                    Matcher matcher = pattern.matcher(chord);
                    String key = "";
                    if(matcher.find()){
                        key = matcher.group();
                    }
                    String remains = chord.substring(matcher.end());
                    Log.d("transpose_debug", "setTranspose: " + key+ " " + chord.substring(matcher.end()));
                    String transposedChord = StringManipulationHelper.getTransposedChord(key.toLowerCase(),transpose)+remains;
                    Log.d("transpose_debug", "setTranspose: " + transposedChord);
                    if( AppSharedComponents.getAllChords().containsKey(transposedChord.toLowerCase())){
                        TransposedChordClassArrayList.add(AppSharedComponents.getAllChords().get(transposedChord.toLowerCase()));
                    }

                }

                transposedTabDisplayChords.postValue(TransposedChordClassArrayList);
            }
        }).start();
    }


    public SingleLiveEvent<ArrayList<ChordClass>> getTransposedTabDisplayChords() {
        return transposedTabDisplayChords;
    }

    public SingleLiveEvent<ArrayList<ChordClass>> getObservableTabDisplayChords() {
        return tabDisplayChords;
    }
}
