package com.result.activity;

import android.content.Intent;

/**
 * com.forScreen.util.result
 * 2018/9/27 17:05
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public interface IResult {
    /**
     * 2019/1/8 10:48
     * annotation：有返回结果的启动activity
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void startActivityForResult(int requsetCode, Intent intent, ResultCallback... callbacks);
    /**
     * 2019/1/8 10:48
     * annotation：启动activity
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void startActivity(Intent intent);
}
