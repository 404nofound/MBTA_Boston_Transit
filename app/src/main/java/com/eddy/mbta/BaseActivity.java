package com.eddy.mbta;

import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eddy.mbta.receiver.GPSBroadcastReceiver;
import com.eddy.mbta.receiver.NetBroadcastReceiver;
import com.eddy.mbta.utils.LogUtil;

abstract public class BaseActivity extends AppCompatActivity implements
        NetBroadcastReceiver.NetEvent,
        GPSBroadcastReceiver.GpsEvent {

    public NetBroadcastReceiver netBroadcastReceiver;
    public static NetBroadcastReceiver.NetEvent net_event;

    public GPSBroadcastReceiver gpsBroadcastReceiver;
    public static GPSBroadcastReceiver.GpsEvent gps_event;


    @Override
    protected void onCreate(Bundle arg) {
        super.onCreate(arg);

        net_event = this;
        gps_event = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netBroadcastReceiver = new NetBroadcastReceiver();
        registerReceiver(netBroadcastReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationManager.MODE_CHANGED_ACTION);
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        gpsBroadcastReceiver = new GPSBroadcastReceiver();
        registerReceiver(gpsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netBroadcastReceiver);
        unregisterReceiver(gpsBroadcastReceiver);
        LogUtil.d("BaseActivity", "onDestroy()");
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
