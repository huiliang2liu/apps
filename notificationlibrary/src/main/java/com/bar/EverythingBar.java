package com.bar;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

class EverythingBar extends ABar {
    private static final String CONTENT_URI = "content://me.everything.badger/apps";
    private static final String COLUMN_PACKAGE_NAME = "package_name";
    private static final String COLUMN_ACTIVITY_NAME = "activity_name";
    private static final String COLUMN_COUNT = "count";
    EverythingBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("package_name", mComponentName.getPackageName());
        contentValues.put("activity_name", mComponentName.getClassName());
        contentValues.put("count", Integer.valueOf(count));
        mContext.getContentResolver().insert(Uri.parse("content://me.everything.badger/apps"), contentValues);
    }
}
