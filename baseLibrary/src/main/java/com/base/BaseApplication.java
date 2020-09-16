package com.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Process;

import com.base.net.NetSpeed;
import com.base.thread.PoolManager;
import com.http.FileHttp;
import com.http.Http;
import com.http.down.DownFile;
import com.image.IImageLoad;


import java.util.List;

import androidx.multidex.MultiDex;

public class BaseApplication extends Application {
    public Http http;
    public FileHttp fileHttp;
    public DownFile downFile;
    public IImageLoad imageLoad;
    public PoolManager poolManager;
    public NetSpeed netSpeed;

    @Override
    public void onCreate() {
        super.onCreate();
        http = new Http.Build().context(this).build();
        fileHttp = new FileHttp.Build().build(this);
        downFile=new DownFile.Build().context(this).build();
        imageLoad = new IImageLoad.Builder().context(this).build();
        poolManager = new PoolManager();
        netSpeed = new NetSpeed(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        netSpeed.stop();
    }
    /**
     * 判断该进程ID是否属于该进程名
     * @param pid 进程ID
     * @param p_name 进程名
     * @return true属于该进程名
     */
    public boolean isPidOfProcessName(int pid, String p_name) {
        if (p_name == null)
            return false;
        boolean isMain = false;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有进程
        for (ActivityManager.RunningAppProcessInfo process : am.getRunningAppProcesses()) {
            if (process.pid == pid) {
                //进程ID相同时判断该进程名是否一致
                if (process.processName.equals(p_name)) {
                    isMain = true;
                }
                break;
            }
        }
        return isMain;
    }
    /**
     * 获取当前App所有进程
     */
    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfos() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses();
    }

    /**
     * 获取主进程名
     * @return 主进程名
     */
    public String getMainProcessName() throws PackageManager.NameNotFoundException {
        return getPackageManager().getApplicationInfo(getPackageName(), 0).processName;
    }
    /**
     * 判断是否主进程
     * @return true是主进程
     */
    public boolean isMainProcess()  {
        try {
            return isPidOfProcessName(Process.myPid(), getMainProcessName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  false;
    }
}
