package com.live.control;

import android.view.MotionEvent;

import com.base.widget.PlayGestureView;

public interface IControl extends PlayGestureView.PlayGestureListener {
    void dismiss();

    boolean isShow();

    void dispatchTouchEvent(MotionEvent ev);

    void show();
}
