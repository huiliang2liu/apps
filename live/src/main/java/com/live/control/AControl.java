package com.live.control;

import android.os.Message;
import android.view.MotionEvent;

import com.base.thread.Handler;

public abstract class AControl implements IControl {
    private static final int HIDE_TIME = 10000;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    public void dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            handler.removeMessages(0);
        else if (ev.getAction() == MotionEvent.ACTION_UP) {
            handler.sendEmptyMessageDelayed(0, HIDE_TIME);
        }
    }

    @Override
    public void dismiss() {
        handler.removeMessages(0);
    }


    @Override
    public void show() {
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, HIDE_TIME);
    }
}
