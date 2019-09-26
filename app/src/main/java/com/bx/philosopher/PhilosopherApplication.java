package com.bx.philosopher;

import android.app.Application;
import android.content.Context;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by newbiechen on 17-4-15.
 */

public class PhilosopherApplication extends Application {
    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initUMeng();
    }

    public static Context getContext() {
        return sInstance;
    }

    void initUMeng() {


        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, BuildConfig.UMENGKEY
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
//        PlatformConfig.setWeixin("", "");
        PlatformConfig.setSinaWeibo("2645350077", "9d1215ccbce1caa9720bfa29a403403c", "https://api.weibo.com/oauth2/default.html");
//        PlatformConfig.setTwitter("", "");
        PlatformConfig.setQQZone("101652906", "fc1a58dcab7238e42a27156e1c4d5aba");
    }
}