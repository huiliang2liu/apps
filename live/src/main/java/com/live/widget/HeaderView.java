package com.live.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.animation.ViewEmbellish;
import com.base.widget.RefreshView;
import com.live.R;

public class HeaderView extends FrameLayout implements RefreshView.ChangeListener {
    private TextView type;
    private TextView refreshTime;
    private ObjectAnimator animator;
    private ImageView imageView;

    {
        View item = LayoutInflater.from(getContext()).inflate(R.layout.header_viev_item, null);
        type = item.findViewById(R.id.header_view_type);
        refreshTime = item.findViewById(R.id.header_view_refreshTime);
        imageView = item.findViewById(R.id.header_view_iv);
        animator = new ViewEmbellish(imageView).rotationAnimator(30000l, 0, 360 * 30);
        addView(item, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public HeaderView(@NonNull Context context) {
        super(context);
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    public void onLoaded() {
        refreshTime.setText("" + System.currentTimeMillis());
        animator.cancel();
    }

    @Override
    public void onLoad() {
        animator.start();
    }

    @Override
    public void onChange(boolean load) {
        if (load) {
            imageView.setRotation(90);
        } else
            imageView.setRotation(0);
    }
}
