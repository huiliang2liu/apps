package com.live.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.base.widget.PlayGestureView;
import com.media.MediaListener;
import com.media.widget.VideoView;

public class PlayView extends FrameLayout implements PlayGestureView.PlayGestureListener {
    private boolean fullScreen = false;
    private int height;
    private VideoView videoView;
    private PlayGestureView gestureView;
    private ScreenListener screenListener;
    private PlayGestureView.PlayGestureListener playGestureListener;

    public PlayView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public PlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        videoView = new VideoView(getContext());
        addView(videoView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        gestureView = new PlayGestureView(getContext());
        gestureView.setPlayGestureListener(this);
        addView(gestureView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setPlayGestureListener(PlayGestureView.PlayGestureListener playGestureListener) {
        this.playGestureListener = playGestureListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setScreenListener(ScreenListener screenListener) {
        this.screenListener = screenListener;
    }

    public void setMediaListener(MediaListener listener){
        videoView.setMediaListener(listener);
    }

    public void exitFullScreen() {
        if (!fullScreen)
            return;
        fullScreen = false;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }

        Context context = getContext();
        if (context instanceof Activity) {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (screenListener != null)
            screenListener.fullScreen(false);
    }

    public void fullScreen() {
        if (fullScreen)
            return;
        fullScreen = true;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            height = layoutParams.height;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(layoutParams);
        }
        Context context = getContext();
        if (context instanceof Activity) {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (screenListener != null)
            screenListener.fullScreen(true);
    }

    public synchronized void setPath(String path) {
        videoView.setPath(path);
    }

    public synchronized void play(String path) {
        videoView.play(path);
    }

    public synchronized void pause() {
        videoView.pause();
    }

    public synchronized void resume() {
        videoView.resume();

    }

    public synchronized void destroy() {
        videoView.destroy();
    }

    public synchronized void play() {
        videoView.play();
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public void onHorizontalDistance(float downX, float nowX) {
        if (fullScreen && playGestureListener != null)
            playGestureListener.onHorizontalDistance(downX, nowX);
    }

    @Override
    public void onLeftVerticalDistance(float downY, float nowY) {
        if (fullScreen && playGestureListener != null)
            playGestureListener.onLeftVerticalDistance(downY, nowY);
    }

    @Override
    public void onRightVerticalDistance(float downY, float nowY) {
        if (fullScreen && playGestureListener != null)
            playGestureListener.onRightVerticalDistance(downY, nowY);
    }

    @Override
    public void onGestureEnd() {
        if (fullScreen && playGestureListener != null)
            playGestureListener.onGestureEnd();
    }

    @Override
    public void onSingleTap(MotionEvent e) {
        if (fullScreen) {
            if (playGestureListener != null)
                playGestureListener.onSingleTap(e);
        } else
            fullScreen();
    }

    @Override
    public void onDoubleTap(MotionEvent e) {
        if (fullScreen && playGestureListener != null)
            playGestureListener.onDoubleTap(e);
    }

    public interface ScreenListener {
        void fullScreen(boolean full);
    }
}
