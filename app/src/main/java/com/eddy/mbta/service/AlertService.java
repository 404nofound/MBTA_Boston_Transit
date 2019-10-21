package com.eddy.mbta.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.eddy.mbta.json.AlertBean;
import com.eddy.mbta.ui.alerts.AlertsFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlertService extends Service {

    private requestAlertTask mTask;
    private List<AlertBean.DataBean> list = new ArrayList<>();

    public AlertService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AlertService", "onCreate() Executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlertService", "onStartCommand() Executed");

        mTask = new requestAlertTask();
        mTask.execute((Void) null);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int period = 5 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + period;

        Intent i = new Intent(this, AlertService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AlertService", "onDestroy() Executed");
    }

    private class requestAlertTask extends AsyncTask<Void, Void, AlertBean> {

        requestAlertTask() {
        }

        @Override
        protected AlertBean doInBackground(Void... params) {
            AlertBean alertItem = null;
            try {
                String url = "https://api-v3.mbta.com/alerts?filter[route_type]=0,1&sort=lifecycle";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                alertItem = gson.fromJson(response.body().string().trim(), AlertBean.class);

            } catch (Exception e) {
            }
            return alertItem;
        }

        @Override
        protected void onPostExecute(final AlertBean alertItem) {

            list.clear();
            list.addAll(alertItem.getData());

            Message message = new Message();
            message.what = 1;
            message.obj = list;
            AlertsFragment.handler.sendMessage(message);

            Log.d("AlertService", "Service:"+list.size()+"");

            mTask = null;
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }
}