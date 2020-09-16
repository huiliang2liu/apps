package com.live;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.LruCache;

import com.base.util.L;
import com.http.service.HttpService;
import com.http.service.HttpServiceResponse;
import com.http.service.Response;
import com.http.service.ResponseException;
import com.io.StreamUtil;
import com.io.sava.FileUtil;
import com.live.entities.LiveEntity;
import com.live.provide.ChannelProvide;
import com.log.Logcat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestService implements HttpServiceResponse {
    private static final String TAG = "TestService";

    @Override
    public Response get(Context context, String url, Map<String, String> headers, Map<String, String> params, HttpService.IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        msg += "<p>We serve " + url + " !</p>";
        if (url.contains("play")) {
            Intent intent = new Intent();
            intent.setAction("play.activity.url");
            intent.putExtra("play", params.get("url"));
            context.sendBroadcast(intent);
        } else if (url.contains("log")) {
            File[] files = Logcat.logFiles(context);
            long t = System.currentTimeMillis();
            long n = 31 * 60 * 1000;
            StringBuffer stringBuffer=new StringBuffer();
            for (File file : files) {
                if (t - file.lastModified() > n)
                    continue;
                Log.d(TAG,""+file.lastModified());
                stringBuffer.append(FileUtil.file2string(file));
            }
            return HttpService.newFixedLengthResponse(stringBuffer.toString());
        }
//        ChannelProvide.putChannel(headers.get("body"),context);
        return HttpService.newFixedLengthResponse(msg + "</body></html>\n");
    }

    @Override
    public Response post(Context context, String url, Map<String, String> headers, Map<String, String> params, HttpService.IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        msg += "<p>We serve " + url + " !</p>";
        Intent intent = new Intent();
        intent.setAction("play.activity.url");
        System.out.println(params.get("url"));
        L.d(TAG, "收到消息");
        Map<String, String> map = new HashMap<>();
        try {
            session.parseBody(map);
            intent.putExtra("url", params.get("data"));
            if (url.contains("channel")) {
                ChannelProvide.putChannel(params.get("data"), context);
                context.sendBroadcast(intent);
            } else if (url.contains("type")) {
                ChannelProvide.putType(params.get("data"), context);
            } else if (url.contains("radio")) {
                ChannelProvide.putRadio(params.get("data"), context);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        return HttpService.newFixedLengthResponse(msg + "</body></html>\n");
    }

    @Override
    public Response other(Context context, String method, String url, Map<String, String> headers, Map<String, String> params, HttpService.IHTTPSession session) {
        return null;
    }
}
