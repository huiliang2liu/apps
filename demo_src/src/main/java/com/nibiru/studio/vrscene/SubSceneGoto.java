package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.graphics.Color;

import com.nibiru.studio.TestActivity;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.util.NibiruStudioUtils;

/**
 * 演示Scene跳转，包括Scene之间跳转，Scene往Activity跳转，Scene之间跳转带有返回值，Scene与Activity跳转带返回值
 */

/**
 * Show Scene jumping, including jumping between Scenes, jumping from Scene to general(2D) Activity, jumping between Scenes with return value and jumping from Scene to Activity with return value
 */
public class SubSceneGoto extends XBaseScene implements IXActorEventListener {

    XLabel mStartLabel;
    XLabel mStartSettingLabel;
    XLabel mStartForActivityResultLabel;
    XLabel mStartForSceneResultLabel;
    XLabel mResultLabel;

    @Override
    public void onCreate() {
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

    public void init() {


        XLabel titleLabel = new XLabel("Example：Goto");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 2.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //启动应用内部的TestActivity，演示Scene与Activity跳转
        //Launch the app internal TestActivity, demo Scene and Activity jumping
        mStartLabel  = new XLabel("Click to Start TestActivity");
        mStartLabel.setCenterPosition(0, 1.0f, -4f);
        mStartLabel.setAlignment(XAlign.Center);
        mStartLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        mStartLabel.setSize(3.8f, 0.25f);
        mStartLabel.setEventListener(this);

        addActor(mStartLabel);

        //演示通过包名启动其他应用的Activity
        //Demon launches Activity of other apps through package names
        mStartSettingLabel  = new XLabel("Click to Start OS Settings");
        mStartSettingLabel.setAlignment(XAlign.Center);
        mStartSettingLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        mStartSettingLabel.setCenterPosition(0, 0.5f, -4f);
        mStartSettingLabel.setSize(3.8f, 0.25f);
        mStartSettingLabel.setEventListener(this);

        addActor(mStartSettingLabel);

        //演示启动Activity获取结果返回值
        //Launch Activity and get return value of result
        mStartForActivityResultLabel  = new XLabel("Click to Start for Activity Result");
        mStartForActivityResultLabel.setAlignment(XAlign.Center);
        mStartForActivityResultLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        mStartForActivityResultLabel.setCenterPosition(0, 0.0f, -4f);
        mStartForActivityResultLabel.setSize(3.8f, 0.25f);
        mStartForActivityResultLabel.setEventListener(this);

        addActor(mStartForActivityResultLabel);

        //演示启动Scene获取结果返回值
        //Launch Scene and get return value of result
        mStartForSceneResultLabel  = new XLabel("Click to Start for Scene Result");
        mStartForSceneResultLabel.setAlignment(XAlign.Center);
        mStartForSceneResultLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        mStartForSceneResultLabel.setCenterPosition(0, -0.5f, -4f);
        mStartForSceneResultLabel.setSize(3.8f, 0.25f);
        mStartForSceneResultLabel.setEventListener(this);

        addActor(mStartForSceneResultLabel);

        mResultLabel  = new XLabel("");
        mResultLabel.setAlignment(XAlign.Center);
        mResultLabel.setArrangementMode(XArrangementMode.SingleRowMove);
        mResultLabel.setCenterPosition(0, -1.5f, -4f);
        mResultLabel.setSize(5.8f, 0.2f);

        addActor(mResultLabel);

    }

    @Override
    public void onGazeEnter(XActor actor) {

    }

    @Override
    public void onGazeExit(XActor actor) {

    }

    @Override
    public boolean onGazeTrigger(XActor actor) {
        //获取Utils实例
        //Get Utils instance
        NibiruStudioUtils utils = getNibiruStudioUtils();

        //演示通过包名启动其他应用的Activity
        //Launch Activity in other apps through packge names
        if( actor == mStartLabel ) {
            //通过class获取Intent
            //Get Intent through class
            Intent intent = utils.getInternalActivityIntent(TestActivity.class);

            //intent可带参数
            //Intent can go with parameters
            intent.putExtra("param", "start activity param");

            //启动应用内部的Activity
            //Launch the app internal Activity
            utils.startInternalActivity(intent);

            return true;
        }
        //演示通过包名启动其他应用的Activity
        //Launch Activity in other apps through package names
        else if( actor == mStartSettingLabel ){

            //通过应用包名获取intent，获取后可加入参数
            //Get Intent through package names, after which parameters can be added
            Intent intent = utils.getAppIntent("com.android.nibiru.settings.vr");

            //如果无需配置参数，可以直接调用startApp(packageName)方法
            //If there's no need to configure parameters, directly call startApp(packagename)
            utils.startApp(intent);

            return true;
        }
        //演示启动Activity获取结果返回值
        //Launch Activity and get result return value
        else if( actor == mStartForActivityResultLabel ){

            Intent intent = utils.getInternalActivityIntent(TestActivity.class);

            //intent可带参数
            //Intent can go with parameters
            intent.putExtra("param", "start activity for result param");

            intent.putExtra("request", 500);

            //打开带有返回值的Activity，参数为intent和requestCode
            //Open Activity with return value, parameters are intent and requestCode
            utils.startActivityForResult(intent, 500);

            return true;
        }
        //演示启动Scene获取结果返回值
        //Launch Scene and get result return value
        else if( actor == mStartForSceneResultLabel ){

            Intent intent = new Intent( this, SubSceneForResult.class);
            intent.putExtra("param", "start scene for result param");

            //打开带有返回值的Scene，参数为intent和requestCode
            //Open Scene with return value, parameters are intent and requestCode
            startSceneForResult(intent, 600);

        }

        return false;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    //在带有返回值的Scene返回后自动回调，回调在onResume之前
    //Call back automatically when the Scene with return value is returned, the callback is before onResume
    @Override
    public void onSceneResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onSceneResult(requestCode, resultCode, intent);

        //请求来自于Activity的返回结果
        //The request is from the return result of Activity
        if( requestCode == 500 ){
            //如果是对控件操作一定要放入Render线程中执行
            //If it's control operation, it must be proceeded in Render thread
            runOnRenderThread(new Runnable() {
                @Override
                public void run() {
                    if( mResultLabel != null ){
                        mResultLabel.setTextContent("Request Code: "+requestCode+" Result Code: "+resultCode+" Intent: "+(intent != null ? intent.getStringExtra("result") : "null"));
                    }
                }
            });
        }
        //请求来自于Scene的返回结果
        //The request is from the return result of Scene
        else if( requestCode == 600 ){
            //如果是对控件操作一定要放入Render线程中执行
            //If it's control operation, it must be proceeded in Render thread
            runOnRenderThread(new Runnable() {
                @Override
                public void run() {
                    if( mResultLabel != null ){
                        mResultLabel.setTextContent("Request Code: "+requestCode+" Result Code: "+resultCode+" Intent: "+(intent != null ? intent.getStringExtra("result") : "null"));
                    }
                }
            });
        }
    }
}
