package com.live.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.base.BaseActivity;
import com.base.adapter.RecyclerViewAdapter;
import com.base.widget.RecyclerView;
import com.live.R;
import com.live.adapter.ListAdapter;
import com.live.entities.ListEntity;
import com.live.entities.LiveEntity;
import com.live.entities.RadioEntity;
import com.live.entities.ShowEntity;
import com.live.provide.ChannelProvide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.annotation.TestAnnotation;
@TestAnnotation("adad")
public class HistoryActivity extends BaseActivity {
    @BindView(R.id.more_back)
    ImageView moreBack;
    @BindView(R.id.history_rv)
    RecyclerView historyRv;
    private ListAdapter adaapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        moreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adaapter = new ListAdapter(historyRv);
        List<ListEntity> entities = new ArrayList<>();
        List<LiveEntity> liveEntities = ChannelProvide.getHistoryChannels(this);
        if (liveEntities != null && liveEntities.size() > 0)
            for (LiveEntity entity : liveEntities) {
                ShowEntity showEntity = new ShowEntity();
                showEntity.name = entity.name;
                ListEntity listEntity = new ListEntity();
                listEntity.type = 13;
                listEntity.title = entity.name;
                listEntity.object = showEntity;
                entities.add(listEntity);
            }
        List<RadioEntity> radioEntities = ChannelProvide.getHistoryRadios(this);
        if (radioEntities != null && radioEntities.size() > 0)
            for (RadioEntity entity : radioEntities) {
                ShowEntity showEntity = new ShowEntity();
                showEntity.name = entity.name;
                ListEntity listEntity = new ListEntity();
                listEntity.type = 13;
                listEntity.title = "电台";
                listEntity.object = showEntity;
                entities.add(listEntity);
            }
        adaapter.addItem(entities);
        adaapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                ListEntity entity = adaapter.getItem(position);
                ShowEntity showEntity = (ShowEntity) entity.object;
                Intent intent;
                if ("电台".equals(entity.title)) {
                    intent = new Intent(HistoryActivity.this, TvStationActivity.class);
                    intent.putExtra(TvStationActivity.PLAY_RADIO, showEntity.name);
                } else {
                    intent = new Intent(HistoryActivity.this, PlayActivity.class);
                    intent.putExtra(PlayActivity.PLAY_TYPE, entity.title);
                    intent.putExtra(PlayActivity.PLAY_CHANNEL, showEntity.name);
                }
                startActivity(intent);
            }
        });
    }
}
