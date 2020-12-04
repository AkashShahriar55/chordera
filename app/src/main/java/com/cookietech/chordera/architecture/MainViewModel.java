package com.cookietech.chordera.architecture;



import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.chordera.appcomponents.SharedPreferenceManager;
import com.cookietech.chordera.appcomponents.SingleLiveEvent;
import com.cookietech.chordera.models.Navigator;
import com.cookietech.chordera.models.SelectionType;
import com.cookietech.chordera.models.SongsPOJO;
import com.cookietech.chordera.models.TabPOJO;
import com.cookietech.chordera.repositories.DatabaseRepository;
import com.cookietech.chordera.repositories.DatabaseResponse;
import com.google.gson.JsonStreamParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainViewModel extends ViewModel {
    private WeakReference<EditText> searchBox;
    private SingleLiveEvent<String> searchKeyword = new SingleLiveEvent<>();
    private DatabaseRepository databaseRepository = new DatabaseRepository();
    @NonNull private SingleLiveEvent<Navigator> navigation = new SingleLiveEvent<>();
    private SingleLiveEvent<SongsPOJO> selectedSong = new SingleLiveEvent<>();
    private SingleLiveEvent<SelectionType> selectedType = new SingleLiveEvent<>();

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

    public SingleLiveEvent<SelectionType> getObservableSelectedTab(){
        return selectedType;
    }

    public void loadTab(SelectionType selectionType) {
        databaseRepository.loadTab(selectionType);
    }

    public SingleLiveEvent<TabPOJO> getObservableSelectedTabLiveData() {
        return databaseRepository.getObservableSelectedTabLiveData();
    }

    public SingleLiveEvent<DatabaseResponse> getObservableTopTenResponse() {
        return databaseRepository.getObservableTopTenResponse();
    }
}
