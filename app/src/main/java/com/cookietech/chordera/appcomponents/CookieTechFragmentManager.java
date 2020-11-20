package com.cookietech.chordera.appcomponents;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class CookieTechFragmentManager {
    public static CookieTechFragmentManager instance;
    private FragmentManager manager;

    public static CookieTechFragmentManager getInstance(){
        if(instance == null){
            instance = new CookieTechFragmentManager();
        }
        return instance;
    }

    public void initCookieTechFragmentManager(FragmentManager manager){
        this.manager = manager;
        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d("akash_debug", "onCreate: "+ getFragmentsTagList());
            }
        });
    }
    public void addFragmentToBackStack(FragmentManager manager,Fragment fragment, String tag, int containerViewId) {
        this.manager = manager;
        addFragmentToBackStack(fragment,tag,containerViewId);
    }

    public void addFragmentToBackStack(Fragment fragment, String tag, int containerViewId) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.add(containerViewId, fragment,tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addFragmentToBackStackWithAnimation(Fragment fragment, String tag, int containerViewId,int enter,int exit,int popEnter,int popExit) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.setCustomAnimations(enter,exit,popEnter,popExit);
        fragmentTransaction.add(containerViewId, fragment,tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addFragmentWithoutBackStack(Fragment fragment, String tag, int containerViewId) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    public void popFragment( String name) {
        manager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void popFragmentExclusive( String name) {
        manager.popBackStack(name, 0);
    }

    private void replaceFragment(Fragment fragment, String tag,int containerViewId) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }


    public ArrayList<String> getFragmentsTagList(){
        ArrayList<String> fragmentList = new ArrayList<>();
        for(int i = 0 ; i< manager.getBackStackEntryCount();i++){
            fragmentList.add(manager.getBackStackEntryAt(i).getName());
        }
        return fragmentList;
    }

    public boolean getIsFragmentAvailable(String tag) {
        return manager.findFragmentByTag(tag) != null;
    }


    public Fragment getTopFragment(){
        List<Fragment> fragments = manager.getFragments();

        return fragments.get(fragments.size()-1);
    }
}
