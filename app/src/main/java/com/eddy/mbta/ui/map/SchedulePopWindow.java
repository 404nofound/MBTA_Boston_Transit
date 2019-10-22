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
import com.eddy.mbta.utils.Utility;

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

        Log.d("Service", Utility.start[route]+","+Utility.end[route]+","+Utility.route_id[route]+","+direction);

    }

    static class CustomerHandler extends Handler {

        private final WeakReference<SchedulePopWindow> mWindow;
        public CustomerHandler(SchedulePopWindow context) {
            mWindow = new WeakReference<SchedulePopWindow> (context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                Bean obj = (Bean) msg.obj;
                SchedulePopWindow windowReference = mWindow.get();
                if(windowReference != null) {
                    windowReference.mTotalList = obj.getList();
                    windowReference.set = obj.getSet();
                    windowReference.init();
                }
            }
        }
    }

    private void setTrainLine(int mRoute, int direction_id) {
        route = mRoute;
        direction = direction_id;

        if (direction_id == 0) {
            startStation.setText(Utility.start[mRoute]);
            endStation.setText(Utility.end[mRoute]);
        } else {
            startStation.setText(Utility.end[mRoute]);
            endStation.setText(Utility.start[mRoute]);
        }

        int preSize = mScheduleList.size();
        if (preSize != 0) {
            mScheduleList.clear();
            adapter.notifyItemRangeRemoved(0, preSize);
        }

        for (Schedule s : mTotalList) {
            if (s.getRoute_id().equals(Utility.route_id[mRoute]) && s.getDirection_id() == direction_id) {
                mScheduleList.add(s);
            }
        }
        Log.d("Service", "Set FUnction"+mTotalList.size());
        Log.d("Service", "Set FUnction: Show List:"+mScheduleList.size());

        if (mScheduleList.size() != 0) {
            holderView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyItemRangeInserted(0, mScheduleList.size());
        } else {
            holderView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
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


