package com.base.thread;

import android.os.Looper;


import com.base.thread.pool.CPUThreadPool;

import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * com.thread
 * 2018/9/26 12:16
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class PoolManager {
    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;
    private static volatile ThreadPoolExecutor singleThread = new CPUThreadPool(1);
    private static volatile ThreadPoolExecutor shortTimeThread = new CPUThreadPool(Integer.MAX_VALUE);
    private static volatile ThreadPoolExecutor longTimeThread = new CPUThreadPool();
    private static volatile ScheduledThreadPoolExecutor scheduledThread = new ScheduledThreadPoolExecutor(0);
    private static volatile Handler handler = new Handler();


    public static Future single(Runnable runnable) {
        return singleThread.submit(runnable);
    }

    public static void singleRemove(Runnable runnable) {
        singleThread.remove(runnable);
    }


    public static List<Future> single(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return null;
        List<Future> futures = new ArrayList<>(runnables.size());
        for (Runnable runnable : runnables)
            futures.add(single(runnable));
        return futures;
    }

    public static void singleRemove(Collection<Runnable> runnables) {
        if (runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            singleRemove(runnable);
    }

    public static Future shortTime(Runnable runnable) {
        return shortTimeThread.submit(runnable);
    }

    public static void shortTimeRemove(Runnable runnable) {
        shortTimeThread.remove(runnable);
    }

    public static List<Future> shortTime(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return null;
        List<Future> futures = new ArrayList<>(runnables.size());
        for (Runnable runnable : runnables)
            futures.add(shortTime(runnable));
        return futures;
    }

    public static void shortTimeRemove(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            shortTimeThread.remove(runnable);
    }

    public static Future longTime(Runnable runnable) {
        return longTimeThread.submit(runnable);
    }

    public static void longTimeRemove(Runnable runnable) {
        longTimeThread.remove(runnable);
    }

    public static List<Future> longTime(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return null;
        List<Future> futures = new ArrayList<>(runnables.size());
        for (Runnable runnable : runnables)
            futures.add(longTime(runnable));
        return futures;
    }

    public static void longTimeRemove(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            longTimeThread.remove(runnable);
    }

    public static Future scheduled(Runnable runnable, long delay, long period) {
        if (delay <= 0)
            delay = 0;
        if (period <= 0)
            return scheduledThread.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        else
            return scheduledThread.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public static void scheduledRemove(Runnable runnable) {
        scheduledThread.remove(runnable);
    }

    public static void scheduledRemove(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            scheduledThread.remove(runnable);
    }

    public static Future scheduled(Runnable runnable, long delay) {
        return scheduled(runnable, delay, 0);
    }

    public static Future scheduledEnd(Runnable runnable, long delay, long period) {
        if (period <= 0)
            return scheduled(runnable, delay, period);
        else {
            return scheduledThread.scheduleWithFixedDelay(runnable, delay, period, TimeUnit.MILLISECONDS);
        }
    }

    public static void runUiThread(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
            return;
        }
        handler.post(runnable);
    }

    public static void runUiThread(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            runUiThread(runnable);
    }

    public static void runUiThread(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    public static void runUiThread(Collection<Runnable> runnables, long delay) {
        if (delay <= 0) {
            runUiThread(runnables);
            return;
        }
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables) {
            runUiThread(runnable, delay);
        }
    }

    public static void runUiThreadAtTime(Runnable runnable, long uptimeMillis) {
        handler.postAtTime(runnable, uptimeMillis);
    }

    public static void runUiThreadAtTime(Collection<Runnable> runnables, long uptimeMillis) {
        if (uptimeMillis <= System.currentTimeMillis()) {
            runUiThread(runnables);
            return;
        }
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            runUiThreadAtTime(runnable, uptimeMillis);
    }

    public static void removeUi(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    public static void removeUi(Collection<Runnable> runnables) {
        if (runnables == null || runnables.size() <= 0)
            return;
        for (Runnable runnable : runnables)
            handler.removeCallbacks(runnable);
    }

    public static void clearUi() {
        handler.removeCallbacksAndMessages(null);
    }
}
