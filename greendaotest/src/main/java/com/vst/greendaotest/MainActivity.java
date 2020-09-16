package com.vst.greendaotest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.LruCache;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this,"user.db");
        DaoMaster daoMaster=new DaoMaster(helper.getWritableDatabase());
        DaoSession session=daoMaster.newSession();
        session.getUseInfoDao();
//        DiskLruCache
    }
}
