package com.cookietech.chordera.architecture;



import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.chordera.appcomponents.SingleLiveEvent;


public class MainViewModel extends ViewModel {
    @NonNull private SingleLiveEvent<String> navigation = new SingleLiveEvent<>();

    public MainViewModel() {
        navigation.setValue("none");

    }

    public MutableLiveData<String> getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigateTo){
        navigation.setValue(navigateTo);
    }
}
