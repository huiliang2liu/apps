package com.http.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class HttpServiceProvider extends ContentProvider {
    private final static String TAG = "HttpServiceProvider";
    private final static String IS_NEW_PROCESS = "http_service_new_process";
    private Context context;
    private HttpService httpService;

    @Override
    public boolean onCreate() {
        context = getContext();
        try {
            Class.forName(context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString(Service.H_S_R));
            boolean isNewProcess = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getBoolean(IS_NEW_PROCESS, false);
            if (isNewProcess) {
                Log.d(TAG, "开启新进程");
                Intent intent = new Intent(context, HttpServiceS.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(intent);
            } else {
                Log.d(TAG, "同一个线程");
                httpService = new Service(context.getApplicationContext());
            }
        } catch (Exception e) {
            Log.d(TAG, "启动服务失败", e);
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        stop();
        return null;
    }


    @Override
    public String getType(Uri uri) {
        stop();
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        stop();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        stop();
        return 0;
    }

    private void stop() {
        Log.e(TAG, "停止服务");
        if (httpService != null) {
            httpService.stop();
            httpService = null;
        } else {
            context.stopService(new Intent(context, HttpServiceS.class));
        }
    }
}
