package com.nibiru.studio.arscene;


import android.content.Intent;
import android.graphics.Color;

import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.arscene.GridView.GridViewScene;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.group.XActorPageView;

/**
 * 选择GridView布局的类型，与PageView类似，凸形，凹形和平面，选择的类型将传给SubSceneXGridViewShow显示
 */

/**
 * Select the layout type of GridView, similar to PageView, convex, concave and flat, the selected type will be passed to SubSceneXGridViewShow for display
 */
public class SubSceneXGridView extends BaseScene {

    XImageText concave, convex, flat;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init() {

        XLabel titleLabel = new XLabel("GridView");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.10f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        concave = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        concave.setTitle("Concave", "Concave");
        concave.setName("concave");
        concave.setSizeOfImage(0.1f, 0.1f);
        concave.setSize(0.1f, 0.1f);
        concave.setSizeOfTitle(0.1f, 0.03f);
        concave.setTitleColor(Color.RED, Color.WHITE);
        concave.setTitlePosition(0, -0.1f);
        concave.setEventListener(itemActorListener);
        concave.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        concave.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        concave.setCenterPosition(0, 0, CalculateUtils.CENTER_Z);
        addActor(concave);

        convex = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        convex.setTitle("Convex", "Convex");
        convex.setName("convex");
        convex.setSizeOfImage(0.1f, 0.1f);
        convex.setSize(0.1f, 0.1f);
        convex.setSizeOfTitle(0.1f, 0.03f);
        convex.setTitleColor(Color.RED, Color.WHITE);
        convex.setTitlePosition(0, -0.1f);
        convex.setEventListener(itemActorListener);
        convex.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        convex.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        convex.setCenterPosition(-0.18f, 0, CalculateUtils.CENTER_Z);
        addActor(convex);

        flat = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        flat.setTitle("Flat", "Flat");
        flat.setName("flat");
        flat.setSizeOfImage(0.1f, 0.1f);
        flat.setSize(0.1f, 0.1f);
        flat.setSizeOfTitle(0.1f, 0.03f);
        flat.setTitleColor(Color.RED, Color.WHITE);
        flat.setTitlePosition(0, -0.1f);
        flat.setEventListener(itemActorListener);
        flat.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        flat.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        flat.setCenterPosition(0.18f, 0, CalculateUtils.CENTER_Z);
        addActor(flat);

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
            String name = actor.getName();
            //将actor名称传给SubSceneXGridViewShow，用于决定SubSceneXGridViewShow的布局类型
            //Pass the actor name to SubSceneXGridViewShow, to decide the layout type of SubSceneXGridViewShow
            Intent intent = new Intent(SubSceneXGridView.this, GridViewScene.class);

            if (name.equals("concave")) {
                intent.putExtra("type", XActorPageView.PageViewDefaultType.CONCAVE.ordinal());
            } else if (name.equals("convex")) {
                intent.putExtra("type", XActorPageView.PageViewDefaultType.CONVEX.ordinal());
            } else if (name.equals("flat")) {
                intent.putExtra("type", XActorPageView.PageViewDefaultType.FLAT.ordinal());
            }

            startScene(intent);
            return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }
    };

}
