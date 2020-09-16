package com.bar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

class LgeBar extends ABar {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
    LgeBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, count);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, mComponentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, mComponentName.getClassName());
        if(BroadcastHelper.canResolveBroadcast(mContext, intent)) {
            mContext.sendBroadcast(intent);
        } else {
            Log.e(TAG,"LG error");
        }
    }
}
