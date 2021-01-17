package com.cookietech.chordera.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cookietech.chordera.models.SongsPOJO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.List;

public class FirebaseUtilClass {
    public static final String VIEWS = "views";
    private static final String MY_TAG = "bishal_db_debug";

    private static FirebaseUtilClass firebaseUtilClass;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference songsCollection = db.collection("songs");
    public CollectionReference tabsCollection = db.collection("song_data");
    //For Cloud Funtions
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();



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

    public ListenerRegistration queryTab(String tabId,EventListener<DocumentSnapshot> listener) {
        DocumentReference tab = tabsCollection.document(tabId);
        return tab.addSnapshotListener(listener);
    }

    public Task<String> getSearchResults(String searchString) {
        // Create the arguments to the callable function.
        return mFunctions
                .getHttpsCallable("getSearchResults")
                .call(searchString)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String  then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        //String result = (String) task.getResult().getData();
                        Log.d(MY_TAG, "then: called");
                        //QuerySnapshot snapshots = (QuerySnapshot) task.getResult().getData();
                        /*if (snapshots.isEmpty()){
                            Log.d(MY_TAG, "then: empty");
                        }
                        else {
                            Log.d(MY_TAG, "then: not empty");
                            for (QueryDocumentSnapshot doc : snapshots) {
                                Log.d(MY_TAG, "then: " + doc.getId());
                            }
                        }*/

                        if (task.isSuccessful()){
                            Log.d(MY_TAG, "then: successful task");
                        }
                        else {
                            Log.d(MY_TAG, "then: not successful");
                        }
                        Log.d(MY_TAG, "full data: " + task.getResult().getData());
                        Log.d("bishal_db_debug", "onClick: " + System.currentTimeMillis());
                        return "Hello";
                    }
                });
    }


    public interface DatabaseUpdateListener{
        void onTopTenDataUpdated(List<SongsPOJO> songsPOJOList);
    }



}
