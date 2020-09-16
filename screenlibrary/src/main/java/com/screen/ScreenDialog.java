package com.screen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import com.screen.iface.BackColor;
import com.screen.iface.FullScreen;
import com.screen.iface.InvisibleStatusBar;
import com.screen.iface.StatusBarColor;
import com.screen.iface.StatusBarTextColorBlack;
import com.screen.iface.StatusBarTextColorWhite;


public class ScreenDialog extends Dialog {
    private static final String TAG = "ScreenDialog";

    @Override
    public void show() {
        if (this instanceof BackColor) {
            BackColor backColor = (BackColor) this;
            getWindow().getDecorView().setBackgroundColor(backColor.backColor());
        }
        super.show();
        if (this instanceof StatusBarTextColorWhite)
            ScreenManager.statusBarTextColorWhite(getWindow());
        else if (this instanceof StatusBarTextColorBlack)
            ScreenManager.statusBarTextColorBlack(getWindow());
        if (this instanceof InvisibleStatusBar)
            ScreenManager.invisibleStatusBar(getWindow());
        if (this instanceof FullScreen) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (!(this instanceof StatusBarColor))
                ScreenManager.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        }
        if (this instanceof StatusBarColor) {
            StatusBarColor statusBarColor = (StatusBarColor) this;
            ScreenManager.setStatusBarColor(getWindow(), statusBarColor.statusBarColor());
        }
    }

    public ScreenDialog(Context context) {
        super(context);
    }

    public ScreenDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ScreenDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
