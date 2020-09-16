package com.electricity.adapter.holder;

import android.widget.ImageView;

import com.base.BaseApplication;
import com.base.adapter.tag.ViewHolder;
import com.electricity.R;

public class MainHolder extends ViewHolder<String> {
    private BaseApplication baseApplication;
    ImageView avatarImageView;

    @Override
    public void setContext(String s) {
        baseApplication.imageLoad.load(R.mipmap.ic_launcher, R.mipmap.ic_launcher, s, avatarImageView, null);
    }

    @Override
    public void bindView() {
        baseApplication= (BaseApplication) context.getContext().getApplicationContext();
        avatarImageView = (ImageView) findViewById(R.id.iv_avatar);
    }
}
