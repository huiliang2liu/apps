package com.nibiru.studio.arscene;

import android.graphics.Color;

import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.studio.CalculateUtils;

import x.core.listener.IXActorEventListener;
import x.core.listener.IXModelLoadListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XModelActor;
import x.core.ui.XSkeletonActor;
import x.core.ui.XUI;
import x.core.util.XLog;

/**
 * 演示动态模型，动态模型需要使用Nibiru模型转换工具进行转换
 */

/**
 * Show the dynamic model, which should be converted by Nibiru model conversion tool
 */
public class SubSceneXSkeleton extends XBaseScene implements IXModelLoadListener {

    XSkeletonActor actor;
    XLabel tip;

    @Override
    public void onCreate() {
        init();
    }

    @Override
    public void onResume() {

        //在熄屏启屏时重新播放
        //Restart to play when the screen once off is started
        if( actor != null ){
            actor.restartPlay();
        }


    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void init() {

        XLabel titleLabel = new XLabel("Example: Skeleton");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.15f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        //初始化模型加载提示文本
        //Initialize model and load hint text
        tip = new XLabel("Prepare Loading Model...");
        tip.setAlignment(XLabel.XAlign.Center);
        tip.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        tip.setCenterPosition(0, 0f, CalculateUtils.CENTER_Z);
        tip.setTextColor(Color.WHITE);
        tip.setSize(0.08f, 0.02f);
        addActor(tip);

        //如果从SD卡加载只需指定nsm文件即可
        //If it's loaded from SD Card, the developer only needs to specify nsm file
//        actor = new XSkeletonActor("/sdcard/nsm/hanjiaohubeizhan.nsm");

        //初始化时可指定NSM路径，轨迹文件路径，是否开启循环
        //Nsm path, trail file, and whether to enable loop can be specified at initialization
//        actor = new XSkeletonActor("/sdcard/nsm/hanjiaohubeizhan.nsm", "/sdcard/nsm/trace_1.txt", true);

        //Skeleton默认支持SD卡加载，如果要通过Assets加载必须指定nsm文件及其所有资源文件列表
        //Skeleton supports SD Card loading by default, if it's required to load from Assets, nsm file and the corresponding resource file list should be specified

        //动态模型不再需要设置文件名
        actor = new XSkeletonActor("nsm/hanjiaohubeizhan.nsm", new String[]{"nsm/"}, XUI.Location.ASSETS);
//        actor = new XSkeletonActor("/system/etc/model/sence_light8_model/model_1.nsm", "/system/etc/model/sence_light8_model/trace_1.txt", true);

        //设置相对中心点的平移
        //Set translationg of the relative center point
        actor.setTranslate(0,-0.1f,-3f);

        //设置缩放比例
        //Set scaling factor
        actor.setAnimationScale(0.001f);

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
        //Set detection method for selection: cubic mode/spherical mode. Cubic mode is the default one
//        actor.setModelSelectionDetector(XModelActor.MODEL_SELECTION_DETECTOR.SPHERE);

        //为显示效果可以隐藏选中点
        //Hide Gaze point for display effects
        hideGaze();

        addActor(actor);

    }

    //模型开始加载
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
        if( keycode == NibiruKeyEvent.KEYCODE_ENTER ){
            if( actor.getModelSelectionDetector() == XModelActor.MODEL_SELECTION_DETECTOR.CUBE ){
                actor.setModelSelectionDetector(XModelActor.MODEL_SELECTION_DETECTOR.SPHERE);
            }else{
                actor.setModelSelectionDetector(XModelActor.MODEL_SELECTION_DETECTOR.CUBE);
            }
        }

//        if( keycode == NibiruKeyEvent.KEYCODE_ENTER ){
//            actor.setEnableSelection(!actor.isEnableSelection());
//        }

        return super.onKeyDown(keycode);
    }

}
