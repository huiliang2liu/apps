package com.screen.placeholder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.screen.PlugInstrumentation;
/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:33
 * Descripotion：占位service
 */
public class PlaceholderService extends Service {
    private static final String TAG = "PlaceholderService";

    @Override
    public IBinder onBind(Intent intent) {
        intent.removeExtra(PlugInstrumentation.ORIGINAL_ACTIVITY);
        intent.removeExtra(PlugInstrumentation.ORIGINL_PACKAGE);
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }
}
