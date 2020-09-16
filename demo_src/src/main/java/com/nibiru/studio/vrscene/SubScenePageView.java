package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.graphics.Color;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.group.XActorPageView;

/**
 * 显示PageView的三个基本布局，凸形，平面和凹形
 */

/**
 * Show three basic layouts of PageView: convex, falt and concave
 */

public class SubScenePageView extends XBaseScene {
    XImageText concave, convex, flat;

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

        XImage xImage = new XImage("black.png");
        xImage.setAlpha(0.8f);
        xImage.setSize(5, 1.8f);
        xImage.setCenterPosition(0, 0, -4);
        xImage.setRenderOrder(2);
        addActor(xImage);

        XLabel titleLabel = new XLabel("PageView");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.2f, -4);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        concave = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        concave.setTitle("Concave", "Concave");
        concave.setName("concave");
        concave.setSizeOfImage(0.5f, 0.5f);
        concave.setSize(0.5f, 0.5f);
        concave.setSizeOfTitle(0.4f, 0.2f);
        concave.setTitleColor(Color.RED, Color.WHITE);
        concave.setTitlePosition(0, -0.4f);
        concave.setEventListener(itemActorListener);
        concave.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        concave.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        concave.setCenterPosition(0, 0, -4);
        addActor(concave);

        convex = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        convex.setTitle("Convex", "Convex");
        convex.setName("convex");
        convex.setSizeOfImage(0.5f, 0.5f);
        convex.setSize(0.5f, 0.5f);
        convex.setSizeOfTitle(0.4f, 0.2f);
        convex.setTitleColor(Color.RED, Color.WHITE);
        convex.setTitlePosition(0, -0.4f);
        convex.setEventListener(itemActorListener);
        convex.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        convex.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        convex.setCenterPosition(-1.8f, 0, -4);
        addActor(convex);

        flat = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        flat.setTitle("Flat", "Flat");
        flat.setName("flat");
        flat.setSizeOfImage(0.5f, 0.5f);
        flat.setSize(0.5f, 0.5f);
        flat.setSizeOfTitle(0.4f, 0.2f);
        flat.setTitleColor(Color.RED, Color.WHITE);
        flat.setTitlePosition(0, -0.4f);
        flat.setEventListener(itemActorListener);
        flat.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        flat.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        flat.setCenterPosition(1.8f, 0, -4);
        addActor(flat);

    }

    IXActorEventListener itemActorListener = new IXActorEventListener() {
        @Override
        public void onGazeEnter(XActor actor) {

        }

        @Override
        public void onGazeExit(XActor actor) {

        }

        //根据选中点点击的Actor决定用哪种布局显示PageView
        //Use a layout to show PageView according to the Actor clicked by Gaze point
        @Override
        public boolean onGazeTrigger(XActor actor) {
            String name = actor.getName();

            //设置Intent参数
            //Set Intent parameter
            Intent intent = new Intent(SubScenePageView.this, SubScenePageViewShow.class);

            if (name.equals("concave")) {
                intent.putExtra("type", XActorPageView.PageViewDefaultType.CONCAVE.ordinal());
            } else if (name.equals("convex")) {
                intent.putExtra("type", XActorPageView.PageViewDefaultType.CONVEX.ordinal());
            } else if (name.equals("flat")) {
                intent.putExtra("type", XActorPageView.PageViewDefaultType.FLAT.ordinal());
            }

            //启动具体PageView的Scene
            //Enable specific PageView Scene
            startScene(intent);

            return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }
    };
}
