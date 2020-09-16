package com.image.glide;

import android.os.Build;
import android.view.View;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

public class GlideViewTarget extends ViewTarget<View, GlideDrawable> {
    public GlideViewTarget(View view) {
        super(view);
    }

    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
        if (Build.VERSION.SDK_INT > 15)
            view.setBackground(resource);
        else
            view.setBackgroundDrawable(resource);
    }
}
