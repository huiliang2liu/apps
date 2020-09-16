package com.bar;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.net.Uri;

class SonyBar extends ABar {
    private static final String INTENT_ACTION = "com.sonyericsson.home.action.UPDATE_BADGE";
    private static final String INTENT_EXTRA_PACKAGE_NAME = "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME";
    private static final String INTENT_EXTRA_MESSAGE = "com.sonyericsson.home.intent.extra.badge.MESSAGE";
    private static final String INTENT_EXTRA_SHOW_MESSAGE = "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE";

    private static final String PROVIDER_CONTENT_URI = "content://com.sonymobile.home.resourceprovider/badge";
    private static final String PROVIDER_COLUMNS_BADGE_COUNT = "badge_count";
    private static final String PROVIDER_COLUMNS_PACKAGE_NAME = "package_name";
    private static final String PROVIDER_COLUMNS_ACTIVITY_NAME = "activity_name";
    private static final String SONY_HOME_PROVIDER_NAME = "com.sonymobile.home.resourceprovider";
    private final Uri BADGE_CONTENT_URI = Uri.parse(PROVIDER_CONTENT_URI);
    private boolean exists = false;
    private AsyncQueryHandler mQueryHandler;

    SonyBar(Context context) {
        super(context);
        ProviderInfo info = context.getPackageManager().resolveContentProvider(SONY_HOME_PROVIDER_NAME, 0);
        if (info != null) {
            exists = true;
            mQueryHandler = new AsyncQueryHandler(
                    context.getApplicationContext().getContentResolver()) {
            };
        }
    }

    @Override
    protected void send(int count) {
        if (exists) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(PROVIDER_COLUMNS_BADGE_COUNT, count);
            contentValues.put(PROVIDER_COLUMNS_PACKAGE_NAME, mComponentName.getPackageName());
            contentValues.put(PROVIDER_COLUMNS_ACTIVITY_NAME, mComponentName.getClassName());
            mQueryHandler.startInsert(0, null, BADGE_CONTENT_URI, contentValues);
        } else {
            Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, mComponentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, mComponentName.getClassName());
            intent.putExtra(INTENT_EXTRA_MESSAGE, String.valueOf(count));
            intent.putExtra(INTENT_EXTRA_SHOW_MESSAGE, count > 0);
            mContext.sendBroadcast(intent);
        }
    }
}
