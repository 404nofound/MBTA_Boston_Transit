package com.eddy.mbta.ui.feedback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.eddy.mbta.MyApplication;
import com.eddy.mbta.R;
import com.eddy.mbta.utils.HttpClientUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Feedback");
        }

        final EditText editText = findViewById(R.id.edit);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyApplication.NET_STATUS == -1) {
                    Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                String text = editText.getText().toString();
                if (text.length() <= 420 && !TextUtils.isEmpty(text)) {

                    String info =
                            "手机型号:" + android.os.Build.MODEL +
                                    ",SDK版本:" + android.os.Build.VERSION.SDK +
                                    ",系统版本:" + android.os.Build.VERSION.RELEASE+
                                    ",软件版本:"+getAppVersionName(FeedbackActivity.this);

                    String url = "http://209.222.10.90/feedback.php";
                    HttpClientUtil.submitAdvice(url, info, text, new Callback() {
                        @Override
                        public void onResponse(Call call, Response response)  throws IOException {
                            String responseText = response.body().string();

                            final String tip;
                            if (!responseText.equals("404")) {
                                tip = "Success";
                            } else {
                                tip = "Server Error";
                            }

                            FeedbackActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            FeedbackActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Internet Error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    finish();
                } else if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getApplicationContext(), "No Empty Please.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please notice word length.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    private  String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.eddy.mbta", 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
