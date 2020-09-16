package com.momo.holder;

import android.widget.ImageView;

import com.base.adapter.tag.ViewHolder;
import com.momo.Application;
import com.momo.R;

public class PicturesShowHolder extends ViewHolder<String> {
    private ImageView imageView;
    private Application application;

    @Override
    public void setContext(String s) {
        application.imageLoad.load(-1, -1, s, imageView, null);
    }

    @Override
    public void bindView() {
        application = (Application) context.getContext().getApplicationContext();
        imageView = (ImageView) findViewById(R.id.pictures_show_iv);
    }
}
