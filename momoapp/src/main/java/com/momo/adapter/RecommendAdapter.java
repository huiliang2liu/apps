package com.momo.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.PeopleEntity;
import com.momo.holder.RecommendHolder;

public class RecommendAdapter extends RecyclerViewAdapter<PeopleEntity> {
    public RecommendAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<PeopleEntity> getViewHolder(int itemType) {
        return new RecommendHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_recommend;
    }
}
