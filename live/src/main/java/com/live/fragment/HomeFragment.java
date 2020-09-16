package com.live.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.base.BaseFragment;
import com.base.adapter.FragmentAdapter;
import com.base.adapter.RecyclerViewAdapter;
import com.base.widget.RecyclerView;
import com.base.widget.ViewPager;
import com.live.R;
import com.live.activity.HistoryActivity;
import com.live.activity.MoreActivity;
import com.live.activity.SearchActivity;
import com.live.adapter.TvAdapter;
import com.live.entities.HomeTabEntity;
import com.live.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment implements RecyclerViewAdapter.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "HomeFragment";
    @BindView(R.id.tv_more)
    ImageView tvMore;
    @BindView(R.id.tv_tab_rv)
    RecyclerView tvTabRv;
    @BindView(R.id.tv_vp)
    ViewPager tvVp;
    private TvAdapter adapter;
    private int tabIndex = 0;
    private FragmentAdapter fragmentAdapter;

    @Override
    public void bindView() {
        super.bindView();
        ButterKnife.bind(this, view);
        adapter = new TvAdapter(tvTabRv);
        adapter.setOnItemClickListener(this);
        tvMore.setOnClickListener(this);
        view.findViewById(R.id.tv_history).setOnClickListener(this);
        view.findViewById(R.id.tv_search).setOnClickListener(this);
        tvMore.setOnClickListener(this);
        String[] tabs = getResources().getStringArray(R.array.tabs);
        List<HomeTabEntity> entities = new ArrayList<>(tabs.length);
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < tabs.length; i++) {
            HomeTabEntity entity = new HomeTabEntity();
            entity.tab = tabs[i];
            BaseFragment baseFragment = new Live();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID, tabs[i]);
            bundle.putString(Constant.TYPE, "home");
            baseFragment.setArguments(bundle);
            entities.add(entity);
            fragments.add(baseFragment);
        }
        fragmentAdapter = new FragmentAdapter(this, fragments);
        tvVp.setAdapter(fragmentAdapter);
        entities.get(0).select = true;
        adapter.addItem(entities);
    }

    @Override
    public int layout() {
        return R.layout.fragment_tv;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_search) {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra(SearchActivity.SEARCH_YTPE, adapter.getItem(tabIndex).tab);
            result.startActivity(intent);
        } else if (id == R.id.tv_history) {
            result.startActivity(new Intent(getContext(), HistoryActivity.class));
        } else {
            Intent intent = new Intent(getContext(), MoreActivity.class);
            intent.putExtra(Constant.TYPE, adapter.getItem(tabIndex).tab);
            result.startActivity(intent);
        }

    }


    @Override
    public void onItemClick(View view, int position, long id) {
        Log.e(TAG, "onItemClick");
        if (tabIndex == position)
            return;
        adapter.getItem(tabIndex).select = false;
        adapter.getItem(position).select = true;
        adapter.notifyDataSetChanged();
        tvTabRv.toCentre(position);
        tabIndex = position;
        tvVp.setCurrentItem(tabIndex, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void event(String tab) {
        for (int i = 0; i < adapter.getCount(); i++) {
            Log.e(TAG, adapter.getItem(i).tab);
            if (adapter.getItem(i).tab.equals(tab)) {
                tvTabRv.toCentre(i, false);
                adapter.getItem(tabIndex).select = false;
                adapter.getItem(i).select = true;
                adapter.notifyDataSetChanged();
                tabIndex = i;
                tvVp.setCurrentItem(tabIndex, false);
                break;
            }
        }

    }
}
