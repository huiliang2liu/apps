package com.momo.adapter;

import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.base.adapter.PagerAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.holder.PeopleDetailHolder1;

public class PeopleDedailAdapter1 extends PagerAdapter<String> {
    public PeopleDedailAdapter1(ViewPager viewPager) {
        super(viewPager);
    }

    @Override
    public ViewHolder<String> getViewHolder(int itemType) {
        return new PeopleDetailHolder1();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_splash;
    }
}
