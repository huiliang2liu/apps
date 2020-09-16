package com.live;

import android.content.Context;

import com.http.Http;
import com.http.interceptor.RequestInterceptor;

import java.util.HashMap;
import java.util.Map;

public class MyIntercepter implements RequestInterceptor {
    @Override
    public Http.RequestEntity intercept(Http.RequestEntity entity, Context context) {
//        Map<String, String> heard = new HashMap<>();
//        heard.put("X-Forwarded-For", "39.108.49.55");
//        heard.put("CLIENT-IP", "39.108.49.55");
//        entity.heard = heard;
        return entity;
    }
}
