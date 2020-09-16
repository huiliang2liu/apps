package com.nibiru.studio.vrscene;

import android.graphics.Color;

import com.nibiru.service.NibiruKeyEvent;

import x.core.listener.IXActorEventListener;
import x.core.listener.IXModelLoadListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XModelActor;
import x.core.ui.XStaticModelActor;
import x.core.ui.XUI;
import x.core.util.XLog;

/**
 * 演示静态模型，目前支持Obj模型的显示
 */

/**
 * Show static model, Obj model is supported to display
 */
public class SubSceneXModel extends XBaseScene implements IXModelLoadListener {


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


    XStaticModelActor actor;
    XLabel tip;

    public void init() {

        XLabel titleLabel = new XLabel("Example: Model");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.5f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //初始化提示文本
        tip = new XLabel("Prepare Loading Model...");
        tip.setAlignment(XLabel.XAlign.Center);
        tip.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        tip.setCenterPosition(0, 0f, -4f);
        tip.setTextColor(Color.WHITE);
        tip.setSize(0.8f, 0.2f);
        addActor(tip);


        //静态OBJ模型默认支持SD卡加载
        //Static OBJ model supports SD Card loading by default
//        XStaticModelActor actor = new XStaticModelActor("/sdcard/nibiru_obj/cat.obj");

        //Assets加载，需要显示指定Location是XUI.Location.ASSETS
        //Assets loading, it should be displayed that Location is specified as XUI.Location.ASSETS
        actor = new XStaticModelActor("obj/cat.obj", XUI.Location.ASSETS);

        //设置模型缩放比例，可选
        //Set model scaling factor, optional
        actor.setStaticModelScale(0.8f);

        //设置模型相对中心点的偏移量
        //Set the translate offest of model to the center point
        actor.setStaticModelTranslate(0,-2,-10);

        //平移后设置旋转中心
        //Set rotation center after the translation
        actor.setRotationCenter(0, -2, -10);



        //设置模型加载监听
        //Set listener for model loading
        actor.setModelLoadListener(this);

        //设置模型选中监听（可选）
        //Set listener for model selection (optional)
        actor.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {
                XLog.logInfo("on gaze enter: "+actor.getActorDesc());
            }

            @Override
            public void onGazeExit(XActor actor) {
                XLog.logInfo("on gaze exit: "+actor.getActorDesc());
            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                XLog.logInfo("on gaze trigger: "+actor.getActorDesc());
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        //设置选中检测方式：立方体模式/球体模式，默认为立方体模式
        //Set the detection method for selection: cubic mode/spherical mode. Cubic mode is the default one
//        actor.setModelSelectionDetector(XModelActor.MODEL_SELECTION_DETECTOR.SPHERE);

        //为显示效果可以隐藏选中点
        //Hide Gaze point for display effects
        hideGaze();

        addActor(actor);

    }

    //旋转模型
    //Rotation model
    public void update(float delta){
        super.update(delta);

        //如果尚未加载完成，不进行旋转
        //If it's not loaded, the rotation won't start
        if( actor.getModelLoadState() != XModelActor.MODEL_LOAD_STATE.COMPLETED )return;

        if(actor != null && actor.isCreated()) {
            //绕Y轴旋转
            //Rotate around y axis
            //每秒转15角度(FPS60)
            //Rotate 15 degree per second (FPS60)
            actor.rotationByY(15 / 60.0f);
        }
    }

    //开始加载模型
    //Start to load model
    @Override
    public void onStartLoad(XModelActor actor) {
        if( tip != null ){
            tip.setTextContent("Loading Model...wait...");
        }
    }

    //模型加载完成
    //The model loading is done
    @Override
    public void onLoadCompleted(XModelActor actor) {
        if( tip != null ){
            tip.setEnabled(false);
        }
    }

    //模型加载失败
    //Fail to load the model
    @Override
    public void onLoadFailed(XModelActor actor) {
        if( tip != null ){
            tip.setTextContent("Ops! Load Model Failed. Please check the file path and location");
        }
    }

    public boolean onKeyDown(int keycode){
//        if( keycode == NibiruKeyEvent.KEYCODE_ENTER ){
//            if( actor.getModelSelectionDetector() == XModelActor.MODEL_SELECTION_DETECTOR.CUBE ){
//                actor.setModelSelectionDetector(XModelActor.MODEL_SELECTION_DETECTOR.SPHERE);
//            }else{
//                actor.setModelSelectionDetector(XModelActor.MODEL_SELECTION_DETECTOR.CUBE);
//            }
//        }

        if( keycode == NibiruKeyEvent.KEYCODE_ENTER ){
            actor.setEnableSelection(!actor.isEnableSelection());
        }

        return super.onKeyDown(keycode);
    }
}
