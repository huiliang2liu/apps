package com.momo.holder;

import android.widget.ImageView;

import com.base.adapter.tag.ViewHolder;
import com.momo.Application;
import com.momo.R;

public class SplashHolder extends ViewHolder<String> {
    private ImageView iv;
    private Application application;

    @Override
    public void setContext(String s) {
        application.imageLoad.load(-1, -1, s, iv, null);
    }

    @Override
    public void bindView() {
        iv = (ImageView) findViewById(R.id.splash_iv);
        application = (Application) context.getContext().getApplicationContext();
    }
}
