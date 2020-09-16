package com.base.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class SetIntent {
    public static void startSystem(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startIntent(context, intent);
    }

    public static void startApn(Context context) {
        Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
        startIntent(context, intent);
    }

    public static void startNotification(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Intent intent = new Intent();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, "message");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                intent.putExtra("app_package", pkg);
                intent.putExtra("app_uid", uid);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
            } else {
                intent.setAction(Settings.ACTION_SETTINGS);
            }
            startIntent(context, intent);
        } catch (Exception e) {
            startIntent(context, new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private static void startIntent(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
