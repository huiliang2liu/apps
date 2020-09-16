package com.base.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.base.R;

public class ShufflingView extends FrameLayout {
    private ViewPager viewPager;
    private LayoutParams layoutParams;
    private float scale = 1;
    private float alpha = 1;

    public ShufflingView(Context context) {
        super(context);
        init(null, 0);
    }

    public ShufflingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ShufflingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public ShufflingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setClipChildren(false);
        viewPager = new ViewPage1(getContext());
        viewPager.setClipChildren(false);
        if (attrs != null) {
            TypedArray a = getResources().obtainAttributes(attrs, R.styleable.ShufflingView);
            layoutParams.leftMargin = a.getDimensionPixelSize(R.styleable.ShufflingView_shufflingIntervalLift, 20);
            layoutParams.rightMargin = a.getDimensionPixelSize(R.styleable.ShufflingView_shufflingIntervalRight, 20);
            viewPager.setPageMargin(a.getDimensionPixelSize(R.styleable.ShufflingView_shufflingPageMargin, 10));
            scale = a.getFloat(R.styleable.ShufflingView_shufflingScale, 1);
            alpha = a.getFloat(R.styleable.ShufflingView_shufflingAlpha, 1);
            a.recycle();
        } else {
            viewPager.setPageMargin(10);
            scale = 1;
            alpha = 1;
        }
        addView(viewPager, layoutParams);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public class ViewPage1 extends ViewPager {

        {

            this.setPageTransformer(false, new DepthPageTransformer());
//        this.setOffscreenPageLimit(2);  // 避免卡顿
            this.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
                @Override
                public void onChildViewAdded(View parent, View child) {
                    child.setScaleY(scale);
                    child.setAlpha(alpha);
                }

                @Override
                public void onChildViewRemoved(View parent, View child) {

                }
            });
//            this.setPageMargin(20);
        }

        public ViewPage1(Context context) {
            super(context);
        }

        public ViewPage1(Context context, AttributeSet attrs) {
            super(context, attrs);

        }

        public class DepthPageTransformer implements androidx.viewpager.widget.ViewPager.PageTransformer {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @SuppressLint("NewApi")
            public void transformPage(View view, float position) {

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setScaleY(scale);
                    view.setAlpha(alpha);
                } else if (position <= 0) { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    float scaleFactor = scale + (1 - scale) * (1 - Math.abs(position));
                    float alphaFactor = alpha + (1 - alpha) * (1 - Math.abs(position));
                    view.setScaleY(scaleFactor);
                    view.setAlpha(alphaFactor);
                } else if (position <= 1) { // (0,1]
                    // Fade the page out.
                    // Counteract the default slide transition

                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = scale + (1 - scale) * (1 - Math.abs(position));
                    float alphaFactor = alpha + (1 - alpha) * (1 - Math.abs(position));
                    view.setScaleY(scaleFactor);
                    view.setAlpha(alphaFactor);
                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setScaleY(scale);
                    view.setAlpha(alpha);
                }
            }
        }
    }
}
