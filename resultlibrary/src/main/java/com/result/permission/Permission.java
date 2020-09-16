package com.result.permission;

import android.app.Activity;
import android.app.Fragment;

import com.result.ResultImpl;


/**
 * com.forScreen.util.result
 * 2018/9/27 16:52
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class Permission implements IResult {
    private final static String TAG = Permission.class.getSimpleName();
    private IResult result;

    public Permission(Activity activity) {
        result = new ResultImpl(activity);
    }

    public Permission(Fragment fragment) {
        result = new ResultImpl(fragment);
    }

    public Permission(androidx.fragment.app.Fragment fragment) {
        result = new ResultImpl(fragment);
    }

    @Override
    public String[] check(String... permissions) {
        return result.check(permissions);
    }

    @Override
    public void requestPermissions(int requsetCode, PermissionCallback callback, String... permissions) {
        result.requestPermissions(requsetCode, callback, permissions);
    }


    @Override
    public boolean checkWriteSettings() {
        return result.checkWriteSettings();
    }

    @Override
    public void requestWriteSettings(PermissionCallback callback) {
        result.requestWriteSettings(callback);
    }

    @Override
    public boolean checkSystemAlertWindow() {
        return result.checkSystemAlertWindow();
    }

    @Override
    public void requestSystemAlertWindow(PermissionCallback callback) {
        result.requestSystemAlertWindow(callback);
    }
}
