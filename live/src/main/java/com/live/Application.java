package com.live;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RatingBar;

import com.base.BaseApplication;
import com.base.util.L;
import com.dlna.util.DeviceManager;
import com.io.PublicFiles;
import com.live.entities.RadioEntity;
import com.media.MusicService;
import com.media.bind.IPlayMusic;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;


public class Application extends BaseApplication implements PublicFiles {
    //    public FloatManager floatManager;
    public DeviceManager clingManager;
    public IPlayMusic playMusic;
    public RadioEntity radioEntity;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindService(new Intent(getApplicationContext(), MusicService.class), conn, Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            playMusic = IPlayMusic.Stub.asInterface(service);
            try {
                if (playMusic != null && !playMusic.isPlay()) {

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public boolean play(RadioEntity entity) {
        if (playMusic == null || entity == null)
            return false;
        try {
            if (entity.equals(radioEntity) && playMusic.isPlay())
                return false;
            if (playMusic.isPlay())
                playMusic.pause();
            radioEntity = entity;
            playMusic.setPath(entity.strings.get(entity.playIndex));
            playMusic.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unbindService(conn);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        floatManager = new FloatManager(this);
//        floatManager.shortToast("测试");
        Log.e("onCreate", isMainProcess()+"");
        if(!isMainProcess())
            return;
        UMConfigure.init(this, "5df75e17570df3c614000986", "live", UMConfigure.DEVICE_TYPE_PHONE, null);
        bindService(new Intent(getApplicationContext(), MusicService.class), conn, Context.BIND_AUTO_CREATE);
        clingManager = new DeviceManager(this);
        try {
            Field[] implField = InetAddress.class.getDeclaredFields();
            for (Field field : implField)
                Log.d("==", field.getName() + "  " + field.getType());
//            implField.setAccessible(true);
//            Object impl = implField.get(null);
//            Field addressCacheField = impl.getClass().getDeclaredField("addressCache");
//            addressCacheField.setAccessible(true);
//            Object addressCache = addressCacheField.get(null);
//            Method put = addressCache.getClass().getDeclaredMethod("put", new Class[]{String.class, int.class, InetAddress[].class});
//            put.setAccessible(true);
//            put.invoke(addressCache,"res.cp88.ott.cibntv.net",0,InetAddress.getAllByName("down.xiaoweizhibo.com"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class c = Class.forName("com.tvblack.tvf.ad.TVBADF");
            c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            PackageInfo info= getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
//            ActivityInfo[] activities=info.activities;
//            for (ActivityInfo activityInfo:activities)
//                L.d("===",activityInfo.taskAffinity);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        getApplicationInfo().sourceDir
//        clingManager.
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public File[] publicFiles() {
        return new File[]{getCacheDir()};
    }
}
