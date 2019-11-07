package com.eddy.mbta;

import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eddy.mbta.receiver.MyBroadcastReceiver;
import com.eddy.mbta.utils.LogUtil;

abstract public class BaseActivity extends AppCompatActivity implements
        MyBroadcastReceiver.Event {

    public MyBroadcastReceiver broadcastReceiver;
    public static MyBroadcastReceiver.Event event;

    @Override
    protected void onCreate(Bundle arg) {
        super.onCreate(arg);

        event = this;

        broadcastReceiver = new MyBroadcastReceiver();

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { }

        IntentFilter filter = new IntentFilter();
        //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //filter.addAction(LocationManager.MODE_CHANGED_ACTION);

        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("BaseActivity", "onDestroy()");

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        MyApplication.NET_STATUS = netMobile;
        LogUtil.d("BaseActivity", "NET: "+netMobile);
    }

    @Override
    public void onGpsChange(boolean is_gps_enabled) {
        MyApplication.GPS_ENABLED = is_gps_enabled;
        LogUtil.d("BaseActivity", "GPS: "+is_gps_enabled);
    }
}
