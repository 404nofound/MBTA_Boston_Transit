package com.eddy.mbta;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import com.eddy.mbta.utils.NetUtil;

import org.litepal.LitePal;

public class MyApplication extends Application {

    private static Context context;
    public static int NET_STATUS = -1;
    public static boolean GPS_ENABLED = false;
    public static String station = "";
    public static int notification_id = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        LitePal.initialize(this);

        NET_STATUS = NetUtil.getNetWorkState(context);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GPS_ENABLED = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static Context getContext() {
        return context;
    }

}
