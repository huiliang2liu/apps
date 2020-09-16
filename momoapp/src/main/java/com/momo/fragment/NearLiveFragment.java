package com.momo.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.adapter.IAdapter;
import com.momo.R;
import com.momo.adapter.NearLiveAdapter;
import com.momo.entities.LiveEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NearLiveFragment extends Fragment {
    @BindView(R.id.near_dynamic_rv)
    RecyclerView nearDynamicRv;
    Unbinder unbinder;
    private View view;
    private IAdapter<LiveEntity> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_near_dynamic, null);
            unbinder = ButterKnife.bind(this, view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            nearDynamicRv.setLayoutManager(linearLayoutManager);
            adapter = new NearLiveAdapter(nearDynamicRv);
            for (int i = 0; i < 100; i++) {
                LiveEntity entity = new LiveEntity();
                entity.name = String.format("name%d", i);
                adapter.addItem(entity);
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
