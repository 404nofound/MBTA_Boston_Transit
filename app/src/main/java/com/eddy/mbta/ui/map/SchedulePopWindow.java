package com.eddy.mbta.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.eddy.mbta.service.Bean;
import com.eddy.mbta.service.TimeScheduleService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SchedulePopWindow extends PopupWindow implements View.OnClickListener {

    private TimeScheduleAdapter adapter;
    private List<Schedule> mTotalList = new ArrayList<>();
    private List<Schedule> mScheduleList = new ArrayList<>();
    private Set<Integer> set = new TreeSet<>();

    private View view;
    private RecyclerView recyclerView;
    private View holderView;
    private TextView startStation, endStation;
    private ImageView red, mattapan, orange, greenb, greenc, greend, greene, blue, overturn;

    public String[] route_id = {"Red", "Mattapan", "Orange", "Green-B", "Green-C", "Green-D", "Green-E", "Blue"};
    public String[] start = {"Alewife", "Ashmont", "Oak Grove", "Park St", "North Station", "Park St", "Lechmere", "Bowdoin"};
    public String[] end = {"Braintree", "Mattapan", "Forest Hills", "Boston College", "Cleveland Circle", "Riverside", "Health St", "Wonderland"};

    private int route = -1;
    private int direction = 0;

    public static Handler handler;

    public SchedulePopWindow(final Context mContext, String station_name, String stop_id) {

        view = LayoutInflater.from(mContext).inflate(R.layout.pop_window_schedule, null);

        handler = new CustomerHandler(this);

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
        holderView = view.findViewById(R.id.holder_place);

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

        Intent startIntent = new Intent(mContext, TimeScheduleService.class);
        startIntent.putExtra("stop_id", stop_id);
        mContext.startService(startIntent);

        //Intent bindIntent = new Intent(mContext, TimeScheduleService.class);
        //mContext.bindService(startIntent, connection, Context.BIND_AUTO_CREATE);

        //mTask = new requestScheduleTask(stop_id);
        //mTask.execute((Void) null);

        recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TimeScheduleAdapter(mScheduleList);
        recyclerView.setAdapter(adapter);

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y <= height) {
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

    private void init() {
        int first_tag = 10;

        red.setVisibility(View.GONE);
        mattapan.setVisibility(View.GONE);
        orange.setVisibility(View.GONE);
        greenb.setVisibility(View.GONE);
        greenc.setVisibility(View.GONE);
        greend.setVisibility(View.GONE);
        greene.setVisibility(View.GONE);
        blue.setVisibility(View.GONE);

        for (int i : set) {
            if (i == 0) {
                if (first_tag > 0) first_tag = 0;
                red.setVisibility(View.VISIBLE);
            } else if (i == 1) {
                if (first_tag > 1) first_tag = 1;
                mattapan.setVisibility(View.VISIBLE);
            } else if (i == 2) {
                if (first_tag > 2) first_tag = 2;
                orange.setVisibility(View.VISIBLE);
            } else if (i == 3) {
                if (first_tag > 3) first_tag = 3;
                greenb.setVisibility(View.VISIBLE);
            } else if (i == 4) {
                if (first_tag > 4) first_tag = 4;
                greenc.setVisibility(View.VISIBLE);
            } else if (i == 5) {
                if (first_tag > 5) first_tag = 5;
                greend.setVisibility(View.VISIBLE);
            } else if (i == 6) {
                if (first_tag > 6) first_tag = 6;
                greene.setVisibility(View.VISIBLE);
            } else if (i == 7) {
                if (first_tag > 7) first_tag = 7;
                blue.setVisibility(View.VISIBLE);
            }
        }


        if (route == -1) {
            route = first_tag;

            if (first_tag == 0) {
                setBackground(red);
            } else if (first_tag == 1) {
                setBackground(mattapan);
            } else if (first_tag == 2) {
                setBackground(orange);
            } else if (first_tag == 3) {
                setBackground(greenb);
            } else if (first_tag == 4) {
                setBackground(greenc);
            } else if (first_tag == 5) {
                setBackground(greend);
            } else if (first_tag == 6) {
                setBackground(greene);
            } else if (first_tag == 7) {
                setBackground(blue);
            }
        }

        setTrainLine(route, direction);

        Log.d("Service", start[route]+","+end[route]+","+route_id[route]+","+direction);

    }

    class CustomerHandler extends Handler {

        private final WeakReference<SchedulePopWindow> mActivity;
        public CustomerHandler(SchedulePopWindow activity) {
            mActivity=new WeakReference<SchedulePopWindow>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                Bean obj = (Bean) msg.obj;
                SchedulePopWindow activity = mActivity.get();
                if(activity != null) {
                    mTotalList = obj.getList();
                    set = obj.getSet();
                    init();
                    /*Log.d("Service", "PopWindow:"+obj.getList().size()+"");
                    if (first_tag == 10) {
                        //mTotalList.clear();
                        //set.clear();
                        Log.d("Service", "1 Works");
                        mTotalList = obj.getList();
                        set = obj.getSet();
                        Log.d("Service", "1 inside:"+mTotalList.size());
                        init();
                    } else {
                        Log.d("Service", "2 Works");
                        //mTotalList.clear();
                        mTotalList = obj.getList();
                        Log.d("Service", "2 inside:OBJ"+obj.getList().size());
                        Log.d("Service", "2 inside:TOTAL"+mTotalList.size());
                        init();
                    }*/

                }
            }
        }
    }

    /*private class requestScheduleTask extends AsyncTask<Void, Void, TimeScheduleBean> {
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
                            schedule.setArrTime(timeDifference+"s");
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

                mTotalList.add(schedule);
            }

            if (first_tag != 10) {
                if (first_tag == 0) {
                    red.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 1) {
                    mattapan.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 2) {
                    orange.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 3) {
                    greenb.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 4) {
                    greenc.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 5) {
                    greend.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 6) {
                    greene.setBackgroundResource(R.drawable.bg_border);
                } else if (first_tag == 7) {
                    blue.setBackgroundResource(R.drawable.bg_border);
                }

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
    }*/

    private void setTrainLine(int mRoute, int direction_id) {
        route = mRoute;
        direction = direction_id;

        if (direction_id == 0) {
            startStation.setText(start[mRoute]);
            endStation.setText(end[mRoute]);
        } else {
            startStation.setText(end[mRoute]);
            endStation.setText(start[mRoute]);
        }

        mScheduleList.clear();
        for (Schedule s : mTotalList) {
            if (s.getRoute_id().equals(route_id[mRoute]) && s.getDirection_id() == direction_id) {
                mScheduleList.add(s);
            }
        }
        Log.d("Service", "Set FUnction"+mTotalList.size());
        Log.d("Service", "Set FUnction: Show List:"+mScheduleList.size());

        if (mScheduleList.size() != 0) {
            holderView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            holderView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.red:
                setBackground(red);
                setTrainLine(0, 0);
                break;
            case R.id.mattapan:
                setBackground(mattapan);
                setTrainLine(1, 0);
                break;
            case R.id.orange:
                setBackground(orange);
                setTrainLine(2, 0);
                break;
            case R.id.greenb:
                setBackground(greenb);
                setTrainLine(3, 0);
                break;
            case R.id.greenc:
                setBackground(greenc);
                setTrainLine(4, 0);
                break;
            case R.id.greend:
                setBackground(greend);
                setTrainLine(5, 0);
                break;
            case R.id.greene:
                setBackground(greene);
                setTrainLine(6, 0);
                break;
            case R.id.blue:
                setBackground(blue);
                setTrainLine(7, 0);
                break;
            case R.id.overturn:
                if (direction == 0) {
                    setTrainLine(route, 1);
                } else {
                    setTrainLine(route, 0);
                }
                break;
            default:
                break;
        }
    }

    private void setBackground(ImageView view) {
        red.setBackgroundResource(0);
        mattapan.setBackgroundResource(0);
        orange.setBackgroundResource(0);
        greenb.setBackgroundResource(0);
        greenc.setBackgroundResource(0);
        greend.setBackgroundResource(0);
        greene.setBackgroundResource(0);
        blue.setBackgroundResource(0);

        view.setBackgroundResource(R.drawable.bg_border);
    }

}


