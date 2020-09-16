package com.momo.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.LiveEntity;
import com.momo.holder.NearLiveHolder;

public class NearLiveAdapter extends RecyclerViewAdapter<LiveEntity> {
    public NearLiveAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<LiveEntity> getViewHolder(int itemType) {
        return new NearLiveHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_near_live;
    }
}
