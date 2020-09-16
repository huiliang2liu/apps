package com.momo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.BaseFragment;
import com.base.adapter.FragmentAdapter;
import com.base.adapter.IAdapter;
import com.momo.R;
import com.momo.adapter.MainAdapter;
import com.momo.entities.MainItemEntity;
import com.screen.ScreenManager;
import com.screen.iface.TextStyle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends BaseFragment implements View.OnClickListener, TextStyle {
    private static final String TAG = "MainFragment";
    private static List<MainItemEntity> itemEntities = new ArrayList<>();
    Unbinder unbinder;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.main_dynamic)
    TextView mainDynamic;
    @BindView(R.id.main_people)
    TextView mainPeople;
    @BindView(R.id.main_live)
    TextView mainLive;
    @BindView(R.id.main_vp)
    ViewPager mainVp;
    @BindView(R.id.main_rv)
    RecyclerView mainRv;
    private IAdapter<MainItemEntity> mainItemEntityIAdapter;
    Unbinder unbinder1;
    private FragmentAdapter adapter;
    private View view;
    private int page = 0;
    private List<Fragment> fragments;

    static {
        itemEntities.add(new MainItemEntity("附近的群组", R.mipmap.ic_nav_nearby_active));
        itemEntities.add(new MainItemEntity("热门视频", R.mipmap.ic_nav_nearby_active));
        itemEntities.add(new MainItemEntity("天天抢车位", R.mipmap.ic_nav_nearby_active));
        itemEntities.add(new MainItemEntity("天天庄园", R.mipmap.ic_nav_nearby_active));
        itemEntities.add(new MainItemEntity("姻缘寺", R.mipmap.ic_nav_nearby_active));
    }

    @Override
    public void setTextStyle(TextView textView, String textStyle) {
        int ramd = (int) (Math.random() * 10);
        if (ramd % 3 == 0)
            textView.setTextColor(Color.RED);
        else if (ramd % 3 == 1)
            textView.setTextColor(Color.YELLOW);
        else {
            textView.setTextColor(Color.BLUE);
        }
        textView.setTextSize(30);
    }


    @Override
    public int layout() {
        return R.layout.fragment_main;
    }

    @Override
    public void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        mainDynamic.setOnClickListener(this);
        mainPeople.setOnClickListener(this);
        mainLive.setOnClickListener(this);
        fragments = new ArrayList<>();
        fragments.add(new NearDynamicFragment());
        fragments.add(new NearPeopleFragment());
        fragments.add(new NearLiveFragment());
        adapter = new FragmentAdapter(this, fragments);
        mainVp.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mainRv.setLayoutManager(linearLayoutManager);
        mainItemEntityIAdapter = new MainAdapter(mainRv);
        mainItemEntityIAdapter.addItem(itemEntities);
        page();
        ScreenManager.getInstance(getContext()).replaceFontFromAsset(view, "font/AliHYAiHei.ttf");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isResumed()){

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.main_dynamic) {
            if (page != 0) {
                page = 0;
                page();
            }
        } else if (id == R.id.main_people) {
            if (page != 1) {
                page = 1;
                page();
            }
        } else if (id == R.id.main_live) {
            if (page != 2) {
                page = 2;
                page();
            }
        }
    }

    private void page() {
        mainDynamic.setTextSize(page == 0 ? 15 : 12);
        mainPeople.setTextSize(page == 1 ? 15 : 12);
        mainLive.setTextSize(page == 2 ? 15 : 12);
        mainVp.setCurrentItem(page, false);
    }
}
