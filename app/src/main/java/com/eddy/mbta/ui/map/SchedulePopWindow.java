package com.eddy.mbta.ui.map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.TimeScheduleBean;
import com.eddy.mbta.utils.HttpClientUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SchedulePopWindow extends PopupWindow {

    private TimeScheduleAdapter adapter;

    private List<TimeScheduleBean.DataBeanXXXX> mScheduleList = new ArrayList<>();

    private Context mContext;
    private View view;

    private FragmentActivity activity;

    TimeScheduleBean timeScheduleItem;

    public SchedulePopWindow(Context mContext, String stop_id, FragmentActivity act) {

        view = LayoutInflater.from(mContext).inflate(R.layout.schedule_recyclerview, null);

        activity = act;

        requestSchedule(stop_id);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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

    private void requestSchedule(String id) {
        String url = "https://api-v3.mbta.com/predictions?filter[route_type]=0,1&sort=time&filter[stop]=" + id;

        Log.d("HHH", url);
        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                timeScheduleItem = gson.fromJson(response.body().string().trim(), TimeScheduleBean.class);

                mScheduleList.addAll(timeScheduleItem.getData());
                //adapter.notifyDataSetChanged();

                    /*String arrTimeData = timeScheduleItem.getData().get(i).getAttributes().getArrival_time();
                    String depTimeData = timeScheduleItem.getData().get(i).getAttributes().getDeparture_time();

                    String route_id = timeScheduleItem.getData().get(i).getRelationships().getRoute().getData().getId();
                    int direction_id = timeScheduleItem.getData().get(i).getAttributes().getDirection_id();
                    String stop_id = timeScheduleItem.getData().get(i).getRelationships().getStop().getData().getId();

                    Log.d("DETAIL", route_id+","+direction_id+","+
                            stop_id+","+arrTimeData+","+depTimeData);*/


                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(mContext, nearbyStationItem.getIncluded().get(0).getAttributes().getName(), Toast.LENGTH_LONG).show();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

}


