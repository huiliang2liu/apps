package com.live.fragment;

import android.content.Intent;
import android.view.View;

import com.base.BaseFragment;
import com.base.adapter.RecyclerViewAdapter;
import com.base.thread.PoolManager;
import com.base.util.L;
import com.base.widget.RecyclerView;
import com.base.widget.RefreshView;
import com.live.R;
import com.live.activity.PlayActivity;
import com.live.activity.TvStationActivity;
import com.live.adapter.ListAdapter;
import com.live.entities.ListEntity;
import com.live.entities.LiveEntity;
import com.live.entities.LiveTypeEntity;
import com.live.entities.RadioEntity;
import com.live.entities.ShowEntity;
import com.live.provide.ChannelProvide;
import com.live.utils.Constant;
import com.live.utils.PublicSp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Live extends BaseFragment implements RefreshView.RefreshListener {
    private static final String TAG = "Live";
    @BindView(R.id.list_rv)
    RecyclerView listRv;
    @BindView(R.id.list_refresh)
    RefreshView listRefresh;
    private ListAdapter adaapter;
    private String id;
    private String type;

    @Override
    public void bindView() {
        super.bindView();
        ButterKnife.bind(this, view);
        id = getArguments().getString(Constant.ID);
        type = getArguments().getString(Constant.TYPE);
        adaapter = new ListAdapter(listRv);
        listRefresh.setRefreshListener(this);
        L.d(TAG, String.format("id:%s,type:%s", id, type));
        List<ListEntity> entities = new ArrayList<>();
        if ("live".equals(type)) {
            if ("推荐".equals(id)) {
                entities.add(ListEntity.entity8());
                for (int i = 0; i < 4; i++)
                    entities.add(ListEntity.entity9());
            } else if ("央视".equals(id) || "卫视".equals(id) || PublicSp.getRegion(getContext()).equals(id)) {
                LiveTypeEntity typeEntity = ChannelProvide.getType(getContext(), id);
                if (typeEntity != null) {
                    List<LiveEntity> list = ChannelProvide.getChannels(getContext());
                    List<ShowEntity> showEntities = new ArrayList<>();
                    for (LiveEntity entity : list) {
                        for (int type : entity.types)
                            if (type == typeEntity.type) {
                                ShowEntity showEntity = new ShowEntity();
                                showEntity.name = entity.name;
                                showEntities.add(showEntity);
                                if (showEntities.size() == 2) {
                                    ListEntity listEntity = new ListEntity();
                                    listEntity.type = 10;
                                    listEntity.title = id;
                                    listEntity.object = showEntities;
                                    entities.add(listEntity);
                                    showEntities = new ArrayList<>();
                                }
                                break;
                            }
                    }
                    if (showEntities.size() == 1) {
                        ListEntity listEntity = new ListEntity();
                        listEntity.type = 10;
                        listEntity.title = id;
                        listEntity.object = showEntities;
                        entities.add(listEntity);
                    }
                }
            } else {
                for (int i = 0; i < 12; i++)
                    entities.add(ListEntity.entity10());
            }
        } else if ("home".equals(type)) {
            if ("推荐".equals(id)) {
                entities.add(ListEntity.entity0());
                entities.add(ListEntity.entity1());
                entities.add(ListEntity.entity2());
                entities.add(ListEntity.entity5());
                entities.add(ListEntity.entity5());
                entities.add(ListEntity.entity5());
                entities.add(ListEntity.entity2());
                entities.add(ListEntity.entity3());
                entities.add(ListEntity.entity4());
                entities.add(ListEntity.entity4());
            } else if ("电台".equals(id)) {
                List<RadioEntity> radioEntities = ChannelProvide.getRadios(getContext());
                for (RadioEntity radioEntity : radioEntities) {
                    ListEntity listEntity = new ListEntity();
                    listEntity.type = 13;
                    ShowEntity showEntity = new ShowEntity();
                    showEntity.name = radioEntity.name;
                    listEntity.object = showEntity;
                    entities.add(listEntity);
                    adaapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, long id) {
                            Intent intent = new Intent(getContext(), TvStationActivity.class);
                            ShowEntity entity = (ShowEntity) adaapter.getItem(position).object;
                            intent.putExtra(TvStationActivity.PLAY_RADIO, entity.name);
                            startActivity(intent);
                        }
                    });
                }
            } else if ("短视频".equals(id)) {
                for (int i = 0; i < 10; i++) {
                    entities.add(ListEntity.entity14());
                }
            } else if ("电视剧".equals(id) || "电影".equals(id) || "综艺".equals(id) ||
                    "少儿".equals(id) || "音乐".equals(id) || "体育".equals(id) || "曲艺".equals(id)) {
                String name = id;
                if (name.equals("综艺"))
                    name = "娱乐综艺";
                else if (name.equals("曲艺"))
                    name = "戏剧";
                LiveTypeEntity typeEntity = ChannelProvide.getType(getContext(), name);
                if (typeEntity != null) {
                    List<LiveEntity> list = ChannelProvide.getChannels(getContext());
                    for (LiveEntity entity : list) {
                        for (int type : entity.types)
                            if (type == typeEntity.type) {
                                ShowEntity showEntity = new ShowEntity();
                                showEntity.name = entity.name;
                                ListEntity listEntity = new ListEntity();
                                listEntity.type = 13;
                                listEntity.title = name;
                                listEntity.object = showEntity;
                                entities.add(listEntity);
                                break;
                            }
                    }
                    adaapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, long id) {
                            ListEntity listEntity=adaapter.getItem(position);
                            ShowEntity entity= (ShowEntity) listEntity.object;
                            Intent intent=new Intent(getContext(),PlayActivity.class);
                            intent.putExtra(PlayActivity.PLAY_TYPE,listEntity.title);
                            intent.putExtra(PlayActivity.PLAY_CHANNEL,entity.name);
                            startActivity(intent);
                        }
                    });
                } else
                    for (int i = 0; i < 10; i++) {
                        entities.add(ListEntity.entity13());
                    }
            } else
                for (int i = 0; i < 10; i++) {
                    entities.add(ListEntity.entity13());
                }

        } else if ("hot".equals(type)) {
            if ("影视".equals(id)) {
                for (int i = 0; i < 10; i++) {
                    entities.add(ListEntity.entity14());
                }
            } else
                for (int i = 0; i < 10; i++) {
                    entities.add(ListEntity.entity13());
                }
        }
        adaapter.addItem(entities);
    }

    @Override
    public int layout() {
        return R.layout.fragment_list;
    }

    @Override
    public void onRefresh() {
        PoolManager.runUiThread(new Runnable() {
            @Override
            public void run() {
                listRefresh.stopLoad();
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        PoolManager.runUiThread(new Runnable() {
            @Override
            public void run() {
                listRefresh.stopLoad();
            }
        }, 3000);
    }
}
