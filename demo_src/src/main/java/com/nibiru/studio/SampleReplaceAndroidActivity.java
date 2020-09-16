package com.nibiru.studio;

import android.os.Bundle;
import android.util.Log;

import x.core.XBaseXRActivity;
import x.core.util.XLog;

public class SampleReplaceAndroidActivity extends XBaseXRActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(XLog.TAG, "=========== SampleReplaceAndroidActivity: onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(XLog.TAG, "=========== SampleReplaceAndroidActivity: onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(XLog.TAG, "=========== SampleReplaceAndroidActivity: onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(XLog.TAG, "=========== SampleReplaceAndroidActivity: onDestroy");
    }
}
