package com.momo.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.MainItemEntity;
import com.momo.holder.MainHolder;

public class MainAdapter extends RecyclerViewAdapter<MainItemEntity> {
    public MainAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<MainItemEntity> getViewHolder(int itemType) {
        return new MainHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_main;
    }
}
