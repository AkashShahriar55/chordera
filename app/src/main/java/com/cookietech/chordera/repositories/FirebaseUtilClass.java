package com.cookietech.chordera.repositories;

import com.cookietech.chordera.models.SongsPOJO;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseUtilClass {
    public static final String VIEWS = "views";
    public static final String UPDATE_DATE = "update_date";

    private static FirebaseUtilClass firebaseUtilClass;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference songsCollection = db.collection("songs");
    public CollectionReference tabsCollection = db.collection("song_data");


    static{
        firebaseUtilClass = new FirebaseUtilClass();
    }

    public static FirebaseUtilClass getInstance() {
        if(firebaseUtilClass == null)
            firebaseUtilClass = new FirebaseUtilClass();
        return firebaseUtilClass;
    }


    public ListenerRegistration queryTopTenSongData(EventListener<QuerySnapshot> listener){
        return songsCollection.orderBy(VIEWS, Query.Direction.DESCENDING).limit(10).addSnapshotListener(listener);
    }

    public ListenerRegistration queryNewSongsData(EventListener<QuerySnapshot> listener){
        return songsCollection.orderBy(UPDATE_DATE, Query.Direction.DESCENDING).limit(10).addSnapshotListener(listener);
    }

    public ListenerRegistration queryTab(String tabId,EventListener<DocumentSnapshot> listener) {
        DocumentReference tab = tabsCollection.document(tabId);
        return tab.addSnapshotListener(listener);
    }


    public interface DatabaseUpdateListener{
        void onTopTenDataUpdated(List<SongsPOJO> songsPOJOList);
    }



}
