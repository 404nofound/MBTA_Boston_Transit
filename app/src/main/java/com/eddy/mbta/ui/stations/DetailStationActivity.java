package com.eddy.mbta.ui.stations;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eddy.mbta.MyApplication;
import com.eddy.mbta.R;
import com.eddy.mbta.db.Station;
import com.eddy.mbta.utils.HttpClientUtil;
import com.eddy.mbta.utils.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailStationActivity extends AppCompatActivity {

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
        List<Station> dataList = LitePal.where("trainName = ?", train).find(Station.class);
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

        if (MyApplication.NET_STATUS == -1) {
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

                if (MyApplication.NET_STATUS == -1) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
