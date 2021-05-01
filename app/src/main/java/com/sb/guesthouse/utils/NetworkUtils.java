package com.sb.guesthouse.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sb.guesthouse.GHApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Methods to work with Tools
 * @author Sharn25
 * @since 27-02-2021
 * @version 0.0
 */
public class NetworkUtils {
    private final static String TAG = "NetworkUtils";
    /*public static boolean isInternetAvailable() {
        return true;
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            LogUtil.e(TAG,"Unable to check internet.",true);
        }
        return false;
    }*/

    public static boolean isInternetAvailable(Context mContext) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            LogUtil.e("Connectivity Exception", e.getMessage(),true);
        }
        return connected;
    }
}
