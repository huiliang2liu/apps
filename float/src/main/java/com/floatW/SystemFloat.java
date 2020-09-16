package com.floatW;

import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

public class SystemFloat extends AppFloat {
    public SystemFloat(Context context, WindowManager windowManager) {
        super(context, windowManager);
    }

    @Override
    protected int type() {
//        return WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY | WindowManager.LayoutParams.TYPE_SYSTEM_ERROR | WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
//        return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }
}
