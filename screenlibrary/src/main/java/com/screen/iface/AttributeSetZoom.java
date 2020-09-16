package com.screen.iface;

import android.view.View;

/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:22
 * Descripotion：重新设置布局监听，最好是application实现
 */
public interface AttributeSetZoom {
    /**
     * description：布局解析完成会回调该方法
     *
     * @param view 需要修改的布局
     */
    void onAttributeSetZoom(View view);
}
