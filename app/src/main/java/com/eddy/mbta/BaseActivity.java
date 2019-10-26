package com.eddy.mbta;

import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.eddy.mbta.receiver.GPSBroadcastReceiver;
import com.eddy.mbta.receiver.NetBroadcastReceiver;
import com.eddy.mbta.utils.NetUtil;

abstract public class BaseActivity extends AppCompatActivity implements
        NetBroadcastReceiver.NetEvent,
        GPSBroadcastReceiver.GpsEvent {

    public NetBroadcastReceiver netBroadcastReceiver;
    public static NetBroadcastReceiver.NetEvent net_event;

    public GPSBroadcastReceiver gpsBroadcastReceiver;
    public static GPSBroadcastReceiver.GpsEvent gps_event;

    //private LocationManager mLocationManager;
    /**
     * 网络类型
     */
    private int netMobile;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);

        net_event = this;
        gps_event = this;

        //实例化IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netBroadcastReceiver = new NetBroadcastReceiver();
        registerReceiver(netBroadcastReceiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationManager.MODE_CHANGED_ACTION);
        //intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        gpsBroadcastReceiver = new GPSBroadcastReceiver();
        registerReceiver(gpsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netBroadcastReceiver);
        unregisterReceiver(gpsBroadcastReceiver);
    }
    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(BaseActivity.this);
        return isNetConnect();
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        this.netMobile = netMobile;
        isNetConnect();

    }

    @Override
    public void onGpsChange() {

    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;

        }
        return false;
    }

}
