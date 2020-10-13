package com.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Service;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loader.ApkClassLoader;
import com.screen.entities.ApkEntity;
import com.screen.iface.AttributeSetZoom;
import com.screen.iface.BackColor;
import com.screen.iface.ChangePackage;
import com.screen.iface.FullScreen;
import com.screen.iface.InvisibleStatusBar;
import com.screen.iface.StatusBarColor;
import com.screen.iface.StatusBarTextColorBlack;
import com.screen.iface.StatusBarTextColorWhite;
import com.screen.iface.TextStyle;
import com.screen.placeholder.DefaultActivity;
import com.screen.placeholder.PlaceholderService;
import com.screen.pm.ContextImp;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dalvik.system.BaseDexClassLoader;

public class ScreenManager extends FragmentManager.FragmentLifecycleCallbacks implements Application.ActivityLifecycleCallbacks, PlugInstrumentation.InstrumentationListener, PlugInstrumentation.ActivityLifeListener, TextStyle, AttributeSetZoom, Handler.Callback {
    private static final String TAG = "ScreenManager";
    private static final String SCREEN_WIDTH = "screen_width_in_px";
    private static final String SCREEN_HEIGHT = "screen_height_in_px";
    private static final String SCREEN_LAYOUT_UNIT_PX = "screen_layout_unit_px";
    private static final String SCREEN_WIDGET = "screen_widget";
    private static final String SCREEN_ELLIPSIZE = "screen_ellipsize";
    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    private static Method attach;
    private static final Class<ContextWrapper> CONTEXT_WRAPPER_CLASS = ContextWrapper.class;
    private static final Class<ContextThemeWrapper> CONTEXT_THEME_WRAPPER_CLASS = ContextThemeWrapper.class;
    private static Field mBase;
    private static Field mTheme;
    private static Field mResources;
    private static Field mComponentCallbacks;
    private static Field mActivityLifecycleCallbacks;
    private static Field mAssistCallbacks;
    private static Field mActivityInfo;
    private Map<String, SoftReference<Typeface>> mCache = new HashMap<>();
    protected TextUtils.TruncateAt ellipsize = TextUtils.TruncateAt.END;
    private List<TextStyle> styles;
    private static Class CreateServiceData;
    private static Field mH;
    private static Field mCallback;
    private static Field info;
    private List<String> services = new ArrayList<>();

    static {
        try {
            mCallback = Handler.class.getDeclaredField("mCallback");
            mCallback.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取mCallback字段错误");
        }
        try {
            CreateServiceData = Class.forName("android.app.ActivityThread$CreateServiceData");
            info = CreateServiceData.getDeclaredField("info");
            info.setAccessible(true);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "获取CreateServiceData字段错误");
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "获取info字段错误");
        }
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            mH = activityThreadClass.getDeclaredField("mH");
        } catch (Exception e) {
            Log.e(TAG, "获取mH字段错误");
        }
        try {
            mBase = CONTEXT_WRAPPER_CLASS.getDeclaredField("mBase");
            mBase.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取mBase字段错误");
        }
        try {
            mTheme = CONTEXT_THEME_WRAPPER_CLASS.getDeclaredField("mTheme");
            mTheme.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取mTheme字段错误");
        }
        try {
            mResources = CONTEXT_THEME_WRAPPER_CLASS.getDeclaredField("mResources");
            mResources.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取mResources字段错误");
        }
        try {
            attach = Application.class.getDeclaredMethod("attach", Context.class);
            attach.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取方法attach错误");
        }
        try {
            mComponentCallbacks = Application.class.getDeclaredField("mComponentCallbacks");
            mComponentCallbacks.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mComponentCallbacks错误");
        }
        try {
            mActivityLifecycleCallbacks = Application.class.getDeclaredField("mActivityLifecycleCallbacks");
            mActivityLifecycleCallbacks.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mActivityLifecycleCallbacks错误");
        }
        try {
            mAssistCallbacks = Application.class.getDeclaredField("mAssistCallbacks");
            mAssistCallbacks.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mAssistCallbacks错误");
        }
        try {
            mActivityInfo = Activity.class.getDeclaredField("mActivityInfo");
            mActivityInfo.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mActivityInfo错误");
        }
    }

    //    protected int width;
