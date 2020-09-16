package com.nibiru.studio.arscene;

import android.content.Intent;
import android.graphics.Color;

import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.arscene.ListView.ListViewHorizontalScene;
import com.nibiru.studio.arscene.ListView.ListViewVerticalScene;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XImageText;
import x.core.ui.XLabel;

/**
 * 选择ListView列表控件的横向和纵向布局，选择后进入SubSceneXListView显示
 */

/**
 * Select the horizontal and vertical layout of ListView list control, after it's selected, enter SubSceneXListView to display
 */
public class SubSceneXListView extends BaseScene {


    XImageText horizontal, vertical;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init() {

        XLabel titleLabel = new XLabel("ListView");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.10f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        horizontal = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        horizontal.setTitle("Horizontal", "Horizontal");
        horizontal.setName("horizontal");
        horizontal.setSizeOfImage(0.1f, 0.1f);
        horizontal.setSize(0.1f, 0.1f);
        horizontal.setSizeOfTitle(0.1f, 0.03f);
        horizontal.setTitleColor(Color.RED, Color.WHITE);
        horizontal.setTitlePosition(0, -0.1f);
        horizontal.setEventListener(itemActorListener);
        horizontal.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        horizontal.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        horizontal.setCenterPosition(0.1f, 0, CalculateUtils.CENTER_Z);
        addActor(horizontal);

        vertical = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        vertical.setTitle("vertical", "vertical");
        vertical.setName("vertical");
        vertical.setSizeOfImage(0.1f, 0.1f);
        vertical.setSize(0.1f, 0.1f);
        vertical.setSizeOfTitle(0.1f, 0.03f);
        vertical.setTitleColor(Color.RED, Color.WHITE);
        vertical.setTitlePosition(0, -0.1f);
        vertical.setEventListener(itemActorListener);
        vertical.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        vertical.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        vertical.setCenterPosition(-0.1f, 0, CalculateUtils.CENTER_Z);
        addActor(vertical);

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
            //选中点击的ImageText名称作为参数传递给SubSceneXListViewShow，用于最后展示ListView列表控件
            //Select the clicked ImageText name as the parameter to pass to SubSceneXListViewShow, for display ListView list control in the last
            if (name.equals("horizontal")) {
                startScene(new Intent(SubSceneXListView.this, ListViewHorizontalScene.class));
            } else if (name.equals("vertical")) {
                startScene(new Intent(SubSceneXListView.this, ListViewVerticalScene.class));
            }
            return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }
    };

}
