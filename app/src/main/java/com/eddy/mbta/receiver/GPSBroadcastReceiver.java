package com.eddy.mbta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.TextUtils;

import com.eddy.mbta.BaseActivity;
import com.eddy.mbta.MyApplication;

public class GPSBroadcastReceiver extends BroadcastReceiver {

    public GpsEvent event = BaseActivity.gps_event;
    public LocationManager locationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(intent.getAction())) {
            /*if (intent.getAction().equalsIgnoreCase(LocationManager.MODE_CHANGED_ACTION)
                    || intent.getAction().equalsIgnoreCase(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                event.onGpsChange();
            }*/
            if (intent.getAction().equalsIgnoreCase(LocationManager.MODE_CHANGED_ACTION)) {

                boolean is_gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                event.onGpsChange(is_gps_enabled);
            }
        }
    }

    public interface GpsEvent {
        void onGpsChange(boolean is_gps_enabled);
    }
}

