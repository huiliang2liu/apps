package com.electricity.adapter;

import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.PagerAdapter;
import com.base.adapter.tag.ViewHolder;
import com.electricity.R;
import com.electricity.adapter.holder.SplashHolder;

public class SplashAdapter extends PagerAdapter<String> {
    public SplashAdapter(ViewPager recyclerView) {
        super(recyclerView);
        recyclerView.setAdapter(this);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position % super.getCount());
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
