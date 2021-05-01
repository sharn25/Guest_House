package com.sb.guesthouse;

import android.app.Application;

import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.utils.LogUtil;

import java.security.BasicPermission;

/**
 * Application initiation class
 * @author Sharn25
 * @since 27-02-2021
 * @version 0.0
 */
public class GHApplication extends Application {
    private static String TAG = "";
    private static GHApplication instance;
    //boolean
    private Boolean isDebugMode;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        isDebugMode = false;
        StaticResources.appDir = this.getExternalFilesDir("").getAbsolutePath();
        LogUtil.l(TAG,"app Dir - " + StaticResources.appDir, true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public static GHApplication getInstance() {
        return instance;
    }

    public Boolean getDebugMode(){
        return isDebugMode;
    }
}
