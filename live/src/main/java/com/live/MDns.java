package com.live;

import android.util.Log;

import com.http.interceptor.DnsInterceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MDns implements DnsInterceptor {
    @Override
    public List<InetAddress> interceptor(String hostname) throws UnknownHostException {
        Log.d("MDns", "==========");
        if (hostname.equals("xw.cp88.ott.cibntv.net"))
            return Arrays.asList(InetAddress.getAllByName("live.xiaoweizhibo.com"));
        return Arrays.asList(InetAddress.getAllByName(hostname));
    }

    @Override
    public HostnameVerifier verifer() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
}
