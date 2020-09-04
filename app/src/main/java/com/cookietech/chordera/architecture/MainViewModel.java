package com.cookietech.chordera.architecture;



import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> navigation = new MutableLiveData<>();

    public MutableLiveData<String> getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigateTo){
        navigation.setValue(navigateTo);
    }
}
