package com.cookietech.chordera.appcomponents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionManager {
    ConnectivityManager connectivityManager;
    Context context;
    static SingleLiveEvent<Boolean> networkAvailability = new SingleLiveEvent<>();

    public ConnectionManager(Context context) {
        this.context = context;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn =  (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            // Checks the user prefs and the network connection. Based on the result, decides whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if ( networkInfo != null) {
                Log.d("akash_net_debug", "onReceive: connected");
                networkAvailability.setValue(true);
            } else {
                Log.d("akash_net_debug", "onReceive: not connected");
                networkAvailability.setValue(false);
            }
        }
    }

    public static SingleLiveEvent<Boolean> getObservableNetworkAvailability() {
        return networkAvailability;
    }
}
