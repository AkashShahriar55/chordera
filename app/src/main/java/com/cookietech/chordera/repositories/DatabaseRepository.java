package com.cookietech.chordera.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DatabaseRepository {
    private static final String TAG = "database_repository";
    private final FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();
    private final SingleLiveEvent<ArrayList<SongsPOJO>> topTenLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<TabPOJO> selectedTabLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<DatabaseResponse> topTenResponse = new SingleLiveEvent<>();

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
        Log.d("tab_debug", "loadTab: " + selectionType.getSelectionId());
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
                    selectedTabLiveData.setValue(tabPOJO);
                } else {

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
}
