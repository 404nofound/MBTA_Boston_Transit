package com.eddy.mbta.ui.check;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eddy.mbta.BaseActivity;
import com.eddy.mbta.MainActivity;
import com.eddy.mbta.MyApplication;
import com.eddy.mbta.R;

public class CheckActivity extends BaseActivity {

    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mProgressView = findViewById(R.id.register_progress);
        showProgress(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (MyApplication.NET_STATUS != -1 && MyApplication.GPS_ENABLED) {

            showProgress(false);

            Intent intent = new Intent(CheckActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

            new AlertDialog.Builder(this)
                    .setMessage(R.string.check_gps_network)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            if (MyApplication.NET_STATUS != -1 && MyApplication.GPS_ENABLED) {

                                showProgress(false);

                                Intent intent = new Intent(CheckActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                finish();
                            }
                        }
                    })
                    .setNeutralButton(R.string.finish,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();

            if (!MyApplication.GPS_ENABLED) {
                                    new AlertDialog.Builder(this)
                                            .setMessage(R.string.gps_network_not_enabled)
                                            .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                    finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }

            if (MyApplication.NET_STATUS == -1) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.network_alert)
                        .setPositiveButton(R.string.data_roaming_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                            }
                        })
                        .setNeutralButton(R.string.finish,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.wifi_setting,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
