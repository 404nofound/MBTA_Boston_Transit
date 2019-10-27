package com.eddy.mbta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.eddy.mbta.BaseActivity;
import com.eddy.mbta.MyApplication;

public class NetBroadcastReceiver extends BroadcastReceiver {

    public NetEvent event = BaseActivity.net_event;

    private static final int NETWORK_NONE = -1;
    private static final int NETWORK_MOBILE = 0;
    private static final int NETWORK_WIFI = 1;

    public ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);

    public NetworkInfo activeNetworkInfo = connectivityManager
            .getActiveNetworkInfo();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                int netWorkState = getNetWorkState();

                event.onNetChange(netWorkState);
            }
        }
    }

    public interface NetEvent {
        void onNetChange(int netMobile);
    }

    public int getNetWorkState() {
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }
}
