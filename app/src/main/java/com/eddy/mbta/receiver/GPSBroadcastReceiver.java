package com.eddy.mbta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.TextUtils;

import com.eddy.mbta.BaseActivity;

public class GPSBroadcastReceiver extends BroadcastReceiver {

    public GpsEvent event = BaseActivity.gps_event;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(intent.getAction())) {
            /*if (intent.getAction().equalsIgnoreCase(LocationManager.MODE_CHANGED_ACTION)
                    || intent.getAction().equalsIgnoreCase(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                event.onGpsChange();
            }*/
            if (intent.getAction().equalsIgnoreCase(LocationManager.MODE_CHANGED_ACTION)) {
                event.onGpsChange();
            }
        }
    }

    public interface GpsEvent {
        public void onGpsChange();
    }
}

