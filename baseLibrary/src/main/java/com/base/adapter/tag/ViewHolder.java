package com.base.adapter.tag;

import android.view.View;

import com.base.BaseApplication;
import com.base.adapter.IAdapter;
import com.http.FileHttp;
import com.http.Http;
import com.image.IImageLoad;


/**
 * com.tvblack.lamp.adapter.tag
 * 2019/2/18 16:40
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public abstract class ViewHolder<T> {
    protected IAdapter<T> adapter;
    protected int position = -1;
    protected View context;
    protected BaseApplication application;
    protected Http http;
    protected FileHttp fileHttp;
    protected IImageLoad imageLoad;

    public final void setAdapter(IAdapter<T> adapter) {
        this.adapter = adapter;
    }

    public final void setView(View context) {
        this.context = context;
        application = (BaseApplication) context.getContext().getApplicationContext();
        http = application.http;
        fileHttp = application.fileHttp;
        imageLoad = application.imageLoad;
        bindView();
    }

    public final void setContext(int position) {
        this.position = position;
        setContext(adapter.getItem(position));
    }

    public final <T extends View> T findViewById(int viewId) {
        return context.findViewById(viewId);
    }

    public abstract void setContext(T t);

    public abstract void bindView();
}