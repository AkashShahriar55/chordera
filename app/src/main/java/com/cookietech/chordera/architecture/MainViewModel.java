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

import java.lang.ref.WeakReference;


public class MainViewModel extends ViewModel {
    private WeakReference<EditText> searchBox;
    private SingleLiveEvent<String> searchKeyword = new SingleLiveEvent<>();
    @NonNull private SingleLiveEvent<Navigator> navigation = new SingleLiveEvent<>();

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
}
