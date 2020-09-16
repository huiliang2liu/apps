package com.tvblack.tvf.ad;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

public class TVBADF {
    private static final String TAG = "TVBADF";

    {
        File file = new File(Environment.getExternalStorageDirectory(), "okttpcaches");
        delete(file);
    }

    public void init(com.tvblack.tv.utils.TVBADManager manager) {
        Class<?> umeng_agent = null;
        try {
            umeng_agent = Class.forName("com.umeng.analytics.MobclickAgent");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                umeng_agent = Class.forName("com.b.a.b");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (umeng_agent != null) {
            Method method = null;
            try {
                method = umeng_agent.getMethod("onEvent", new Class[]{Context.class, String.class, String.class});
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    method = umeng_agent.getMethod("a", new Class[]{Context.class, String.class, String.class});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (method != null) {
                try {
                    method.setAccessible(true);
                    method.invoke(null, manager.getContext(), "live_yl_a", "123456");
                    Log.d(TAG, "上报统计成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "上报统计错误");
                }

            } else {
                Log.d(TAG, "没获取到统计方法");
            }
        } else {
            Log.d(TAG, "没获取到统计类");
        }
    }


    public static boolean delete(File file) {
        if (file == null || !file.exists())
            return false;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file2 : files) {
                    delete(file2);
                }
            }
            file.delete();
        } else
            file.delete();
        return true;
    }


}
