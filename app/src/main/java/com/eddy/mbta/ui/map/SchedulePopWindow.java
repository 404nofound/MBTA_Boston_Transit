package com.eddy.mbta.ui.map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.Schedule;
import com.eddy.mbta.json.TimeScheduleBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SchedulePopWindow extends PopupWindow implements View.OnClickListener {

    private TimeScheduleAdapter adapter;
    private List<Schedule> mTotalList = new ArrayList<>();
    private List<Schedule> mScheduleList = new ArrayList<>();

    private View view;
    private TextView startStation, endStation;
    private ImageView red, mattapan, orange, greenb, greenc, greend, greene, blue, overturn;

    public String[] route_id = {"Red", "Mattapan", "Orange", "Green-B", "Green-C", "Green-D", "Green-E", "Blue"};
    public String[] start = {"Alewife", "Ashmont", "Oak Grove", "Park St", "North Station", "Park St", "Lechmere", "Bowdoin"};
    public String[] end = {"Braintree", "Mattapan", "Forest Hills", "Boston College", "Cleveland Circle", "Riverside", "Health St", "Wonderland"};

    private requestScheduleTask mTask;

    public SchedulePopWindow(Context mContext, String station_name, String stop_id) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_window_schedule, null);

        TextView stationName = view.findViewById(R.id.station_name);

        startStation = view.findViewById(R.id.start_station);
        endStation = view.findViewById(R.id.end_station);

        red = view.findViewById(R.id.red);
        mattapan = view.findViewById(R.id.mattapan);
        orange = view.findViewById(R.id.orange);
        greenb = view.findViewById(R.id.greenb);
        greenc = view.findViewById(R.id.greenc);
        greend = view.findViewById(R.id.greend);
        greene = view.findViewById(R.id.greene);
        blue = view.findViewById(R.id.blue);
        overturn = view.findViewById(R.id.overturn);

        red.setOnClickListener(this);
        mattapan.setOnClickListener(this);
        orange.setOnClickListener(this);
        greenb.setOnClickListener(this);
        greenc.setOnClickListener(this);
        greend.setOnClickListener(this);
        greene.setOnClickListener(this);
        blue.setOnClickListener(this);
        overturn.setOnClickListener(this);

        stationName.setText(station_name);

        mTask = new requestScheduleTask(stop_id);
        mTask.execute((Void) null);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TimeScheduleAdapter(mScheduleList);
        recyclerView.setAdapter(adapter);

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_up_anim);
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

            int first_tag = 10;
            int number = timeScheduleItem.getData().size();
            for (int i = 0; i < number; i++) {

                Schedule schedule = new Schedule();

                String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();

                if ("Orange".equals(route_id)) {
                    if (first_tag > 2) first_tag = 2;
                    orange.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_orange);
                    if (direction_id == 0) {
                        schedule.setStart(start[2]);
                        schedule.setEnd(end[2]);
                    } else {
                        schedule.setStart(end[2]);
                        schedule.setEnd(start[2]);
                    }
                } else if ("Red".equals(route_id)) {
                    if (first_tag > 0) first_tag = 0;
                    red.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_red);
                    if (direction_id == 0) {
                        schedule.setStart(start[0]);
                        schedule.setEnd(end[0]);
                    } else {
                        schedule.setStart(end[0]);
                        schedule.setEnd(start[0]);
                    }
                } else if ("Mattapan".equals(route_id)) {
                    if (first_tag > 1) first_tag = 1;
                    mattapan.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_mattapan);
                    if (direction_id == 0) {
                        schedule.setStart(start[1]);
                        schedule.setEnd(end[1]);
                    } else {
                        schedule.setStart(end[1]);
                        schedule.setEnd(start[1]);
                    }
                } else if ("Blue".equals(route_id)) {
                    if (first_tag > 7) first_tag = 7;
                    blue.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_blue);
                    if (direction_id == 0) {
                        schedule.setStart(start[7]);
                        schedule.setEnd(end[7]);
                    } else {
                        schedule.setStart(end[7]);
                        schedule.setEnd(start[7]);
                    }
                } else if (route_id.endsWith("B")) {
                    if (first_tag > 3) first_tag = 3;
                    greenb.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greenb);
                    if (direction_id == 0) {
                        schedule.setStart(start[3]);
                        schedule.setEnd(end[3]);
                    } else {
                        schedule.setStart(end[3]);
                        schedule.setEnd(start[3]);
                    }
                } else if (route_id.endsWith("C")) {
                    if (first_tag > 4) first_tag = 4;
                    greenc.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greenc);
                    if (direction_id == 0) {
                        schedule.setStart(start[4]);
                        schedule.setEnd(end[4]);
                    } else {
                        schedule.setStart(end[4]);
                        schedule.setEnd(start[4]);
                    }
                } else if (route_id.endsWith("D")) {
                    if (first_tag > 5) first_tag = 5;
                    greend.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greend);
                    if (direction_id == 0) {
                        schedule.setStart(start[5]);
                        schedule.setEnd(end[5]);
                    } else {
                        schedule.setStart(end[5]);
                        schedule.setEnd(start[5]);
                    }
                } else if (route_id.endsWith("E")) {
                    if (first_tag > 6) first_tag = 6;
                    greene.setVisibility(View.VISIBLE);
                    schedule.setIcon(R.drawable.ic_greene);
                    if (direction_id == 0) {
                        schedule.setStart(start[6]);
                        schedule.setEnd(end[6]);
                    } else {
                        schedule.setStart(end[6]);
                        schedule.setEnd(start[6]);
                    }
                }

                schedule.setArrTime(arrTimeData);
                schedule.setDepTime(depTimeData);
                schedule.setRoute_id(route_id);
                schedule.setDirection_id(direction_id);

                mTotalList.add(schedule);
            }

            if (first_tag != 10) {
                startStation.setText(start[first_tag]);
                endStation.setText(end[first_tag]);
                setTrainLine(route_id[first_tag], 0);
            }

            mTask = null;
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }

    private void setTrainLine(String route_id, int direction_id) {
        mScheduleList.clear();
        for (Schedule s : mTotalList) {
            if (s.getRoute_id().equals(route_id) && s.getDirection_id() == direction_id) {
                mScheduleList.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.red:
                startStation.setText(start[0]);
                endStation.setText(end[0]);
                setTrainLine("Red", 0);
                break;
            case R.id.mattapan:
                startStation.setText(start[1]);
                endStation.setText(end[1]);
                setTrainLine("Mattapan", 0);
                break;
            case R.id.orange:
                startStation.setText(start[2]);
                endStation.setText(end[2]);
                setTrainLine("Orange", 0);
                break;
            case R.id.greenb:
                startStation.setText(start[3]);
                endStation.setText(end[3]);
                setTrainLine("Green-B", 0);
                break;
            case R.id.greenc:
                startStation.setText(start[4]);
                endStation.setText(end[4]);
                setTrainLine("Green-C", 0);
                break;
            case R.id.greend:
                startStation.setText(start[5]);
                endStation.setText(end[5]);
                setTrainLine("Green-D", 0);
                break;
            case R.id.greene:
                startStation.setText(start[6]);
                endStation.setText(end[6]);
                setTrainLine("Green-E", 0);
                break;
            case R.id.blue:
                startStation.setText(start[7]);
                endStation.setText(end[7]);
                setTrainLine("Blue", 0);
                break;
            case R.id.overturn:
                String s = startStation.getText().toString();
                String e = endStation.getText().toString();
                startStation.setText(e);
                endStation.setText(s);
                if (mScheduleList.get(0).getDirection_id() == 0) {
                    setTrainLine(mScheduleList.get(0).getRoute_id(), 1);
                } else {
                    setTrainLine(mScheduleList.get(0).getRoute_id(), 0);
                }
                break;
            default:
                break;
        }
    }
}


