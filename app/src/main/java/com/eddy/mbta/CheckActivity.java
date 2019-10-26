package com.eddy.mbta;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eddy.mbta.utils.PermissionUtils;

public class CheckActivity extends BaseActivity {

    private View mProgressView;
    private boolean gps_enabled;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mProgressView = findViewById(R.id.register_progress);

        showProgress(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (MyApplication.NET_STATUS != -1 && gps_enabled) {
            Log.d("HEIHEI", "REAL");

            showProgress(true);

            Intent intent = new Intent(CheckActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {

            new AlertDialog.Builder(this)
                    .setMessage(R.string.check_gps_network)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            if (MyApplication.NET_STATUS != -1 && gps_enabled) {
                                showProgress(true);

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

            if (!gps_enabled) {
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
        }
    }

    @Override
    public void onGpsChange() {

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gps_enabled && MyApplication.NET_STATUS != -1) {
            Log.d("HEIHEI", "GPS");
            //start();
        }
    }

    @Override
    public void onNetChange(int netMobile) {

        if (netMobile != -1 && gps_enabled && netMobile != MyApplication.NET_STATUS) {
            Log.d("HEIHEI", "NET");
            //start();
        }

        MyApplication.NET_STATUS = netMobile;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        if (requestCode != 1) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
        }
    }
}
