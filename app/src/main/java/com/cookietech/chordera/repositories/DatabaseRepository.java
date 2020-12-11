package com.cookietech.chordera.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordera.application.ChorderaApplication;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordlibrary.ChordClass;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.bind.TreeTypeAdapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseRepository {
    private static final String TAG = "database_repository";
    private final FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();
    private final SingleLiveEvent<ArrayList<SongsPOJO>> topTenLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<TabPOJO> selectedTabLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<DatabaseResponse> topTenResponse = new SingleLiveEvent<>();
    private final SingleLiveEvent<DatabaseResponse> tabDataResponse = new SingleLiveEvent<>();
    private final SingleLiveEvent<ArrayList<ChordClass>> tabDisplayChords = new SingleLiveEvent<>();
    private ListenerRegistration topTenListenerRegistration;
    private ListenerRegistration tabDataListenerRegistration;

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
                        SongsPOJO song = doc.toObject(SongsPOJO.class);
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

    public void loadTab(final SelectionType selectionType) {
        if(!ConnectionManager.isOnline(ChorderaApplication.getContext())){
            tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.No_internet));
            return;
        }
        tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.Fetching));
        Log.d("tab_debug", "loadTab: " + selectionType.getSelectionId());
        if(tabDataListenerRegistration != null)
            stopListeningTabData();
        tabDataListenerRegistration = firebaseUtilClass.queryTab(selectionType.getSelectionId(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    tabDataResponse.setValue(new DatabaseResponse("tab_data_response",error, DatabaseResponse.Response.Error));
                    return;
                }

                String source = value != null && value.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (value != null && value.exists()) {
                    Log.d(TAG, source + " data: " + value.getData());

                    TabPOJO tabPOJO = value.toObject(TabPOJO.class);
                    selectedTabLiveData.setValue(tabPOJO);
                    tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.Fetched));
                } else {
                    tabDataResponse.setValue(new DatabaseResponse("tab_data_response",null, DatabaseResponse.Response.Invalid_data));
                    Log.d(TAG, source + " data: null");
                }
            }
        });
    }

    public SingleLiveEvent<TabPOJO> getObservableSelectedTabLiveData() {
        return selectedTabLiveData;
    }

    public SingleLiveEvent<DatabaseResponse> getObservableTopTenResponse() {
        return topTenResponse;
    }

    public SingleLiveEvent<DatabaseResponse> getObservableTabDataResponse() {
        return tabDataResponse;
    }

    public void stopListeningTopTen(){
        topTenListenerRegistration.remove();
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
                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
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


    public SingleLiveEvent<ArrayList<ChordClass>> getObservableTabDisplayChords() {
        return tabDisplayChords;
    }
}
