package com.bar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

class HuaweiBar extends ABar {
    HuaweiBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Bundle localBundle = new Bundle();
        localBundle.putString("package", mContext.getPackageName());
        localBundle.putString("class", mComponentName.getClassName());
        localBundle.putInt("badgenumber", count);
        mContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
    }
}
