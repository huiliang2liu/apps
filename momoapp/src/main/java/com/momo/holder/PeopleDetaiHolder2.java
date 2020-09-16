package com.momo.holder;

import android.view.View;
import android.widget.ImageView;

import com.base.adapter.tag.ViewHolder;
import com.image.transform.RoundTransform;
import com.momo.Application;
import com.momo.R;
import com.momo.activity.PeopleDetailActivity;
import com.momo.entities.DynamicEntity;

public class PeopleDetaiHolder2 extends ViewHolder<DynamicEntity> {
    private ImageView imageView;
    private Application application;
    private PeopleDetailActivity activity;

    @Override
    public void setContext(DynamicEntity dynamicEntity) {
        application.imageLoad.load(-1, -1, dynamicEntity.pic.get(0), imageView, new RoundTransform(10, 10));
    }

    @Override
    public void bindView() {
        activity = (PeopleDetailActivity) context.getContext();
        application = (Application) context.getContext().getApplicationContext();
        imageView = (ImageView) findViewById(R.id.people_detail_iv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showDynamic(position);
            }
        });

    }
}
