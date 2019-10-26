package com.eddy.mbta;

import android.app.Application;
import android.content.Context;

import com.eddy.mbta.utils.NetUtil;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.litepal.LitePal;

public class MyApplication extends Application {

    private static Context context;
    public static int NET_STATUS = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);

        NET_STATUS = NetUtil.getNetWorkState(context);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }

    public static Context getContext() {
        return context;
    }

}
