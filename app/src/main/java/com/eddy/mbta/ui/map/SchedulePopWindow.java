package com.eddy.mbta.ui.map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.TimeScheduleBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SchedulePopWindow extends PopupWindow {

    private TimeScheduleAdapter adapter;
    private List<Schedule> mTotalList = new ArrayList<>();
    private List<Schedule> mScheduleList = new ArrayList<>();

    private View view;
    private TextView startStation, endStation;
    private ImageView red, mattapan, orange, greenb, greenc, greend, greene, blue;

    public String[] start = {"Alewife", "Ashmont", "Oak Grove", "Park St", "North Station", "Park St", "Lechmere", "Bowdoin"};
    public String[] end = {"Braintree", "Mattapan", "Forest Hills", "Boston College", "Cleveland Circle", "Riverside", "Health St", "Wonderland"};

    private requestScheduleTask mTask;

    public SchedulePopWindow(Context mContext, String station_name, String stop_id) {

        view = LayoutInflater.from(mContext).inflate(R.layout.schedule_recyclerview, null);

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
        /*this.view.setOnTouchListener(new View.OnTouchListener() {
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
        });*/

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

                //mScheduleList.addAll(timeScheduleItem.getData());

            } catch (Exception e) {
            }
            return timeScheduleItem;
        }

        @Override
        protected void onPostExecute(final TimeScheduleBean timeScheduleItem) {

            int number = timeScheduleItem.getData().size();
            for (int i = 0; i < number; i++) {

                Schedule schedule = new Schedule();

                String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();

                if ("Orange".equals(route_id)) {
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

                mScheduleList.add(schedule);
            }

            startStation.setText(mScheduleList.get(0).getStart());
            endStation.setText(mScheduleList.get(0).getEnd());
            adapter.notifyDataSetChanged();

            mTask = null;

        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }


}


