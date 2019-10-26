package com.eddy.mbta.ui.stations;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eddy.mbta.BaseActivity;
import com.eddy.mbta.MyApplication;
import com.eddy.mbta.R;
import com.eddy.mbta.db.Station;
import com.eddy.mbta.utils.HttpClientUtil;
import com.eddy.mbta.utils.NetUtil;
import com.eddy.mbta.utils.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailStationActivity extends BaseActivity {

    private List<Station> dataList = new ArrayList<>();
    private List<Station> stationList = new ArrayList<>();

    private StationAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    private String train;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_station);

        train = getIntent().getStringExtra("train");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(train);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        Window window = this.getWindow();
        adapter = new StationAdapter(stationList, window);
        recyclerView.setAdapter(adapter);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        queryStation();
    }

    private void queryStation() {
        dataList = LitePal.where("trainName = ?", train).find(Station.class);
        if (dataList.size() > 0) {
            if (stationList.size() != 0) {
                stationList.clear();
                stationList.addAll(dataList);
                adapter.notifyItemRangeChanged(0, stationList.size());
            } else {
                stationList.addAll(dataList);
                adapter.notifyItemRangeInserted(0, stationList.size());
            }
        } else {
            queryFromServer();
        }
    }

    private void queryFromServer() {

        if (!NetUtil.isNetConnect(MyApplication.getContext())) {
            Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://209.222.10.90/stop.php";

        HttpClientUtil.getStation(url, train.replace("-", "").toLowerCase(), new Callback() {
            @Override
            public void onResponse(Call call, Response response)  throws IOException {
                String responseText = response.body().string();

                boolean result = Utility.handleStationResponse(responseText, train);

                if (result) {
                    DetailStationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryStation();
                        }
                    });
                } else {
                    DetailStationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                DetailStationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Internet Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        queryStation();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_station, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.exchange:
                Collections.reverse(stationList);
                adapter.notifyItemRangeChanged(0, stationList.size());
                break;
            case R.id.settings:

                if (!NetUtil.isNetConnect(MyApplication.getContext())) {
                    Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    break;
                }

                Toast.makeText(getApplicationContext(), "Github Visit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/404nofound"));
                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    /*@Override
    public void onNetChange(int netMobile) {

        if ((netMobile == 0 || netMobile == 1) && MyApplication.NET_STATUS == -1) {
            //onCreate(null);
        } else if (netMobile == -1 && MyApplication.NET_STATUS == 1) {

            if(AlertsFragment.handler != null){
                AlertsFragment.handler.removeCallbacksAndMessages(null);
            }

            if(SchedulePopWindow.handler != null){
                SchedulePopWindow.handler.removeCallbacksAndMessages(null);
            }

            Intent stopIntent1 = new Intent(MyApplication.getContext(), AlertService.class);
            MyApplication.getContext().stopService(stopIntent1);

            Intent stopIntent2 = new Intent(MyApplication.getContext(), TimeScheduleService.class);
            MyApplication.getContext().stopService(stopIntent2);

            AlarmManager manager = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);

            PendingIntent pi1 = PendingIntent.getService(MyApplication.getContext(), 0, stopIntent1, 0);
            manager.cancel(pi1);

            PendingIntent pi2 = PendingIntent.getService(MyApplication.getContext(), 0, stopIntent2, 0);
            manager.cancel(pi2);
        }
        Log.d("HEIHEI", netMobile+"");

        if (netMobile == 0 || netMobile == 1) {
            MyApplication.NET_STATUS= 1;
        } else {
            MyApplication.NET_STATUS= -1;
        }
    }*/
}
