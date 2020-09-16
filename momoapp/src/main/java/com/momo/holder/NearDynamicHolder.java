package com.momo.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.adapter.tag.ViewHolder;
import com.image.transform.CircleTransform;
import com.momo.R;
import com.momo.entities.DynamicEntity;

public class NearDynamicHolder extends ViewHolder<DynamicEntity> {
    private ImageView head;
    private TextView name;
    private TextView sex;
    private TextView textView;
    private RecyclerView rv;
    private ImageView give;
    private TextView giveSize;
    private ImageView comment;
    private TextView commentSize;
    private ImageView exceptional;

    @Override
    public void setContext(DynamicEntity dynamicEntity) {
        application.imageLoad.load(dynamicEntity.head, head, new CircleTransform());
        name.setText(dynamicEntity.name);
        sex.setText(dynamicEntity.sex==0?"男":"女");
        textView.setText(dynamicEntity.text);

    }

    @Override
    public void bindView() {
        textView = (TextView) findViewById(R.id.near_dynamic_tv);
        head = (ImageView) findViewById(R.id.near_dynamic_head);
        name = (TextView) findViewById(R.id.near_dynamic_name);
        sex = (TextView) findViewById(R.id.near_dynamic_sex);
        rv = (RecyclerView) findViewById(R.id.near_dynamic_rv);
        give = (ImageView) findViewById(R.id.near_dynamic_give);
        giveSize = (TextView) findViewById(R.id.near_dynamic_give_size);
        comment = (ImageView) findViewById(R.id.near_dynamic_comment);
        commentSize = (TextView) findViewById(R.id.near_dynamic_comment_size);
        exceptional = (ImageView) findViewById(R.id.near_dynamic_exceptional);
    }
}
