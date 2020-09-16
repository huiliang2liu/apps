package com.http.listen;

import com.http.ResponseObject;

/**
 * com.http.listen
 * 2018/10/17 15:52
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public interface ResponseObjectListener {
    /**
     * 开始加载
     */
    void start();

    /**
     * 加载失败
     */
    void failure();

    /**
     * 加载成功
     */
     void success(ResponseObject response);
}
