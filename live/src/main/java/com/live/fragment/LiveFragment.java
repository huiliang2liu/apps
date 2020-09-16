package com.live.fragment;


import android.os.Bundle;
import android.view.View;

import com.base.BaseFragment;
import com.base.adapter.FragmentAdapter;
import com.base.adapter.RecyclerViewAdapter;
import com.base.util.L;
import com.base.widget.RecyclerView;
import com.base.widget.ViewPager;
import com.live.R;
import com.live.adapter.TvAdapter;
import com.live.entities.HomeTabEntity;
import com.live.utils.Constant;
import com.live.utils.PublicSp;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveFragment extends BaseFragment implements RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "LiveFragment";
    @BindView(R.id.live_tab)
    RecyclerView liveTab;
    @BindView(R.id.live_vp)
    ViewPager liveVp;
    private TvAdapter adapter;
    private int tab = 0;
    private FragmentAdapter fragmentAdapter;

    @Override
    public void bindView() {
        super.bindView();
        L.d(TAG, "bindView");
        ButterKnife.bind(this, view);
        String[] tabs = getResources().getStringArray(R.array.live_tabs);
        List<HomeTabEntity> entities = new ArrayList<>(tabs.length);
        List<Fragment> fragments = new ArrayList<>(tabs.length);
        for (String tab : tabs) {
            if ("湖南".equals(tab)) {
                tab = PublicSp.getRegion(getContext());
            }
            HomeTabEntity entity = new HomeTabEntity();
            entity.tab = tab;
            entity.select = false;
            entities.add(entity);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID, tab);
            bundle.putString(Constant.TYPE, "live");
            if ("地方台".equals(tab)) {
                LivePlace fragment = new LivePlace();
                fragment.setArguments(bundle);
                fragments.add(fragment);
            } else {
                Live fragment = new Live();
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }

        }

        entities.get(0).select = true;
        adapter = new TvAdapter(liveTab);
        adapter.addItem(entities);
        adapter.setOnItemClickListener(this);
        fragmentAdapter = new FragmentAdapter(this, fragments);
        liveVp.setAdapter(fragmentAdapter);
    }

    @Override
    public int layout() {
        return R.layout.fragment_live;
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        adapter.getItem(tab).select = false;
        liveVp.setCurrentItem(position, false);
        tab = position;
        adapter.getItem(tab).select = true;
        adapter.notifyDataSetChanged();
    }
}
