package com.threelogin.weixin;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import androidx.annotation.Nullable;

public class WeiXinCallbackActivity extends Activity implements IWXAPIEventHandler {
    WeiXinManager weiXinManager;

    {
        weiXinManager = WeiXinManager.getWeiXinManager();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (weiXinManager != null)
            weiXinManager.handleIntent(getIntent(), this);
    }

    @Override
    public final void onReq(BaseReq baseReq) {
        finish();
        if (weiXinManager != null)
            weiXinManager.onReq(baseReq);
    }

    @Override
    public final void onResp(BaseResp baseResp) {
        if (weiXinManager != null)
            weiXinManager.onResp(baseResp);
        finish();
    }
}
