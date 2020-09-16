package com.electricity;

import android.content.Context;

import com.base.BaseApplication;
import com.floatW.FloatManager;
import com.io.db.DbManager;
import com.wifi.Wifi;

public class ElectricityApplication extends BaseApplication {
    public DbManager manager;
    public Wifi wifi;
    public FloatManager floatManager;

    static {
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//                Log.e("====","====",e);
//                System.exit(0);
//                Process.killProcess(Process.myPid());
//            }
//        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//                Intent intent = new Intent(ElectricityApplication.this, SplashActivity.class);
//                PendingIntent restartIntent = PendingIntent.getActivity(
//                        getApplicationContext(), 0, intent,
//                        PendingIntent.FLAG_CANCEL_CURRENT);
//                //退出程序
//                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
//                        restartIntent); // 1秒钟后重启应用
//                System.exit(0);
//                Process.killProcess(Process.myPid());
//            }
//        });

    }

    @Override
    public void onCreate() {
//        ScreenManager.getInstance(this);
//        registerActivityLifecycleCallbacks();
        super.onCreate();
//        manager = new DbManager(this, "test");
//        List<TableEntity> entities = new ArrayList<>();
//        TableEntity tableEntity = new TableEntity();
//        tableEntity.add(TableEntity.Type.PRIMARY_KEY).add(TableEntity.Type.AUTO_INCREMENT).setName("id").setType(TableEntity.KeyType.INTEGER);
//        entities.add(tableEntity);
//
//        TableEntity tableEntity1 = new TableEntity();
//        tableEntity1.add(TableEntity.Type.NOT_NULL).setName("name").setType(TableEntity.KeyType.STRING);
//        entities.add(tableEntity1);
//        manager.createTable("test", entities);
        wifi = new Wifi(this);
        floatManager = new FloatManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        wifi.destory();
    }
}
