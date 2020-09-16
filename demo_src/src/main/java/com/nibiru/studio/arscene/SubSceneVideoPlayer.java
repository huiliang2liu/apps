package com.nibiru.studio.arscene;

import android.content.Intent;
import android.graphics.Color;


import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.arscene.SubSceneVideo.SubSceneVideoAll2;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XImageText;
import x.core.ui.XLabel;


/**
 * 初始进入场景
 *
 * @author cao.hao
 */
public class SubSceneVideoPlayer extends BaseScene {
    XImageText imageText_2dnormal, imageText_3dnormal;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init() {

        XLabel titleLabel = new XLabel("VideoVRPlayer");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.10f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        imageText_2dnormal = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        imageText_2dnormal.setTitle("2DNormal", "2DNormal");
        imageText_2dnormal.setName("2DNormal");
        imageText_2dnormal.setSizeOfImage(0.1f, 0.1f);
        imageText_2dnormal.setSize(0.1f, 0.1f);
        imageText_2dnormal.setSizeOfTitle(0.1f, 0.03f);
        imageText_2dnormal.setTitleColor(Color.RED, Color.WHITE);
        imageText_2dnormal.setTitlePosition(0, -0.1f);
        imageText_2dnormal.setEventListener(itemActorListener);
        imageText_2dnormal.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        imageText_2dnormal.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        imageText_2dnormal.setCenterPosition(-0.12f, 0, CalculateUtils.CENTER_Z);
        addActor(imageText_2dnormal);

        imageText_3dnormal = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        imageText_3dnormal.setTitle("3DNormal", "3DNormal");
        imageText_3dnormal.setName("3DNormal");
        imageText_3dnormal.setSizeOfImage(0.1f, 0.1f);
        imageText_3dnormal.setSize(0.1f, 0.1f);
        imageText_3dnormal.setSizeOfTitle(0.1f, 0.03f);
        imageText_3dnormal.setTitleColor(Color.RED, Color.WHITE);
        imageText_3dnormal.setTitlePosition(0, -0.1f);
        imageText_3dnormal.setEventListener(itemActorListener);
        imageText_3dnormal.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        imageText_3dnormal.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        imageText_3dnormal.setCenterPosition(0.12f, 0, CalculateUtils.CENTER_Z);
        addActor(imageText_3dnormal);

    }

    IXActorEventListener itemActorListener = new IXActorEventListener() {
        @Override
        public void onGazeEnter(XActor actor) {

        }

        @Override
        public void onGazeExit(XActor actor) {

        }

        @Override
        public boolean onGazeTrigger(XActor actor) {
            Intent starter = new Intent(SubSceneVideoPlayer.this, SubSceneVideoAll2.class);
            if(actor.getName().equals("3DNormal")) {
                starter.putExtra(SubSceneVideoAll2.PLAY_MODE, SubSceneVideoAll2.NORMAL_3D);
            }
            startScene(starter);

            return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }

    };

}
