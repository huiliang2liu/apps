package com.momo.holder;

import android.widget.TextView;

import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.PeopleEntity;

public class NearPeopleHolder extends ViewHolder<PeopleEntity> {
    private TextView textView;

    @Override
    public void setContext(PeopleEntity peopleEntity) {
        textView.setText(peopleEntity.name);
    }

    @Override
    public void bindView() {
        textView = (TextView) findViewById(R.id.near_people_tv);
    }
}
