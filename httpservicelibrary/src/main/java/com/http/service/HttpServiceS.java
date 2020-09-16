package com.http.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class HttpServiceS extends Service {
    private HttpService service;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = new com.http.service.Service(getApplication());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (service != null) {
            service.stop();
            service = null;
        }
    }
}
