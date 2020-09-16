package com.bar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.notification.XHNotification;

class XiaomiBar extends ABar {
    XiaomiBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        if (XHNotification.NotificationBuidler.xiaomiBar(mContext, String.valueOf(count)))
            return;
        Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
        localIntent.putExtra("android.intent.extra.update_application_component_name", mContext.getPackageName() + "/" + getLauncherClassName());
        localIntent.putExtra("android.intent.extra.update_application_message_text", String.valueOf(count));
        mContext.sendBroadcast(localIntent);
    }

    private String getLauncherClassName() {
        PackageManager packageManager = mContext.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(mContext.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }
}
