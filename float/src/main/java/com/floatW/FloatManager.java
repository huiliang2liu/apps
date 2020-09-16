package com.floatW;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public class FloatManager {
    private static final String TAG = "FloatManager";

    static {
    }

    private Context context;
    private WindowManager windowManager;

    public FloatManager(Context context) {
        this.context = context.getApplicationContext();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public Toast createToast(String text, int duration) {
        return Toast.makeText(context, text, duration);
    }

    public Toast createToast(int text, int duration) {
        return Toast.makeText(context, text, duration);
    }

    public void shortToast(String text) {
        createToast(text, Toast.LENGTH_SHORT).show();
    }

    public void shortToast(int text) {
        createToast(text, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String text) {
        createToast(text, Toast.LENGTH_LONG).show();
    }

    public void longToast(int text) {
        createToast(text, Toast.LENGTH_SHORT).show();
    }

    public Toast createToast(View text, int duration) {
        Toast toast = new Toast(context);
        toast.setView(text);
        toast.setDuration(duration);
        return toast;
    }

    public Toast setLocation(Toast toast, int gravity, int x, int y) {
        toast.setGravity(gravity, x, y);
        return toast;
    }

    public AppFloat appFloat(View view) {
        AppFloat appFloat = new AppFloat(context, windowManager);
        appFloat.addView(view);
        return appFloat;
    }

    public SystemFloat systemFloat(View view) {
        SystemFloat appFloat = new SystemFloat(context, windowManager);
        appFloat.addView(view);
        return appFloat;
    }

    public boolean checkFloat() {
        if (Build.VERSION.SDK_INT > 22) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public Intent requestFloatIntent() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

}
