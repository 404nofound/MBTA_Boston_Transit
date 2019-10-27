package com.eddy.mbta;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.eddy.mbta.service.AlertService;
import com.eddy.mbta.service.TimeScheduleService;
import com.eddy.mbta.ui.alerts.AlertsFragment;
import com.eddy.mbta.ui.feedback.FeedbackActivity;
import com.eddy.mbta.ui.map.SchedulePopWindow;
import com.eddy.mbta.utils.LogUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {

    private static final long TIME = 2000;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_main);
        }

        ViewPager mViewPager = findViewById(R.id.view_pager);
        MainPageAdapter adapter = new MainPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("87B8E83525FCB69F71AE1154E35EF784").build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "Welcome to Boston T", Toast.LENGTH_SHORT).show();
                break;
            case R.id.report:
                Intent report = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(report);
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
    public void onGpsChange(boolean is_gps_enabled) {

        LocationManager locationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gps_enabled) {
            Toast.makeText(MyApplication.getContext(), "GPS Enable", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyApplication.getContext(), "GPS Unable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetChange(int netMobile) {

        if (netMobile != -1 && MyApplication.NET_STATUS == -1) {

            if(AlertsFragment.handler != null) {
                LogUtil.d("AlertService", "start");
                Intent startIntent = new Intent(MyApplication.getContext(), AlertService.class);
                MyApplication.getContext().startService(startIntent);
            }

            if(SchedulePopWindow.handler != null && !MyApplication.station.equals("")) {
                LogUtil.d("TimeScheduleService", "start");
                Intent startIntent = new Intent(MyApplication.getContext(), TimeScheduleService.class);
                startIntent.putExtra("stop_id", MyApplication.station);
                MyApplication.getContext().startService(startIntent);
            }

        } else if (netMobile == -1 && MyApplication.NET_STATUS == 1) {

            if(AlertsFragment.handler != null) {
                AlertsFragment.handler.removeCallbacksAndMessages(null);
            }

            if(SchedulePopWindow.handler != null) {
                SchedulePopWindow.handler.removeCallbacksAndMessages(null);
            }

            Intent stopIntent1 = new Intent(MyApplication.getContext(), AlertService.class);
            MyApplication.getContext().stopService(stopIntent1);

            Intent stopIntent2 = new Intent(MyApplication.getContext(), TimeScheduleService.class);
            MyApplication.getContext().stopService(stopIntent2);
        }

        MyApplication.NET_STATUS = netMobile;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断是否按的后退键，而且按了一次
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //获取当前的系统时间，和exitTime相减，判断两次间隔是否大于规定时间
            //exitTime没有初始值则默认为0
            //如果大于设定的时间，则弹出提示，同时把exitTime设置为当前时间
            if (System.currentTimeMillis() - exitTime > TIME) {
                Toast.makeText(MyApplication.getContext(),"Press Again to Exit",Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                //如果再次按后退的时间小于规定时间，则退出
                finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
            //消费事件
            return true;
        }
        //不处理事件
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
    }
}
