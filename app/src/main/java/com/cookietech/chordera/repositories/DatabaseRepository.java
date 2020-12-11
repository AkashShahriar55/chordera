package com.cookietech.chordera.repositories;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cookietech.chordera.Room.SongDataDao;
import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsDao;
import com.cookietech.chordera.Room.SongsDatabase;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.appcomponents.Constants;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.application.ChorderaApplication;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    public DatabaseRepository(){
        SongsDatabase database = SongsDatabase.getInstance(ChorderaApplication.getContext());
        songsDao = database.songsDao();
        songDataDao = database.songDataDao();
    }


    public void queryTopTenSongs(){
        topTenResponse.setValue(new DatabaseResponse("top_ten_response",null, DatabaseResponse.Response.Fetching));
        firebaseUtilClass.queryTopTenSongData(new EventListener<QuerySnapshot>() {
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
                        SongsPOJO song = doc.toObject(SongsPOJO.class);
                        song.setId(doc.getId());
                        songs.add(song);
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
                    } else {

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
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Storing));
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
            fetchAllSongsResponse.setValue(new DatabaseResponse("all_songs_fetch",null, DatabaseResponse.Response.Stored));
            TabPOJO tabPOJO =entity.convertToSongDataPOJO();
            selectedTabLiveData.setValue(tabPOJO);
            super.onPostExecute(entity);
        }
    }
}
