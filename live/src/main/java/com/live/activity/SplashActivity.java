package com.live.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.animation.AnimatorFactory;
import com.base.animation.ViewEmbellish;
import com.base.util.PhoneInfo;

import com.result.permission.PermissionCallback;
import com.base.thread.PoolManager;
import com.base.util.L;
import com.http.Http;
import com.http.ResponseString;
import com.http.listen.ResponseStringListener;
import com.live.R;
import com.live.utils.PublicSp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    @butterknife.BindView(R.id.splash_iv1)
    ImageView splashIv1;
    @butterknife.BindView(R.id.splash_iv2)
    ImageView splashIv2;
    @butterknife.BindView(R.id.splash_fl1)
    FrameLayout splashFl1;
    @butterknife.BindView(R.id.splash_iv3)
    ImageView splashIv3;
    @butterknife.BindView(R.id.splash_iv4)
    ImageView splashIv4;
    @butterknife.BindView(R.id.splash_tv)
    TextView splashTv;
    @butterknife.BindView(R.id.splash_fl2)
    FrameLayout splashFl2;
    private int time = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        L.e(TAG, "" + getApplicationInfo().nativeLibraryDir);
        Http.RequestEntity requestEntity = new Http.RequestEntity();
        requestEntity.url = "http://ip-api.com/json?lang=zh-CN";
        result.requestPermissions(0, new PermissionCallback() {

            @Override
            public void result(String... failPermissions) {

            }
        }, result.check());
        requestEntity.stringListener = new ResponseStringListener() {
            @Override
            public void start() {

            }

            @Override
            public void failure() {
                L.d(TAG, "failure");
            }

            @Override
            public void success(ResponseString response) {
                L.d(TAG, "success");
                try {
                    JSONObject jsonObject = new JSONObject(response.response);
                    if (jsonObject.has("country"))
                        PublicSp.setCountry(SplashActivity.this, jsonObject.getString("country"));
                    if (jsonObject.has("query"))
                        PublicSp.setIp(SplashActivity.this, jsonObject.getString("query"));
                    if (jsonObject.has("city"))
                        PublicSp.setCity(SplashActivity.this, jsonObject.getString("city"));
                    if (jsonObject.has("regionName")) {
                        String region = jsonObject.getString("regionName");
                        region = region.replace("市", "");
                        region = region.replace("省", "");
                        PublicSp.setRegion(SplashActivity.this, region);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        http.getAsyn(requestEntity);
        butterknife.ButterKnife.bind(this);
        splashFl2.setVisibility(View.GONE);
        splashIv2.setVisibility(View.GONE);
        imageLoad.load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565629252335&di=8ace80b811d63924493526bfa695b265&imgtype=0&src=http%3A%2F%2Fpic5.nipic.com%2F20091224%2F3822085_091707089473_2.jpg", splashIv1, null);
        imageLoad.load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566223951&di=6b4fcefd1e7af8e8c0fc549bca545807&imgtype=jpg&er=1&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201806%2F20%2F20180620171603_JsPzu.jpeg", splashIv2, null);
        imageLoad.load("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3017181384,2124302140&fm=26&gp=0.jpg", splashIv3, null);
        imageLoad.load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565630062815&di=39b5bd620d15c658517b70e9f1f342a2&imgtype=0&src=http%3A%2F%2Fpic22.nipic.com%2F20120619%2F10088232_201543326147_2.jpg", splashIv4, null);
        PoolManager.runUiThread(new Runnable() {
            @Override
            public void run() {
                splashIv2.setVisibility(View.VISIBLE);
                ViewEmbellish embellish = new ViewEmbellish(splashIv2);
                embellish.alpha(0, 1);
            }
        }, PoolManager.SECOND);

        Http.RequestEntity entity = new Http.RequestEntity();
        entity.url = "http://mobile.fjtv.net/haibo4/channel_kantv.php?count=20&appkey=YS8UDU14TuhJ4JLpfuV49XSrxNykqihe&appid=8&client_type=iOS&device_token=cd9d42880795d0d6962453326b752b43&version=3.1.6&app_version=3.1.6&avos_device_token=cd9d42880795d0d6962453326b752b43&phone_models=iPhone%208&location_city=%E6%B7%B1%E5%9C%B3&latitude=22.551620&longitude=113.938587&locating_city=%E6%B7%B1%E5%9C%B3%E5%B8%82&language=Chinese&count=20&node_id=1";
//        entity.url = "http://ip-api.com/json?lang=zh-CN";
        String time1 = System.currentTimeMillis() + "SFzirW";
        String vserion = "3.1.6";
        String s1 = "fe9fc289c3ff0af142b6d3bead98a923&RzhGSFhlZFBnbDRpN3NBMnJmVUlTeGZhQjBOQjVXSkM=&" + vserion + "&" + time;
        String bytes = sha1(s1).replaceAll("\n", "");
        String sign = Base64.encodeToString(bytes.getBytes(), 0).replaceAll("\n", "");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 5.0.2; Redmi Note 3 MIUI/6.6.17) m2oSmartCity_142 1.0.0");
        map.put("X-API-TIMESTAMP", time1);
        map.put("X-API-SIGNATURE", sign);
        map.put("X-API-KEY", "fe9fc289c3ff0af142b6d3bead98a923");
        map.put("X-AUTH-TYPE", "sha1");
        map.put("X-API-VERSION", vserion);
        entity.heard = map;
        entity.stringListener = new ResponseStringListener() {
            @Override
            public void start() {

            }

            @Override
            public void failure() {

            }

            @Override
            public void success(ResponseString response) {
                L.e(TAG, "=====" + response.response);
            }
        };
        http.getAsyn(entity);
        PoolManager.runUiThread(new Runnable() {
            @Override
            public void run() {
                splashFl2.setVisibility(View.VISIBLE);
                ViewEmbellish embellish1 = new ViewEmbellish(splashFl1) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        splashFl1.setVisibility(View.GONE);
                    }

                    @Override
                    public float getInterpolation(float input) {
                        if (input <= 0.25) {
                            return 3 * input;
                        } else if (input <= 0.75) {
                            return (1 - input);
                        } else {
                            return 3 * input - 2;
                        }
                    }
                };
                ObjectAnimator animator = AnimatorFactory.alpha(embellish1, 1, 0, 300);
                animator.start();
                ViewEmbellish embellish2 = new ViewEmbellish(splashFl2);
                ObjectAnimator animator1 = AnimatorFactory.alpha(embellish2, 0, 1, 300);
                animator1.start();
                splashTv.setVisibility(View.GONE);
                PoolManager.runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        splashTv.setVisibility(View.VISIBLE);
                        ViewEmbellish ve = new ViewEmbellish(splashTv) {
                            @Override
                            public float getInterpolation(float input) {
                                return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
                            }
                        };
                        ve.topMargin((long) 3000, 0, -180, 180, 90);
                        splashTv.setText(String.valueOf(time));
                        PoolManager.runUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time--;
                                if (time <= 0) {
                                    splashTv.setVisibility(View.GONE);
                                    startMainActivity();
                                } else {
                                    splashTv.setText(String.valueOf(time));
                                    PoolManager.runUiThread(this, PoolManager.SECOND);
                                }
                            }
                        }, 1000);

                    }
                }, PoolManager.SECOND);
            }
        }, PoolManager.SECOND * 5 - 300);

        PoolManager.shortTime(new Runnable() {
            @Override
            public void run() {

                List<PhoneInfo.ARP> arps = PhoneInfo.getArp();
                Log.d("SplashActivity", "开始打印arp"+arps.size());
                if (arps == null || arps.size() <= 0) {
                    Log.d(TAG, "arp is null");
                    return;
                }
                for (PhoneInfo.ARP arp : arps)
                    Log.d(TAG, arp.toString());
            }
        });
    }

    public String sha1(String str) {
        try {
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");
            shaDigest.update(str.getBytes());
            return byteArrayToHex(shaDigest.digest());
        } catch (Throwable e) {
        }
        return "";
    }

    public String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("onTouchEvent", "onTouchEvent");
        return super.onTouchEvent(event);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
