package com.cookietech.chordera.appcomponents;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cookietech.chordera.application.ChorderaApplication;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferenceManager {
    public static final String SEARCH_HISTORY = "search_history";
    public static final String VIEW_MODE = "view_mode";
    private static final String CHORD_LIBRARY_UPDATE_DATE = "chord_library_update_date";

    public static void addSharedPrefSearchHistory(String keyword){
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ChorderaApplication.getContext());
        SharedPreferences.Editor editor = myPreference.edit();
        Set<String> keyword_list = myPreference.getStringSet(SEARCH_HISTORY,null);
        if(keyword_list == null){
            keyword_list = new HashSet<String>();
        }
        keyword_list.add(keyword);
        editor.putStringSet(SEARCH_HISTORY, keyword_list);
        editor.apply();
    }

    public static Set<String> getSharedPrefSearchHistory() {
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ChorderaApplication.getContext());
        return myPreference.getStringSet(SEARCH_HISTORY,null);
    }

    public static void addSharedPrefViewModel(boolean isDarkModeActivated){
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ChorderaApplication.getContext());
        SharedPreferences.Editor editor = myPreference.edit();
        editor.putBoolean(VIEW_MODE,isDarkModeActivated);
        editor.apply();
    }

    public static boolean getSharedPrefViewMode(){
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ChorderaApplication.getContext());
        return myPreference.getBoolean(VIEW_MODE, false);
    }

    public static String getSharedPrefChordLibraryUpdateDate() {
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ChorderaApplication.getContext());
        return myPreference.getString(CHORD_LIBRARY_UPDATE_DATE, "none");
    }

    public static void setSharedPrefChordLibraryUpdateDate(String updateDate) {
        SharedPreferences myPreference = PreferenceManager.getDefaultSharedPreferences(ChorderaApplication.getContext());
        SharedPreferences.Editor editor = myPreference.edit();
        editor.putString(CHORD_LIBRARY_UPDATE_DATE,updateDate);
        editor.apply();
    }
}
