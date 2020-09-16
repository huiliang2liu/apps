package com.floatW;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class AppFloat extends AFloat implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = "AppFloat";
    private Animator showAnimator;
    private Animator hideAnimator;
    private GestureDetector mGestureDetector;
    private FloatListener mFloatListener;
    private float startX;
    private float startY;

    public AppFloat(Context context, WindowManager windowManager) {
        super(context, windowManager);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        setBackgroundColor(Color.argb(0, 0, 0, 0));
        mGestureDetector = new GestureDetector(this.getContext(), this);
        mGestureDetector.setOnDoubleTapListener(this);
        setFloatListener(new FloatListener() {
            @Override
            public void onDown(MotionEvent event) {
                startX = event.getRawX();
                startY = event.getRawY();
            }

            @Override
            public void onMove(MotionEvent event) {
                float x = event.getRawX() - startX;
                float y = event.getRawY() - startY;
                layoutParams.x += x;
                layoutParams.y += y;
                update();
                startX = event.getRawX();
                startY = event.getRawY();
            }

            @Override
            public void onUp(MotionEvent event) {
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (endX < getScreenWidth() / 2) {
                    endX = 0;
                } else {
                    endX = getScreenWidth() - getWidth();
                }
                layoutParams.x = (int) endX;
                update();
            }

            @Override
            public void onSingleTap() {

            }

            @Override
            public void onDoubleTap() {

            }
        });
        show();
    }

    public void setFloatListener(FloatListener floatListener) {
        this.mFloatListener = floatListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mFloatListener != null)
                    mFloatListener.onUp(event);
                Log.e(TAG, "事件结束");
                break;

            default:
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void setHideAnimator(Animator animator) {
        hideAnimator = animator.clone();
        hideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AppFloat.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void setShowAnimator(Animator animator) {
        showAnimator = animator.clone();
    }

    public void addView(View view) {
        addView(view, viewLayoutParams);
        if (showAnimator != null)
            showAnimator.start();
    }

    @Override
    public void dismiss() {
        if (hideAnimator != null)
            hideAnimator.start();
        else
            super.dismiss();
    }

    public void scrollX(int dx) {
        setX(layoutParams.x + dx);
    }

    public void scrollToX(int x) {
        setX(x);
    }

    public void scrollY(int dy) {
        setY(layoutParams.y + dy);
    }

    public void scrollToY(int y) {
        setY(y);
    }

    public void update() {
        windowManager.updateViewLayout(this, layoutParams);
    }

    //获取屏幕宽度
    private int getScreenWidth() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    //获取屏幕高度
    private int getScreenHeight() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.e(TAG, "单击事件");
        if (mFloatListener != null)
            mFloatListener.onSingleTap();
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.e(TAG, "双击事件");
        if (mFloatListener != null)
            mFloatListener.onDoubleTap();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        if (mFloatListener != null)
            mFloatListener.onDown(event);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mFloatListener != null)
            mFloatListener.onMove(e2);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface FloatListener {
        void onDown(MotionEvent event);

        void onMove(MotionEvent event);

        void onUp(MotionEvent event);

        void onSingleTap();

        void onDoubleTap();
    }
}
