package com.electricity.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.electricity.R;
import com.electricity.adapter.holder.MainHolder;

public class MainAdapter extends RecyclerViewAdapter<String> {
    public MainAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public ViewHolder<String> getViewHolder(int itemType) {
        return new MainHolder();
    }

    @Override
    public int getView(int itemType) {
        return itemType == 0 ? R.layout.item : itemType == 1 ? R.layout.item1 : R.layout.item2;
    }
}
