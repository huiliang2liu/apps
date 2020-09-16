package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.graphics.Color;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.group.XActorListView;

/**
 * 选择ListView列表控件的横向和纵向布局，选择后进入SubSceneXListView显示
 */

/**
 * Select the horizontal and vertical layout of ListView list control, after it's selected, enter SubSceneXListView to display
 */
public class SubSceneXListView extends XBaseScene {
    XImageText horizontal, vertical;

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
        xImage.setSize(3, 1.8f);
        xImage.setCenterPosition(0, 0, -4);
        xImage.setRenderOrder(2);
        addActor(xImage);

        XLabel titleLabel = new XLabel("ListView");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.2f, -4);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        horizontal = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        horizontal.setTitle("Horizontal", "Horizontal");
        horizontal.setName("horizontal");
        horizontal.setSizeOfImage(0.5f, 0.5f);
        horizontal.setSize(0.5f, 0.5f);
        horizontal.setSizeOfTitle(0.4f, 0.2f);
        horizontal.setTitleColor(Color.RED, Color.WHITE);
        horizontal.setTitlePosition(0, -0.4f);
        horizontal.setEventListener(itemActorListener);
        horizontal.setTitleAlign(XAlign.Center, XAlign.Center);
        horizontal.setTitleArrangementMode(XArrangementMode.SingleRowNotMove, XArrangementMode.SingleRowNotMove);
        horizontal.setCenterPosition(-0.7f, 0, -3.8f);
        addActor(horizontal);

        vertical = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        vertical.setTitle("vertical", "vertical");
        vertical.setName("vertical");
        vertical.setSizeOfImage(0.5f, 0.5f);
        vertical.setSize(0.5f, 0.5f);
        vertical.setSizeOfTitle(0.4f, 0.2f);
        vertical.setTitleColor(Color.RED, Color.WHITE);
        vertical.setTitlePosition(0, -0.4f);
        vertical.setEventListener(itemActorListener);
        vertical.setTitleAlign(XAlign.Center, XAlign.Center);
        vertical.setTitleArrangementMode(XArrangementMode.SingleRowNotMove, XArrangementMode.SingleRowNotMove);
        vertical.setCenterPosition(0.7f, 0, -3.8f);
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
            Intent intent = new Intent(SubSceneXListView.this, SubSceneXListViewShow.class);

            if (name.equals("horizontal")) {
                intent.putExtra("type", XActorListView.ListViewType.Horizontal.ordinal());
            } else if (name.equals("vertical")) {
                intent.putExtra("type", XActorListView.ListViewType.Vertical.ordinal());
            }

            startScene(intent);
            return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }
    };

}
