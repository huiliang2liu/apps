package com.log;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;

import androidx.annotation.Nullable;


public class LogService extends Service {
    Logcat logcat;

    @Override
    public void onCreate() {
        super.onCreate();
        File cache = getCacheDir();
        String name = getPackageName();
        logcat = new Logcat(new File(cache, name).getAbsolutePath(), name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logcat != null) {
            logcat.stopCollect();
            logcat = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void stopLog(Context context) {
        context.getApplicationContext().stopService(new Intent(context, LogService.class));
    }
}
