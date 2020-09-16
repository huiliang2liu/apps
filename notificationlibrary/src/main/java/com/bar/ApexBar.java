package com.bar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

class ApexBar extends ABar {
    private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
    private static final String PACKAGENAME = "package";
    private static final String COUNT = "count";
    private static final String CLASS = "class";
    ApexBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, mComponentName.getPackageName());
        intent.putExtra(COUNT, count);
        intent.putExtra(CLASS, mComponentName.getClassName());
        if (BroadcastHelper.canResolveBroadcast(mContext, intent)) {
            mContext.sendBroadcast(intent);
        } else {
            Log.e(TAG,"apex error");
        }
    }
}
