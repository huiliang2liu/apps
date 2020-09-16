package com.bar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

abstract class ABar implements Bar {
    protected static final String TAG = "Bar";
    protected Context mContext;
    protected ComponentName mComponentName;

    ABar(Context context) {
        Log.d(TAG, this.getClass().getSimpleName());
        mContext = context.getApplicationContext();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            Log.e(TAG, "Unable to find launch intent for package " + context.getPackageName());
            return;
        }
        mComponentName = launchIntent.getComponent();
    }

    @Override
    public final void sendCount(int count) {
        if (mComponentName == null)
            return;
        if (count < 0)
            count = 0;
        send(count);
    }

    protected abstract void send(int count);

    @Override
    public final void clearCount() {
        sendCount(0);
    }

}
