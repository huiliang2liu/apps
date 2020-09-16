package com.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;

public abstract class BaseDialog extends Dialog {
    private Animation enter;
    private Animation exit;
    private View view;
    private boolean dismiss = false;

    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(touchClose());
        setCancelable(backClose());
        enter = enter();
        if (enter != null)
            enter.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    showed();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        exit = exit();
        if (exit != null)
            exit.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    BaseDialog.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
    }

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = layoutId();
        if (layout > 0)
            view = LayoutInflater.from(getContext()).inflate(layout, null);
        else
            view = layoutView();
        if (view == null)
            throw new RuntimeException("you view is null");
        setContentView(view);
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        int width = width();
//        if (width == WindowManager.LayoutParams.WRAP_CONTENT || width == WindowManager.LayoutParams.MATCH_PARENT || width == WindowManager.LayoutParams.FILL_PARENT)
        lp.width = width;
        int height = height();
//        if (height == WindowManager.LayoutParams.WRAP_CONTENT || height == WindowManager.LayoutParams.MATCH_PARENT || height == WindowManager.LayoutParams.FILL_PARENT)
        lp.height = height;
        lp.gravity = gravity();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(lp);
        bindView();
    }

    protected abstract void bindView();

    public int layoutId() {
        return 0;
    }

    public Animation enter() {
        return null;
    }

    public Animation exit() {
        return null;
    }

    public int width() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public int height() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public View layoutView() {
        return null;
    }

    public boolean backClose() {
        return true;
    }

    public boolean touchClose() {
        return true;
    }

    /**
     * 昏暗程度
     *
     * @return
     */
    public float dimAmount() {
        return .5f;
    }

    public float alpha() {
        return 1;
    }

    public void showed() {

    }

    @Override
    public void show() {
        if (isShowing())
            return;
        super.show();
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        float dimAmount = dimAmount();
        if (dimAmount < 0 || dimAmount > 1)
            dimAmount = .5f;
        lp.dimAmount = dimAmount;
        float alpha = alpha();
        if (alpha < 0 || alpha > 1)
            alpha = 1;
        lp.alpha = alpha;
        mWindow.setAttributes(lp);
        if (enter != null)
            view.startAnimation(enter);
        else
            showed();
    }

    public int gravity() {
        return Gravity.CENTER;
    }

    @Override
    public void dismiss() {
        if (dismiss)
            return;
        dismiss = true;
        Animation animation = view.getAnimation();
        if (animation != null && !animation.hasEnded())
            animation.cancel();
        if (exit != null) {
            view.startAnimation(exit);
        } else
            super.dismiss();
    }
}
