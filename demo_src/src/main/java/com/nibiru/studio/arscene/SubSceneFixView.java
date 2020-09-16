package com.nibiru.studio.arscene;

import android.graphics.Color;

import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.studio.CalculateUtils;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XImage;
import x.core.ui.XLabel;

/**
 * 演示控件跟随选中点功能：点击图片开启图片跟随模式，图片会跟随选中点移动，按Back键取消跟随
 * 演示重置/取消所有控件位置的功能：在任意位置按OK键将所有控件重置到选中点为中心的区域内，点击Cancel文本可以取消重置效果
 */

/**
 * Show actor following Gaze point: click image to enable follow mode, and the image will follow the Gaze moving, press Back to cancel the follow
 * Show actor reset/cancel all the control positions: press OK in any position and reset all the controls to the area taking Gaze point as the center, click Cancel text to cancel the reset effect
 */
public class SubSceneFixView extends BaseScene {

    XImage xImage;

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

    public void init() {

        XLabel titleLabel = new XLabel("Example：FixView");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.10f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        XLabel followGazeTip = new XLabel("Click Image to Follow Gaze, Press Back to Stop");
        followGazeTip.setAlignment(XLabel.XAlign.Center);
        followGazeTip.setCenterPosition(0f, 0.06f, CalculateUtils.CENTER_Z);
        followGazeTip.setSize(0.46f, 0.02f);
        addActor(followGazeTip);


        xImage = new XImage("ic_image_focused.png");
        xImage.setSize(0.06f, 0.06f);
        xImage.setCenterPosition(0, 0.0f, CalculateUtils.CENTER_Z);
        xImage.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                //判断控件当前是否正在跟随选中点，如果没有，设置为跟随
                //Judge whether the control is currently following the selected point, if not, set to follow
                if( !actor.isActorFollowGaze() ){
                    actor.setActorFollowGaze(true);
                }
                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        xImage.setRenderOrder(6);
        addActor(xImage);

        XLabel fixviewTip = new XLabel("Sample: Re-locate UIs ( Press OK to Show ) ");
        fixviewTip.setAlignment(XLabel.XAlign.Center);
        fixviewTip.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        fixviewTip.setCenterPosition(0, -0.1f, CalculateUtils.CENTER_Z);
        fixviewTip.setTextColor(Color.WHITE);
        fixviewTip.setSize(0.26f, 0.02f);
        addActor(fixviewTip);


        XLabel fixviewCancelTip = new XLabel("Click to Cancel Re-locate");
        fixviewCancelTip.setAlignment(XLabel.XAlign.Center);
        fixviewCancelTip.setCenterPosition(0f, -0.14f, CalculateUtils.CENTER_Z);
        fixviewCancelTip.setSize(0.36f, 0.02f);
        fixviewCancelTip.setEnableGazeAnimation(true);
        fixviewCancelTip.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                //取消通过OK键重置的控件位置，也就是取消fixedRotateView操作
                //Cancel control position reset by OK button, i.e. cancel fixedRotateView operation
                cancelRotateView();
                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        addActor(fixviewCancelTip);

    }

    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == NibiruKeyEvent.KEYCODE_ENTER) {
            //将所有控件重定位到选中点所在位置
            //Reset all the controls to the Gaze position
            fixedRotateView();

            return true;
        }else if( keyCode == NibiruKeyEvent.KEYCODE_BACK ){
            //如果Image控件正在跟随选中点，则取消跟随
            //If Image control is following Gaze point, cancel the follow
            if( xImage != null && xImage.isActorFollowGaze() ){
                xImage.setActorFollowGaze(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode);
    }

}

