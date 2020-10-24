package com.cookietech.chordera.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class ChorderaApplication extends MultiDexApplication {

    private static ChorderaApplication APP_CONTEXT ;

    public static Context getContext() {
        return APP_CONTEXT;
    }

    @Override
    public void onCreate() {
        APP_CONTEXT = this;
        super.onCreate();
    }
}
