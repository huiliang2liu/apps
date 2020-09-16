package com.result.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.result.ResultImpl;


/**
 * com.forScreen.util.result
 * 2018/9/27 16:52
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class ActivityResult implements IResult {
    private final static String TAG = ActivityResult.class.getName();
    private IResult result;

    public ActivityResult(Activity activity) {
        result = new ResultImpl(activity);
    }

    public ActivityResult(Fragment fragment) {
        result = new ResultImpl(fragment);
    }

    public ActivityResult(androidx.fragment.app.Fragment fragment) {
        result = new ResultImpl(fragment);
    }

    @Override
    public void startActivityForResult(int requsetCode, Intent intent, ResultCallback... callbacks) {
        replace(intent);
        if (result != null)
            result.startActivityForResult(requsetCode, intent, callbacks);
    }

    @Override
    public void startActivity(Intent intent) {
        replace(intent);
        result.startActivity(intent);
    }

    private void replace(Intent intent) {
        if (intent == null)
            return;

    }
}
