package com.eddy.mbta.ui.alerts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eddy.mbta.R;
import com.eddy.mbta.json.AlertBean;
import com.eddy.mbta.service.AlertService;
import com.eddy.mbta.utils.HttpClientUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlertsFragment extends Fragment {

    private AlertAdapter adapter;
    private List<AlertBean.DataBean> alertList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;

    public static Handler handler;
    private Context mContext;

    public static AlertsFragment newInstance() {
        Bundle args = new Bundle ();

        AlertsFragment fragment = new AlertsFragment ();
        fragment.setArguments (args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_alerts, container, false);

        mContext = getActivity();

        Intent startIntent = new Intent(mContext, AlertService.class);
        mContext.startService(startIntent);

        handler = new CustomerHandler(this);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(adapter);

        swipeRefresh = root.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return root;
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestAlerts();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    class CustomerHandler extends Handler {

        private final WeakReference<AlertsFragment> mActivity;
        public CustomerHandler(AlertsFragment activity) {
            mActivity = new WeakReference<AlertsFragment> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {

                AlertsFragment activity = mActivity.get();
                if(activity != null) {
                    List<AlertBean.DataBean> list = (List<AlertBean.DataBean>) msg.obj;
                    Log.d("AlertService", "Fragment:"+list.size());
                    alertList.clear();
                    alertList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void requestAlerts() {

        String url = "https://api-v3.mbta.com/alerts?filter[route_type]=0,1&sort=lifecycle";

        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                AlertBean alertItem = gson.fromJson(response.body().string().trim(), AlertBean.class);

                alertList.clear();
                alertList.addAll(alertItem.getData());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Internet Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

        Intent stopIntent = new Intent(mContext, AlertService.class);
        mContext.stopService(stopIntent);

        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(mContext, 0, stopIntent, 0);
        manager.cancel(pi);
    }
}