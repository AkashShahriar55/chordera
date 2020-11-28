package com.cookietech.chordera.repositories;

import com.cookietech.chordera.models.SongsPOJO;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseUtilClass {
    public static final String VIEWS = "views";

    private static FirebaseUtilClass firebaseUtilClass;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference songsCollection = db.collection("songs");


    static{
        firebaseUtilClass = new FirebaseUtilClass();
    }

    public static FirebaseUtilClass getInstance() {
        if(firebaseUtilClass == null)
            firebaseUtilClass = new FirebaseUtilClass();
        return firebaseUtilClass;
    }


    public void queryTopTenSongData(EventListener<QuerySnapshot> listener){
        songsCollection.orderBy(VIEWS, Query.Direction.DESCENDING).limit(2).addSnapshotListener(listener);
    }




    public interface DatabaseUpdateListener{
        void onTopTenDataUpdated(List<SongsPOJO> songsPOJOList);
    }



}
