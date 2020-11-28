package com.cookietech.chordera.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.models.SongsPOJO;
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

    public void queryTopTenSongs(){
        firebaseUtilClass.queryTopTenSongData(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                ArrayList<SongsPOJO> songs = new ArrayList<>();
                if (snapshots != null) {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        SongsPOJO song = doc.toObject(SongsPOJO.class);
                        songs.add(song);

                    }

                    topTenLiveData.setValue(songs);
                }
            }
        });
    }

    public SingleLiveEvent<ArrayList<SongsPOJO>> getObservableTopTenSongs() {
        return topTenLiveData;
    }
}
