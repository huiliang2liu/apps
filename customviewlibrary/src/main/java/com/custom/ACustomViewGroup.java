package com.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class ACustomViewGroup extends ViewGroup {
    protected List<ViewTag> viewTags = new ArrayList<>();

    public ACustomViewGroup(Context context) {
        super(context);
    }

    public ACustomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ACustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ACustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measure();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected final int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(), minWidth());
    }

    @Override
    protected final int getSuggestedMinimumHeight() {
        return Math.max(super.getSuggestedMinimumHeight(), minHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (ViewTag viewTag : viewTags) {
            viewTag.child.layout(viewTag.left, viewTag.top, viewTag.right, viewTag.bottom);
        }
    }

    protected abstract void measure();

    protected abstract int minWidth();

    protected abstract int minHeight();

    private class ViewTag {
        int top;
        int left;
        int right;
        int bottom;
        View child;
    }
}
