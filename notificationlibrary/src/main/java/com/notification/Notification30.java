package com.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

@TargetApi(value = Build.VERSION_CODES.O)
class Notification30 extends ANotification {
    private String id;
    private String name;
    private String packageName;

    public Notification30(Context context) {
        super(context);
        packageName = context.getPackageName();
    }

    @Override
    public XHNotification setChannel(String id, String name, int importance) {
        if (id == null || id.isEmpty())
            this.id = "default";
        else this.id = id;
        if (name == null || name.isEmpty())
            this.name = "默认";
        else
            this.name = name;
        if (importance != NotificationManager.IMPORTANCE_UNSPECIFIED &&
                importance != NotificationManager.IMPORTANCE_MIN &&
                importance != NotificationManager.IMPORTANCE_LOW &&
                importance != NotificationManager.IMPORTANCE_DEFAULT &&
                importance != NotificationManager.IMPORTANCE_HIGH &&
                importance != NotificationManager.IMPORTANCE_MAX)
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = mManager.getNotificationChannel(name);
        if (channel == null) {
            channel = new NotificationChannel(id, name, importance);
            mManager.createNotificationChannel(channel);
        }

        if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
            Log.e(TAG, String.format("%s channel is close,please open", name));
        }
        this.id = id;
        this.name = name;
        return super.setChannel(id, name, importance);
    }

    @Override
    protected Notification createNotification() {
        Notification notification = new Notification.Builder(mContext, id)
                .build();
        return notification;
    }

    public boolean channelOpen() {
        if (name == null || name.isEmpty())
            name = packageName;
        NotificationChannel channel = mManager.getNotificationChannel(name);
        if (channel == null)
            return true;
        return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
    }

    public void openChannel() {
        if (id == null || id.isEmpty())
            id = packageName;
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
