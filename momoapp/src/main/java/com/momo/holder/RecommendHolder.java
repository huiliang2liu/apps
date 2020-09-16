package com.momo.holder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.adapter.tag.ViewHolder;
import com.momo.Application;
import com.momo.R;
import com.momo.activity.PeopleDetailActivity;
import com.momo.entities.PeopleEntity;
import com.momo.utils.Constants;

public class RecommendHolder extends ViewHolder<PeopleEntity> {
    private Application application;
    private ImageView recommendIv;
    private TextView recommendName;
    private TextView recommendCity;
    private ImageView recommendSex;
    private TextView recommendAge;
    private View recommendPrevious;

    @Override
    public void setContext(final PeopleEntity peopleEntity) {
        application.imageLoad.load(-1, -1, peopleEntity.head, recommendIv, null);
        recommendName.setText(peopleEntity.name);
        recommendCity.setText(peopleEntity.city);
        recommendAge.setText(String.valueOf(peopleEntity.age));
        recommendPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(application, PeopleDetailActivity.class);
                intent.putExtra(Constants.PEOPLE_ENTITY, peopleEntity);
                context.getContext().startActivity(intent);
            }
        });
        recommendSex.setImageResource(peopleEntity.sex == 0 ? R.mipmap.hani_live_home_filter_male_icon : R.mipmap.hani_live_home_filter_female_icon);
    }

    @Override
    public void bindView() {
        application = (Application) context.getContext().getApplicationContext();
        recommendIv = (ImageView) findViewById(R.id.recommend_iv);
        recommendName = (TextView) findViewById(R.id.recommend_name);
        recommendCity = (TextView) findViewById(R.id.recommend_city);
        recommendSex = (ImageView) findViewById(R.id.recommend_sex);
        recommendAge = (TextView) findViewById(R.id.recommend_age);
        recommendPrevious = findViewById(R.id.recommend_previous);
    }
}
