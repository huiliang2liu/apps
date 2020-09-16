package com.live.fragment;

import android.content.Intent;
import android.view.View;

import com.base.BaseFragment;
import com.base.adapter.RecyclerViewAdapter;
import com.base.widget.RecyclerView;
import com.live.R;
import com.live.activity.PlayActivity;
import com.live.adapter.ListAdapter;
import com.live.entities.ListEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LivePlace extends BaseFragment {
    @BindView(R.id.live_place_place)
    RecyclerView livePlacePlace;
    @BindView(R.id.live_place_live)
    RecyclerView livePlaceLive;
    private ListAdapter placeAdapter;
    private ListAdapter liveAdapter;

    @Override
    public void bindView() {
        super.bindView();
        ButterKnife.bind(this, view);
        placeAdapter = new ListAdapter(livePlacePlace);
        liveAdapter = new ListAdapter(livePlaceLive);
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entities.add(ListEntity.entity11());
        }
        entities.get(5).select = true;
        placeAdapter.addItem(entities);
        placeAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                for (int i = 0; i < placeAdapter.getCount(); i++) {
                    if (i == position)
                        placeAdapter.getItem(i).select = true;
                    else
                        placeAdapter.getItem(i).select = false;
                }
                placeAdapter.notifyDataSetChanged();
                liveAdapter.clean();
                liveAdapter.addItem((List) placeAdapter.getItem(position).object);
            }
        });
        liveAdapter.addItem((List)placeAdapter.getItem(5).object);
        liveAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                startActivity(new Intent(application, PlayActivity.class));
            }
        });
    }

    @Override
    public int layout() {
        return R.layout.fragment_live_place;
    }
}
