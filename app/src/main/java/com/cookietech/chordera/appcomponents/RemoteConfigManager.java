package com.cookietech.chordera.appcomponents;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cookietech.chordera.BuildConfig;
import com.cookietech.chordera.R;
import com.cookietech.chordera.application.ChorderaApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class RemoteConfigManager {

    private static final String TAG = RemoteConfigManager.class.getName();
    private static final String METRONOME_BANNER_ADS = "metronome_banner_ads_show";
    private static final String CHORD_LIBRARY_EXIT_FULL_SCREEN_ADS = "chord_library_exit_full_screen_ads_show";
    public static final String CHORD_DISPLAY_NATIVE_ADS = "chord_display_native_ads";
    public static final String NATIVE_AD_AT_EXIT = "native_ad_at_exit";

    private static final FirebaseRemoteConfig sFirebaseRemoteConfig;
    private static final String METRONOME_EXIT_FULL_SCREEN_ADS = "metronome_exit_full_screen_ads_show";
    private static final String SELECTION_NATIVE_ADS = "selection_native_ads";

    static {
        sFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = null;
        if(BuildConfig.DEBUG){
            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build();
        }else{
            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();
        }

        sFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        sFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public static FirebaseRemoteConfig getsFirebaseRemoteConfig() {
        return sFirebaseRemoteConfig;
    }

    public static void fetchRemoteConfigValues(final RemoteConfigFetchListener fetchListener) {
        sFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener( new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " + updated);
                            fetchListener.onCompletion(true);

                        } else {
                            fetchListener.onCompletion(false);
                            Log.d(TAG, "Config failed: ");
                        }
                        //displayWelcomeMessage();
                    }
                });
    }

    public static boolean shouldShowMetronomeBannerAds() {
        return sFirebaseRemoteConfig.getBoolean(METRONOME_BANNER_ADS);
    }

    public static boolean shouldShowChordLibraryExitFullScreenAds() {
        return sFirebaseRemoteConfig.getBoolean(CHORD_LIBRARY_EXIT_FULL_SCREEN_ADS);
    }

    public static boolean shouldShowMetronomeExitFullScreenAds() {
        return sFirebaseRemoteConfig.getBoolean(METRONOME_EXIT_FULL_SCREEN_ADS);
    }

    public static boolean shouldShowChordDisplayNativeAds(){
        return sFirebaseRemoteConfig.getBoolean(CHORD_DISPLAY_NATIVE_ADS);
    }

    public static boolean shouldShowSelectionNativeAds(){
        return sFirebaseRemoteConfig.getBoolean(SELECTION_NATIVE_ADS);
    }


    public static boolean shouldShowNativeAdAtExit(){
        return sFirebaseRemoteConfig.getBoolean(NATIVE_AD_AT_EXIT);
    }

    public interface RemoteConfigFetchListener {
        void onCompletion(boolean isFetchSuccessful);
    }
}
