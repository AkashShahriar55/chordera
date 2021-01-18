package com.cookietech.chordera.architecture;



import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.appcomponents.SharedPreferenceManager;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordera.repositories.DatabaseRepository;
import com.cookietech.chordera.repositories.DatabaseResponse;
import com.cookietech.chordlibrary.ChordClass;
import com.google.gson.JsonStreamParser;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainViewModel extends ViewModel {
    private WeakReference<EditText> searchBox;
    private SingleLiveEvent<String> searchKeyword = new SingleLiveEvent<>();
    private DatabaseRepository databaseRepository = new DatabaseRepository();
    @NonNull private SingleLiveEvent<Navigator> navigation = new SingleLiveEvent<>();
    private SingleLiveEvent<SongsPOJO> selectedSong = new SingleLiveEvent<>();
    private SingleLiveEvent<SelectionType> selectedType = new SingleLiveEvent<>();
    private MutableLiveData<String> songListShowingCalledFrom = new MutableLiveData<>();
    private SingleLiveEvent<String> loadTabCalledFor = new SingleLiveEvent<>();

    public MainViewModel() {
        navigation.setValue(new Navigator("none",0));
    }



    public MutableLiveData<Navigator> getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigateTo,int containerId){
        navigation.setValue(new Navigator(navigateTo,containerId));
    }

    public void bindSearchBox(EditText edtSearchBox) {
/*        RxTextView.textChanges(edtSearchBox)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(it -> {

                })
                .observeOn(Schedulers.io())
                .switchMap( text -> {

                }).subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
        searchBox = new WeakReference<EditText>(edtSearchBox);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKeyword.setValue(s.toString());
            }
        };

        searchBox.get().addTextChangedListener(textWatcher);
    }


    public SingleLiveEvent<String> getObservableSearchKeyword(){
        return searchKeyword;
    }

    public MutableLiveData<String> getObservableSongListShowingCalledFrom(){
        return songListShowingCalledFrom;
    }

    public void SaveSearchKeyWordHistory(String keyword) {
        SharedPreferenceManager.addSharedPrefSearchHistory(keyword);
    }


    public void queryTopTenSongs(){
        databaseRepository.queryTopTenSongs();
    }

    public SingleLiveEvent<ArrayList<SongsPOJO>> getObservableTopTenSongs(){
        return databaseRepository.getObservableTopTenSongs();
    }

    public SingleLiveEvent<SongsPOJO> getObservableSelectedSong(){
        return selectedSong;
    }

    public void setSelectedSong(SongsPOJO selectedSong){
        this.selectedSong.setValue(selectedSong);
    }

    public void setSelectedTab(SelectionType selectionType) {
        this.selectedType.setValue(selectionType);
    }

    public void setSongListShowingCalledFrom(String calledFrom){
        this.songListShowingCalledFrom.setValue(calledFrom);
    }

    public SingleLiveEvent<SelectionType> getObservableSelectedTab(){
        return selectedType;
    }

    public void loadTab(SelectionType selectionType) {
        databaseRepository.loadTab(selectionType,songListShowingCalledFrom.getValue());
    }

    public SingleLiveEvent<TabPOJO> getObservableSelectedTabLiveData() {
        return databaseRepository.getObservableSelectedTabLiveData();
    }

    public SingleLiveEvent<DatabaseResponse> getObservableTopTenResponse() {
        return databaseRepository.getObservableTopTenResponse();
    }


    public void roomInsertSong(SongsEntity entity) {
        databaseRepository.roomInsertSong(entity);
    }

    public void roomInsertSongData(SongDataEntity entity){
        databaseRepository.roomInsertSongData(entity);
    }


    public SingleLiveEvent<DatabaseResponse> getObservableDownloadSongDataResponse() {
        return databaseRepository.getObservableDownloadSongDataResponse();
    }

    public SingleLiveEvent<DatabaseResponse> getObservableDownloadSongResponse() {
        return databaseRepository.getObservableDownloadSongResponse();
    }

    public void fetchAllSongs(){
        databaseRepository.fetchAllSongs();
    }

    public SingleLiveEvent<List<SongsEntity>> getObservableAllSongs() {
        return databaseRepository.getObservableAllSongs();
    }

    public SingleLiveEvent<DatabaseResponse> getObservableFetchAllSongsResponse() {
        return databaseRepository.getObservableFetchAllSongsResponse();
    }


    public SingleLiveEvent<String> getObservableLoadTabCalledFor() {
        return loadTabCalledFor;
    }

    public void setLoadTabCalledFor(String loadTabCalledFor) {
        this.loadTabCalledFor.setValue(loadTabCalledFor);
    }
    public SingleLiveEvent<DatabaseResponse> getObservableTabDataResponse() {
        return databaseRepository.getObservableTabDataResponse();
    }

    public void stopListeningTopTen(){
        databaseRepository.stopListeningTopTen();
    }

    public void decodeChordsFromData(String data) {
        databaseRepository.decodeChordsFromData(data);
    }

    public void transposeChords(final ArrayList<ChordClass> chordClassArrayList,final int transpose) {
        databaseRepository.transposeChords(chordClassArrayList,transpose);
    }

    public SingleLiveEvent<ArrayList<ChordClass>> getObservableTabDisplayChords() {
        return databaseRepository.getObservableTabDisplayChords();
    }

    public SingleLiveEvent<ArrayList<ChordClass>> getObservableTransposedTabDisplayChords() {
        return databaseRepository.getTransposedTabDisplayChords();
    }
}
