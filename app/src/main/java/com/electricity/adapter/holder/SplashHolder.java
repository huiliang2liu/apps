package com.electricity.adapter.holder;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.base.BaseApplication;
import com.base.adapter.tag.ViewHolder;
import com.base.thread.PoolManager;
import com.electricity.R;
import com.electricity.activitys.AnimationActivity;
import com.electricity.activitys.MainActivity;
import com.http.Http;
import com.image.transform.CircleTransform;
import com.image.transform.RoundTransform;
import com.screen.placeholder.PlaceholderService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SplashHolder extends ViewHolder<String> {
    private static final String TAG = "SplashHolder";
    ImageView imageView;
    private BaseApplication application;
    private Activity activity;
    ServiceConnection connection;

    @Override
    public void setContext(final String s) {
//        imageView
        Log.e(TAG, s);
//        Http.RequestEntity requestEntity=new Http.RequestEntity();
//        requestEntity.
//        application.http.getAsyn(requestEntity);
//        PoolManager.longTime(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HttpURLConnection connection = (HttpURLConnection) new URL(s).openConnection();
//                    connection.connect();
//                    Log.e(TAG, "" + connection.getResponseCode()+""+connection.getContentLength());
//                    final Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
//                    PoolManager.runUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageBitmap(bitmap);
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        application.imageLoad.load(R.mipmap.ic_launcher, R.mipmap.ic_launcher, s, imageView, new RoundTransform());
    }

    @Override
    public void bindView() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(TAG, "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisconnected");
            }
        };
        application = (BaseApplication) context.getContext().getApplicationContext();
        imageView = (ImageView) findViewById(R.id.splash_iv);
        activity = (Activity) context.getContext();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MainActivity.class);
//                intent.setClassName(application.getPackageName(), "com.xiaowei.media.LauncherActivity");
//                intent.setClassName(application.getPackageName(),"com.tvblackAD.demo.MainActivity");
                activity.startActivity(intent);
//                activity.bindService(intent, connection, Service.BIND_ABOVE_CLIENT);
            }
        });
    }
}
