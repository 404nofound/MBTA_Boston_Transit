package com.eddy.mbta.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.eddy.mbta.R;
import com.eddy.mbta.json.Schedule;
import com.eddy.mbta.json.TimeScheduleBean;
import com.eddy.mbta.ui.map.SchedulePopWindow;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TimeScheduleService extends Service {

    private requestScheduleTask mTask;
    private List<Schedule> list = new ArrayList<>();
    private Set<Integer> set = new TreeSet<>();

    public String[] route_id = {"Red", "Mattapan", "Orange", "Green-B", "Green-C", "Green-D", "Green-E", "Blue"};
    public String[] start = {"Alewife", "Ashmont", "Oak Grove", "Park St", "North Station", "Park St", "Lechmere", "Bowdoin"};
    public String[] end = {"Braintree", "Mattapan", "Forest Hills", "Boston College", "Cleveland Circle", "Riverside", "Health St", "Wonderland"};

    private String stop_id;

    public TimeScheduleService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "onCreate() Executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand() Executed");

        String id = intent.getStringExtra("stop_id");

        if (TextUtils.isEmpty(stop_id)) {
            stop_id = id;
        } else if (!TextUtils.isEmpty(id) && !stop_id.equals(stop_id)) {
            stop_id = id;
        }

        Log.d("Service", "Station: " + stop_id);
        mTask = new requestScheduleTask(stop_id);
        mTask.execute((Void) null);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int period = 10000;
        long triggerAtTime = SystemClock.elapsedRealtime() + period;

        Intent i = new Intent(this, TimeScheduleService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        //manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy() Executed");
    }

    private class requestScheduleTask extends AsyncTask<Void, Void, TimeScheduleBean> {
        private final String mId;

        requestScheduleTask(String id) {
            mId = id;
        }

        @Override
        protected TimeScheduleBean doInBackground(Void... params) {
            TimeScheduleBean timeScheduleItem = null;
            try {
                String url = "https://api-v3.mbta.com/predictions?filter[route_type]=0,1&sort=direction_id,time&filter[stop]=" + mId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                timeScheduleItem = gson.fromJson(response.body().string().trim(), TimeScheduleBean.class);

            } catch (Exception e) {

            }
            return timeScheduleItem;
        }

        @Override
        protected void onPostExecute(final TimeScheduleBean timeScheduleItem) {

            //int first_tag = 10;
            set.clear();
            list.clear();
            int number = timeScheduleItem.getData().size();
            for (int i = 0; i < number; i++) {

                Schedule schedule = new Schedule();

                String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();

                if ("Orange".equals(route_id)) {
                    set.add(2);
                    //if (first_tag > 2) first_tag = 2;
                    //orange.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_orange);
                    if (direction_id == 0) {
                        schedule.setStart(start[2]);
                        schedule.setEnd(end[2]);
                    } else {
                        schedule.setStart(end[2]);
                        schedule.setEnd(start[2]);
                    }
                } else if ("Red".equals(route_id)) {
                    set.add(0);
                    //if (first_tag > 0) first_tag = 0;
                    //red.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_red);
                    if (direction_id == 0) {
                        schedule.setStart(start[0]);
                        schedule.setEnd(end[0]);
                    } else {
                        schedule.setStart(end[0]);
                        schedule.setEnd(start[0]);
                    }
                } else if ("Mattapan".equals(route_id)) {
                    set.add(1);
                    //if (first_tag > 1) first_tag = 1;
                    //mattapan.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_mattapan);
                    if (direction_id == 0) {
                        schedule.setStart(start[1]);
                        schedule.setEnd(end[1]);
                    } else {
                        schedule.setStart(end[1]);
                        schedule.setEnd(start[1]);
                    }
                } else if ("Blue".equals(route_id)) {
                    set.add(7);
                    //if (first_tag > 7) first_tag = 7;
                    //blue.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_blue);
                    if (direction_id == 0) {
                        schedule.setStart(start[7]);
                        schedule.setEnd(end[7]);
                    } else {
                        schedule.setStart(end[7]);
                        schedule.setEnd(start[7]);
                    }
                } else if (route_id.endsWith("B")) {
                    set.add(3);
                    //if (first_tag > 3) first_tag = 3;
                    //greenb.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greenb);
                    if (direction_id == 0) {
                        schedule.setStart(start[3]);
                        schedule.setEnd(end[3]);
                    } else {
                        schedule.setStart(end[3]);
                        schedule.setEnd(start[3]);
                    }
                } else if (route_id.endsWith("C")) {
                    set.add(4);
                    //if (first_tag > 4) first_tag = 4;
                    //greenc.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greenc);
                    if (direction_id == 0) {
                        schedule.setStart(start[4]);
                        schedule.setEnd(end[4]);
                    } else {
                        schedule.setStart(end[4]);
                        schedule.setEnd(start[4]);
                    }
                } else if (route_id.endsWith("D")) {
                    set.add(5);
                    //if (first_tag > 5) first_tag = 5;
                    //greend.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greend);
                    if (direction_id == 0) {
                        schedule.setStart(start[5]);
                        schedule.setEnd(end[5]);
                    } else {
                        schedule.setStart(end[5]);
                        schedule.setEnd(start[5]);
                    }
                } else if (route_id.endsWith("E")) {
                    set.add(6);
                    //if (first_tag > 6) first_tag = 6;
                    //greene.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greene);
                    if (direction_id == 0) {
                        schedule.setStart(start[6]);
                        schedule.setEnd(end[6]);
                    } else {
                        schedule.setStart(end[6]);
                        schedule.setEnd(start[6]);
                    }
                }

                if (!TextUtils.isEmpty(arrTimeData)||!TextUtils.isEmpty(depTimeData)) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date showDate;
                        if (TextUtils.isEmpty(arrTimeData)) {
                            showDate = df.parse(depTimeData.substring(0, 19).replace("T", " "));
                        } else {
                            showDate = df.parse(arrTimeData.substring(0, 19).replace("T", " "));
                        }
                        //Date arr = df.parse(arrTimeData.substring(0, 19).replace("T", " "));
                        //Date dep = df.parse(arrTimeData.substring(0, 19).replace("T", " "));

                        Date nowDate = df.parse(df.format(new Date()));

                        long timeDifference = (showDate.getTime() - nowDate.getTime())/1000;
                        if (timeDifference < 0) {
                            //timeDifference = (dep.getTime() - nowDate.getTime())/1000;
                            schedule.setArrTime("Boarding");
                        } else if (timeDifference > 60) {
                            schedule.setArrTime(timeDifference/60+"m");
                        } else {
                            schedule.setArrTime("Coming Soon");
                            //schedule.setArrTime(timeDifference+"s");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    continue;
                }
                //schedule.setArrTime(arrTimeData);
                schedule.setDepTime(depTimeData);
                schedule.setRoute_id(route_id);
                schedule.setDirection_id(direction_id);

                list.add(schedule);
            }

            Bean bean = new Bean();
            bean.setList(list);
            bean.setSet(set);

            Message message = new Message();
            message.what = 1;
            message.obj = bean;
            SchedulePopWindow.handler.sendMessage(message);

            Log.d("Service", "Service:"+list.size()+"");

            mTask = null;
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }
}
