package com.momo.holder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.base.adapter.tag.ViewHolder;
import com.momo.Application;
import com.momo.R;
import com.momo.activity.PeopleDetailActivity;

public class PeopleDetailHolder1 extends ViewHolder<String> {
    private static final String TAG = "PeopleDetailHolder1";
    Application application;
    PeopleDetailActivity activity;
    ImageView iv;

    @Override
    public void setContext(String s) {
        application.imageLoad.load(-1, -1, s, iv, null);
        Log.e(TAG, "setContext");
    }

    @Override
    public void bindView() {
        activity = (PeopleDetailActivity) context.getContext();
        application = (Application) context.getContext().getApplicationContext();
        iv = (ImageView) findViewById(R.id.splash_iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showPics(position);
            }
        });
    }
}
