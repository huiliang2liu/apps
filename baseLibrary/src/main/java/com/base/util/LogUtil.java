package com.base.util;

import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * com.tv.vst.utils
 * 2018/9/3 13:00
 * instructions：日志工具
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class LogUtil {
    private static final String FORMAT = "[TAG] : %s , [MSG] : %s";
    private static boolean isDebug = true;
    private static String TAG = "LogUtil";
    private static List<String> shields = new ArrayList<>();

    static {
        shields.add(LogUtil.class.getName());
    }

    public static void addShield(String shield) {
        shields.add(shield);
    }

    public static void removeShield(String shield) {
        shields.remove(shield);
    }

    public static void addShield(Collection<String> shields) {
        shields.addAll(shields);
    }

    public static void removeShield(Collection<String> shields) {
        shields.removeAll(shields);
    }

    public static void setIsDebug(boolean isDebug) {
        LogUtil.isDebug = isDebug;
    }

    /**
     * 2018/9/3 15:33
     * annotation：打印运行堆栈信息
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void stack() {
        stack(new Throwable().getStackTrace());
    }

    public static void stack(Thread thread) {
        if (thread == null)
            return;
        stack(thread.getStackTrace());
    }

    public static void threadStack() {
        stack(Thread.currentThread());
    }

    public static void mainThreadStack() {
        stack(Looper.getMainLooper().getThread());
    }

    public static void stack(StackTraceElement[] elements) {
        if (elements == null || elements.length <= 0) {
            i("stack", "栈长度为0");
            return;
        }
        for (StackTraceElement element : elements) {
            String className = element.getClassName();
            if (shields.size() > 0) {
                boolean isShield = false;
                for (String shield : shields) {
                    if (className.contains(shield)) {
                        isShield = true;
                        break;
                    }
                }
                if (isShield)
                    continue;
            }
            e("stack", className + "  " + element.getMethodName() + "  " + element.getLineNumber());
        }
    }

    /**
     * 2018/4/13 10:35
     * annotation：将tag和msg格式化
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    private static String buildMsg(String tag, String msg) {
        return String.format(FORMAT, tag, msg);
    }

    /**
     * 2018/4/13 10:36
     * annotation：设置tag标志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void setTAG(String TAG) {
        LogUtil.TAG = TAG;
    }

    /**
     * 2018/4/13 10:48
     * annotation：蓝色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void d(String msg) {
        d(null, msg);
    }

    /**
     * 2018/4/13 10:49
     * annotation：蓝色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    /**
     * 2018/4/13 10:49
     * annotation：蓝色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void d(String tag, String msg, Throwable er) {
        log("d", tag, msg, er);
    }

    /**
     * 2018/4/13 10:53
     * annotation：红色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void e(String msg) {
        e(null, msg);
    }

    /**
     * 2018/4/13 10:53
     * annotation：红色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    /**
     * 2018/4/13 10:53
     * annotation：红色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void e(String tag, String msg, Throwable tr) {
        log("e", tag, msg, tr);
    }

    /**
     * 2018/4/13 10:56
     * annotation：绿色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void i(String msg) {
        i(null, msg);
    }

    /**
     * 2018/4/13 10:56
     * annotation：绿色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    /**
     * 2018/4/13 10:56
     * annotation：绿色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void i(String tag, String msg, Throwable tr) {
        log("i", tag, msg, tr);
    }

    /**
     * 2018/4/13 10:58
     * annotation：黑色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void v(String msg) {
        v(null, msg);
    }

    /**
     * 2018/4/13 10:58
     * annotation：黑色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    /**
     * 2018/4/13 10:58
     * annotation：黑色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void v(String tag, String msg, Throwable tr) {
        log("v", tag, msg, tr);
    }

    /**
     * 2018/4/13 11:00
     * annotation：橙色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void w(String msg) {
        w(null, msg);
    }

    /**
     * 2018/4/13 11:00
     * annotation：橙色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    /**
     * 2018/4/13 11:00
     * annotation：橙色日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public static void w(String tag, String msg, Throwable tr) {
        log("w", tag, msg, tr);
    }

    /**
     * 2018/4/13 10:53
     * annotation：输出日志
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    private static void log(String methodName, String tag, String msg, Throwable er) {
        if (!isDebug)
            return;
        if (msg == null || msg.isEmpty())
            return;
        if (tag == null || msg.isEmpty())
            tag = "null";
        msg = buildMsg(tag, msg);
        Class cls = Log.class;
        Class[] types;
        Object[] values;
        if (er == null) {
            types = new Class[]{String.class, String.class};
            values = new Object[]{TAG, msg};
        } else {
            types = new Class[]{String.class, String.class, Throwable.class};
            values = new Object[]{TAG, msg, er};
        }
        try {
            Method method = cls.getDeclaredMethod(methodName, types);
            if (!method.isAccessible())
                method.setAccessible(true);
            method.invoke(null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
