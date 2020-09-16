package com.base.util;

import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class L {
    private static String TAG = "liu";
    private static boolean writeLogs = true;
    private static boolean writeDebugLogs = true;
    private static List<String> shields = new ArrayList<>();

    static {
        shields.add(L.class.getName());
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

    public static void enableLogging() {
        writeLogs(true);
    }

    public static void disableLogging() {
        writeLogs(false);
    }

    public static void enableDebug() {
        writeDebugLogs(true);
    }

    public static void disableDebug() {
        writeDebugLogs(false);
    }

    public static void writeDebugLogs(boolean writeDebugLogs) {
        L.writeDebugLogs = writeDebugLogs;
    }

    public static void writeLogs(boolean writeLogs) {
        L.writeLogs = writeLogs;
    }

    public static void setTag(String tag) {
        if (tag != null && !tag.isEmpty())
            L.TAG = tag;
    }

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
        StringBuffer sb = new StringBuffer();
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
            sb.append(className).append("  ").append(element.getMethodName()).append("  ").append(element.getLineNumber()).append("\n");
        }
        if (sb.length() > 0)
            d("stack", sb.toString());
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable ex) {
        if (writeDebugLogs)
            log(Log.DEBUG, tag, msg, ex);
    }

    public static void e(String msg) {
        e(null, msg);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable ex) {
        log(Log.ERROR, tag, msg, ex);
    }

    public static void i(String msg) {
        i(null, msg);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable ex) {
        log(Log.INFO, tag, msg, ex);
    }

    public static void v(String msg) {
        v(null, msg);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable ex) {
        log(Log.VERBOSE, tag, msg, ex);
    }

    public static void w(String msg) {
        w(null, msg);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable ex) {
        log(Log.WARN, tag, msg, ex);
    }

    private static void log(int priority, String tag, String msg, Throwable ex) {
        if (!writeLogs)
            return;
        if ((msg == null || msg.isEmpty()) && ex == null)
            return;
        if(tag==null||tag.isEmpty()){
            StackTraceElement[] elements=new Throwable().getStackTrace();
            if(elements.length>3){
                tag=elements[4].getClassName();
                tag=tag.split("\\$")[0];
                tag=tag.substring(tag.lastIndexOf(".")+1);
            }
        }
        Log.println(priority, TAG, String.format("[TAG] : %s , [MSG] : %s", tag, msg(msg, ex)));
    }

    private static String msg(String msg, Throwable ex) {
        if (ex == null)
            return msg;
        return String.format("%s\n%s", msg, Log.getStackTraceString(ex));
    }
}
