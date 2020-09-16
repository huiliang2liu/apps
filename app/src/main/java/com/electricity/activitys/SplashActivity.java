package com.electricity.activitys;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import android.util.Log;

import com.base.BaseActivity;
import com.base.adapter.IAdapter;
import com.base.util.PhoneInfo;
import com.result.permission.PermissionCallback;
import com.base.thread.PoolManager;
import com.base.widget.ShufflingView;
import com.electricity.R;
import com.electricity.adapter.SplashAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    @BindView(R.id.splash_rv)
    ShufflingView splashRv;
    private IAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("onActivityCreated", "SplashActivity onCreate");
        super.onCreate(savedInstanceState);
        Log.e("onActivityCreated", "SplashActivity onCreate =====");
        setContentView(R.layout.activity_splash);
        Log.e("onActivityCreated", "SplashActivity onCreate -------");
        ButterKnife.bind(this);
        Log.e("getExternalFilesDir", getExternalFilesDir(null).getAbsolutePath());
        adapter = new SplashAdapter(splashRv.getViewPager());
        adapter.addItem("http://vod.india.tvblack.com/image/default/4EF02F103EFA4BABB52E9CED03B73437-6-2.jpg");
        adapter.addItem("http://vod.india.tvblack.com/image/default/903D78FF10B54E9D86D7FD2241800915-6-2.jpg");
        adapter.addItem("http://vod.india.tvblack.com/image/default/3FDEA004A90E4B579C603C2486D3B7AE-6-2.jpg");
        adapter.addItem("http://vod.india.tvblack.com/image/default/C4B80C96CA0E4A49A9083ACF3F48D91D-6-2.jpg");
        adapter.addItem("http://vod.india.tvblack.com/image/default/4655C11333A94AD29B36D2DCC7AC45AE-6-2.jpg");
        result.requestPermissions(100, new PermissionCallback() {
            @Override
            public void result(String... failPermissions) {
                if (Build.VERSION.SDK_INT > 22) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                }

            }
        }, Manifest.permission.SYSTEM_ALERT_WINDOW);

        PoolManager.shortTime(new Runnable() {
            @Override
            public void run() {

            }
        });

    }
}
