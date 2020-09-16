package com.screen.entities;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.screen.PackageParser;
import com.screen.ScreenManager;
import com.screen.pm.ContextImp;
import com.screen.pm.ScreenPackageManage;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:18
 * Descripotion：apk实体，用于替换不同包的资源
 */
public class ApkEntity {
    private static final String TAG = "ApkEntity";
    private static Class packageParse;
    private static Method addAssetPath;
    private static Method setConfiguration;
    public Resources.Theme mTheme;
    public Resources mResources;
    public String mPackageName;
    public AssetManager mAssetManager;
    public PackageInfo mPackageInfo;
    public Application application;
    public int cookie;
    public PackageParser packageParser;
    public PackageParser.Package mPackage;
    public ScreenPackageManage packageManage;
    public List<ContentProvider> contentProviders = new ArrayList<>();

    static {
        try {
            addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            if (!addAssetPath.isAccessible())
                addAssetPath.setAccessible(true);
        } catch (Exception e) {
            Log.i(TAG, "not fount method addAssetPath");
        }
        try {
            setConfiguration = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            if (!setConfiguration.isAccessible())
                setConfiguration.setAccessible(true);
        } catch (Exception e) {
            Log.i(TAG, "not fount method setConfiguration");
        }
        try {
            packageParse = Class.forName("android.content.pm.PackageParser");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApkEntity() {
    }

    public static ApkEntity apkPath2apkEntity(String apkPath, Context context) {
        Log.e(TAG, "apkPath2apkEntity");
        ApkEntity apkEntity = new ApkEntity();
        try {
            apkEntity.mPackageInfo = context
                    .getPackageManager()
                    .getPackageArchiveInfo(
                            apkPath,
                            PackageManager.GET_ACTIVITIES
                                    | PackageManager.GET_SERVICES
                                    | PackageManager.GET_META_DATA
                                    | PackageManager.GET_PERMISSIONS
                                    | PackageManager.GET_SIGNATURES
                                    | PackageManager.GET_PROVIDERS);
            apkEntity.mPackageName = apkEntity.mPackageInfo.packageName;
            apkEntity.mAssetManager = AssetManager.class.newInstance();
            apkEntity.cookie = (int) addAssetPath.invoke(apkEntity.mAssetManager,
                    apkPath);
//            addAssetPath.invoke(context.getResources().getAssets(), apkPath);
            Resources superRes = context.getResources();
            apkEntity.mResources = new Resources(apkEntity.mAssetManager,
                    superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
//            apkEntity.mResources = apkEntity.packageContext.getResources();
            apkEntity.mTheme = apkEntity.mResources.newTheme();
            // Finals适配三星以及部分加载XML出现异常BUG
            apkEntity.mTheme.applyStyle(apkEntity.mPackageInfo.applicationInfo.theme, true);
            apkEntity.packageParser = new PackageParser(apkPath);
            apkEntity.mPackage = apkEntity.packageParser.parsePackage(new File(apkPath), apkPath, apkEntity.mAssetManager, apkEntity.mResources, apkEntity.cookie);
//           Context c=context.createApplicationContext()
            apkEntity.packageManage = new ScreenPackageManage(apkEntity, context.getPackageManager());
            Log.d(TAG, "启动广播");
            List<PackageParser.Activity> receivers = apkEntity.mPackage.receivers;
            if (receivers != null && receivers.size() > 0) {
                for (PackageParser.Activity re : receivers) {
                    try {
                        BroadcastReceiver receiver = (BroadcastReceiver) Class.forName(re.className).newInstance();
                        context.registerReceiver(receiver, re.getIntentInfo());
                    } catch (Exception e) {
                        Log.e(TAG, String.format("启动广播%s失败", re.className));
                        e.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "创建Application");
            apkEntity.application = ScreenManager.application(apkEntity.mPackageInfo.applicationInfo.className, context);
            Log.d(TAG, "创建Context");
            Context context1 = new ContextImp(apkEntity.application, apkEntity);
            Log.d(TAG, "启动Provider");
            List<PackageParser.Provider> providers = apkEntity.mPackage.providers;
            if (providers != null && providers.size() > 0) {
                List<ProviderInfo> providerInfos = new ArrayList<>(providers.size());
                for (PackageParser.Provider provider : providers) {
                    try {
                        Class.forName(provider.className);
                        providerInfos.add(provider.info);
                    } catch (Exception e) {
                        Log.d(TAG, String.format("没有provider：%s", provider.className));
                    }
                }
                try {
                    ScreenManager.rigitsProvider(context1, providerInfos);
                } catch (Exception e) {
                    Log.e(TAG, String.format("%s,启动provider失败", apkEntity.mPackageName));
                    e.printStackTrace();
                }
            }
            return apkEntity;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "", e);
            return null;
        }
    }
}
