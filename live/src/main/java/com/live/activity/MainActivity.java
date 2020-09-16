package com.live.activity;

import android.accessibilityservice.AccessibilityService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.RecoverySystem;
import android.util.Log;
import android.view.Choreographer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bar.Bar;
import com.base.BaseActivity;
import com.base.adapter.FragmentAdapter;
import com.base.thread.PoolManager;
import com.base.util.L;
import com.base.widget.ViewPager;
import com.http.Http;
import com.http.ResponseString;
import com.http.down.DownListener;
import com.http.listen.ResponseStringListener;
import com.io.StreamUtil;
import com.io.provider.FileProvider;
import com.io.sava.AndroidFileUtil;
import com.io.sava.FileUtil;
import com.live.R;
import com.live.fragment.MyFragment;
import com.live.fragment.LiveFragment;
import com.live.fragment.HotFragment;
import com.live.fragment.HomeFragment;
import com.live.widget.MainButtonView;
import com.log.Logcat;
import com.notification.XHNotification;
import com.operation.IOperation;
import com.screen.ScreenManager;
import com.threelogin.ILogin;
import com.threelogin.LoginListener;
import com.threelogin.UserEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.main_vp)
    ViewPager mainVp;
    @BindView(R.id.main_tv)
    MainButtonView mainTv;
    @BindView(R.id.main_nice)
    MainButtonView mainNice;
    @BindView(R.id.main_remote_control)
    MainButtonView mainRemoteControl;
    @BindView(R.id.main_my)
    MainButtonView mainMy;
    private List<MainButtonView> views;
    private FragmentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScreenManager.setStatusBarColor(getWindow(), getResources().getColor(R.color.color13));
        ScreenManager.statusBarTextColorBlack(getWindow());
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Log.e(TAG,appInfo.metaData.getString("app_channel","test"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PoolManager.shortTime(new Runnable() {
            @Override
            public void run() {
                File t = new File(getCacheDir(), "text.so");
                L.e(TAG, "开始复制");
                if (FileUtil.copy(new File("/system/lib/libutils.so"), t)) {
                    L.d(TAG, "复制成功");
                    try {
                        L.d(TAG, String.format("%s,%s,%s,%s", t.exists(), t.isFile(), t.canRead(), t.length()));
                        System.load(t.getAbsolutePath());
                        L.d(TAG, "加载成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Toast toast=Toast.makeText(this,"测试",Toast.LENGTH_SHORT);
        TextView textView=toast.getView().findViewById(android.R.id.message);
        textView.setTextColor(Color.RED);
        toast.show();
//        PoolManager.runUiThread(new Runnable() {
//            @Override
//            public void run() {
//                HttpService.stopHttpService(getApplicationContext());
//                Log.e(TAG,"start");
//        final Choreographer choreographer = Choreographer.getInstance();
//                choreographer.postFrameCallback(new Choreographer.FrameCallback() {
//                    @Override
//                    public void doFrame(long frameTimeNanos) {
//                        Log.e(TAG,"===========");
//                        choreographer.postFrameCallback(this);
//                    }
//                });
//            }
//        }, 3000);
        ButterKnife.bind(this);
        views = new ArrayList<>();
        views.add(mainTv);
        views.add(mainNice);
        views.add(mainRemoteControl);
        views.add(mainMy);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new LiveFragment());
        fragments.add(new HotFragment());
        fragments.add(new MyFragment());
        adapter = new FragmentAdapter(this, fragments);
        mainVp.setAdapter(adapter);
        for (View view : views) {
            view.setOnClickListener(this);
        }
        mainTv.select(true);
        EventBus.getDefault().register(this);
        downFile.down("http://down.xiaoweizhibo.com/xw/upload/201911/12/23/67797d1d91a88d758329c9ed26635557.apk", new File(getCacheDir() + "/test.apk"), new DownListener() {
            @Override
            public void downed(String url, File file) {
                L.d( "下载完成" + file.length());
                installNormal(file);
            }

            @Override
            public void downFailure(String url, File file) {

            }
        }).down();
//        Http.RequestEntity entity1 = new Http.RequestEntity();
//        entity1.raw = "{\n" +
//                "        \"self\":{\n" +
//                "            \"md5\":\"0\"\n" +
//                "        },\n" +
//                "        \"programs\":[],\n" +
//                "        \"system\":{\n" +
//                "            \"mac\":\"52b497e97eb9\",\n" +
//                "            \"interfaces\":\"br0-eth0\",\n" +
//                "            \"chip\":\"armeabi-v7a\",\n" +
//                "            \"machine\":\"armeabi-v7a\",\n" +
//                "            \"os\":\"Android\",\n" +
//                "            \"version\":\"9\",\n" +
//                "            \"arch\":\"armeabi-v7a\",\n" +
//                "            \"manu\":\"feiyi033\"\n" +
//                "        }\n" +
//                "    }";
//        entity1.url = "http://m.shifen1.com:80/manage";
//        entity1.stringListener = new ResponseStringListener() {
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void failure() {
//                Log.d(TAG, "====failure");
//            }
//
//            @Override
//            public void success(ResponseString response) {
//                Log.d(TAG, String.format("code:%s,response:%s", response.code, response.response));
//            }
//        };
//        http.postAsyn(entity1);
    }

    public boolean installNormal(File filePath) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT > 23) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//            }
//            intent.setDataAndType(AndroidFileUtil.file2uri(filePath, this), "application/vnd.android.package-archive");
//            startActivity(intent);
//            return true;
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
        installSilent(filePath.getAbsolutePath());
        return false;
    }

    /**
     * install slient
     *
     * @param filePath
     * @return 0 means normal, 1 means file not exist, 2 means other exception error
     */
    public static int installSilent(String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0 || file == null || file.length() <= 0 || !file.exists() || !file.isFile()) {
            return 1;
        }

        String[] args = {"pm", "install", "-r", filePath};
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        // TODO should add memory is not enough here
        if (successMsg.toString().contains("Success") || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = 2;
        }
        Log.d("test-test", "successMsg:" + successMsg + ", ErrorMsg:" + errorMsg);
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(String tab) {
        Log.e(TAG, tab);
        ((HomeFragment) adapter.getItem(0)).event(tab);
    }

    @Override
    public void onClick(View v) {
        for (MainButtonView view : views)
            view.select(false);
        int index = views.indexOf(v);
        if (index >= 0) {
            views.get(index).select(true);
            mainVp.setCurrentItem(index, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
