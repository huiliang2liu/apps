package com.bar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class VivoBar extends ABar {


    VivoBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", mContext.getPackageName());
        intent.putExtra("className", mComponentName.getClassName());
        intent.putExtra("notificationNum", count);
        mContext.sendBroadcast(intent);
    }


}
