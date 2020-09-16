package com.base.widget.book;

import android.view.View;

public abstract class CurlViewAdapter implements CurlView.PageProvider {

    @Override
    public final void updatePage(CurlPage page, int width, int height, int index) {
        int layoutId = getView(index);

//        View view=layoutId
    }

    public int getView(int position) {
        return position;
    }
}
