package com.base.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayInfo {
    DisplayInfo(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;//宽度像素
        int height = dm.heightPixels;//高度像素
        float xdpi = dm.xdpi;//宽度像素密度
        float ydpi = dm.ydpi;//高度像素密度
        float widthInch = 1.0f * width / xdpi;//屏幕宽度
        float heightInch = 1.0f * height / ydpi;//屏幕高度
        double inch = Math.sqrt(Math.pow(widthInch, 2) + Math.pow(heightInch, 2));//屏幕尺寸
        int densityDpi = dm.densityDpi;
        float density = dm.density;
        float widthDp = width * 160.0f / densityDpi;
        float heightDDp = height * 160.0f / densityDpi;
    }

    private String densityDpiToString(int densityDpi) {
        String str;
        switch (densityDpi) {
            case 120:
                str = "ldpi";
                break;
            case 160:
                str = "mdpi";
                break;
            case 213:
                str = "tvdpi";
                break;
            case 240:
                str = "hdpi";
                break;
            case 320:
                str = "xhdpi";
                break;
            case 480:
                str = "xxhdpi";
                break;
            case 640:
                str = "xxxhdpi";
                break;
            default:
                str = "N/A";
                break;
        }
        return str;
    }
}
