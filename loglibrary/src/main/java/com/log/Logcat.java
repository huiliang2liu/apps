package com.log;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User：liuhuiliang
 * Date：2019-12-19
 * Time：15:40
 * Descripotion：收集系统日志
 */
public class Logcat extends Thread {
    private static final String TAG = "Logcat";
    /*
     *description：保存数据文件名
     */
    private static final String FILE_NAME = "log%s";
    /*
     *description：保存日志数据的临时文件名
     */
    private static final String TEMPORARY = "temporary";
    /*
     *description：最大收集日志的时间
     */
    private static final long MAX_LOG_TIME = 30 * 60 * 1000;
    private final String cmd;
    /*
     *description：是否收集日志
     */
    private boolean collect;
    /*
     *description：一个文件保存的日志时间
     */
    private long fileMaxTime;
    private File parent;

    Logcat(String fileParent, String processName) {
        cmd = String.format("logcat %s", processName);
        collect = true;
        parent = new File(fileParent);
        if (!parent.exists())
            parent.mkdirs();
        fileMaxTime = MAX_LOG_TIME / 10;
        start();
    }

    /**
     * description：停止收集日志
     */
    public void stopCollect() {
        collect = false;
    }

    @Override
    public void run() {
        super.run();
        BufferedReader re = null;
        try {
            re = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));
        } catch (Exception e) {
            Log.e(TAG, "运行收集日志命令错误");
        }
        if (re == null) {
            collect = false;
        }
        int i = 1;//文件标示
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        String time;
        BufferedWriter bw = null;
        File temporary = null;
        long startTime = System.currentTimeMillis();
        temporary = new File(parent, TEMPORARY);
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temporary)));
        } catch (Exception e) {
            Log.e(TAG, "打开保存临时文件错误");
        }
        if (bw == null)
            collect = false;
        while (collect) {
            String name = null;
            try {
                name = re.readLine();
            } catch (Exception e) {
                Log.e(TAG, "读取日志错误");
            }
            if (name == null || name.isEmpty())
                continue;
            long now = System.currentTimeMillis();
            time = simpleDateFormat.format(new Date(now));
            try {
                bw.write(String.format("%s %s", time, name), 0, time.length() + name.length() + 1);
                bw.newLine();
            } catch (Exception e) {
                Log.e(TAG, "写入日志错误");
            }
            if (now - startTime > fileMaxTime) {
                Log.d(TAG, "保存完一个日志");
                try {
                    bw.flush();
                    bw.close();
                } catch (Exception e) {
                }
                boolean rename = temporary.renameTo(new File(parent, String.format(FILE_NAME, i)));
                if (!rename)
                    Log.e(TAG, "临时文件更改为正式文件错误");
                startTime = now;
                temporary = new File(parent, TEMPORARY);
                try {
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temporary)));
                } catch (Exception e) {
                    Log.e(TAG, "打开保存临时文件错误");
                    collect = false;
                }
                i++;
                if (i > 10)
                    i = 1;
            }
        }
        try {
            if (re != null)
                re.close();
        } catch (Exception e) {
        }
        if (bw != null) {
            try {
                bw.flush();
                bw.close();
            } catch (Exception e) {
            }
            if (temporary != null) {
                boolean rename = temporary.renameTo(new File(parent, String.format(FILE_NAME, i)));
                if (!rename)
                    Log.e(TAG, "临时文件更改为正式文件错误");
            }
        }
        Log.d(TAG, "收集结束");
    }

    public static File[] logFiles(Context context) {
        File cache = context.getCacheDir();
        String name = context.getPackageName();
        File parent = new File(cache, name);
        File[] files = parent.listFiles();
        if (files == null || files.length <= 0)
            return null;
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long last = o1.lastModified() - o2.lastModified();
                if (last == 0)
                    return 0;
                if (last > 0)
                    return 1;
                return -1;
            }
        });
        List<File> list = new ArrayList<>();
        for (File file : files) {
            if (file.getName().startsWith("log"))
                list.add(file);
        }
        return list.toArray(new File[list.size()]);
    }
}
