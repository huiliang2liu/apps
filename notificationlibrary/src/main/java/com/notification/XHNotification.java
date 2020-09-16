package com.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.widget.RemoteViews;

import com.utils.ResourceUtils;

import java.lang.reflect.Field;

import androidx.core.app.NotificationCompat;

/**
 * User：liuhuiliang
 * Date：2019-12-19
 * Time：10:57
 * Descripotion：系统通知
 */
public interface XHNotification {
    /**
     * description：设置展示的布局
     *
     * @param remoteViews 展示布局
     */
    XHNotification setContent(RemoteViews remoteViews);

    /**
     * description：是否常驻
     *
     * @param ongoing
     */
    XHNotification setOngoing(boolean ongoing);

    /**
     * description：是否可以取消
     */
    XHNotification setAutoCancel(boolean autoCancel);

    /**
     * description：设置等级
     */
    XHNotification setPriority(int priority);

    /**
     * description：设置提示信息
     */
    XHNotification setTicker(String ticker);

    /**
     * description：设置icon
     */
    XHNotification setIcon(int icon);

    /**
     * description：设置震动
     */
    XHNotification vibration();

    /**
     * description：设置响铃
     */
    XHNotification voice();

    /**
     * description：设置标题
     */
    XHNotification setContentTitle(String title);

    /**
     * description：设置内容
     */
    XHNotification setContentText(String text);

    /**
     * description：设置点击跳转
     */
    XHNotification setContentIntent(PendingIntent pendingIntent);

    /**
     * description：设置渠道
     *
     * @param id         渠道id
     * @param name       渠道名
     * @param importance 重要性
     */
    XHNotification setChannel(String id, String name, int importance);

    /**
     * description：展示
     *
     * @param id 通知id
     */
    void show(int id);

    class NotificationBuidler {
        private Context mContext;
        private String id;
        private String channel;
        private int importance;

        public NotificationBuidler context(Context context) {
            mContext = context;
            return this;
        }


        public NotificationBuidler channel(String channelId, String channel, int importance) {
            id = channelId;
            this.channel = channel;
            this.importance = importance;
            return this;
        }

        public XHNotification build() {
            ANotification notification;
            if (Build.VERSION.SDK_INT < 16)
                notification = new Notification15(mContext);
            else if (Build.VERSION.SDK_INT < 26)
                notification = new Notification25(mContext);
            else
                notification = (ANotification) new Notification30(mContext)
                        .setChannel(id, channel, importance);
            notification.mNotification = notification.createNotification();
            notification.mNotification.flags = Notification.FLAG_AUTO_CANCEL;
            return notification;
        }

        public static boolean xiaomiBar(Context context, String number) {
            try {
                ResourceUtils.Initialize(context.getPackageName(), "R");
                ANotification notification = (ANotification) new NotificationBuidler().context(context).channel("bar", "小米", NotificationManager.IMPORTANCE_DEFAULT).build();
                notification.setContentTitle(number);
                notification.setTicker(number);
                notification.setAutoCancel(true);
                notification.setIcon(ResourceUtils.getDrawable("ic_launcher"));
                notification.mNotification.defaults = Notification.DEFAULT_LIGHTS;
                Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
                Object miuiNotification = miuiNotificationClass.newInstance();
                Field field = miuiNotification.getClass().getDeclaredField("messageCount");
                field.setAccessible(true);
                field.set(miuiNotification, Integer.parseInt(number));
                field = Notification.class.getField("extraNotification");
                field.setAccessible(true);
                field.set(notification.mNotification, miuiNotification);
                notification.show(101010);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
