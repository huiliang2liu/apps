package com.http;

import java.util.Map;

/**
 * com.http
 * 2018/10/17 15:39
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class ResponseObject {
    public int code;//响应码
    public Map<String,String> heard;//响应头部信息
    public Object response;//响应结果
}
