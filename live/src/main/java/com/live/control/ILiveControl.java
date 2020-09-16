package com.live.control;

import android.view.MotionEvent;

public interface ILiveControl {
    void dismiss();

    boolean isShow();

    void dispatchTouchEvent(MotionEvent ev);

    void show();
}
