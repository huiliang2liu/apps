package com.base.widget;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.renderscript.RenderScript;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * com.view
 * 2018/10/19 18:26
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class FloatView {
    private final static String TAG = "FloatView";
    private ViewGroup viewGroup;
    private final static int TIME = 300;
    private Activity mActivity;
    AnimatorSet set;

    public FloatView(Activity activity) {
        viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);
        mActivity = activity;
        set = new AnimatorSet();
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void addView(View view) {
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void addView(View view, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                params);
        layoutParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void addViewBottom(View view, View referenceView) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                params);
        layoutParams.leftMargin = referenceView.getLeft();
        layoutParams.topMargin = referenceView.getBottom();
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void addViewTop(View view, View referenceView) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                params);
        layoutParams.leftMargin = referenceView.getLeft();
        layoutParams.topMargin = referenceView.getTop() - layoutParams.height;
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void addViewRigth(View view, View referenceView) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                params);
        layoutParams.leftMargin = referenceView.getRight();
        layoutParams.topMargin = referenceView.getTop();
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void addViewRigthBottom(View view, View referenceView) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                params);
        layoutParams.leftMargin = referenceView.getRight() - params.width;
        layoutParams.topMargin = referenceView.getBottom();
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }


    public void addViewLeft(View view, View referenceView) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                params);
        layoutParams.leftMargin = referenceView.getLeft() - layoutParams.width;
        layoutParams.topMargin = referenceView.getTop();
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
        addAnimation(view);
    }

    public void remove(View view) {
        try {
            viewGroup.removeView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        viewGroup.removeAllViews();
    }

    private void addAnimation(View view) {
//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        if (params == null)
//            return;
//        int width = params.width;
//        ViewEmbellish embellish = new ViewEmbellish(view);
//        ObjectAnimator animatorW = AnimatorUtil.width(embellish, 0, width,
//                TIME);
//        ObjectAnimator animatorH = AnimatorUtil.height(embellish, 0, width,
//                TIME);
//        ObjectAnimator animatorA = AnimatorUtil
//                .alpha(embellish, 0, 1, TIME);
//        set.play(animatorH).with(animatorW).with(animatorA);
//        set.setDuration(TIME);
//        set.start();
    }

}
