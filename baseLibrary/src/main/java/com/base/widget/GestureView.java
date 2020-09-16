package com.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GestureView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;

    public GestureView(Context context) {
        super(context);
        init(null);
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mGestureDetector = new GestureDetector(getContext(), this);
        mGestureDetector.setOnDoubleTapListener(this);
    }

    public void setGestureListener(GestureListener gestureListener) {
        mGestureListener = gestureListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mGestureListener != null)
                    mGestureListener.onGestureEnd(event);
                break;

            default:
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mGestureListener != null)
            mGestureListener.onGestureSingleTap(e);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mGestureListener != null)
            mGestureListener.onGestureDoubleTap(e);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mGestureListener != null)
            mGestureListener.onGestureDown(e);
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
        if (mGestureListener != null)
            mGestureListener.onGestureMove(e2);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface GestureListener {
        void onGestureEnd(MotionEvent event);

        void onGestureDown(MotionEvent event);

        void onGestureMove(MotionEvent event);

        void onGestureSingleTap(MotionEvent e);

        void onGestureDoubleTap(MotionEvent e);
    }

}
