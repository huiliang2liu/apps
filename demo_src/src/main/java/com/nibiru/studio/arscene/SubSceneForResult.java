package com.nibiru.studio.arscene;

import android.content.Intent;
import android.graphics.Color;

import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.studio.CalculateUtils;

import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;

/**
 * 演示Scene之间传递参数和设置返回值
 * SubSceneForResult由SubSceneGoto跳转过来，从Intnet获取参数，并且通过调用setResult设置返回值
 */

/**
 * Show passing parameters between Scenes and set return value
 * SubSceneForResult is jumped from SubSceneGoto, gets parameters from Intent, and set return value through calling setResult
 */
public class SubSceneForResult extends BaseScene {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    int requestCode = 0;
    public void init() {

        XLabel titleLabel = new XLabel("Example: Scene For Result");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.20f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        //从请求的Scene获取参数
        //Get parameters from the request Scene
        requestCode = getIntent()!= null ? getIntent().getIntExtra("request", 0) : 0;
        XLabel label  = new XLabel("Get Param from Scene: "+( getIntent()!= null ? getIntent().getStringExtra("param") : "null" ));
        label.setCenterPosition(0, 0.1f, CalculateUtils.CENTER_Z);
        label.setAlignment(XAlign.Center);
        label.setArrangementMode(XArrangementMode.SingleRowNotMove);
        label.setSize(0.38f, 0.025f);

        addActor(label);

        label  = new XLabel("Press OK to give result and back");
        label.setAlignment(XAlign.Center);
        label.setArrangementMode(XArrangementMode.SingleRowNotMove);
        label.setCenterPosition(0, 0.05f, CalculateUtils.CENTER_Z);
        label.setSize(0.38f, 0.025f);

        addActor(label);

    }

    public boolean onKeyDown(int keyCode){
        if( keyCode == NibiruKeyEvent.KEYCODE_ENTER ){

            //设置Result结果给请求的Scene
            //Set Result to the request Scene
            Intent data = new Intent();
            data.putExtra("result", "result is ok from scene");
            //第一个参数为resultCode，第二个参数为Intent存放数据
            //The first parameter is resultCode, the second is Intent storing data
            setResult(601, data);

            //设置result后finish
            //Set result and call finish
            finish();

            return true;
        }

        return super.onKeyDown(keyCode);
    }


}
