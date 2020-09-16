package com.momo.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.entities.PeopleEntity;
import com.momo.holder.NearPeopleHolder;

public class NearPeopleAdapter extends RecyclerViewAdapter<PeopleEntity> {
    public NearPeopleAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<PeopleEntity> getViewHolder(int itemType) {
        return new NearPeopleHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_near_people;
    }
}
