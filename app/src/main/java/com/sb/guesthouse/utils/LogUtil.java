package com.sb.guesthouse.utils;

import android.util.Log;

import com.sb.guesthouse.GHApplication;

/*
    * Created by Sharn25
    * dated 12-06-2020
    */
public class LogUtil {
    public static void l(String tag, String msg, boolean z){
        if(GHApplication.getInstance().getDebugMode() && z) {
            Log.d("[Log] " + tag, msg);
        }
    }
    public static void e(String tag, String msg, boolean z){
        if(z) {
            Log.d("[Error] " + tag, msg);
        }
    }
    public static void i(String tag, String msg, boolean z){
        if(z) {
            Log.d("[INFO] " + tag, msg);
        }
    }
}
