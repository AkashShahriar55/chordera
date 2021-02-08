package com.cookietech.chordera.architecture;



import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.cookietech.chordera.Room.SongDataEntity;
import com.cookietech.chordera.Room.SongsEntity;
import com.cookietech.chordera.appcomponents.SharedPreferenceManager;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordera.models.SearchData;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordera.repositories.DatabaseRepository;
import com.cookietech.chordera.repositories.DatabaseResponse;
import com.cookietech.chordlibrary.ChordClass;
import com.jakewharton.rxbinding4.widget.RxTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;


public class MainViewModel extends AndroidViewModel {
    private WeakReference<EditText> searchBox;
    private final SingleLiveEvent<String> searchKeyword = new SingleLiveEvent<>();
    private final DatabaseRepository databaseRepository = new DatabaseRepository();
    @NonNull private final SingleLiveEvent<Navigator> navigation = new SingleLiveEvent<>();
    private final SingleLiveEvent<SongsPOJO> selectedSong = new SingleLiveEvent<>();
    private final SingleLiveEvent<SelectionType> selectedType = new SingleLiveEvent<>();
    private final MutableLiveData<String> songListShowingCalledFrom = new MutableLiveData<>();
    private final SingleLiveEvent<String> loadTabCalledFor = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> isDarkModeActivated = new SingleLiveEvent<>();
    private final SingleLiveEvent<Integer> transposeValue = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> mLoadingLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        navigation.setValue(new Navigator("none",0,null));
    }


    public MutableLiveData<Navigator> getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigateTo,int containerId,Bundle arg){
        navigation.setValue(new Navigator(navigateTo,containerId,arg));
    }

    public void setNavigation(String navigateTo,Bundle arg){
        navigation.setValue(new Navigator(navigateTo,1,arg));
    }

    public void setNavigation(String navigateTo, int containerId){
        navigation.setValue(new Navigator(navigateTo,containerId,null));
    }

    public void setNavigation(String navigateTo){
        setNavigation(navigateTo,1,null);
    }

/*    public void bindSearchBox(EditText edtSearchBox) {
*//*        RxTextView.textChanges(edtSearchBox)
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
                });*//*
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
    }*/


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

    public void getSearchResults(String searchString) {
        databaseRepository.getSearchResults(searchString);
    }
    public SingleLiveEvent<ArrayList<ChordClass>> getObservableTransposedTabDisplayChords() {
        return databaseRepository.getTransposedTabDisplayChords();
    }

    public SingleLiveEvent<Boolean> getObservableIsDarkModeActivated() {
        return isDarkModeActivated;
    }

    public void setIsDarkModeActivated(Boolean isDarkModeActivated) {
        this.isDarkModeActivated.setValue(isDarkModeActivated);
    }

    public void setTransposeValue(int transposeValue) {
        this.transposeValue.setValue(transposeValue);
    }

    public SingleLiveEvent<Integer> getObservableTransposeValue() {
        return transposeValue;
    }
    public SingleLiveEvent<DatabaseResponse> fetchNewSongsData() {
        return databaseRepository.fetchNewSongsData();
    }


    public SingleLiveEvent<ArrayList<SongsPOJO>> getNewSongsData(){
        return databaseRepository.getNewSongsLiveData();
    }



    ///searching
    private final MutableLiveData<DatabaseResponse> mSearchResponses = new MutableLiveData<>();

    public MutableLiveData<DatabaseResponse> getObservableSearchResponses() {
        return databaseRepository.getObservableSearchResponse();
    }

    public void bindSearch(EditText searchView){
        RxTextView.textChanges(searchView)
                .skipInitialValue()
                .debounce(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Throwable {
                        Log.d("debounce_debug", "accept: " + charSequence);
                        if(!TextUtils.isEmpty(charSequence))
                            databaseRepository.getSearchResults(charSequence.toString());
                    }
                });
    }

    public SingleLiveEvent<ArrayList<SearchData>> getObservableSearchResult() {
        return databaseRepository.getObservableSearchResults();
    }
}
