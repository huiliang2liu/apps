package com.momo.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.DynamicEntity;
import com.momo.holder.PeopleDetaiHolder2;

public class PeopleDedailAdapter2 extends RecyclerViewAdapter<DynamicEntity> {
    public PeopleDedailAdapter2(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<DynamicEntity> getViewHolder(int itemType) {
        return new PeopleDetaiHolder2();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_people_detail;
    }
}
