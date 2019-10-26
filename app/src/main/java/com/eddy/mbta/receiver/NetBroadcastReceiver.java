package com.eddy.mbta.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.eddy.mbta.BaseActivity;
import com.eddy.mbta.utils.NetUtil;

public class NetBroadcastReceiver extends BroadcastReceiver {

    public NetEvent event = BaseActivity.net_event;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            // 接口回调传过去状态的类型
            event.onNetChange(netWorkState);
        }
    }

    public interface NetEvent {
        public void onNetChange(int netMobile);
    }
}