//    protected int height;
    private static ScreenManager manager;
    private Application application;
    protected DisplayMetrics displayMetrics;
    protected int widthPixels;
    protected int heightPixels;
    protected float widthScale = 1.0f;
    protected float wHScale = 1.0f;
    protected LayoutInflater systemLayoutInflater;
    private int densityDpi;
    private float density;
    private float scaledDensity;
    protected float fontScale;
    protected boolean px;
    private static int statusBarHeight = -1;
    private Instrumentation instrumentation;
    private Object activityThread;
    private List<PlugListener> listeners;
    protected String[] widget;
    protected String placeholderActivity;
    protected String placeholderService;
    protected PackageInfo packageInfo;
    protected PackageManager packageManager;
    protected ApkClassLoader apkClassLoader;
    private List<ApkEntity> entities = new Vector<>();

    public static ScreenManager getInstance(Context context) {
        if (manager == null) {
            synchronized (TAG) {
                if (manager == null) {
                    manager = new ScreenManager(context);
                }
            }
        }
        return manager;
    }

    private ScreenManager(Context context) {
        Log.e(TAG, "ScreenManager");
        styles = new ArrayList<>();
        application = (Application) context.getApplicationContext();
        if (application instanceof TextStyle)
            registTextStyle((TextStyle) application);
        application.registerActivityLifecycleCallbacks(this);
        ScreenApplicationV15 screenApplicationV15;
        if (Build.VERSION.SDK_INT > 17) {
            screenApplicationV15 = new ScreenApplicationV18();
            application.registerOnProvideAssistDataListener((Application.OnProvideAssistDataListener) screenApplicationV15);
        } else {
            screenApplicationV15 = new ScreenApplicationV15();
        }
        systemLayoutInflater = LayoutInflater.from(application);
        application.registerActivityLifecycleCallbacks(screenApplicationV15);
        application.registerComponentCallbacks(screenApplicationV15);
        displayMetrics = new DisplayMetrics();
        displayMetrics.setTo(context.getResources().getDisplayMetrics());
        widthPixels = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;
        density = displayMetrics.density;
        densityDpi = displayMetrics.densityDpi;
        scaledDensity = displayMetrics.scaledDensity;
        fontScale = context.getResources().getConfiguration().fontScale;
        packageManager = context.getPackageManager();
        listeners = new ArrayList<>();
        String packageName = context.getPackageName();
        int width = 0, height = 0;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES
                    | PackageManager.GET_SERVICES
                    | PackageManager.GET_META_DATA
                    | PackageManager.GET_PERMISSIONS
                    | PackageManager.GET_SIGNATURES);
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            try {
                width = applicationInfo.metaData
                        .getInt(SCREEN_WIDTH);
            } catch (Exception e) {
                Log.e(TAG, "获取宽度出错了");
                width = widthPixels;
            }
            try {
                height = applicationInfo.metaData.getInt(SCREEN_HEIGHT);
            } catch (Exception e) {
                Log.e(TAG, "获取高度出错了");
                height = heightPixels;
            }
            try {
                String screenEllipsize = applicationInfo.metaData.getString(SCREEN_ELLIPSIZE, "end");
                if ("start".equals(screenEllipsize))
                    ellipsize = TextUtils.TruncateAt.START;
                else if ("middle".equals(screenEllipsize))
                    ellipsize = TextUtils.TruncateAt.MIDDLE;
                else if ("marquee".equals(screenEllipsize))
                    ellipsize = TextUtils.TruncateAt.MARQUEE;
                else
                    ellipsize = TextUtils.TruncateAt.END;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                px = applicationInfo.metaData.getBoolean(SCREEN_LAYOUT_UNIT_PX);
            } catch (Exception e) {
                Log.e(TAG, "获取使用像素出错了");
                px = false;
            }
            try {
                String widget = applicationInfo.metaData.getString(SCREEN_WIDGET);
                Log.e(TAG, "" + widget);
                if (widget == null || widget.isEmpty()) {
                    this.widget = new String[0];
                } else
                    this.widget = widget.split("\\|");
            } catch (Exception e) {
                Log.e(TAG, "获取自定义布局包错误");
                px = false;
            }
            placeholderActivity = DefaultActivity.class.getName();
            placeholderService = PlaceholderService.class.getName();
        } catch (Exception e) {
            Log.e(TAG, "获取适配出错了");
        }
        if (width > 0) {
            widthScale = widthPixels * 1.0f / width;
            wHScale = heightPixels * 1.0f / width;
        } else {
            widthScale = density;
            wHScale = heightPixels * 1.0f / widthPixels;
        }
        apkClassLoader = new ApkClassLoader(context);
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            if (!sCurrentActivityThreadField.isAccessible())
                sCurrentActivityThreadField.setAccessible(true);
            activityThread = sCurrentActivityThreadField.get(null);
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            if (!mInstrumentationField.isAccessible())
                mInstrumentationField.setAccessible(true);
            instrumentation = new PlugInstrumentation((Instrumentation) mInstrumentationField.get(activityThread), this, this);
            mInstrumentationField.set(activityThread, instrumentation);
            mCallback.set(mH.get(activityThread), this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "not found android.app.ActivityThread");
        }
    }

    public static void hideSoftBack(Window window){
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v =window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void showSoftBack(Window window){
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            //低版本sdk
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView =window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        Object o = msg.obj;
        if (o != null) {
            Class oc = o.getClass();
            Log.d(TAG, o.toString());
            if (oc.isAssignableFrom(CreateServiceData)) {
                try {
                    ServiceInfo serviceInfo = (ServiceInfo) info.get(o);
                    serviceInfo.name = services.get(0);
                    services.remove(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, String.format("CreateServiceData,%s", o));
            }
        }
        return false;
    }

    public List<ApkEntity> apkEntities() {
        return entities;
    }

    public ApkEntity name2apkEntity(String name) {
        if (name == null || name.isEmpty())
            return null;
        for (ApkEntity apkEntity : entities) {
            if (name.equals(apkEntity.mPackageName))
                return apkEntity;
        }
        return null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager
                .LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setConfiguration(activity.getResources().getConfiguration(), activity);
        activity.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {

            }

            @Override
            public void onLowMemory() {

            }
        });

        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(this, true);
        }

    }

    private void setConfiguration(Configuration newConfig, Activity activity) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater instanceof AppLayoutInflater) {
            AppLayoutInflater appLayoutInflater = (AppLayoutInflater) layoutInflater;
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                appLayoutInflater.setScale(widthScale);
            else
                appLayoutInflater.setScale(wHScale);
        }
