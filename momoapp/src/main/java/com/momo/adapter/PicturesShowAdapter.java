package com.momo.adapter;


import com.base.adapter.PagerAdapter;
import com.base.adapter.tag.ViewHolder;
import com.momo.R;
import com.momo.holder.PicturesShowHolder;

import androidx.viewpager.widget.ViewPager;

public class PicturesShowAdapter extends PagerAdapter<String> {
    public PicturesShowAdapter(ViewPager viewPager) {
        super(viewPager);
    }

    @Override
    public ViewHolder<String> getViewHolder(int itemType) {
        return new PicturesShowHolder();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.adapter_pictures_show;
    }
}
