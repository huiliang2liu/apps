package com.momo.holder;

import android.widget.TextView;

import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.LiveEntity;

public class NearLiveHolder extends ViewHolder<LiveEntity> {
    private TextView textView;
    @Override
    public void setContext(LiveEntity liveEntity) {
        textView.setText(liveEntity.name);
    }

    @Override
    public void bindView() {
        textView= (TextView) findViewById(R.id.near_live_tv);
    }
}
