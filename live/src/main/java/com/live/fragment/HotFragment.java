package com.live.fragment;

import android.os.Bundle;
import android.view.View;

import com.base.BaseFragment;
import com.base.adapter.FragmentAdapter;
import com.base.adapter.RecyclerViewAdapter;
import com.base.widget.RecyclerView;
import com.base.widget.ViewPager;
import com.live.R;
import com.live.adapter.TvAdapter;
import com.live.entities.HomeTabEntity;
import com.live.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HotFragment extends BaseFragment implements RecyclerViewAdapter.OnItemClickListener {
    @BindView(R.id.hot_tab)
    RecyclerView hotTab;
    @BindView(R.id.hot_vp)
    ViewPager hotVp;
    private TvAdapter adapter;
    private FragmentAdapter fragmentAdapter;
    private int tab=0;

    @Override
    public void bindView() {
        super.bindView();
        ButterKnife.bind(this, view);
        adapter=new TvAdapter(hotTab);
        String[] tabs=getResources().getStringArray(R.array.hot_tabs);
        List<HomeTabEntity> entities=new ArrayList<>();
        List<Fragment> fragments=new ArrayList<>();
        for (String tab:tabs){
            HomeTabEntity entity=new HomeTabEntity();
            entity.tab=tab;
            entities.add(entity);
            Bundle bundle=new Bundle();
            bundle.putString(Constant.ID,tab);
            bundle.putString(Constant.TYPE,"hot");
            Fragment fragment=new Live();
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        fragmentAdapter=new FragmentAdapter(this,fragments);
        hotVp.setAdapter(fragmentAdapter);
        entities.get(0).select=true;
        adapter.setOnItemClickListener(this);
        adapter.addItem(entities);
    }

    @Override
    public int layout() {
        return R.layout.fragment_hot;
    }
    @Override
    public void onItemClick(View view, int position, long id) {
        adapter.getItem(tab).select = false;
        hotVp.setCurrentItem(position, false);
        tab = position;
        adapter.getItem(tab).select = true;
        adapter.notifyDataSetChanged();
    }
}
