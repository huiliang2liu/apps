package com.bar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

class AdwBar extends ABar {
    public static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
    public static final String PACKAGENAME = "PNAME";
    public static final String CLASSNAME = "CNAME";
    public static final String COUNT = "COUNT";

    AdwBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, mComponentName.getPackageName());
        intent.putExtra(CLASSNAME, mComponentName.getClassName());
        intent.putExtra(COUNT, count);
        if (BroadcastHelper.canResolveBroadcast(mContext, intent)) {
            mContext.sendBroadcast(intent);
        } else {
            Log.e(TAG, "adw error");
        }
    }
}
