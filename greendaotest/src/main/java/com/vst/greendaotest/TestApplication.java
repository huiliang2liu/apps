package com.vst.greendaotest;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class TestApplication extends Application {

    static Activity activity;
    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            Log.d("application","installed");
            return;
        }
        Log.d("application","install");

    }
}
