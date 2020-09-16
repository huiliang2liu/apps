package com.bar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class OppoBar extends ABar {
    private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
    private static final String INTENT_ACTION = "com.oppo.unsettledevent";
    private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
    private static final String INTENT_EXTRA_BADGE_COUNT = "number";
    private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";
    private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
    private static int ROMVERSION = -1;
    private int version;

    OppoBar(Context context) {
        super(context);
        version = getSupportVersion();
    }

    @Override
    protected void send(int count) {
        if (count == 0) {
            count = -1;
        }
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, mComponentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, count);
        intent.putExtra(INTENT_EXTRA_BADGE_UPGRADENUMBER, count);
        if (BroadcastHelper.canResolveBroadcast(mContext, intent)) {
            mContext.sendBroadcast(intent);
        } else {
            if (Build.VERSION.SDK_INT >= 11 || version == 6) {
                try {
                    Bundle extras = new Bundle();
                    extras.putInt(INTENT_EXTRA_BADGEUPGRADE_COUNT, count);
                    mContext.getContentResolver().call(Uri.parse(PROVIDER_CONTENT_URI), "setAppBadgeCount", null, extras);
                } catch (Throwable th) {
                    Log.e(TAG, "oppo error");
                }
            }
        }
    }

    private int getSupportVersion() {
        int i = ROMVERSION;
        if (i >= 0) {
            return ROMVERSION;
        }
        try {
            i = ((Integer) executeClassLoad(getClass("com.color.os.ColorBuild"), "getColorOSVERSION", null, null)).intValue();
        } catch (Exception e) {
            i = 0;
        }
        if (i == 0) {
            try {
                String str = getSystemProperty("ro.build.version.opporom");
                if (str.startsWith("V1.4")) {
                    return 3;
                }
                if (str.startsWith("V2.0")) {
                    return 4;
                }
                if (str.startsWith("V2.1")) {
                    return 5;
                }
            } catch (Exception ignored) {

            }
        }
        ROMVERSION = i;
        return ROMVERSION;
    }

    private Object executeClassLoad(Class cls, String str, Class[] clsArr, Object[] objArr) {
        Object obj = null;
        if (!(cls == null || checkObjExists(str))) {
            Method method = getMethod(cls, str, clsArr);
            if (method != null) {
                method.setAccessible(true);
                try {
                    obj = method.invoke(null, objArr);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    private Method getMethod(Class cls, String str, Class[] clsArr) {
        Method method = null;
        if (cls == null || checkObjExists(str)) {
            return method;
        }
        try {
            cls.getMethods();
            cls.getDeclaredMethods();
            return cls.getDeclaredMethod(str, clsArr);
        } catch (Exception e) {
            try {
                return cls.getMethod(str, clsArr);
            } catch (Exception e2) {
                return cls.getSuperclass() != null ? getMethod(cls.getSuperclass(), str, clsArr) : method;
            }
        }
    }

    private Class getClass(String str) {
        Class cls = null;
        try {
            cls = Class.forName(str);
        } catch (ClassNotFoundException ignored) {
        }
        return cls;
    }


    private boolean checkObjExists(Object obj) {
        return obj == null || obj.toString().equals("") || obj.toString().trim().equals("null");
    }


    private String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            CloseHelper.closeQuietly(input);
        }
        return line;
    }
}
