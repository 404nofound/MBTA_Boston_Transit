package com.eddy.mbta.ui.stations;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
        adapter = new StationAdapter(stationList);
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
            stationList.clear();
            for (Station s : dataList) {
                stationList.add(s);
            }
            adapter.notifyDataSetChanged();
        } else {
            String url = "http://209.222.10.90/stop.php";
            queryFromServer(url);
        }
    }

    private void queryFromServer(String url) {
        HttpClientUtil.sendOkHttpPostRequest(url, "train", train.replace(" ", "").toLowerCase(), new Callback() {
            @Override
            public void onResponse(Call call, Response response)  throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                result = Utility.handleStationResponse(responseText, train);

                if (result) {
                    DetailStationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryStation();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                DetailStationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailStationActivity.this, "Internet Error", Toast.LENGTH_SHORT).show();
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
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        queryStation();
                        adapter.notifyDataSetChanged();
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
            case R.id.backup:
                Collections.reverse(stationList);
                adapter.notifyDataSetChanged();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked About", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }
}
