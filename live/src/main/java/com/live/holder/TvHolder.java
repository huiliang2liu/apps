package com.live.holder;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.base.adapter.tag.ViewHolder;
import com.live.R;
import com.live.entities.HomeTabEntity;

public class TvHolder extends ViewHolder<HomeTabEntity> {
    private TextView textView;

    @Override
    public void setContext(HomeTabEntity s) {
        textView.setText(s.tab);
        if(s.select){
           textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,application.getResources().getDimensionPixelSize(R.dimen.sp22));
        }else{
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,application.getResources().getDimensionPixelSize(R.dimen.sp16));
        }
    }

    @Override
    public void bindView() {
        textView = (TextView) findViewById(R.id.tv_tv);

    }
}
