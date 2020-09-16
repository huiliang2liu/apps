package com.notification;

import android.app.Notification;
import android.content.Context;

class Notification15 extends ANotification {

    public Notification15(Context context) {
        super(context);
    }

    @Override
    protected Notification createNotification() {
        Notification notification = new Notification();
        return notification;
    }
}
