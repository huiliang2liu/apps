package com.momo.holder;

import android.widget.ImageView;
import android.widget.TextView;

import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.MainItemEntity;

public class MainHolder extends ViewHolder<MainItemEntity> {
    TextView textView;
    ImageView mainIv;

    @Override
    public void setContext(MainItemEntity mainItemEntity) {
        textView.setText(mainItemEntity.path);
        mainIv.setImageResource(mainItemEntity.id);
    }

    @Override
    public void bindView() {
        textView = (TextView) findViewById(R.id.main_tv);
        mainIv = (ImageView) findViewById(R.id.main_iv);
    }
}
