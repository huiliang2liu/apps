package com.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.os.Build;

@TargetApi(value = Build.VERSION_CODES.JELLY_BEAN)
class Notification25 extends ANotification {
    public Notification25(Context context) {
        super(context);
    }

    @Override
    protected Notification createNotification() {
        Notification notification = new Notification.Builder(mContext)
                .build();
        return notification;
    }
}
