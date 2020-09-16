package com.plug.entities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Method;

public class ApkEntity {
    private static final String TAG = "ApkEntity";
    private static Method addAssetPath;
    public Resources.Theme mTheme;
    public Resources mResources;
    public String mPackageName;
    public AssetManager mAssetManager;
    public PackageInfo mPackageInfo;

    static {
        try {
            addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            if (!addAssetPath.isAccessible())
                addAssetPath.setAccessible(true);
        } catch (Exception e) {
            Log.i(TAG, "not fount method addAssetPath");
        }
    }

    private ApkEntity() {
    }

    public static ApkEntity apkPath2apkEntity(String apkPath, Context context) {
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
                                    | PackageManager.GET_SIGNATURES);
            apkEntity.mPackageName = apkEntity.mPackageInfo.packageName;
            apkEntity.mAssetManager = AssetManager.class.newInstance();
            addAssetPath.invoke(apkEntity.mAssetManager,
                    apkPath);
            Resources superRes = context.getResources();
            apkEntity.mResources = new Resources(apkEntity.mAssetManager,
                    superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
            apkEntity.mTheme = apkEntity.mResources.newTheme();
            // Finals适配三星以及部分加载XML出现异常BUG
            apkEntity.mTheme.applyStyle(apkEntity.mPackageInfo.applicationInfo.theme, true);
            return apkEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
