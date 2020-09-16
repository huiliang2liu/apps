package com.result.activity;

import android.content.Intent;

/**
 * com.forScreen.util.result
 * 2018/9/27 17:09
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public interface ResultCallback {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
