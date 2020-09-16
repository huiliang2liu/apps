package com.threelogin.weixin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.utils.ILog;
import com.threelogin.LoginListener;

class WeiXinManager implements IWXAPI, IWXAPIEventHandler {
    public static WeiXinManager mWeiXinManager;
    protected LoginListener loginListener;
    private IWXAPI iwxapi;
    private WeixinLoginImpl login;

    public static WeiXinManager init(Context context, String appId, WeixinLoginImpl login) {
        if (mWeiXinManager == null) {
            synchronized (WeiXinManager.class) {
                if (mWeiXinManager == null)
                    mWeiXinManager = new WeiXinManager(context, appId, login);
            }
        }
        return mWeiXinManager;
    }

    void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    private WeiXinManager(Context context, String appId, WeixinLoginImpl login) {
        iwxapi = WXAPIFactory.createWXAPI(context, appId);
        iwxapi.registerApp(appId);
        this.login = login;
    }

    public static WeiXinManager getWeiXinManager() {
        return mWeiXinManager;
    }

    @Override
    public boolean registerApp(String s) {
        return iwxapi.registerApp(s);
    }

    @Override
    public boolean registerApp(String s, long l) {
        return iwxapi.registerApp(s, l);
    }

    @Override
    public void unregisterApp() {
        iwxapi.unregisterApp();
    }

    @Override
    public boolean handleIntent(Intent intent, IWXAPIEventHandler iwxapiEventHandler) {
        return iwxapi.handleIntent(intent, iwxapiEventHandler);
    }

    @Override
    public boolean isWXAppInstalled() {
        return iwxapi.isWXAppInstalled();
    }

    @Override
    public int getWXAppSupportAPI() {
        return iwxapi.getWXAppSupportAPI();
    }

    @Override
    public boolean openWXApp() {
        return iwxapi.openWXApp();
    }

    @Override
    public boolean sendReq(BaseReq baseReq) {
        return iwxapi.sendReq(baseReq);
    }

    @Override
    public boolean sendResp(BaseResp baseResp) {
        return iwxapi.sendResp(baseResp);
    }

    @Override
    public void detach() {
        iwxapi.detach();
    }

    @Override
    public void setLogImpl(ILog iLog) {
        iwxapi.setLogImpl(iLog);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("Login", "onReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int code = baseResp.errCode;
        Log.e("Login", "onResp");
        if (code == BaseResp.ErrCode.ERR_OK) {
            if (baseResp instanceof SendAuth.Resp) {
                login.successCode(((SendAuth.Resp) baseResp).code);
            } else {
                if (loginListener != null)
                    loginListener.onLoginFailure();
            }
        } else {
            if (loginListener != null)
                loginListener.onLoginFailure();
            if (code == BaseResp.ErrCode.ERR_USER_CANCEL) {
                Log.e("Login", "用户取消");
            } else {
                Log.e("Login", "用户拒绝授权");
            }
        }
    }
}
