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
import com.eddy.mbta.utils.Utility;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TimeScheduleService extends Service {

    private static requestScheduleTask mTask;
    private List<Schedule> list = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();
    private Set<Integer> set = new TreeSet<>();

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

        if (TextUtils.isEmpty(stop_id)) {
            stop_id = intent.getStringExtra("stop_id");
        }
        Log.d("Service", "Station: " + stop_id);

        mTask = new requestScheduleTask(stop_id, TimeScheduleService.this);
        mTask.execute((Void) null);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int period = 10 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + period;

        Intent i = new Intent(this, TimeScheduleService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mTask.cancel(true);
        }
        Log.d("Service", "onDestroy() Executed");
    }

    private static class requestScheduleTask extends AsyncTask<Void, Void, TimeScheduleBean> {
        private final String mId;

        private WeakReference<TimeScheduleService> reference;

        requestScheduleTask(String id, TimeScheduleService context) {
            reference = new WeakReference<>(context);
            mId = id;
        }

        @Override
        protected TimeScheduleBean doInBackground(Void... params) {
            TimeScheduleBean timeScheduleItem = null;
            try {
                String url = "https://api-v3.mbta.com/predictions?filter[route_type]=0,1&sort=direction_id,time&include=trip&filter[stop]=" + mId;

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

            TimeScheduleService service = reference.get();
            if (service == null) return;

            service.set.clear();
            service.list.clear();
            service.map.clear();

            boolean isRedExist = false;

            int number = timeScheduleItem.getData().size();
            for (int i = 0; i < number; i++) {

                Schedule schedule = new Schedule();

                String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();

                if ("Orange".equals(route_id)) {
                    service.set.add(2);
                    schedule.setIcon(R.drawable.ic_orange);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[2]);
                        schedule.setEnd(Utility.end[2]);
                    } else {
                        schedule.setStart(Utility.end[2]);
                        schedule.setEnd(Utility.start[2]);
                    }
                } else if ("Red".equals(route_id)) {
                    isRedExist = true;
                    service.set.add(0);
                    schedule.setIcon(R.drawable.ic_red);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[0]);
                        schedule.setEnd(Utility.end[0]);
                        schedule.setTrip(timeScheduleItem.getData().get(i).getRelationships().getTrip().getData().getId());
                    } else {
                        schedule.setStart(Utility.end[0]);
                        schedule.setEnd(Utility.start[0]);
                    }
                } else if ("Mattapan".equals(route_id)) {
                    service.set.add(1);
                    schedule.setIcon(R.drawable.ic_mattapan);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[1]);
                        schedule.setEnd(Utility.end[1]);
                    } else {
                        schedule.setStart(Utility.end[1]);
                        schedule.setEnd(Utility.start[1]);
                    }
                } else if ("Blue".equals(route_id)) {
                    service.set.add(7);
                    schedule.setIcon(R.drawable.ic_blue);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[7]);
                        schedule.setEnd(Utility.end[7]);
                    } else {
                        schedule.setStart(Utility.end[7]);
                        schedule.setEnd(Utility.start[7]);
                    }
                } else if (route_id.endsWith("B")) {
                    service.set.add(3);
                    schedule.setIcon(R.drawable.ic_greenb);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[3]);
                        schedule.setEnd(Utility.end[3]);
                    } else {
                        schedule.setStart(Utility.end[3]);
                        schedule.setEnd(Utility.start[3]);
                    }
                } else if (route_id.endsWith("C")) {
                    service.set.add(4);
                    schedule.setIcon(R.drawable.ic_greenc);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[4]);
                        schedule.setEnd(Utility.end[4]);
                    } else {
                        schedule.setStart(Utility.end[4]);
                        schedule.setEnd(Utility.start[4]);
                    }
                } else if (route_id.endsWith("D")) {
                    service.set.add(5);
                    schedule.setIcon(R.drawable.ic_greend);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[5]);
                        schedule.setEnd(Utility.end[5]);
                    } else {
                        schedule.setStart(Utility.end[5]);
                        schedule.setEnd(Utility.start[5]);
                    }
                } else if (route_id.endsWith("E")) {
                    service.set.add(6);
                    schedule.setIcon(R.drawable.ic_greene);
                    if (direction_id == 0) {
                        schedule.setStart(Utility.start[6]);
                        schedule.setEnd(Utility.end[6]);
                    } else {
                        schedule.setStart(Utility.end[6]);
                        schedule.setEnd(Utility.start[6]);
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
                schedule.setDepTime(depTimeData);
                schedule.setRoute_id(route_id);
                schedule.setDirection_id(direction_id);

                service.list.add(schedule);
            }

            if (isRedExist) {
                for (TimeScheduleBean.IncludedBean m : timeScheduleItem.getIncluded()) {
                    if (m.getAttributes().getDirection_id() == 0
                            && m.getRelationships().getRoute().getData().getId().equals("Red")) {
                        service.map.put(m.getId(), m.getAttributes().getHeadsign());
                    }
                }

                for (Schedule s : service.list) {
                    if (s.getRoute_id().equals("Red") && s.getDirection_id() == 0) {
                        if (service.map.containsKey(s.getTrip())) {
                            s.setRedEndStation(service.map.get(s.getTrip()));
                        }
                    }
                }
            }

            Bean bean = new Bean();
            bean.setList(service.list);
            bean.setSet(service.set);

            Message message = new Message();
            message.what = 1;
            message.obj = bean;
            SchedulePopWindow.handler.sendMessage(message);

            mTask = null;
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }
}
