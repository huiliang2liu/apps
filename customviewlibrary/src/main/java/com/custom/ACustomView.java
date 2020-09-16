package com.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public abstract class ACustomView extends View {
    public ACustomView(Context context) {
        super(context);
    }

    public ACustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ACustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ACustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected final int getSuggestedMinimumHeight() {
        return Math.max(super.getSuggestedMinimumHeight(), minHeight());
    }

    @Override
    protected final int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(), minWidth());
    }

    protected abstract int minWidth();

    protected abstract int minHeight();
}
