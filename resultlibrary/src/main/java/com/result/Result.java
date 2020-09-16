package com.result;


import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;

import com.result.activity.IResult;
import com.result.activity.ResultCallback;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import androidx.annotation.NonNull;

/**
 * com.result
 * 2018/10/26 16:07
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public interface Result extends IResult, com.result.permission.IResult {
    /**
     * 2019/1/8 10:50
     * annotation：从相册中选择照片
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void photo2PhotoAlbum(ResultCallback... callback);

    /**
     * 2019/1/8 10:50
     * annotation：打开照相机
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void openCamera(@NonNull File file, String authority, ResultCallback... callback);

    /**
     * 2019/1/8 10:50
     * annotation：截图
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void screenshots(@NonNull File imageFile, String authorityImage, @NonNull File saveFile, String authoritySave, int aspectX, int aspectY, int outputX,
                     int outputY, ResultCallback... callback);

    /**
     * 2019/1/8 10:50
     * annotation：截图
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    void screenshots(@NonNull Uri imageUri, @NonNull Uri saveUri, int aspectX, int aspectY, int outputX,
                     int outputY, ResultCallback... callback);

    class Build {
        private ResultImpl result;

        public Build(@NonNull Activity activity) {
            result = new ResultImpl(activity);
        }

        public Build(@NonNull Fragment fragment) {
            result = new ResultImpl(fragment);
        }

        public Build(@NonNull androidx.fragment.app.Fragment fragment) {
            result = new ResultImpl(fragment);
        }

        public Result build() {
            return (Result) Proxy.newProxyInstance(Build.class.getClassLoader(), new Class[]{Result.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return method.invoke(result, args);
                }
            });
        }
    }
}
