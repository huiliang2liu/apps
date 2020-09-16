package com.threelogin.weixin;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.http.Http;
import com.http.ResponseString;
import com.http.listen.ResponseStringListener;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.utils.ILog;
import com.threelogin.ILogin;
import com.threelogin.LoginListener;
import com.threelogin.UserEntity;

import org.json.JSONObject;

public class WeixinLoginImpl implements ILogin, ILog {
    private static final String TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private static final String USER_INFO_URL = "https://api.weixin.qq.com//sns/userinfo?access_token=%s&openid=%s";
    private static final String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";
    private static final String TAG = "WeixinLoginImpl";
    private static final String MAT = "TAG:%s,MSG:%s";
    private static final String WEI_XIN_APPIP = "wei_xin_appid";
    private static final String WEI_XIN_SECRET = "wei_xin_secret";
    private static final String CALLBACK_ACTIVITY = "WXEntryActivity";
    private String weiAppId = "";
    private String weiSecret = "";
    private boolean back = false;
    private WeiXinManager weiXinManager;
    private Http http;
    private String code;
    private LoginListener loginListener;
    private String accessToken;
    private String refreshToken;
    private String openId;

    public WeixinLoginImpl(Context context, Http http) {
        String packageName = context.getPackageName();
        try {
            ApplicationInfo aI = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            weiAppId = aI.metaData.getString(WEI_XIN_APPIP);
            weiSecret = aI.metaData.getString(WEI_XIN_SECRET);
            if (weiAppId != null && !weiAppId.isEmpty()) {
                weiXinManager = WeiXinManager.init(context, weiAppId, this);
                weiXinManager.setLogImpl(this);
            }
        } catch (Exception e) {
            weiAppId = "";
        }
        this.http = http;
        try {
            Class.forName(String.format("%s.wxapi.%s", packageName, CALLBACK_ACTIVITY));
            back = true;
        } catch (Exception e) {
            back = false;
        }
    }

    @Override
    public void login(LoginListener loginListener) {
        if (weiAppId == null || weiAppId.isEmpty()) {
            Log.e("Login", "Login error weiAppId is empty");
            if (loginListener != null)
                loginListener.onLoginFailure();
            return;
        }
        if (weiSecret == null || weiSecret.isEmpty()) {
            Log.e("Login", "Login error appSecret is empty");
            if (loginListener != null)
                loginListener.onLoginFailure();
            return;
        }
        if (!back) {
            Log.e("Login", "you callback activity to WeiXinCallbackActivity");
            if (loginListener != null)
                loginListener.onLoginFailure();
            return;
        }
        if (!weiXinManager.isWXAppInstalled()) {
            Log.e("Login", "uninstall weiXinApp");
            return;
        }
        this.loginListener = loginListener;
        if (accessToken != null && !accessToken.isEmpty()) {
            getUserInfo();
        } else {
            weiXinManager.setLoginListener(loginListener);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            boolean send = weiXinManager.sendReq(req);
            Log.e(TAG, String.format("请求状态:%s", send));
            if (!send) {
                if (loginListener != null)
                    loginListener.onLoginFailure();
            }
        }
    }

    @Override
    public void v(String s, String s1) {
        Log.v(TAG, String.format(MAT, s, s1));
    }

    @Override
    public void d(String s, String s1) {
        Log.d(TAG, String.format(MAT, s, s1));
    }

    @Override
    public void i(String s, String s1) {
        Log.i(TAG, String.format(MAT, s, s1));
    }

    @Override
    public void w(String s, String s1) {
        Log.w(TAG, String.format(MAT, s, s1));
    }

    @Override
    public void e(String s, String s1) {
        Log.e(TAG, String.format(MAT, s, s1));
    }

    public void successCode(String code) {
        this.code = code;
        getToken();
    }

    public void getUserInfo() {
        Http.RequestEntity entity = new Http.RequestEntity();
        entity.url = String.format(USER_INFO_URL, accessToken, openId);
        entity.stringListener = new ResponseStringListener() {
            @Override
            public void start() {

            }

            @Override
            public void failure() {
                Log.e("Login", "get weiXin userInfo failure");
                if (loginListener != null)
                    loginListener.onLoginFailure();
            }

            @Override
            public void success(ResponseString response) {
                Log.e("Login", "" + response.response);
                try {
                    JSONObject jsonObject = new JSONObject(response.response);
                    if (jsonObject.has("nickname")) {
                        UserEntity userEntity = new UserEntity();
                        userEntity.headimgurl = jsonObject.optString("headimgurl", "");
                        userEntity.id = jsonObject.optString("unionid", "");
                        userEntity.nickname = jsonObject.optString("nickname", "");
                        userEntity.sex = jsonObject.optInt("", 1) == 1 ? "男" : "女";
                        if (loginListener != null)
                            loginListener.onLoginSuccess(userEntity);
                    } else {
                        Log.e("Login", "get userInfo error");
                        refreshToken();
                    }
                } catch (Exception e) {
                    Log.e("Login", "get userInfo pars error");
                    if (loginListener != null)
                        loginListener.onLoginFailure();
                }
            }
        };
        http.getAsyn(entity);
    }

    public void refreshToken() {
        Http.RequestEntity entity = new Http.RequestEntity();
        entity.url = String.format(REFRESH_TOKEN, weiAppId, refreshToken);
        entity.stringListener = new ResponseStringListener() {
            @Override
            public void start() {

            }

            @Override
            public void failure() {
                Log.e("Login", "refresh weiXin token failure");
                if (loginListener != null)
                    loginListener.onLoginFailure();
            }

            @Override
            public void success(ResponseString response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.response);
                    if (jsonObject.has("access_token")) {
                        accessToken = jsonObject.optString("access_token", "");//接口调用凭证
                        openId = jsonObject.optString("openid", "");//授权用户唯一标识
                        refreshToken = jsonObject.optString("refresh_token", "");
                        getUserInfo();
                    } else {
                        Log.e("Login", "refresh weiXin token error");
                        login(loginListener);
                    }
                } catch (Exception e) {
                    Log.e("Login", "refresh pars  error");
                    if (loginListener != null)
                        loginListener.onLoginFailure();
                }
            }
        };
        http.getAsyn(entity);
    }

    public void getToken() {
        Http.RequestEntity entity = new Http.RequestEntity();
        entity.url = String.format(TOKEN_URL, weiAppId, weiSecret, code);
        entity.stringListener = new ResponseStringListener() {
            @Override
            public void start() {

            }

            @Override
            public void failure() {
                Log.e("Login", "get weiXin token failure");
                if (loginListener != null)
                    loginListener.onLoginFailure();
            }

            @Override
            public void success(ResponseString response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.response);
                    if (jsonObject.has("access_token")) {
                        accessToken = jsonObject.optString("access_token", "");//接口调用凭证
                        openId = jsonObject.optString("openid", "");//授权用户唯一标识
                        refreshToken = jsonObject.optString("refresh_token", "");
                        getUserInfo();
                    } else {
                        Log.e("Login", "get weiXin token error");
                        login(loginListener);
                    }
                } catch (Exception e) {
                    Log.e("Login", "get pars  error");
                    if (loginListener != null)
                        loginListener.onLoginFailure();
                }
            }
        };
        http.getAsyn(entity);
    }
}
