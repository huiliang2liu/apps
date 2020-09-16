package com.live.control;

import android.view.MotionEvent;

import com.live.activity.PlayActivity;

public class LiveControl extends AControl {
    private static final String TAG = "LiveControl";
    private PlayActivity activity;
    private LiveRightControl rightControl;
    private LiveLeftControl liveLeftControl;
    private int halfWidth;
    private ILiveControl control;


    public LiveControl(PlayActivity activity) {
        this.activity = activity;
        rightControl = new LiveRightControl(activity);
        liveLeftControl = new LiveLeftControl(activity);
        halfWidth = activity.getResources().getDisplayMetrics().heightPixels >> 1;
    }


    @Override
    public void onHorizontalDistance(float downX, float nowX) {

    }

    @Override
    public void onLeftVerticalDistance(float downY, float nowY) {

    }

    @Override
    public void onRightVerticalDistance(float downY, float nowY) {
    }

    @Override
    public void onGestureEnd() {

    }

    @Override
    public void onSingleTap(MotionEvent e) {
        if (control != null && control.isShow())
            return;
        if (e.getRawX() > halfWidth) {
            if (activity.liveEntity() == null)
                return;
            control = rightControl;
        } else {
            if (activity.typeEntities() == null)
                return;
            control = liveLeftControl;
        }

        if (control != null) {
            show();
        }
    }

    @Override
    public void onDoubleTap(MotionEvent e) {

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (control != null)
            control.dismiss();
    }

    @Override
    public boolean isShow() {
        if (control != null)
            return control.isShow();
        return false;
    }

    @Override
    public void show() {
        super.show();
        control.show();
    }
}
