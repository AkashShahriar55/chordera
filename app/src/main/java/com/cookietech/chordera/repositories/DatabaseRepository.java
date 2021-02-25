package com.cookietech.chordera.repositories;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;


import com.cookietech.chordera.Room.SongDataDao;
import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsDao;
import com.cookietech.chordera.Room.SongsDatabase;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.Util.StringManipulationHelper;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordera.application.ChorderaApplication;
import com.cookietech.chordera.models.CollectionsPOJO;
import com.cookietech.chordera.models.SearchData;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.cookietech.chordlibrary.ChordClass;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseRepository {
    private static final String TAG = "database_repository";
    private static final String MY_TAG = "bishal_db_debug";
    private final FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();
    private final SingleLiveEvent<ArrayList<SongsPOJO>> topTenLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<TabPOJO> selectedTabLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<DatabaseResponse> topTenResponse = new SingleLiveEvent<>();
    private LiveData<PagedList<SongsEntity>> allSavedSongs;
    private SingleLiveEvent<SongsEntity> roomFetchedSong  = new SingleLiveEvent<>();
    private SongsDao songsDao;
    private SongDataDao songDataDao;
    private SingleLiveEvent<DatabaseResponse> downloadSongDataResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> downloadSongResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> fetchAllSongsResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> roomFetchedSongResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> roomUpdateSongResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> newSongsResponse =new SingleLiveEvent<>();
    private SingleLiveEvent<ArrayList<SongsPOJO>> newSongsLiveData = new SingleLiveEvent<>();
    private QueryDocumentSnapshot lastFetchedNewSongDoc = null;

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


    public void roomFetchASong(String id) {
        new RoomFetchASongAsyncTask().execute(id);
    }

    public void roomUpdateExistingSongData(SongsEntity fetchedSong) {
        new roomUpdateExistingSongDataAsyncTask().execute(fetchedSong);
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
                if(snapshots.isEmpty()){
                    newSongsResponse.setValue(new DatabaseResponse("top_ten_response",error, DatabaseResponse.Response.No_internet));
                    return;
                }
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

    SingleLiveEvent<DatabaseResponse> allNewSongsResponse = new SingleLiveEvent<>();
    private ListenerRegistration allNewSongListenerRegistration;
    SingleLiveEvent<ArrayList<SongsPOJO>> allNewSongsLiveData = new SingleLiveEvent<>();
    public SingleLiveEvent<DatabaseResponse> fetchAllNewSongsData() {
        allNewSongsResponse.setValue(new DatabaseResponse("all_new_song_response",null, DatabaseResponse.Response.Fetching));
        if(allNewSongListenerRegistration != null)
            stopListeningAllNewSongs();
        allNewSongListenerRegistration = firebaseUtilClass.queryAllNewSongsData((snapshots, error) -> {
            Log.d("new_explore_debug", "fetchAllNewSongsData: DatabaseRepository");
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                allNewSongsResponse.setValue(new DatabaseResponse("all_new_song_response", error, DatabaseResponse.Response.Error));
                return;
            }

            ArrayList<SongsPOJO> songs = new ArrayList<>();
            if (snapshots != null) {
                //Log.d("new_explore_debug", "DatabaseRepo:  snapshot not null");
                if(snapshots.size()<=0){
                    allNewSongsResponse.setValue(new DatabaseResponse("all_new_song_response", null, DatabaseResponse.Response.LastSongFetched));
                    return;
                }
                for (QueryDocumentSnapshot doc : snapshots) {
                    try {
                        SongsPOJO song = doc.toObject(SongsPOJO.class);
                        song.setId(doc.getId());
                        songs.add(song);
                        lastFetchedNewSongDoc = doc;
                    } catch (Exception e) {

                    }

                }
                allNewSongsLiveData.setValue(songs);
                allNewSongsResponse.setValue(new DatabaseResponse("all_new_song_response", null, DatabaseResponse.Response.Fetched));
            } else {
                //Log.d("new_explore_debug", "DatabaseRepo:  snapshot null");
                allNewSongsResponse.setValue(new DatabaseResponse("all_new_song_response", null, DatabaseResponse.Response.Invalid_data));
            }
        },lastFetchedNewSongDoc);

        Log.d("new_explore_debug", "fetchAllNewSongsData: DatabaseRepository  " + allNewSongListenerRegistration);

        return allNewSongsResponse;
    }

    public void stopListeningAllNewSongs() {
        allNewSongListenerRegistration.remove();
    }

    public SingleLiveEvent<ArrayList<SongsPOJO>> getObservableAllNewSongsLiveData() {
        return allNewSongsLiveData;
    }

    public SingleLiveEvent<DatabaseResponse> getNewSongsResponse() {
        return newSongsResponse;
    }

    public SingleLiveEvent<ArrayList<SongsPOJO>> getNewSongsLiveData() {
        return newSongsLiveData;
    }


    private SingleLiveEvent<SongsPOJO> searchSelectedSong = new SingleLiveEvent<>();
    private SingleLiveEvent<DatabaseResponse> searchSelectionResponse = new SingleLiveEvent<>();
    public SingleLiveEvent<SongsPOJO> getObservableSearchSelectedSong(){
        return  searchSelectedSong;
    }

    public SingleLiveEvent<DatabaseResponse> getObservableSearchSelectionResponse(){
        return searchSelectionResponse;
    }
    public void downloadSearchedDataAndNavigate(SearchData data) {
        Log.d("search_debug", "downloadSearchedDataAndNavigate: start");
        firebaseUtilClass.querySearchedSong(data.getId(), (value, error) -> {
            Log.d("search_debug", "downloadSearchedDataAndNavigate: " + error);
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                searchSelectionResponse.setValue(new DatabaseResponse("search_selected_response",error, DatabaseResponse.Response.Error));
                return;
            }

            if (value != null) {
                try{
                    SongsPOJO song = value.toObject(SongsPOJO.class);
                    song.setId(value.getId());
                    searchSelectedSong.setValue(song);
                    Log.d("search_debug", "downloadSearchedDataAndNavigate: " + song);
                }catch (Exception e){
                    searchSelectionResponse.setValue(new DatabaseResponse("search_selected_response",e, DatabaseResponse.Response.Error));
                }

            }else{
                searchSelectionResponse.setValue(new DatabaseResponse("search_selected_response",null, DatabaseResponse.Response.Invalid_data));
            }
        });
    }

    private final SingleLiveEvent<DatabaseResponse> collectionResponse = new SingleLiveEvent<>();
    private final SingleLiveEvent<ArrayList<CollectionsPOJO>> collectionsData = new SingleLiveEvent<>();
    private ListenerRegistration collectionDataListenerRegistration;
    public SingleLiveEvent<DatabaseResponse> fetchCollectionsData() {
        collectionResponse.setValue(new DatabaseResponse("collections_response",null, DatabaseResponse.Response.Fetching));
        if(collectionDataListenerRegistration != null)
            stopListeningCollectionData();
        collectionDataListenerRegistration = firebaseUtilClass.queryCollectionsData((snapshots, error) -> {
            if (error != null) {
                Log.d("collection_debug", "fetchCollectionsData: "+ error);
                collectionResponse.setValue(new DatabaseResponse("collections_response",error, DatabaseResponse.Response.Error));
                return;
            }

            ArrayList<CollectionsPOJO> collections = new ArrayList<>();
            if (snapshots != null) {
                if(snapshots.isEmpty()){
                    collectionResponse.setValue(new DatabaseResponse("collections_response",error, DatabaseResponse.Response.No_internet));
                    return;
                }

                for (QueryDocumentSnapshot doc : snapshots) {
                    try{
                        CollectionsPOJO collection = doc.toObject(CollectionsPOJO.class);
                        collection.setId(doc.getId());
                        collections.add(collection);

                    }catch (Exception e){
                        Log.d("collection_debug", "fetchCollectionsData: "+ e);
                    }

                }
                collectionsData.setValue(collections);
                collectionResponse.setValue(new DatabaseResponse("collections_response",null, DatabaseResponse.Response.Fetched));
            }else{
                collectionResponse.setValue(new DatabaseResponse("collections_response",null, DatabaseResponse.Response.Invalid_data));
            }
        });

        return collectionResponse;
    }

    public SingleLiveEvent<ArrayList<CollectionsPOJO>> getObservableCollectionsData() {
        return collectionsData;
    }

    private void stopListeningCollectionData() {
        collectionDataListenerRegistration.remove();
    }


    private SingleLiveEvent<DatabaseResponse> collectionSongsDatabaseResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<ArrayList<SongsPOJO>> collectionSongsData = new SingleLiveEvent<>();
    private ListenerRegistration collectionSongsListenerRegistration;
    public SingleLiveEvent<DatabaseResponse> fetchCollectionSongs(String response) {
        collectionSongsDatabaseResponse.setValue(new DatabaseResponse("collection_song_response",null, DatabaseResponse.Response.Fetching));
        if(collectionSongsListenerRegistration != null)
            stopListeningCollectionSongsData();
        collectionSongsListenerRegistration = firebaseUtilClass.queryCollectionSongsData(response,(snapshots, error) -> {
            if (error != null) {
                Log.d("collection_debug", "fetchCollectionsData: "+ error);
                collectionSongsDatabaseResponse.setValue(new DatabaseResponse("collection_song_response",error, DatabaseResponse.Response.Error));
                return;
            }

            ArrayList<SongsPOJO> collectionSongs = new ArrayList<>();
            if (snapshots != null) {
                Log.d("collection_debug", "fetchCollectionsData: "+ snapshots.size());
                for (QueryDocumentSnapshot doc : snapshots) {
                    try{
                        SongsPOJO collectionSong = doc.toObject(SongsPOJO.class);
                        collectionSong.setId(doc.getId());
                        collectionSongs.add(collectionSong);

                    }catch (Exception e){
                        Log.d("collection_debug", "fetchCollectionsData: "+ e);
                    }

                }
                this.collectionSongsData.setValue(collectionSongs);
                collectionSongsDatabaseResponse.setValue(new DatabaseResponse("collection_song_response",null, DatabaseResponse.Response.Fetched));
            }else{
                collectionSongsDatabaseResponse.setValue(new DatabaseResponse("collection_song_response",null, DatabaseResponse.Response.Invalid_data));
            }
        });

        return collectionResponse;
    }

    public void stopListeningCollectionSongsData() {
        collectionSongsListenerRegistration.remove();
    }

    public LiveData<ArrayList<SongsPOJO>> getObservableCollectionSongsData() {
        return collectionSongsData;
    }

    public void resetLastNewSongDocument() {
        lastFetchedNewSongDoc = null;
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


    public void fetchAllSavedSongs(){
        //new RoomFetchAllSongsAsyncTask().execute();
        PagedList.Config savedSongPagingConfig = new PagedList.Config.Builder()
                .setPageSize(5)
                .setInitialLoadSizeHint(5)
                .setEnablePlaceholders(false)
                .build();
        allSavedSongs = new LivePagedListBuilder<>(songsDao.roomFetchAllSongs(), savedSongPagingConfig).build();
    }

    public void refreshSavedSong() {
        //savedSongDataSource.getValue().invalidate();
        allSavedSongs.getValue().getDataSource().invalidate();
    }



    /**AsyncTask Section**/

    /**AsyncTask for fetching all saved songs from Room Database**/
    /*private class RoomFetchAllSongsAsyncTask extends AsyncTask<SongDataEntity,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Storing));
        }

        @Override
        protected Void doInBackground(SongDataEntity... songDataEntities) {
            //allSavedSongs.postValue(songsDao.roomFetchAllSongs());

            allSavedSongs = new LivePagedListBuilder<>(songsDao.roomFetchAllSongs(), 5).build();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Stored));
            super.onPostExecute(aVoid);
        }
    }*/


    /**AsyncTask for fetching a Song by a song ID from Room Database**/
    private class RoomFetchASongAsyncTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            roomFetchedSongResponse.setValue(new DatabaseResponse("room_fetch_a_song_response",null, DatabaseResponse.Response.Fetching ));
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                roomFetchedSong.postValue(songsDao.roomFetchASong(strings[0]));
                return true;
            }catch (SQLiteConstraintException exception){
                Log.d(TAG, "doInBackground: " + exception.hashCode() + " " + exception.getCause());
                roomFetchedSongResponse.postValue(new DatabaseResponse("room_fetch_a_song_response",null, DatabaseResponse.Response.Error));
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if(isSuccess)
                roomFetchedSongResponse.setValue(new DatabaseResponse("room_fetch_a_song_response",null, DatabaseResponse.Response.Fetched));
            super.onPostExecute(isSuccess);
        }

    }

    /**AsyncTask for updating song_data field **/
    //roomUpdateSongResponse

    private class roomUpdateExistingSongDataAsyncTask extends AsyncTask<SongsEntity,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            roomUpdateSongResponse.setValue(new DatabaseResponse("room_update_song_response",null, DatabaseResponse.Response.Updating));
        }

        @Override
        protected Boolean doInBackground(SongsEntity... songsEntities) {
            try{
                //roomFetchedSong.postValue(songsDao.roomFetchASong(songsEntities[0]));
                songsDao.roomUpdateExistingSongData(songsEntities[0].getSong_id(),songsEntities[0].getSong_data());
                return true;
            }catch (SQLiteConstraintException exception){
                Log.d(TAG, "doInBackground: " + exception.hashCode() + " " + exception.getCause());
                roomUpdateSongResponse.postValue(new DatabaseResponse("room_update_song_response",null, DatabaseResponse.Response.Error));
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if(isSuccess)
                roomUpdateSongResponse.setValue(new DatabaseResponse("room_update_song_response",null, DatabaseResponse.Response.Updated));
            super.onPostExecute(isSuccess);
        }
    }

    public LiveData<PagedList<SongsEntity>> getObservableAllSavedSongs() {
        return allSavedSongs;
    }

    public SingleLiveEvent<SongsEntity> getObservableRoomFetchedSong() {
        return roomFetchedSong;
    }


    public SingleLiveEvent<DatabaseResponse> getObservableFetchAllSongsResponse() {
        return fetchAllSongsResponse;

    }

    public SingleLiveEvent<DatabaseResponse> getObservableRoomFetchedSongResponse() {
        return roomFetchedSongResponse;
    }


    public SingleLiveEvent<DatabaseResponse> getObservableRoomUpdateSongResponse() {
        return roomUpdateSongResponse;
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

    private final SingleLiveEvent<ArrayList<SearchData>> searchResults = new SingleLiveEvent<>();

    public SingleLiveEvent<ArrayList<SearchData>> getObservableSearchResults() {
        return searchResults;
    }

    private final SingleLiveEvent<DatabaseResponse> searchResponse = new SingleLiveEvent<>();

    public SingleLiveEvent<DatabaseResponse> getObservableSearchResponse() {
        return searchResponse;
    }

    public void getSearchResults(String searchString){
        Log.d("search_debug", "downloadSearchedDataAndNavigate: start");
        searchResponse.setValue(new DatabaseResponse("search_response",null, DatabaseResponse.Response.Fetching));
        firebaseUtilClass.getSearchResults(searchString).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                Log.d("search_debug", "downloadSearchedDataAndNavigate: start" + task.getResult().getData());
                if(task.isSuccessful()){
                    ArrayList<SearchData> allData = new ArrayList<>();
                    String jsonString = task.getResult().getData().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if(jsonArray.length()<=0){
                            searchResponse.setValue(new DatabaseResponse("search_response",null, DatabaseResponse.Response.Invalid_data));
                        }
                        else{
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                SearchData singleData = SearchData.fromJson(object);
                                if(singleData!= null)
                                    allData.add(singleData);
                                Log.d("search_result", "onComplete: " + singleData);
                            }
                            searchResponse.setValue(new DatabaseResponse("search_response",null, DatabaseResponse.Response.Fetched));
                            searchResults.setValue(allData);
                        }

                    } catch (JSONException e) {
                        searchResponse.setValue(new DatabaseResponse("search_response",e, DatabaseResponse.Response.Error));
                        e.printStackTrace();
                    }
                }
            }
        });

    /*.addOnCompleteListener(new OnCompleteListener<String>() {

            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    Log.d(MY_TAG, "onComplete: task successful" + task.getResult());
                }
                else {
                    Log.d(MY_TAG, "onComplete: task is not successful");
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();

                        Log.d(MY_TAG, "onComplete: " +  "code: " + code + " details: "+ details);
                    }
                }
            }
        });*/

    }


}
