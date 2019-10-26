package com.eddy.mbta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.eddy.mbta.service.AlertService;
import com.eddy.mbta.service.TimeScheduleService;
import com.eddy.mbta.ui.alerts.AlertsFragment;
import com.eddy.mbta.ui.map.SchedulePopWindow;
import com.eddy.mbta.utils.NetUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private MainPageAdapter adapter;

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

        mViewPager = findViewById(R.id.view_pager);
        adapter = new MainPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
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

    @Override
    public void onNetChange(int netMobile) {

        if (netMobile != -1 && MyApplication.NET_STATUS == -1) {

            /*Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);*/

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
        //Log.d("HEIHEI", netMobile+"");

        if (netMobile == 0 || netMobile == 1) {
            MyApplication.NET_STATUS= 1;
        } else {
            MyApplication.NET_STATUS= -1;
        }
    }

}
