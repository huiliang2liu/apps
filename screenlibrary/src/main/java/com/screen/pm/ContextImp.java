package com.screen.pm;

import android.app.Activity;
import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.IInterface;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.screen.AppLayoutInflater;
import com.screen.PackageParser;
import com.screen.entities.ApkEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:33
 * Descripotion：自定义base上下文，用于加载不同的包属性
 */
public class ContextImp extends ContextWrapper {
    private static final String TAG = "ContextImp";
    private static final Class<ContextWrapper> CONTEXT_WRAPPER_CLASS = ContextWrapper.class;
    private static final Class<ContextThemeWrapper> CONTEXT_THEME_WRAPPER_CLASS = ContextThemeWrapper.class;
    private static Field mBaseField;
    private static Field mTheme;
    private static Field mInflater;
    private static Field mResourcesField;
    private static Field mApplicationActivity;
    private static Field mApplicationService;
    private static Method acquireExistingProvider;

    static {
        try {
            mBaseField = CONTEXT_WRAPPER_CLASS.getDeclaredField("mBase");
            mBaseField.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mBase错误");
        }
        try {
            mTheme = CONTEXT_THEME_WRAPPER_CLASS.getDeclaredField("mTheme");
            mTheme.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mTheme错误");
        }
        try {
            mInflater = CONTEXT_THEME_WRAPPER_CLASS.getDeclaredField("mInflater");
            mInflater.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mInflater错误");
        }
        try {
            mResourcesField = CONTEXT_THEME_WRAPPER_CLASS.getDeclaredField("mResources");
            mResourcesField.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mResources错误");
        }
        try {
            mApplicationActivity = Activity.class.getDeclaredField("mApplication");
            mApplicationActivity.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mApplicationActivity错误");
        }
        try {
            mApplicationService = Service.class.getDeclaredField("mApplication");
            mApplicationService.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取字段mApplicationService错误");
        }

        try {
            acquireExistingProvider = ContentResolver.class.getDeclaredMethod("acquireExistingProvider", Context.class, String.class);
            acquireExistingProvider.setAccessible(true);
        } catch (Exception e) {
            Log.e(TAG, "获取方法acquireExistingProvider错误");
        }

    }


    private ApkEntity apkEntity;
    private Context mContext;

    public ContextImp(ContextWrapper context, ApkEntity resources) {
        super(context.getBaseContext());
        mContext = context;
        apkEntity = resources;
        try {
            mBaseField.set(context, this);
        } catch (Exception e) {
        }
        if (resources != null) {
            if (context instanceof Activity)
                try {
                    mApplicationActivity.set(context, resources.application);
                } catch (Exception e) {
                }
            else if (context instanceof Service)
                try {
                    mApplicationService.set(context, resources.application);
                } catch (Exception e) {
                }
            try {
                mTheme.set(context, resources.mTheme);
                mTheme.set(context.getBaseContext(), resources.mTheme);
            } catch (Exception e) {
            }
            try {
                mResourcesField.set(context, resources.mResources);
            } catch (Exception e) {
            }
//            try {
//                mInflater.set(context, AppLayoutInflater.from(context));
//            } catch (Exception e) {
//            }
//            mInflater.set(context,);
//            FieldManager.setField(context, mTheme, resources.mTheme);
//            FieldManager.setField(context, mResourcesField, resources.mResources);
//            FieldManager.setField(context, mInflater, AppLayoutInflater.from(context));
        }
    }

    @Override
    public Resources getResources() {
        if (apkEntity != null)
            return apkEntity.mResources;
        return super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        if (apkEntity != null)
            return apkEntity.mAssetManager;
        return super.getAssets();
    }

    @Override
    public Context getApplicationContext() {
        if (apkEntity != null)
            return apkEntity.application;
        return super.getApplicationContext();
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equals(Context.LAYOUT_INFLATER_SERVICE)) {
            if (mContext instanceof Activity)
                return mContext.getSystemService(name);
            return AppLayoutInflater.form(this);
        }
        return super.getSystemService(name);
    }

    @Override
    public PackageManager getPackageManager() {
        if (apkEntity != null)
            return apkEntity.packageManage;
        return super.getPackageManager();
    }

    @Override
    public Resources.Theme getTheme() {
        if (apkEntity != null)
            return apkEntity.mTheme;
        return super.getTheme();
    }

    @Override
    public String getPackageName() {
        if (apkEntity != null)
            return apkEntity.mPackageName;
        return super.getPackageName();
    }

    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    @Override
    public ContentResolver getContentResolver() {
        return super.getContentResolver();
    }

}
