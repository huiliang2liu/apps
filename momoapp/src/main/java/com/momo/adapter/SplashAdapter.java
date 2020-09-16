package com.momo.adapter;

import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.PagerAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.holder.SplashHolder;

import androidx.viewpager.widget.ViewPager;

public class SplashAdapter extends PagerAdapter<String> {
    public SplashAdapter(ViewPager viewPager) {
        super(viewPager);
    }

    @Override
    public ViewHolder<String> getViewHolder(int itemType) {
        return new SplashHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_splash;
    }
}
