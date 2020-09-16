package com.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import androidx.annotation.NonNull;

public class LogExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "LogExceptionHandler";
    private static final String ERROR_LISTENER = "log_exception_handler";
    private Thread.UncaughtExceptionHandler exceptionHandler;
    private File parent;
    private ErrorListener listener;
    private Context context;

    LogExceptionHandler(String parent, Context context) {
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.parent = new File(parent);
        this.context = context;
        if (!this.parent.exists())
            this.parent.mkdirs();
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Class cl = Class.forName(applicationInfo.metaData.getString(ERROR_LISTENER));
            if (ErrorListener.class.isAssignableFrom(cl))
                listener = (ErrorListener) cl.newInstance();
        } catch (Exception e) {
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
        File file = new File(parent, "error");
        if (file.exists()) {
            if (listener != null)
                listener.onError(file2string(file), anr(), Logcat.logFiles(context));
            file.delete();
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        String throwa = throwable2string(throwable);
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(parent, "error"));
            os.write(throwa.getBytes());
            os.flush();
        } catch (Exception e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
        if (listener != null)
            listener.onError(throwa, anr(), Logcat.logFiles(context));
        if (exceptionHandler != null) {
            exceptionHandler.uncaughtException(thread, throwable);
        }
    }

    public String anr() {
        return file2string(new File("data/anr/traces.txt"));
    }

    public String file2string(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;
                if (line.isEmpty())
                    continue;
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (Exception e) {
                }
        }
        return sb.toString();
    }

    private String throwable2string(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append("model:");
        sb.append(Build.MODEL);
        sb.append("\nmake:");
        sb.append(Build.MANUFACTURER);
        sb.append("\nbrand:");
        sb.append(Build.BRAND);
        sb.append("\nsystem_version:");
        sb.append(Build.VERSION.RELEASE);
        sb.append("\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }
}
