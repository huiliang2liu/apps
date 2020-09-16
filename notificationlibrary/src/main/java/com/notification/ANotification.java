package com.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.widget.RemoteViews;


abstract class ANotification implements XHNotification {
    protected static final String TAG = "Notification";
    protected Context mContext;
    protected NotificationManager mManager;
    protected Notification mNotification;

    public ANotification(Context context) {
        mContext = context;
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    /**
     * description：构建通知
     */
    protected abstract Notification createNotification();

    @Override
    public final XHNotification setContent(RemoteViews remoteViews) {
        mNotification.contentView = remoteViews;
        return this;
    }

    @Override
    public final XHNotification setContentIntent(PendingIntent pendingIntent) {
        mNotification.contentIntent = pendingIntent;
        return this;
    }

    @Override
    public void show(int id) {
        mNotification.when = System.currentTimeMillis();
        mManager.notify(id, mNotification);
    }

    @Override
    public XHNotification setChannel(String id, String name, int importance) {
        return this;
    }

    public final XHNotification setOngoing(boolean ongoing) {
        if (ongoing) {
            mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        } else
            mNotification.flags &= ~Notification.FLAG_ONGOING_EVENT;
        return this;
    }

    @TargetApi(value = Build.VERSION_CODES.JELLY_BEAN)
    public final XHNotification setPriority(int priority) {
        mNotification.priority = priority;
        return this;
    }

    @Override
    public final XHNotification setIcon(int icon) {
        mNotification.icon = icon;
        return this;
    }

    @Override
    public final XHNotification setTicker(String ticker) {
        mNotification.tickerText = ticker;
        return this;
    }

    @Override
    public final XHNotification vibration() {
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        return this;
    }

    @Override
    public final XHNotification voice() {
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        return this;
    }

    @TargetApi(value = 19)
    @Override
    public final XHNotification setContentTitle(String title) {
        if (title != null && !title.isEmpty()) {
            mNotification.extras.putCharSequence(Notification.EXTRA_TITLE, title);
        }
        return this;
    }

    @TargetApi(value = 19)
    @Override
    public final XHNotification setContentText(String text) {
        if (text != null && !text.isEmpty()) {
            mNotification.extras.putCharSequence(Notification.EXTRA_TEXT, text);
        }
        return this;
    }

    @Override
    public final XHNotification setAutoCancel(boolean autoCancel) {
        if (autoCancel) {
            mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else
            mNotification.flags &= ~Notification.FLAG_AUTO_CANCEL;
        return this;
    }
}
