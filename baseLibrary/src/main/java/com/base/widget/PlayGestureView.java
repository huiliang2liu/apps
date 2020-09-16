package com.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

public class PlayGestureView extends GestureView implements GestureView.GestureListener {
    private static final int HORIZENAL = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private int gesture = 0;
    private float downX;
    private float downY;
    private int width;
    private int height;
    private PlayGestureListener playGestureListener;

    public PlayGestureView(Context context) {
        super(context);
        init(null,0);
    }

    public PlayGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public PlayGestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    public PlayGestureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,defStyleAttr);
    }

    private void init(AttributeSet attrs,int defStyleAttr) {
        setGestureListener(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    public void setPlayGestureListener(PlayGestureListener playGestureListener) {
        this.playGestureListener = playGestureListener;
    }

    @Override
    public void onGestureEnd(MotionEvent event) {
        gesture = 0;
        downX = 0;
        downY = 0;
        if (playGestureListener != null)
            playGestureListener.onGestureEnd();
    }

    @Override
    public void onGestureDown(MotionEvent event) {
        downX = event.getRawX();
        downY = event.getRawY();
    }

    @Override
    public void onGestureMove(MotionEvent event) {
        if (gesture == 0) {
            if (Math.abs(downX - event.getRawX()) > Math.abs(downY - event.getRawY()))
                gesture = 1;
            else if (downX < width / 2)
                gesture = 2;
            else
                gesture = 3;
        }
        if (gesture == 1) {//水平滑动
            if (playGestureListener != null)
                playGestureListener.onHorizontalDistance(downX, event.getRawX());
        } else if (gesture == 2) {//左边竖直滑动
            if (playGestureListener != null)
                playGestureListener.onLeftVerticalDistance(downY, event.getRawY());
        } else {//右边竖直滑动
            if (playGestureListener != null)
                playGestureListener.onRightVerticalDistance(downY, event.getRawY());
        }
    }

    @Override
    public void onGestureSingleTap(MotionEvent e) {
        if (playGestureListener != null)
            playGestureListener.onSingleTap(e);
    }

    @Override
    public void onGestureDoubleTap(MotionEvent e) {
        if (playGestureListener != null)
            playGestureListener.onDoubleTap(e);
    }

    public interface PlayGestureListener {
        /**
         * 水平滑动距离
         *
         * @param downX 按下位置
         * @param nowX  当前位置
         */
        void onHorizontalDistance(float downX, float nowX);

        /**
         * 左边垂直滑动距离
         *
         * @param downY 按下位置
         * @param nowY  当前位置
         */
        void onLeftVerticalDistance(float downY, float nowY);

        /**
         * 右边垂直滑动距离
         *
         * @param downY 按下位置
         * @param nowY  当前位置
         */
        void onRightVerticalDistance(float downY, float nowY);

        /**
         * 手势结束
         */
        void onGestureEnd();

        /**
         * 单击事件
         */
        void onSingleTap(MotionEvent e);

        /**
         * 双击事件
         */
        void onDoubleTap(MotionEvent e);
    }
}
