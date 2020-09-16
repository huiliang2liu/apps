package com.bar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;


class ZuiBar extends ABar {
    private final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");

    ZuiBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Bundle extra = new Bundle();
        extra.putInt("app_badge_count", count);
        mContext.getContentResolver().call(CONTENT_URI, "setAppBadgeCount", null, extra);
    }
}
