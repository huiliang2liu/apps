package com.momo.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.DynamicEntity;
import com.momo.holder.NearDynamicHolder;

public class NearDynamicAdapter extends RecyclerViewAdapter<DynamicEntity> {
    public NearDynamicAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<DynamicEntity> getViewHolder(int itemType) {
        return new NearDynamicHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_near_dynamic;
    }
}
