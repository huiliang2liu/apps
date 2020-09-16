package com.move;

import android.content.Intent;
import android.os.Bundle;

import com.base.thread.PoolManager;
import com.base.util.L;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.io.StreamUtil;
import com.screen.ScreenManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        PoolManager.shortTime(new Runnable() {
            @Override
            public void run() {
                try {
                   final File file = new File(getCacheDir(), "text.apk");
                    InputStream is = getAssets().open("tielu12306_194.apk");
                    StreamUtil.stream2file(is, file);
                    L.e(TAG,"开始启动工作");
                    ScreenManager.getInstance(MainActivity.this).loadApk(file.getAbsolutePath());
                    PoolManager.runUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                L.e(TAG,"加载");
                                ScreenManager.getInstance(MainActivity.this).addResources(file.getAbsolutePath());
                                L.e(TAG,"开始启动电视家");
                                startActivity(new Intent(getBaseContext(),Class.forName("com.alipay.mobile.quinox.LauncherActivity")));
                                L.e(TAG,"启动电视家");
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                L.e(TAG,"启动电视家失败");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        com.elinkway.infinitemovies.ui.activity.SplashActivity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
