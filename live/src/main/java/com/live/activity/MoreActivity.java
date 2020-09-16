package com.live.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;

import com.base.BaseActivity;
import com.base.adapter.RecyclerViewAdapter;
import com.base.widget.RecyclerView;
import com.live.R;
import com.live.adapter.MoreAdapter;
import com.live.entities.MoreEntity;
import com.live.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreActivity extends BaseActivity {
    @BindView(R.id.more_rv)
    RecyclerView moreRv;
    private RecyclerViewAdapter<MoreEntity> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        findViewById(R.id.more_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String type="推荐";
        try {
             type=getIntent().getStringExtra(Constant.TYPE);
        } catch (Exception e) {
        }
        adapter = new MoreAdapter(moreRv);
        String[] strings = getResources().getStringArray(R.array.tabs);
        List<MoreEntity> entities = new ArrayList<>(strings.length);
        for (int i = 0; i < strings.length; i++) {
            MoreEntity event = new MoreEntity();
            event.name = strings[i];
            event.ico = getResources().getIdentifier(String.format("tab%d", i), "mipmap", getPackageName());
            entities.add(event);
        }
        adapter.addItem(entities);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                EventBus.getDefault().post(adapter.getItem(position).name);
                finish();
            }
        });
    }
}
