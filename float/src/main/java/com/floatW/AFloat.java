package com.floatW;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

abstract class AFloat extends FrameLayout implements IFloat {


    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    WindowManager windowManager;
    LayoutParams viewLayoutParams;

    public AFloat(Context context, WindowManager windowManager) {
        super(context);
        layoutParams.type =type();
        layoutParams.packageName=context.getPackageName();
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.setTitle("Toast");
        layoutParams.dimAmount = 1f;
//        layoutParams.token=
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        setBackgroundColor(Color.argb(0x00, 0x00, 0x00, 0x00));
        layoutParams.format = PixelFormat.TRANSPARENT;
        viewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        viewLayoutParams.gravity = Gravity.CENTER;
        this.windowManager = windowManager;
    }
    protected int type(){
        if("Xiaomi".equals(Build.MANUFACTURER) || Build.VERSION.SDK_INT < 21)
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                    | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        return WindowManager.LayoutParams.TYPE_TOAST;
    }

    public void setGravity(int gravity) {
        layoutParams.gravity = gravity;
        windowManager.updateViewLayout(this, layoutParams);
    }

    @Override
    public final void show() {
        windowManager.addView(this, layoutParams);
    }

    public void setX(int x) {
        layoutParams.x = x;
        windowManager.updateViewLayout(this, layoutParams);
    }

    public void setY(int y) {
        layoutParams.y = y;
        windowManager.updateViewLayout(this, layoutParams);
    }

    public void setAlpha(float alpha) {
        layoutParams.alpha = alpha;
        windowManager.updateViewLayout(this, layoutParams);
    }

    public void setWidth(int width) {
        layoutParams.width = width;
        windowManager.updateViewLayout(this, layoutParams);
    }

    public void setHeight(int height) {
        layoutParams.height = height;
        windowManager.updateViewLayout(this, layoutParams);
    }

    @Override
    public void dismiss() {
        windowManager.removeView(this);
    }
}
