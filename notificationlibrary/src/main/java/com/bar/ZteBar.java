package com.bar;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

class ZteBar extends ABar {
    ZteBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Bundle extra = new Bundle();
        extra.putInt("app_badge_count", count);
        extra.putString("app_badge_component_name", mComponentName.flattenToString());
        if (Build.VERSION.SDK_INT >= 11) {
            mContext.getContentResolver().call(
                    Uri.parse("content://com.android.launcher3.cornermark.unreadbadge"),
                    "setAppUnreadCount", null, extra);
        }
    }
}
