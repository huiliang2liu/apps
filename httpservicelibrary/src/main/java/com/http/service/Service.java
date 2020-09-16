package com.http.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

class Service extends HttpService {
    private static final String TAG = "Service";
    private final static String HOST_NAME = "http_service_host_name";
    private final static String PORT = "http_service_port";
    public final static String H_S_R = "http_service_response";
    private HttpServiceResponse response;
    private Context context;

    protected Service(Context context) {
        super(getHostname(context), getPort(context));
        Log.d(TAG, "开启服务");
        this.context = context;
        try {
            Class cl = Class.forName(context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString(H_S_R));
            if (HttpServiceResponse.class.isAssignableFrom(cl)) {
                response = (HttpServiceResponse) cl.newInstance();
                start();
            } else {
                Log.d(TAG, "相应类型不对");
            }

        } catch (Exception e) {
            Log.d(TAG, "开启服务失败", e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (response != null) {
            String uri = session.getUri();
            String method = session.getMethod().toString();
            Log.d(TAG, String.format("接收到请求：uri=%s,method=%s", uri, method));
            if ("GET".equals(method))
                return response.get(context, uri, session.getHeaders(), session.getParms(), session);
            if ("POST".equals(method))
                return response.post(context, uri, session.getHeaders(), session.getParms(), session);
            return response.other(context, method, uri, session.getHeaders(), session.getParms(), session);
        }
        return super.serve(session);
    }

    @Override
    public void stop() {
        super.stop();
        Log.d(TAG, "停止服务");
    }

    private static String getHostname(Context context) {
        String hostName = null;
        try {
            hostName = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString(HOST_NAME, "");
        } catch (Exception e) {
        }
        return hostName == null || hostName.isEmpty() ? null : hostName;
    }

    private static int getPort(Context context) {
        int port = 0;
        try {
            port = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getInt(PORT, 8050);
        } catch (Exception e) {
        }
        return port;
    }
}
