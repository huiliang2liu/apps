package com.base.net;

/**
 * com.net
 * 2018/9/28 9:59
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public interface NetCallback {
    /**
     * 连接网络
     */
    void onAvailable();

    /**
     * 网络断开
     */
    void onLost();
}
