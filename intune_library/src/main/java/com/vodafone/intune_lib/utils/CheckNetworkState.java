package com.vodafone.intune_lib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import com.vodafone.intunes_poc.logging.AppLoggerWrapper;


public class CheckNetworkState {

    /**
     * Check whether device has internet connection or not
     *
     * @param context
     * */
    public static boolean isOnline(Context context) {
        boolean isWifi = false;
        boolean isOtherNetwork = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        isWifi = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        isOtherNetwork = true;
                    }
                }
            }
        }
        AppLoggerWrapper.infoLog(context,"CheckNetworkState",
                "Internet connectivity status -- "+String.valueOf(isWifi||isOtherNetwork));

        return isWifi || isOtherNetwork;
    }
}
