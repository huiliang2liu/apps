package com.momo;

import android.widget.TextView;

import com.base.BaseApplication;
import com.screen.iface.TextStyle;

public class Application extends BaseApplication implements TextStyle {
    @Override
    public void onCreate() {
        super.onCreate();
//        ScreenManager.getInstance(this).replaceSystemDefaultFontFromAsset("font/ali
    }

    @Override
    public void setTextStyle(TextView textView, String textStyle) {
    }
}
