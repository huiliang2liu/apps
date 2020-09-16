package com.threelogin;

import android.content.Context;

import com.http.Http;
import com.threelogin.weixin.WeixinLoginImpl;

public interface ILogin {
    void login(LoginListener loginListener);

    enum LoginType {
        WEIXIN,
    }

    class LoginBuilder {
        private Context context;
        private Http http;

        public LoginBuilder context(Context context) {
            this.context = context;
            return this;
        }

        public LoginBuilder http(Http http) {
            this.http = http;
            return this;
        }

        public ILogin build(LoginType type) {
            switch (type) {
                case WEIXIN:
                    return new WeixinLoginImpl(context, http);
            }
            return null;
        }
    }
}
