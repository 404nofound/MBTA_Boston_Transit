package com.eddy.mbta.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.AlertBean;
import com.eddy.mbta.utils.HttpClientUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlertsFragment extends Fragment {

    private AlertsViewModel alertsViewModel;

    private AlertAdapter adapter;
    private List<AlertBean.DataBean> alertList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alertsViewModel =
                ViewModelProviders.of(this).get(AlertsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alerts, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);
        alertsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        requestAlerts();

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(adapter);



        return root;
    }

    public void requestAlerts() {

        String url = "https://api-v3.mbta.com/alerts?filter[route_type]=0,1&filter[datetime]=NOW";

        HttpClientUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Gson gson = new Gson();
                AlertBean alertItem = gson.fromJson(response.body().string().trim(), AlertBean.class);

                for (int i = 0; i < alertItem.getData().size(); i++) {
                    alertList.add(alertItem.getData().get(i));
                    //final String cause = alertItem.getData().get(i).getAttributes().getCause();
                    //String header = alertItem.getData().get(i).getAttributes().getHeader();
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }




                /*final String cause = alertItem.getData().get(0).getAttributes().getCause();
                final String header = alertItem.getData().get(0).getAttributes().getHeader();

                if (true) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getActivity(), header+","+cause, Toast.LENGTH_SHORT).show();
                                //showProgress(false);
                                //updateMap();
                                //fab.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }*/

            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}