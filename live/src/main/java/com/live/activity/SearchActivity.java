package com.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.base.BaseActivity;
import com.base.adapter.RecyclerViewAdapter;
import com.base.widget.RecyclerView;
import com.live.R;
import com.live.adapter.ListAdapter;
import com.live.entities.ListEntity;
import com.live.entities.LiveEntity;
import com.live.entities.LiveTypeEntity;
import com.live.entities.RadioEntity;
import com.live.entities.ShowEntity;
import com.live.provide.ChannelProvide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    public static final String SEARCH_YTPE = "search_type";
    @BindView(R.id.search_rv)
    RecyclerView searchRv;
    @BindView(R.id.search_tv)
    EditText searchTv;
    @BindView(R.id.search_iv)
    ImageView searchIv;
    @BindView(R.id.more_back)
    ImageView moreBack;
    private ListAdapter adapter;
    private int mType = 0;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        searchIv.setOnClickListener(this);
        moreBack.setOnClickListener(this);
        type = getIntent().getStringExtra(SEARCH_YTPE);
        if ("电台".equals(type)) {
            searchTv.setHint(String.format("搜索电台", type));
        } else {
            if (type != null) {
                LiveTypeEntity liveType = ChannelProvide.getType(this, type);
                if (liveType != null)
                    mType = liveType.type;
                searchTv.setHint(String.format("搜索%s", type));
            } else {
                mType = 0;
                searchTv.setHint(String.format("搜索全部", type));
            }
        }
        adapter = new ListAdapter(searchRv);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                ListEntity listEntity = adapter.getItem(position);
                ShowEntity entity = (ShowEntity) listEntity.object;
                Intent intent;
                if ("电台".equals(type)) {
                    intent = new Intent(SearchActivity.this, TvStationActivity.class);
                    intent.putExtra(TvStationActivity.PLAY_RADIO, entity.name);
                } else {
                    intent = new Intent(SearchActivity.this, PlayActivity.class);
                    intent.putExtra(PlayActivity.PLAY_TYPE, listEntity.title);
                    intent.putExtra(PlayActivity.PLAY_CHANNEL, entity.name);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_iv) {
            String text = searchTv.getText().toString();
            List<ListEntity> entities = new ArrayList<>();
            if (text == null || text.isEmpty()) {
                Toast.makeText(SearchActivity.this, "请输入搜索的内容", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("电台".equals(type)) {
                List<RadioEntity> radioEntities = ChannelProvide.getRadios(this, text);
                if (radioEntities != null && radioEntities.size() > 0)
                    for (RadioEntity entity : radioEntities) {
                        ShowEntity showEntity = new ShowEntity();
                        showEntity.name = entity.name;
                        ListEntity listEntity = new ListEntity();
                        listEntity.type = 13;
                        listEntity.title = type;
                        listEntity.object = showEntity;
                        entities.add(listEntity);
                    }

            } else {
                List<LiveEntity> liveEntities = ChannelProvide.getChannels(this, text);
                if (liveEntities != null && liveEntities.size() > 0)
                    for (LiveEntity entity : liveEntities) {
                        if (mType != 0 && entity.types.indexOf(mType) < 0)
                            continue;
                        ShowEntity showEntity = new ShowEntity();
                        showEntity.name = entity.name;
                        ListEntity listEntity = new ListEntity();
                        listEntity.type = 13;
                        listEntity.title =type;
                        listEntity.object = showEntity;
                        entities.add(listEntity);
                    }
            }
            if (entities.size() <= 0) {
                Toast.makeText(SearchActivity.this, String.format("没有搜索到%s", text), Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.clean();
            adapter.addItem(entities);
        } else if (id == R.id.more_back) {
            finish();
        }

    }
}
