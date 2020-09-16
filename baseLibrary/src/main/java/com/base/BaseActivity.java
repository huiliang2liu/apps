package com.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.result.ResultImpl;
import com.base.widget.FloatView;
import com.http.FileHttp;
import com.http.Http;
import com.http.down.DownFile;
import com.image.IImageLoad;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class BaseActivity extends FragmentActivity {
    private static final String TAG = "BaseActivity";
    protected BaseApplication baseApplication;
    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    protected int statusBarHeight;
    protected Window window;
    protected ResultImpl result;
    private View view;
    private FloatView floatView;
    private Resources resources;
    protected Http http;
    protected FileHttp fileHttp;
    protected DownFile downFile;
    protected IImageLoad imageLoad;
    protected Uri mUri;
    public int dp1;
    public int sp1;
    public int dip1;
    public int width;
    public int height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseApplication = (BaseApplication) getApplication();
        http = baseApplication.http;
        fileHttp = baseApplication.fileHttp;
        imageLoad = baseApplication.imageLoad;
        downFile = baseApplication.downFile;
//        if (fullScreen())
        int resourceId = getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        statusBarHeight = getResources()
                .getDimensionPixelSize(resourceId);
        window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager
                .LayoutParams.FLAG_HARDWARE_ACCELERATED);
        result = new ResultImpl(this);
        floatView = new FloatView(this);
        resources = getResources();
        dp1 = resources.getDimensionPixelSize(R.dimen.dp1);
        sp1 = resources.getDimensionPixelSize(R.dimen.sp1);
        dip1 = resources.getDimensionPixelOffset(R.dimen.dip1);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        initDate(getIntent());
    }

    public void landscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void portrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setContentView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initDate(intent);
    }

    private void setContentView() {
        view = getWindow().getDecorView();
    }

    protected void initDate(Intent intent) {
        mUri = intent.getData();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(enterAnim(), exitAnim());
    }

    protected int enterAnim() {
        return R.anim.activity_translate_enter;
    }

    protected int exitAnim() {
        return R.anim.activity_scale_exit;
    }

    @Override
    protected void onResume() {
        setStatusBar();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, exitAnim());
    }

    //    public boolean has

    protected boolean fullScreen() {
        return false;
    }

    protected int statusBarColorId() {
        return 0;
    }

    protected boolean changeTextColor() {
        return false;
    }

    public void invisibleStatusBar() {
        int vis = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(vis
                | View.INVISIBLE);

    }


    public void visibleStatusBar() {
        int vis = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(vis
                & (~View.INVISIBLE));

    }

    public void statusBarTextColorWhite() {
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            setOPPOStatusTextColor1();
        } else {
            int vis = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(vis
                    & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
                mIUISetStatusBarLightMode(false);
            else if (Build.MANUFACTURER.equalsIgnoreCase("Meizu"))
                flymeLightStatusBar(false);
        }
    }

    public void statusBarTextColorBlack() {
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            setOPPOStatusTextColor();
        } else {
            int vis = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(vis
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
                mIUISetStatusBarLightMode(true);
            else if (Build.MANUFACTURER.equalsIgnoreCase("Meizu"))
                flymeLightStatusBar(true);
        }
    }

    public boolean mIUISetStatusBarLightMode(boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    private boolean flymeLightStatusBar(boolean dark) {
        boolean result = false;
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            result = true;
        } catch (Exception e) {
        }
        return result;
    }

    private final void setStatusBar() {
        if (fullScreen()) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= 21)
                window.setStatusBarColor(Color.TRANSPARENT);
        }
        int color = statusBarColorId();
        if (color <= 0)
            return;
        Log.e(TAG, String.format("statusBarColor:%d", color));
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(color);
        else {
            View statusView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusView.setLayoutParams(params);
            statusView.setBackgroundColor(color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) getWindow()
                    .getDecorView();
            decorView.addView(statusView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
        if (changeTextColor()) {
            statusBarTextColorBlack();
        }
    }

    private void setOPPOStatusTextColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;

        }
        window.getDecorView().setSystemUiVisibility(vis);
    }

    private void setOPPOStatusTextColor1() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;

        }
        window.getDecorView().setSystemUiVisibility(vis);
    }

    public Uri getReferrer() {
        if (Build.VERSION.SDK_INT > 21)
            return super.getReferrer();
        return getIntent().getParcelableExtra(Intent.EXTRA_REFERRER);
    }

    public String getReferrerPackage() {
        Uri uri = getReferrer();
        if (uri != null)
            return uri.getAuthority();
        return null;
    }

    public String getScheme() {
        if (mUri != null)
            return mUri.getScheme();
        return null;
    }

    public String getHost() {
        if (mUri != null)
            return mUri.getHost();
        return null;
    }

    public int getPort() {
        if (mUri != null)
            return mUri.getPort();
        return -1;
    }

    public String getPath() {
        if (mUri != null)
            return mUri.getPath();
        return null;
    }

    public String getQuery() {
        if (mUri != null)
            return mUri.getQuery();
        return null;
    }

    public String getParameter(String key) {
        if (mUri != null)
            return mUri.getQueryParameter(key);
        return null;
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    public Set<String> getKeys() {
        if (mUri != null)
            return mUri.getQueryParameterNames();
        return null;
    }
}