//        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
//        if (activity instanceof CancelAdapt) {
//            displayMetrics.density = this.displayMetrics.density;
//            displayMetrics.xdpi = this.displayMetrics.xdpi;
//            displayMetrics.scaledDensity = this.displayMetrics.scaledDensity;
//            displayMetrics.densityDpi = this.displayMetrics.densityDpi;
//            return;
//        }
//        float widthScale = this.widthScale, wHScale = this.wHScale;
//        if (activity instanceof CustomAdapt) {
//            CustomAdapt customAdapt = (CustomAdapt) activity;
//            int width = customAdapt.width();
//            if (width > 0) {
//                widthScale = widthPixels * 1.0f / width;
//                wHScale = heightPixels * 1.0f / width;
//            }
//        }
//        if (!px) {
//            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                displayMetrics.density = widthScale;
//                displayMetrics.xdpi = widthScale;
//                displayMetrics.scaledDensity = scaledDensity * widthScale / density;
//                displayMetrics.densityDpi = (int) (widthScale * 160);
////                newConfig.fontScale = widthScale;
//            } else {
//                displayMetrics.density = wHScale;
//                displayMetrics.densityDpi = (int) (wHScale * 160);
//                displayMetrics.scaledDensity = scaledDensity * wHScale / density;
//                displayMetrics.xdpi = wHScale;
////                newConfig.fontScale = displayMetrics.scaledDensity;
//            }
//        } else {
//            displayMetrics.density = 1;
//            displayMetrics.densityDpi = 160;
//            displayMetrics.scaledDensity = 1;
//            displayMetrics.xdpi = 1;
//            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            if (layoutInflater instanceof AppLayoutInflater) {
//                AppLayoutInflater appLayoutInflater = (AppLayoutInflater) layoutInflater;
//                if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
//                    appLayoutInflater.setScale(widthScale);
//                else
//                    appLayoutInflater.setScale(wHScale);
//            }
//        }

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }


    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof StatusBarTextColorWhite)
            statusBarTextColorWhite(activity.getWindow());
        else if (activity instanceof StatusBarTextColorBlack)
            statusBarTextColorBlack(activity.getWindow());
        if (activity instanceof FullScreen) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (!(activity instanceof StatusBarColor))
                setStatusBarColor(activity.getWindow(), Color.TRANSPARENT);
        }
        if (activity instanceof StatusBarColor) {
            StatusBarColor statusBarColor = (StatusBarColor) activity;
            setStatusBarColor(activity.getWindow(), statusBarColor.statusBarColor());
        }
        if (activity instanceof InvisibleStatusBar)
            invisibleStatusBar(activity.getWindow());
        if (activity instanceof BackColor) {
            BackColor backColor = (BackColor) activity;
            activity.getWindow().getDecorView().setBackgroundColor(backColor.backColor());
        }
    }

    @Override
    public void setTextStyle(TextView textView, String textStyle) {
        if (styles.size() > 0)
            for (TextStyle style : styles)
                style.setTextStyle(textView, textStyle);
    }

    public void registTextStyle(TextStyle style) {
        if (styles != null && styles.indexOf(style) < 0)
            styles.add(style);
    }

    public void unRegistTextStyle(TextStyle style) {
        styles.remove(style);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof TextStyle)
            unRegistTextStyle((TextStyle) activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof TextStyle)
            unRegistTextStyle((TextStyle) activity);
    }


    @Override
    public void onFragmentPreAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
        super.onFragmentPreAttached(fm, f, context);
    }

    @Override
    public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
        super.onFragmentAttached(fm, f, context);
    }

    @Override
    public void onFragmentPreCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
        super.onFragmentPreCreated(fm, f, savedInstanceState);
    }

    @Override
    public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);
        if (f instanceof TextStyle)
            registTextStyle((TextStyle) f);
    }

    @Override
    public void onFragmentActivityCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @Nullable Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
    }

    @Override
    public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
    }

    @Override
    public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentStarted(fm, f);
    }

    @Override
    public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentResumed(fm, f);
    }

    @Override
    public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentPaused(fm, f);
    }

    @Override
    public void onFragmentStopped(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentStopped(fm, f);
    }

    @Override
    public void onFragmentSaveInstanceState(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Bundle outState) {
        super.onFragmentSaveInstanceState(fm, f, outState);
    }

    @Override
    public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentViewDestroyed(fm, f);
    }

    @Override
    public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentDestroyed(fm, f);
        if (f instanceof TextStyle)
            unRegistTextStyle((TextStyle) f);
    }

    @Override
    public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
        super.onFragmentDetached(fm, f);
    }

    @Override
    public String transformNew(String newClass) {
        for (PlugListener listener : listeners) {
            String nC = listener.transformNew(newClass);
            if (nC != null)
                return nC;
        }
        return newClass;
    }

    @Override
    public void onCreate(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent != null) {
            String string = intent.getStringExtra(PlugInstrumentation.ORIGINL_PACKAGE);
            intent.removeExtra(PlugInstrumentation.ORIGINL_PACKAGE);
            ApkEntity apkEntity = name2apkEntity(string);
            if (apkEntity != null) {
                String name = activity.getClass().getName();
                for (PackageParser.Activity activity1 : apkEntity.mPackage.activities) {
                    if (name.equals(activity1.className)) {
                        ActivityInfo info = activity1.info;
                        activity.setRequestedOrientation(info.screenOrientation);
                        Window mWindow = activity.getWindow();
                        if (info.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                            mWindow.setSoftInputMode(info.softInputMode);
                        }
                        if (info.uiOptions != 0) {
                            mWindow.setUiOptions(info.uiOptions);
                        }
                        if ((info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0)
                            mWindow.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
                        if (Build.VERSION.SDK_INT > 25)
                            mWindow.setColorMode(info.colorMode);
                        try {
                            mActivityInfo.set(activity, info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                try {
                    new ContextImp(activity, apkEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (activity instanceof ChangePackage) {
                    String packageName = ((ChangePackage) activity).packageName();
                    apkEntity = name2apkEntity(packageName);
                    if (apkEntity != null) {
                        try {
                            new ContextImp(activity, apkEntity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        for (PlugListener listener : listeners) {
            listener.onCreate(activity);
        }
        if (activity instanceof TextStyle)
            registTextStyle((TextStyle) activity);
    }

    public void startService(Context context, Intent intent) {
        ResolveInfo resolveInfo = packageManager.resolveService(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            context.startService(intent);
            return;
        }
        List<ApkEntity> entities = apkEntities();
        if (entities != null && entities.size() > 0) {
            for (ApkEntity entity : entities) {
                resolveInfo = entity.packageManage.resolveService(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo != null) {
                    intent.setComponent(new ComponentName(context.getPackageName(), placeholderService));
                    intent.putExtra(PlugInstrumentation.ORIGINAL_ACTIVITY, resolveInfo.serviceInfo.name);
                    intent.putExtra(PlugInstrumentation.ORIGINL_PACKAGE, entity.mPackageName);
                    services.add(resolveInfo.serviceInfo.name);
                    context.startService(intent);
                    return;
                }
            }
        }
        ComponentName componentName = intent.getComponent();
        if (componentName != null) {
            Log.e(TAG, "没注册的service");
            intent.putExtra(PlugInstrumentation.ORIGINAL_ACTIVITY, componentName.getClassName());
            intent.setClassName(componentName.getPackageName(), placeholderService);
            services.add(componentName.getClassName());
            context.startService(intent);
        }

    }

    public boolean bindService(Context context, Intent intent, ServiceConnection conn, int flags) {
        ResolveInfo resolveInfo = packageManager.resolveService(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            return context.bindService(intent, conn, flags);
        }
        List<ApkEntity> entities = apkEntities();
        if (entities != null && entities.size() > 0) {
            for (ApkEntity entity : entities) {
                resolveInfo = entity.packageManage.resolveService(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo != null) {
                    intent.setComponent(new ComponentName(context.getPackageName(), placeholderService));
                    intent.putExtra(PlugInstrumentation.ORIGINAL_ACTIVITY, resolveInfo.serviceInfo.name);
                    intent.putExtra(PlugInstrumentation.ORIGINL_PACKAGE, entity.mPackageName);
                    services.add(resolveInfo.serviceInfo.name);
                    return context.bindService(intent, conn, flags);

                }
            }
        }
        ComponentName componentName = intent.getComponent();
        if (componentName != null) {
            Log.e(TAG, "没注册的service");
            intent.putExtra(PlugInstrumentation.ORIGINAL_ACTIVITY, componentName.getClassName());
            intent.setClassName(componentName.getPackageName(), placeholderService);
            services.add(componentName.getClassName());
            return context.bindService(intent, conn, flags);
        }
        return false;
    }

    @Override
    public void onNewIntent(Activity activity) {
        for (PlugListener listener : listeners) {
            listener.onNewIntent(activity);
        }
    }

    public void registerPlugListener(PlugListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void unRegisterPlugListener(PlugListener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    public void load() {

    }

    public void addClassLoader(BaseDexClassLoader classLoader) {
        apkClassLoader.addClassLoader(classLoader);

    }

    public void loadDex(String... paths) {
        apkClassLoader.addDex(paths);
    }

    public void loadApk(String... paths) {
        apkClassLoader.addApk(paths);
    }

    public void addSo(String... so) {
        apkClassLoader.addSo(so);
    }

    public void addResources(String... resources) {
        if (resources == null || resources.length <= 0)
            return;
        for (String resoure : resources) {
            ApkEntity apkEntity = ApkEntity.apkPath2apkEntity(resoure, application);
            if (apkEntity != null)
                entities.add(apkEntity);
        }

    }


    public void loadSo(String... sos) {
        for (String so : sos) {
            String name = apkClassLoader.findLibrary(so);
            if (name != null && !name.isEmpty())
                System.load(name);
        }
    }

    public interface PlugListener extends PlugInstrumentation.InstrumentationListener, PlugInstrumentation.ActivityLifeListener {

    }

    public float wPx(float px) {
        return widthScale * px;
    }

    public float hPx(float px) {
        return wHScale * px;
    }

    /**
     * dp 的单位 转成为 px(像素)
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null)
            return (int) dpValue;
        return scale(context.getResources().getDisplayMetrics().density, dpValue);
    }

    /**
     * px(像素) 的单位 转成为 dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        if (context == null)
            return (int) pxValue;
        return scale(1 / context.getResources().getDisplayMetrics().density, pxValue);
    }

    /**
     * 像素转为sp
     *
     * @param pxValue
     * @return int
     */
    public static int px2sp(Context context, float pxValue) {
        if (context == null)
            return (int) pxValue;
        return scale(1 / context.getResources().getDisplayMetrics().scaledDensity, pxValue);
    }

    /**
     * sp转为像素
     *
     * @param spValue
     * @return int
     */
    public static int sp2px(Context context, float spValue) {
        if (context == null)
            return (int) spValue;
        return scale(context.getResources().getDisplayMetrics().scaledDensity, spValue);
    }

    public static int scale(float scale, float value) {
        return BigDecimal.valueOf(scale * value).setScale(0, BigDecimal.ROUND_CEILING).intValue();
    }

    public static void statusBarTextColorWhite(Window window) {
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            setOPPOStatusTextColor1(window);
        } else {
            int vis = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(vis
                    & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
                mIUISetStatusBarLightMode(false, window);
            else if (Build.MANUFACTURER.equalsIgnoreCase("Meizu"))
                flymeLightStatusBar(false, window);
        }
    }

    public static void statusBarTextColorBlack(Window window) {
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            setOPPOStatusTextColor(window);
        } else {
            int vis = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(vis
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
                mIUISetStatusBarLightMode(true, window);
            else if (Build.MANUFACTURER.equalsIgnoreCase("Meizu"))
                flymeLightStatusBar(true, window);
        }
    }

    private static void setOPPOStatusTextColor(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;

        }
        window.getDecorView().setSystemUiVisibility(vis);
    }

    private static void setOPPOStatusTextColor1(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int vis = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;

        }
        window.getDecorView().setSystemUiVisibility(vis);
    }


    private static boolean mIUISetStatusBarLightMode(boolean dark, Window window) {
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

    private static boolean flymeLightStatusBar(boolean dark, Window window) {
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

    public static void invisibleStatusBar(Window window) {
        int vis = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(vis
                | View.INVISIBLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }


    public static void visibleStatusBar(Window window) {
        int vis = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(vis
                & (~View.INVISIBLE));

    }

    public static void setStatusBarColor(Window window, int color) {
        Context context = window.getContext();
        if (statusBarHeight <= 0) {
            int resourceId = context.getResources().getIdentifier(
                    "status_bar_height", "dimen", "android");
            statusBarHeight = context.getResources()
                    .getDimensionPixelSize(resourceId);
        }
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(color);
        else {
            View statusView = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusView.setLayoutParams(params);
            statusView.setBackgroundColor(color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            decorView.addView(statusView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) decorView.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    public static Application application(String name, Context context) {
        if (name == null || name.isEmpty()) {
            name = Application.class.getName();
        }
        Class cls;
        try {
            cls = Class.forName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (!Application.class.isAssignableFrom(cls))
            return null;
        Application application;
        try {
            application = (Application) cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Application ac = (Application) context.getApplicationContext();
        try {
            attach.invoke(application, mBase.get(ac));
            application.onCreate();
        } catch (Exception e) {
        }
        return application;
    }

    public static void rigitsProvider(Context context, List<ProviderInfo> infos) throws Exception {
        Class activityThread = Class.forName("android.app.ActivityThread");
        Field sCurrentActivityThread = activityThread.getDeclaredField("sCurrentActivityThread");
        sCurrentActivityThread.setAccessible(true);
        Object at = sCurrentActivityThread.get(null);
        Method installContentProviders = activityThread.getDeclaredMethod("installContentProviders", new Class[]{Context.class, List.class});
        installContentProviders.setAccessible(true);
        installContentProviders.invoke(at, context, infos);
    }

    /**
     * <p>Replace the font of specified view and it's children</p>
     *
     * @param root     The root view.
     * @param fontPath font file path relative to 'assets' directory.
     */
    public void replaceFontFromAsset(@NonNull View root, @NonNull String fontPath) {
        replaceFont(root, createTypefaceFromAsset(fontPath));
    }

    public void replaceFontFromAsset(@NonNull View root, @NonNull String fontPath, int style) {
        replaceFont(root, createTypefaceFromAsset(fontPath), style);
    }

    /**
     * <p>Replace the font of specified view and it's children</p>
     *
     * @param root     The root view.
     * @param fontPath The full path to the font data.
     */
    public void replaceFontFromFile(@NonNull View root, @NonNull String fontPath) {
        replaceFont(root, createTypefaceFromFile(fontPath));
    }


    public void replaceFontFromFile(@NonNull View root, @NonNull String fontPath, int style) {
        replaceFont(root, createTypefaceFromFile(fontPath), style);
    }

    /**
     * <p>Replace the font of specified view and it's children with specified typeface</p>
     */
    private void replaceFont(@NonNull View root, @NonNull Typeface typeface) {
        if (root == null || typeface == null) {
            return;
        }

        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView) root;
            // Extract previous style of TextView
            int style = Typeface.NORMAL;
            if (textView.getTypeface() != null) {
                style = textView.getTypeface().getStyle();
            }
            textView.setTypeface(typeface, style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), typeface);
            }
        } // else return
    }

    /**
     * <p>Replace the font of specified view and it's children with specified typeface and text style</p>
     *
     * @param style One of {@link Typeface#NORMAL}, {@link Typeface#BOLD}, {@link Typeface#ITALIC}, {@link Typeface#BOLD_ITALIC}
     */
    private void replaceFont(@NonNull View root, @NonNull Typeface typeface, int style) {
        if (root == null || typeface == null) {
            return;
        }
        if (style < 0 || style > 3) {
            style = Typeface.NORMAL;
        }

        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView) root;
            textView.setTypeface(typeface, style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), typeface, style);
            }
        } // else return
    }

    /**
     * <p>Create a Typeface instance with specified font file</p>
     *
     * @param fontPath font file path relative to 'assets' directory.
     * @return Return created typeface instance.
     */
    private Typeface createTypefaceFromAsset(String fontPath) {
        SoftReference<Typeface> typefaceRef = mCache.get(fontPath);
        Typeface typeface = null;
        if (typefaceRef == null || (typeface = typefaceRef.get()) == null) {
            typeface = Typeface.createFromAsset(application.getAssets(), fontPath);
            typefaceRef = new SoftReference<>(typeface);
            mCache.put(fontPath, typefaceRef);
        }
        return typeface;
    }

    private Typeface createTypefaceFromFile(String fontPath) {
        SoftReference<Typeface> typefaceRef = mCache.get(fontPath);
        Typeface typeface = null;
        if (typefaceRef == null || (typeface = typefaceRef.get()) == null) {
            typeface = Typeface.createFromFile(fontPath);
            typefaceRef = new SoftReference<>(typeface);
            mCache.put(fontPath, typefaceRef);
        }
        return typeface;
    }

    /**
     * <p>Replace system default font. <b>Note:</b>you should also add code below to your app theme in styles.xml. </p>
     * {@code <item name="android:typeface">monospace</item>}
     * <p>The best place to call this method is {@link Application#onCreate()}, it will affect
     * whole app font.If you call this method after view is visible, you need to invalid the view to make it effective.</p>
     *
     * @param fontPath font file path relative to 'assets' directory.
     */
    public void replaceSystemDefaultFontFromAsset(@NonNull String fontPath) {
        replaceSystemDefaultFont(createTypefaceFromAsset(fontPath));
    }

    /**
     * <p>Replace system default font. <b>Note:</b>you should also add code below to your app theme in styles.xml. </p>
     * {@code <item name="android:typeface">monospace</item>}
     * <p>The best place to call this method is {@link Application#onCreate()}, it will affect
     * whole app font.If you call this method after view is visible, you need to invalid the view to make it effective.</p>
     *
     * @param context  {@link Context Context}
     * @param fontPath The full path to the font data.
     */
    public void replaceSystemDefaultFontFromFile(@NonNull Context context, @NonNull String fontPath) {
        replaceSystemDefaultFont(createTypefaceFromFile(fontPath));
    }

    /**
     * <p>Replace system default font. <b>Note:</b>you should also add code below to your app theme in styles.xml. </p>
     * {@code <item name="android:typeface">monospace</item>}
     * <p>The best place to call this method is {@link Application#onCreate()}, it will affect
     * whole app font.If you call this method after view is visible, you need to invalid the view to make it effective.</p>
     */
    private void replaceSystemDefaultFont(@NonNull Typeface typeface) {
        modifyObjectField(null, "MONOSPACE", typeface);
    }

    private void modifyObjectField(Object obj, String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(obj, value);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(18)
    private class ScreenApplicationV18 extends ScreenApplicationV15 implements Application.OnProvideAssistDataListener {
        @Override
        public void onProvideAssistData(Activity activity, Bundle data) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.OnProvideAssistDataListener> dataListeners = (List<Application.OnProvideAssistDataListener>) mAssistCallbacks.get(entity.application);
                            if (dataListeners != null && dataListeners.size() > 0)
                                for (Application.OnProvideAssistDataListener listener : dataListeners)
                                    listener.onProvideAssistData(activity, data);
                        } catch (Exception e) {
                        }
                    }
                }
        }
    }

    private class ScreenApplicationV15 implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivityCreated(activity, savedInstanceState);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivityStarted(activity);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivityResumed(activity);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivityPaused(activity);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivityStopped(activity);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivitySaveInstanceState(activity, outState);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null) {
                        try {
                            List<Application.ActivityLifecycleCallbacks> callbacks = (List<Application.ActivityLifecycleCallbacks>) mActivityLifecycleCallbacks.get(entity.application);
                            if (callbacks != null && callbacks.size() > 0)
                                for (Application.ActivityLifecycleCallbacks callbacks1 : callbacks) {
                                    callbacks1.onActivityDestroyed(activity);
                                }
                        } catch (Exception e) {
                        }
                    }
                }
        }

        @Override
        public void onTrimMemory(int level) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null)
                        entity.application.onTrimMemory(level);
                }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null)
                        entity.application.onConfigurationChanged(newConfig);
                }
        }

        @Override
        public void onLowMemory() {
            List<ApkEntity> entities = apkEntities();
            if (entities != null && entities.size() > 0)
                for (ApkEntity entity : entities) {
                    if (entity.application != null)
                        entity.application.onLowMemory();
                }
        }
    }

    @Override
    public void onAttributeSetZoom(View view) {
        if (application instanceof AttributeSetZoom)
            ((AttributeSetZoom) application).onAttributeSetZoom(view);
    }
}
