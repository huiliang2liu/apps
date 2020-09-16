package com.bar;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

class NovaBar extends ABar {
    private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
    private static final String COUNT = "count";
    private static final String TAG = "tag";

    NovaBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG, mComponentName.getPackageName() + "/" + mComponentName.getClassName());
        contentValues.put(COUNT, count);
        mContext.getContentResolver().insert(Uri.parse(CONTENT_URI), contentValues);
    }
}
