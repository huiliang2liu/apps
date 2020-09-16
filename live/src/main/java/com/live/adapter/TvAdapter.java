package com.live.adapter;


import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.live.R;
import com.live.entities.HomeTabEntity;
import com.live.holder.TvHolder;

import androidx.recyclerview.widget.RecyclerView;

public class TvAdapter extends RecyclerViewAdapter<HomeTabEntity> {
    public TvAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<HomeTabEntity> getViewHolder(int itemType) {
        return new TvHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.holder_tv;
    }
}
