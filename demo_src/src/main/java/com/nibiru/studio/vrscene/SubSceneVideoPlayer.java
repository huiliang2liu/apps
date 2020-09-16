package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImageText;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.group.XActorGridView;
import x.core.ui.group.XActorPageView;
import x.core.ui.group.XItem;
import x.core.util.XLog;


/**
 * 视频播放选择列表，选择对应格式后进入SubSceneVideoAll2播放
 */

/**
 * Video play select list, after selecting the corresponding formats, enter SubSceneVideoAll2 to play
 */
public class SubSceneVideoPlayer extends XBaseScene {

    String[] str = {"2DNormal", "2D180", "2D360", "2DSphere", "3DNormal", "3D180", "3D360", "3DSphere"};

    @Override
    public void onCreate() {
        init();
    }

    private SubSceneVideoPlayer.MyAdapter myAdapter;
    XActorGridView actorGridView;

    private class MyAdapter extends XItemAdapter {

        @Override
        public int getCount() {
            return str.length;
        }

        @Override
        public Object getObject(int position) {
            return null;
        }

        @Override
        public XItem getXItem(int position, XItem convertItem, XActor parent) {
            if (convertItem == null) {

                //初始化Item，显示str数组的字符串内容
                //Initialize Item, and show content of str array
                convertItem = new XItem();
                convertItem.setSize(0.5f, 0.5f);

                XImageText imageText = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
                imageText.setTitle(str[position], str[position]);
                imageText.setName(str[position]);
                imageText.setSizeOfImage(0.5f, 0.5f);
                imageText.setSize(0.5f, 0.5f);
                imageText.setSizeOfTitle(0.5f, 0.2f);
                imageText.setTitleColor(Color.RED, Color.WHITE);
                imageText.setTitlePosition(0, -0.35f);
                imageText.setEventListener(itemActorListener);
                imageText.setTitleAlign(XAlign.Center, XAlign.Center);
                imageText.setTitleArrangementMode(XArrangementMode.SingleRowNotMove, XArrangementMode.SingleRowNotMove);
                imageText.setTag(position);
                convertItem.addLayer(imageText);
            } else {
                XImageText imageText = (XImageText) convertItem.getChild("imageText");
                imageText.setTitle(str[position], str[position]);
                imageText.setTag(position);
            }
            return convertItem;
        }
    }

    public void initGridView() {
        actorGridView = new XActorGridView(XActorPageView.PageViewDefaultType.FLAT, 8f, 1.5f, 1, str.length);
        myAdapter = new SubSceneVideoPlayer.MyAdapter();
        actorGridView.setPageBackGroundName("blackxx.png");
        actorGridView.setAdapter(myAdapter);
        addActor(actorGridView);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    public void init() {
        initGridView();
    }

    @Override
    public void update(float deltatime) {
        super.update(deltatime);
    }



    @Override
    public void onDestroy() {

    }

    @Override
    public boolean onKeyDown(int keyCode) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode);
    }

    IXActorEventListener itemActorListener = new IXActorEventListener() {

        @Override
        public void onGazeEnter(XActor actor) {

        }

        @Override
        public void onGazeExit(XActor actor) {

        }

        //点击不同的Item，对Intent设置参数，启动SubSceneVideoAll2播放视频
        //Click different Item, set parameters for Intent, and enable SubSceneVideoAll2 to play
        // String[] str = {"2DNormal", "2D180", "2D360", "2DSphere", "3DNormal", "3D180", "3D360", "3DSphere"};
        @Override
        public boolean onGazeTrigger(XActor actor) {
            XLog.logInfo("on gaze trigger: "+actor.getName());

            Intent intent = new Intent(SubSceneVideoPlayer.this, SubSceneVideoAll2.class);
            intent.putExtra(SubSceneVideoAll2.PLAY_MODE, (int)actor.getTag());

            startScene(intent);

            return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }

    };

    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
    }
}
