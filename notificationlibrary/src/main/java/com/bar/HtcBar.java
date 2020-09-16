package com.bar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

class HtcBar extends ABar {
    public static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
    public static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
    public static final String PACKAGENAME = "packagename";
    public static final String COUNT = "count";
    public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
    public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";

    HtcBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Intent intent1 = new Intent(INTENT_SET_NOTIFICATION);
        intent1.putExtra(EXTRA_COMPONENT, mComponentName.flattenToShortString());
        intent1.putExtra(EXTRA_COUNT, count);

        Intent intent = new Intent(INTENT_UPDATE_SHORTCUT);
        intent.putExtra(PACKAGENAME, mComponentName.getPackageName());
        intent.putExtra(COUNT, count);

        if (BroadcastHelper.canResolveBroadcast(mContext, intent1) || BroadcastHelper.canResolveBroadcast(mContext, intent)) {
            mContext.sendBroadcast(intent1);
            mContext.sendBroadcast(intent);
        } else {
            Log.e(TAG, "newHtc error");
        }
    }
}
