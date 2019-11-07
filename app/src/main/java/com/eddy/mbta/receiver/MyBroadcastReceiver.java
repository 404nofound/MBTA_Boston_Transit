package com.eddy.mbta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.eddy.mbta.BaseActivity;
import com.eddy.mbta.MyApplication;
import com.eddy.mbta.utils.NetUtil;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public Event event = BaseActivity.event;

    public LocationManager locationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equalsIgnoreCase(LocationManager.PROVIDERS_CHANGED_ACTION)) {

                boolean is_gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (event != null) {
                    event.onGpsChange(is_gps_enabled);
                }
            } else if (intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {

                int netWorkState = NetUtil.getNetWorkState(context);

                if (event != null) {
                    event.onNetChange(netWorkState);
                }
            }
        }
    }

    public interface Event {

        void onNetChange(int netMobile);

        void onGpsChange(boolean is_gps_enabled);
    }
}
