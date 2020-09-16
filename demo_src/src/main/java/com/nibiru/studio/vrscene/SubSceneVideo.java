package com.nibiru.studio.vrscene;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.nibiru.api.SysSleepApi;
import com.nibiru.studio.xrdemo.R;

import java.util.Timer;
import java.util.TimerTask;

import x.core.listener.IXActorEventListener;
import x.core.listener.IXProgressBarListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.XPanel;
import x.core.ui.XProgressBar;
import x.core.ui.XUI;

/**
 * 包含控制控件和事件处理的视频播放场景
 */

/**
 * Video playback scene with control controls and event handling
 */

public class SubSceneVideo extends SubSceneVideoBase implements IXActorEventListener {

    /**
     * UI
     */
    private XPanel controlPanel;    //控制面板
    private XPanel completePanel;    //播放完成显示的面板
    private XPanel typePanel;   //播放模式选择面板
    private XPanel morePanel;   //扩展选择面板

    /**
     * 控制面板的控件
     */
    /**
     * Control Panel Controls
     */
    private XProgressBar progressBar;  //进度条
    private XLabel currentTimeShow;//当前时间显示
    private XLabel totalTimeShow;//总时间显示
    private XImageText button_play; //播放按钮
    private XImageText voice;
    private XImageText bright;

    private XImageText control_showDisplay;
    private XImageText control_noSee;
    /**
     * 播放模式选择面板的控件
     */
    /**
     * Play mode selection panel controls
     */
    private XImageText button_type;
    private XImageText[] typeViews = new XImageText[3];  //2d,3d左右,3d上下
    private XImageText[] modelViews = new XImageText[5];  //平面,球,半球,球幕,EAC
    /**
     * 扩展选择面板的控件
     */
    /**
     * Expand the control of the selection panel
     */
    private XImageText more_LoopPlay;//循环播放
    private XImageText more_notLoopPlay;//非循环播放
    /**
     * 状态和事件
     */
    /**
     * Status and events
     */
    private int brightIndex;     //亮度
    private int volumeIndex;     //音量
    private AudioManager mAudioManager;

    private boolean isLoop; //循环播放


    private boolean tag_lockView_range; //锁定视角区域判断
    private boolean tag_keyEvent_center;//是否锁定

    @Override
    public void onCreate() {
        super.onCreate();

        initControlPanel();
        initCompletePanel();
        initTypePanel();
        initMorePanel();

        updateBrightIndex();
        updateVolumeIndex();

        startRefreshTimer();
        registerBrightnessListener();
        registerVolumeListener();

        initLocalData();
        hideGaze();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveLocalData();
        stopRefreshTimer();
        unRegisterBrightnessListener();
        unRegisterVolumeListener();
    }

    @Override
    public String[] getScenePlist() {
        return new String[]{"playControl.plist"};
    }

    /**
     * 处理按键事件
     * Handle key events
     * @param keyCode
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == 255) {
            if (tag_keyEvent_center) {
                control_noSee.setEnabled(true);
                nibiruVRView.unlockTracker();
                tag_keyEvent_center = false;

                //解锁视角，进入原始设置的休眠状态
                //Unlock the perspective and enter the sleep state of the original settings
                unLockView_resetSleep();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {

            if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
                //锁定区域判断
                //Locked area judgment
                tag_lockView_range = XUI.getInstance().isGazeInArea(-34, 34, -18, 18);
                if (!tag_keyEvent_center && !typePanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled() && tag_lockView_range) {
                    control_noSee.setEnabled(false);
                    tag_keyEvent_center = true;
                    cancelRotateView();
                    nibiruVRView.lockTrackerToFront();

                    //锁定视角，进入永不休眠状态
                    //Lock the viewing angle and enter never sleep
                    lockView_neverSleep();

                    return true;
                } else if (tag_keyEvent_center) {
                    control_noSee.setEnabled(true);
                    nibiruVRView.unlockTracker();
                    tag_keyEvent_center = false;

                    //解锁视角，进入原始设置的休眠状态
                    // Unlock the view and enter the sleep state of the original settings
                    unLockView_resetSleep();
                }
            } else {
                if (!typePanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
                    if (!controlPanel.isEnabled()) {
                        showControlPanel(false);
                    } else {
                        controlPanel.setEnabled(false);
                        hideGaze();
                    }
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeAdd();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDec();
            return true;
        }
        return super.onKeyDown(keyCode);
    }

    @Override
    public void onGazeEnter(XActor xActor) {
        if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
            if (!typePanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
                if (xActor.getName().equals("control_noSee")) {
                    control_showDisplay.setEnabled(true);
                    showGaze();
                }
                if (xActor.getName().equals("control_showDisplay")) {
                    getMainHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            control_showDisplay.setEnabled(false);
                            showControlPanel(false);
                            if (hideControl != null) {
                                getMainHandler().removeCallbacks(hideControl);
                            }
                        }
                    }, 300);
                } else if (xActor.getName().equals("controlPanel")) {
                    if (hideControl != null) {
                        getMainHandler().removeCallbacks(hideControl);
                    }
                }
            }
        }
    }

    @Override
    public void onGazeExit(XActor xActor) {
        if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
            if (!typePanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
                if (xActor.getName().equals("controlPanel")) {
                    autoHideControlPanel();
                }
            }
        }
    }

    /**
     * 处理控件点击事件
     * Handling control click events
     * @param xActor
     * @return
     */
    @Override
    public boolean onGazeTrigger(XActor xActor) {
        String name = xActor.getName();
        if ("progressBar".equals(name)) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo((int) (progressBarProcess * (mediaPlayer.getDuration())));
                progressBar.setProcess(progressBarProcess);
                refreshVideoTime();
            }
            return true;
        } else if ("button_addVoice".equals(name)) {
            volumeAdd();
            return true;
        } else if ("button_decVoice".equals(name)) {
            volumeDec();
            return true;
        } else if ("button_addLight".equals(name)) {
            brightAdd();
            return true;
        } else if ("button_decLight".equals(name)) {
            brightDec();
            return true;
        } else if ("button_play".equals(name)) {
            changeVideoPlayState();
            if (isPlaying()) {
                button_play.setImgName("button_play_on.png", "button_play.png");
            } else {
                button_play.setImgName("button_pause_on.png", "button_pause.png");
            }
            return true;
        } else if ("button_type".equals(name)) {
            controlPanel.setEnabled(false);
            showTypePanel();
            return true;
        } else if ("button_more".equals(name)) {
            controlPanel.setEnabled(false);
            showMorePanel();
            return true;
        } else if ("button_exit".equals(name)) {
            finish();
        } else if ("type_sure".equals(name)) {
            typePanel.setEnabled(false);
            hideGaze();
            return true;
        } else if (name.equals("more_LoopPlay")) {
            isLoop = true;
            updateMorePanelState();
            return true;
        } else if (name.equals("more_LoopPlay")) {
            isLoop = false;
            updateMorePanelState();
            return true;
        } else if (name.equals("more_sure")) {
            morePanel.setEnabled(false);
            hideGaze();
            return true;
        } else if (name.equals("complete_rePlay")) {
            handleReplay();
            completePanel.setEnabled(false);
            hideGaze();
            return true;
        } else if (name.equals("complete_back")) {
            finish();
            return true;
        } else if (name.startsWith("typePanel")) {
            handleTypeChange(name);
            return true;
        }
        return false;
    }

    @Override
    public void onAnimation(XActor xActor, boolean b) {

    }

    /**
     * 初始化控制面板
     * Initialize the control panel
     */
    private void initControlPanel() {
        controlPanel = new XPanel();
        controlPanel.setBackGround("backGround.png", 0);
        controlPanel.setCenterPosition(0f, -2.5f, -DISTANCE);
        controlPanel.setSize(5.2f, 1.1f);
        controlPanel.setName("controlPanel");
        controlPanel.setEventListener(this);
        addActor(controlPanel);
//        controlPanel.setEnabled(false);

        progressBar = new XProgressBar(0.0f, "progress_pp1_repair.png", "progress_pp2_repair.png", "progresspp2_Btn.png");
        progressBar.setSize(4.5f, 0.06f);
        progressBar.setBarSizeRatio(0.06f, 2.5f);
        progressBar.setName("progressBar");
        progressBar.setProgressBarListener(progressBarListener);
        progressBar.setEventListener(this);
        controlPanel.addChild(progressBar, new XActorGroup.LayoutParam(0f, 0.3f, 0.07f));

        currentTimeShow = new XLabel("00:00:00");
        currentTimeShow.setSize(0.8f, 0.15f);
        currentTimeShow.setAlignment(XLabel.XAlign.Center);
        controlPanel.addChild(currentTimeShow, new XActorGroup.LayoutParam(-1.9f, 0.15f, 0.02f, 5));

        totalTimeShow = new XLabel("00:00:00");
        totalTimeShow.setSize(0.8f, 0.15f);
        totalTimeShow.setAlignment(XLabel.XAlign.Center);
        controlPanel.addChild(totalTimeShow, new XActorGroup.LayoutParam(1.9f, 0.15f, 0.02f, 5));

        button_play = new XImageText("button_play_on.png", "button_play.png");
        button_play.setSize(0.4f, 0.4f);
        button_play.setSizeOfImage(0.4f, 0.4f);
        button_play.setName("button_play");
        button_play.setEventListener(this);
        controlPanel.addChild(button_play, new XActorGroup.LayoutParam(0f, -0.2f, 0.02f));

        XImageText button_addVoice = new XImageText("button_add_on.png", "button_add.png");
        button_addVoice.setSize(0.3f, 0.3f);
        button_addVoice.setSizeOfImage(0.3f, 0.3f);
        button_addVoice.setName("button_addVoice");
        button_addVoice.setEventListener(this);
        controlPanel.addChild(button_addVoice, new XActorGroup.LayoutParam(-0.5f, -0.2f, 0.02f));

        voice = new XImageText("voice_0.png", "voice_0.png");
        voice.setSize(0.3f, 0.3f);
        voice.setSizeOfImage(0.3f, 0.3f);
        controlPanel.addChild(voice, new XActorGroup.LayoutParam(-0.8f, -0.2f, 0.02f));

        XImageText button_decVoice = new XImageText("button_dec_on.png", "button_dec.png");
        button_decVoice.setSize(0.3f, 0.3f);
        button_decVoice.setSizeOfImage(0.3f, 0.3f);
        button_decVoice.setName("button_decVoice");
        button_decVoice.setEventListener(this);
        controlPanel.addChild(button_decVoice, new XActorGroup.LayoutParam(-1.1f, -0.2f, 0.02f));

        XImageText button_addLight = new XImageText("button_add_on.png", "button_add.png");
        button_addLight.setSize(0.3f, 0.3f);
        button_addLight.setSizeOfImage(0.3f, 0.3f);
        button_addLight.setName("button_addLight");
        button_addLight.setEventListener(this);
        controlPanel.addChild(button_addLight, new XActorGroup.LayoutParam(-1.6f, -0.2f, 0.02f));

        bright = new XImageText("light_0.png", "light_0.png");
        bright.setSize(0.3f, 0.3f);
        bright.setSizeOfImage(0.3f, 0.3f);
        controlPanel.addChild(bright, new XActorGroup.LayoutParam(-1.9f, -0.2f, 0.02f));

        XImageText button_decLight = new XImageText("button_dec_on.png", "button_dec.png");
        button_decLight.setSize(0.3f, 0.3f);
        button_decLight.setSizeOfImage(0.3f, 0.3f);
        button_decLight.setName("button_decLight");
        button_decLight.setEventListener(this);
        controlPanel.addChild(button_decLight, new XActorGroup.LayoutParam(-2.2f, -0.2f, 0.02f));

        button_type = new XImageText("imax_2d_on.png", "imax_2d.png");
        button_type.setSize(0.4f, 0.4f);
        button_type.setSizeOfImage(0.4f, 0.4f);
        button_type.setName("button_type");
        button_type.setEventListener(this);
        controlPanel.addChild(button_type, new XActorGroup.LayoutParam(0.7f, -0.2f, 0.02f));


        XImageText button_more = new XImageText("moreChoose_on.png", "moreChoose.png");
        button_more.setSize(0.4f, 0.4f);
        button_more.setSizeOfImage(0.4f, 0.4f);
        button_more.setName("button_more");
        button_more.setEventListener(this);
        controlPanel.addChild(button_more, new XActorGroup.LayoutParam(1.4f, -0.2f, 0.02f));

        XImageText button_exit = new XImageText("button_exit_on.png", "button_exit.png");
        button_exit.setSize(0.4f, 0.4f);
        button_exit.setSizeOfImage(0.4f, 0.4f);
        button_exit.setName("button_exit");
        button_exit.setEventListener(this);
        controlPanel.addChild(button_exit, new XActorGroup.LayoutParam(2.1f, -0.2f, 0.02f));

//        controlPanel.setRotationX(-30);
        controlPanel.setEnabled(false);

        //播控条显隐延时控制按钮
        // Control panel display delay control button
        control_showDisplay = new XImageText("control_light.png", "control_UnLight.png");
        control_showDisplay.setSize(0.7f, 0.7f);
        control_showDisplay.setSizeOfImage(0.7f, 0.7f);
        control_showDisplay.setCenterPosition(0f, -2.5f, -DISTANCE - 0.1f);
        control_showDisplay.setRotationX(-30);
        control_showDisplay.setName("control_showDisplay");
        control_showDisplay.setEventListener(this);
        addActor(control_showDisplay);
        control_showDisplay.setEnabled(false);

        //不可见的矩形控制位（当瞄准时，出现control_showDisplay）
        //Invisible rectangle control bit (control_showDisplay appears when aiming)
        control_noSee = new XImageText("transparent.png", "transparent.png");
        control_noSee.setSize(8f, 1f);
        control_noSee.setSizeOfImage(8f, 1f);
        control_noSee.setCenterPosition(0f, -2.5f, -DISTANCE - 0.2f);
        control_noSee.setName("control_noSee");
        control_noSee.setEventListener(this);
        addActor(control_noSee);
    }

    /**
     * 初始化播放完成面板
     * Initialize playback complete panel
     */
    private void initCompletePanel() {
        completePanel = new XPanel();
        completePanel.setBackGround("chooseBackGround.png", 0);
        completePanel.setCenterPosition(0f, 0f, -DISTANCE);
        completePanel.setSize(2.5f, 1.6f);
        completePanel.setEventListener(this);
        addActor(completePanel);
        completePanel.setEnabled(false);

        XImageText complete_rePlay = new XImageText("replay_on.png", "replay.png");
        complete_rePlay.setTitle(getString(R.string.video_replay), getString(R.string.video_replay));//重播
        complete_rePlay.setTitleColor(Color.WHITE, Color.GRAY);
        complete_rePlay.setSize(0.4f, 0.4f);
        complete_rePlay.setSizeOfImage(0.4f, 0.4f);
        complete_rePlay.setTitlePosition(0.0f, -0.3f);
        complete_rePlay.setSizeOfTitle(1.0f, 0.14f);
        complete_rePlay.setSelectedAlign(XLabel.XAlign.Center);
        complete_rePlay.setUnselectedAlign(XLabel.XAlign.Center);
        complete_rePlay.setName("complete_rePlay");
        complete_rePlay.setEventListener(this);
        completePanel.addChild(complete_rePlay, new XActorGroup.LayoutParam(-0.5f, 0f, 0.02f));

        XImageText complete_back = new XImageText("button_exit_on.png", "button_exit.png");
        complete_back.setTitle(getString(R.string.video_finish), getString(R.string.video_finish));//退出
        complete_back.setTitleColor(Color.WHITE, Color.GRAY);
        complete_back.setSize(0.4f, 0.4f);
        complete_back.setSizeOfImage(0.4f, 0.4f);
        complete_back.setTitlePosition(0.0f, -0.3f);
        complete_back.setSizeOfTitle(1.0f, 0.14f);
        complete_back.setSelectedAlign(XLabel.XAlign.Center);
        complete_back.setUnselectedAlign(XLabel.XAlign.Center);
        complete_back.setName("complete_back");
        complete_back.setEventListener(this);
        completePanel.addChild(complete_back, new XActorGroup.LayoutParam(0.5f, 0f, 0.02f));
    }

    /**
     * 初始化播放模式选择面板
     * Initialize playback mode selection panel
     */
    private String[] modelImages = {"module_screen_imax", "module_screen_all360", "module_screen_all180", "module_screen_ball", "module_screen_eac"};

    private void initTypePanel() {
        typePanel = new XPanel();
        typePanel.setBackGround("chooseBackGround.png", 0);
        typePanel.setCenterPosition(0f, 0f, -DISTANCE);
        typePanel.setSize(3.5f, 1.4f);
        typePanel.setEventListener(this);
        addActor(typePanel);
        typePanel.setEnabled(false);

        //初始化格式选择控件：2d,3d左右,3d上下
        //Initialize format selection controls: 2d, 3d left and right, 3d up and down
        int[] typeTitles = {R.string.video_type_2D, R.string.video_type_3D_leftRight, R.string.video_type_3D_upDown};

        for (int i = 0; i < typeViews.length; i++) {
            typeViews[i] = new XImageText("assign_on.png", "assign_on.png");
            typeViews[i].setTitle(getString(typeTitles[i]), getString(typeTitles[i]));
            typeViews[i].setTitleColor(Color.BLACK, Color.BLACK);
            typeViews[i].setSize(0.7f, 0.2f);
            typeViews[i].setSizeOfImage(0.7f, 0.2f);
            typeViews[i].setTitlePosition(0.0f, 0.0f);
            typeViews[i].setSizeOfTitle(0.7f, 0.12f);
            typeViews[i].setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
            typeViews[i].setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
            typeViews[i].setName("typePanel_type" + i);
            typeViews[i].setEventListener(this);
            typePanel.addChild(typeViews[i], new XActorGroup.LayoutParam((-0.53f + i * 0.85f), 0.4f, 0.02f));
        }

        //初始化模型选择控件：平面,球,半球,球幕
        //Initialize model selection controls: plane, ball, hemisphere, dome screen
        int[] modelTitles = {R.string.video_model_plane, R.string.video_model_sphere, R.string.video_model_halfSphere, R.string.video_model_dome, R.string.video_model_eac};

        for (int i = 0; i < modelViews.length; i++) {
            modelViews[i] = new XImageText(modelImages[i] + "_on.png", modelImages[i] + ".png");
            modelViews[i].setTitle(getString(modelTitles[i]), getString(modelTitles[i]));
            modelViews[i].setTitleColor(Color.WHITE, Color.WHITE);
            modelViews[i].setSize(0.25f, 0.25f);
            modelViews[i].setSizeOfImage(0.25f, 0.25f);
            modelViews[i].setTitlePosition(0.0f, -0.25f);
            modelViews[i].setSizeOfTitle(0.7f, 0.09f);
            modelViews[i].setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
            modelViews[i].setName("typePanel_model" + i);
            modelViews[i].setEventListener(this);
            typePanel.addChild(modelViews[i], new XActorGroup.LayoutParam((-0.9f + i * 0.58f), 0f, 0.02f));
        }

        XLabel type_title = new XLabel(getString(R.string.video_type_title));//格式
        type_title.setSize(0.9f, 0.13f);
        type_title.setAlignment(XLabel.XAlign.Center);
        typePanel.addChild(type_title, new XActorGroup.LayoutParam(-1.37f, 0.4f, 0.02f, 5));

        XLabel model_title = new XLabel(getString(R.string.video_model_title));//屏幕
        model_title.setSize(0.9f, 0.13f);
        model_title.setAlignment(XLabel.XAlign.Center);
        typePanel.addChild(model_title, new XActorGroup.LayoutParam(-1.37f, -0.1f, 0.02f, 5));

        XImageText sure = new XImageText("assign_on.png", "assign.png");
        sure.setTitle(getString(R.string.video_sure), getString(R.string.video_sure));//确定
        sure.setTitleColor(Color.BLACK, Color.WHITE);
        sure.setSize(0.7f, 0.18f);
        sure.setSizeOfImage(0.7f, 0.18f);
        sure.setTitlePosition(0.0f, 0.0f);
        sure.setSizeOfTitle(0.7f, 0.1f);
        sure.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        sure.setName("type_sure");
        sure.setEventListener(this);
        typePanel.addChild(sure, new XActorGroup.LayoutParam(0f, -0.5f, 0.02f));
    }

    /**
     * 更新模式选择面板控件的选中状态
     * Update mode selection panel controls selected state
     */
    private String[][] buttonTypeImages = {{"imax_2d", "quan360_2d", "quan180_2d", "ball_2d", "module_screen_eac"}, {"imax_3d", "quan360_3d", "quan180_3d", "ball_3d", "module_screen_eac"}};

    private void updateTypePanelState() {
        for (int i = 0; i < typeViews.length; i++) {
            if (i == videoType) {
                typeViews[i].setImgName("assign_on.png", "assign_on.png");
                typeViews[i].setTitleColor(Color.BLACK, Color.BLACK);
            } else {
                typeViews[i].setImgName("assign_on.png", "assign.png");
                typeViews[i].setTitleColor(Color.BLACK, Color.WHITE);
            }
        }
        for (int i = 0; i < modelViews.length; i++) {
            if (i == videoModel) {
                modelViews[i].setImgName(modelImages[i] + "_on.png", modelImages[i] + "_on.png");
                modelViews[i].setTitleColor(Color.WHITE, Color.WHITE);
            } else {
                modelViews[i].setImgName(modelImages[i] + "_on.png", modelImages[i] + ".png");
                modelViews[i].setTitleColor(Color.WHITE, Color.GRAY);
            }
        }
        if(videoModel == MODEL_EAC) {
//            typeViews[1].setEnabled(false);
//            typeViews[2].setEnabled(false);
            typeViews[1].setEnabled(true);
            typeViews[2].setEnabled(false);
        } else {
            typeViews[1].setEnabled(true);
            typeViews[2].setEnabled(true);
        }
        //更新控制面板选择模式按钮的图标
        button_type.setImgName(buttonTypeImages[videoType > 1 ? 1 : videoType][videoModel] + "_on.png", buttonTypeImages[videoType > 1 ? 1 : videoType][videoModel] + ".png");
    }

    /**
     * 处理选择面板控件的点击事件
     * Handle click events for select panel controls
     */
    private void handleTypeChange(String name) {
        if (name.startsWith("typePanel_type")) {
            for (int i = 0; i < typeViews.length; i++) {
                if (name.equals("typePanel_type" + i)) {
                    // TODO: 2019-03-06 zkk 现在支持3D上下模式
//                    if(videoModel == MODEL_EAC) {
//                        //EAC模式视频仅支持2D
//                        return;
//                    }
                    if (videoType != i) {
                        videoType = i;
                        updateTypePanelState();
                        changeVideoType();
                    }
                }
            }
        } else {
            for (int i = 0; i < modelViews.length; i++) {
                if (name.equals("typePanel_model" + i)) {
                    if (videoModel != i) {
                        videoModel = i;
//                        if(videoModel == MODEL_EAC) {
//                            videoType = TYPE_2D;
//                        }
                        updateTypePanelState();
                        changeVideoType();
                    }
                }
            }
        }
    }

    /**
     * 初始化扩展选择面板
     * Initialize the expansion selection panel
     */
    private void initMorePanel() {

        morePanel = new XPanel();
        morePanel.setBackGround("chooseBackGround.png", 0);
        morePanel.setCenterPosition(0f, 0f, -DISTANCE);
        morePanel.setSize(2.5f, 1.4f);
        morePanel.setEventListener(this);
        addActor(morePanel);
        morePanel.setEnabled(false);

        XLabel more_loop = new XLabel(getString(R.string.video_loop));//循环播放
        more_loop.setSize(2.2f, 0.13f);
        more_loop.setAlignment(XLabel.XAlign.Center);
        morePanel.addChild(more_loop, new XActorGroup.LayoutParam(0f, 0.3f, 0.02f));

        more_LoopPlay = new XImageText("assign_on.png", "assign.png");
        more_LoopPlay.setTitle(getString(R.string.video_yes), getString(R.string.video_yes));//开
        more_LoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
        more_LoopPlay.setSize(1.0f, 0.2f);
        more_LoopPlay.setSizeOfImage(1.0f, 0.2f);
        more_LoopPlay.setTitlePosition(0.0f, 0.0f);
        more_LoopPlay.setSizeOfTitle(1.0f, 0.12f);
        more_LoopPlay.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        more_LoopPlay.setName("more_LoopPlay");
        more_LoopPlay.setEventListener(this);
        morePanel.addChild(more_LoopPlay, new XActorGroup.LayoutParam(-0.6f, 0.05f, 0.02f));

        more_notLoopPlay = new XImageText("assign_on.png", "assign.png");
        more_notLoopPlay.setTitle(getString(R.string.video_no), getString(R.string.video_no));//开
        more_notLoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
        more_notLoopPlay.setSize(1.0f, 0.2f);
        more_notLoopPlay.setSizeOfImage(1.0f, 0.2f);
        more_notLoopPlay.setTitlePosition(0.0f, 0.0f);
        more_notLoopPlay.setSizeOfTitle(1.0f, 0.12f);
        more_notLoopPlay.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        more_notLoopPlay.setName("more_notLoopPlay");
        more_notLoopPlay.setEventListener(this);
        morePanel.addChild(more_notLoopPlay, new XActorGroup.LayoutParam(0.6f, 0.05f, 0.02f));

        XImageText sure = new XImageText("assign_on.png", "assign.png");
        sure.setTitle(getString(R.string.video_sure), getString(R.string.video_sure));//确定
        sure.setTitleColor(Color.BLACK, Color.WHITE);
        sure.setSize(0.7f, 0.2f);
        sure.setSizeOfImage(0.7f, 0.2f);
        sure.setTitlePosition(0.0f, 0.0f);
        sure.setSizeOfTitle(0.7f, 0.12f);
        sure.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        sure.setName("more_sure");
        sure.setEventListener(this);
        morePanel.addChild(sure, new XActorGroup.LayoutParam(0f, -0.3f, 0.02f));
    }

    /**
     * 更新扩展面板控件的选中状态
     * Update the expanded panel control's selected state
     */
    private void updateMorePanelState() {
        if (isLoop) {
            more_LoopPlay.setImgName("assign_on.png", "assign_on.png");
            more_LoopPlay.setTitleColor(Color.BLACK, Color.BLACK);
            more_notLoopPlay.setImgName("assign_on.png", "assign.png");
            more_notLoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
        } else {
            more_LoopPlay.setImgName("assign_on.png", "assign.png");
            more_LoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
            more_notLoopPlay.setImgName("assign_on.png", "assign_on.png");
            more_notLoopPlay.setTitleColor(Color.BLACK, Color.BLACK);
        }
    }

    private void showControlPanel(boolean firstIn) {
        showGaze();
        if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
            controlPanel.setEnabled(true);
            controlPanel.setRotationX(-30);
            controlPanel.setCenterPosition(0f, -2.5f, -DISTANCE);
        } else {
            if (!firstIn) {
                controlPanel.setEnabled(true);
                controlPanel.setCenterPosition(0f, 0f, -DISTANCE);
                controlPanel.setRotationX(0);
                controlPanel.setXActorTransformMatrix(getGazeFloatArray());
            }
        }
    }

    private void showCompletePanel() {
        showGaze();
        if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
            completePanel.setEnabled(true);
            completePanel.setCenterPosition(0f, 0f, -DISTANCE);
        } else {
            completePanel.setEnabled(true);
            completePanel.setCenterPosition(0f, 0f, -DISTANCE);
            completePanel.setXActorTransformMatrix(getGazeFloatArray());
        }
    }

    private void showTypePanel() {
        showGaze();
        if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
            typePanel.setEnabled(true);
            typePanel.setCenterPosition(0f, 0.01f, -DISTANCE);
        } else {
            typePanel.setEnabled(true);
            typePanel.setCenterPosition(0f, 0f, -DISTANCE);
            typePanel.setXActorTransformMatrix(getGazeFloatArray());
        }
        updateTypePanelState();
    }

    private void showMorePanel() {
        showGaze();
        if (videoModel == MODEL_PLANE || videoModel == MODEL_DOME) {
            morePanel.setEnabled(true);
            morePanel.setCenterPosition(0f, 0.01f, -DISTANCE);
        } else {
            morePanel.setEnabled(true);
            morePanel.setCenterPosition(0f, 0f, -DISTANCE);
            morePanel.setXActorTransformMatrix(getGazeFloatArray());
        }
        updateMorePanelState();
    }

    private void hideAllPanel() {
        controlPanel.setEnabled(false);
        completePanel.setEnabled(false);
        typePanel.setEnabled(false);
        morePanel.setEnabled(false);
        hideGaze();
    }

    private Runnable hideControl;

    public void autoHideControlPanel() {
        if (hideControl != null) {
            getMainHandler().removeCallbacks(hideControl);
        }

        hideControl = new Runnable() {
            @Override
            public void run() {
                controlPanel.setEnabled(false);
                if (!typePanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
                    hideGaze();
                }
            }
        };
        getMainHandler().postDelayed(hideControl, 3000);
    }

    /**
     * 播放完成的处理
     * Playback completed processing
     */
    @Override
    protected void onVideoCompletion() {
        super.onVideoCompletion();
        if (isLoop) {
            //循环播放的处理
            //Looping processing
            handleReplay();
        } else {
            if (tag_keyEvent_center) {
                control_noSee.setEnabled(true);
                nibiruVRView.unlockTracker();
                tag_keyEvent_center = false;

                //解锁视角，进入原始设置的休眠状态
                //Unlock the perspective and enter the sleep state of the original settings
                unLockView_resetSleep();
            }
            hideAllPanel();
            showCompletePanel();
        }
    }

    /**
     * 进度条监听
     * Progress bar monitoring
     */
    public float progressBarProcess = 0.0f;

    IXProgressBarListener progressBarListener = new IXProgressBarListener() {

        @Override
        public void onProgress(XProgressBar progressBar, float progress) {
            progressBarProcess = progress;
        }
    };

    /**
     * 更新进度条和时间的计时器
     * Timer to update progress bar and time
     */
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private void startRefreshTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    refreshVideoTime();
                }
            };
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 800, 800);  //800ms执行一次

    }

    private void stopRefreshTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void refreshVideoTime() {
        try {
            if (mediaPlayer == null || !isPrepared() || !mediaPlayer.isPlaying()) {
                return;
            }

            int curPos = mediaPlayer.getCurrentPosition();
            int totalPos = mediaPlayer.getDuration();
            currentTimeShow.setTextContent(formatLongToTimeStr(curPos));
            totalTimeShow.setTextContent(formatLongToTimeStr(totalPos));
            // 更新progressBar的实时进度
            progressBar.setProcess(((float) curPos) / totalPos, true);
        } catch (IllegalStateException e) {

        }
    }

    /**
     * 时间格式转换
     * Time format conversion
     * @param time
     * @return
     */
    private String formatLongToTimeStr(int time) {
        int hour = time / 1000 / 60 / 60;
        int minute = time / 1000 / 60 % 60;
        int second = time / 1000 % 60;
        if (second >= 0 && second < 10) {
            if (minute >= 0 && minute < 10) {
                return "0" + hour + ":0" + minute + ":0" + second;
            } else {
                return "0" + hour + ":" + minute + ":0" + second;
            }
        } else if (second >= 10) {
            if (minute >= 0 && minute < 10) {
                return "0" + hour + ":0" + minute + ":" + second;
            } else {
                return "0" + hour + ":" + minute + ":" + second;
            }
        }
        return "00:00:00";
    }

    /**
     * 更新音量控件
     * Update volume controls
     */
    private void changeVoiceImage(int temp_voice) {
        if (temp_voice == 0) {
            voice.setImgName("voice_0.png", "voice_0.png");
        } else if (temp_voice == 1) {
            voice.setImgName("voice_1.png", "voice_1.png");
        } else if (temp_voice == 2) {
            voice.setImgName("voice_2.png", "voice_2.png");
        } else if (temp_voice == 3) {
            voice.setImgName("voice_3.png", "voice_3.png");
        } else if (temp_voice == 4) {
            voice.setImgName("voice_4.png", "voice_4.png");
        } else if (temp_voice == 5) {
            voice.setImgName("voice_5.png", "voice_5.png");
        } else if (temp_voice == 6) {
            voice.setImgName("voice_6.png", "voice_6.png");
        }
    }

    /**
     * 更新亮度控件
     * Update brightness control
     */
    private void changeBrightImage(int brightness) {
        if (brightness == 0) {
            bright.setImgName("light_0.png", "light_0.png");
        } else if (brightness > 0 && brightness <= 42) {
            bright.setImgName("light_1.png", "light_1.png");
        } else if (brightness > 42 && brightness <= 84) {
            bright.setImgName("light_2.png", "light_2.png");
        } else if (brightness > 84 && brightness <= 126) {
            bright.setImgName("light_3.png", "light_3.png");
        } else if (brightness > 126 && brightness <= 168) {
            bright.setImgName("light_4.png", "light_4.png");
        } else if (brightness > 168 && brightness <= 210) {
            bright.setImgName("light_5.png", "light_5.png");
        } else {
            bright.setImgName("light_6.png", "light_6.png");
        }
    }

    /**
     * 音量变化的广播监听
     * Broadcast monitor with volume change
     *
     * @author Administrator
     */
    class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 如果音量发生变化则更改seekbar的位置
            //Change the position of the seekbar if the volume changes
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                updateVolumeIndex();
            }
        }
    }

    private MyVolumeReceiver mVolumeReceiver;

    private void registerVolumeListener() {
        if (mVolumeReceiver == null) {
            mVolumeReceiver = new MyVolumeReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);
    }

    private void unRegisterVolumeListener() {
        if (mVolumeReceiver != null) {
            unregisterReceiver(mVolumeReceiver);
            mVolumeReceiver = null;
        }
    }

    /**
     * 更新音量值
     * Update the volume value
     */
    private void updateVolumeIndex() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (volume == 0) {
            volumeIndex = 0;
        } else if (volume <= 9) {
            volumeIndex = (volume - 1) / 3 + 1;
        } else {
            volumeIndex = volume / 2 - 1;
        }
        changeVoiceImage(volumeIndex);
    }

    /**
     * 音量加
     * Volume up
     */
    private void volumeAdd() {
        if (volumeIndex < 6) {
            volumeIndex++;
            if (volumeIndex < 4) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex * 3, 0);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9 + (volumeIndex - 3) * 2, 0);
            }
        }
    }

    /**
     * 音量减
     * Volume down
     */
    private void volumeDec() {
        if (volumeIndex > 0) {
            volumeIndex--;
            if (volumeIndex < 4) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex * 3, 0);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9 + (volumeIndex - 3) * 2, 0);
            }
        }
    }

    /**
     * 亮度监听
     * Brightness monitoring
     */
    private ContentObserver mBrightnessObserver;

    private void registerBrightnessListener() {
        if (mBrightnessObserver == null) {
            mBrightnessObserver = new ContentObserver(getMainHandler()) {
                @Override
                public void onChange(boolean selfChange) {
                    updateBrightIndex();
                }
            };
        }
        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, mBrightnessObserver);
    }

    private void unRegisterBrightnessListener() {
        if (mBrightnessObserver != null) {
            getContentResolver().unregisterContentObserver(mBrightnessObserver);
            mBrightnessObserver = null;
        }
    }

    /**
     * 更新亮度值
     * Update the brightness value
     */
    public void updateBrightIndex() {
        try {
            int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightIndex = brightness / 42;
            changeBrightImage(brightness);
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 亮度调节
     * Adjust brightness
     */
    private void setScreenBrightness(int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        WindowManager.LayoutParams localLayoutParams = getActivity().getWindow().getAttributes();
        float f = paramInt / 252.0F;
        localLayoutParams.screenBrightness = f;
        getActivity().getWindow().setAttributes(localLayoutParams);
    }

    /**
     * 亮度加
     * Brighness up
     */
    public void brightAdd() {
        if (brightIndex < 6) {
            brightIndex++;
            setScreenBrightness(brightIndex * 42);
        }
    }

    /**
     * 亮度减
     * Brightness down
     */
    public void brightDec() {
        if (brightIndex > 0) {
            brightIndex--;
            setScreenBrightness(brightIndex * 42);
        }
    }

    private void initLocalData() {
        String localData = getPrefString(this, videoPath, "");
        if (!TextUtils.isEmpty(localData)) {
            String[] data = localData.split("-");
            videoType = Integer.parseInt(data[0]);
            videoModel = Integer.parseInt(data[1]);
        }
        updateTypePanelState();
        changeVideoType();
    }

    private void saveLocalData() {
        String localData = videoType + "-" + videoModel;
        setPrefString(this, videoPath, localData);
    }

    public static String getPrefString(Context context, String key, final String defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static void setPrefString(Context context, final String key, final String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }


    /**
     * 设置锁定、非锁定视角的状态下的休眠机制
     * Setting the sleep mechanism in locked and non-locked view
     */
    SysSleepApi sleepApi;
    ServiceConnection serviceConnection;
    private int tempSleepTime;

    public void lockView_neverSleep() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                sleepApi = SysSleepApi.Stub.asInterface(service);
                Log.d("Player", "aidl_LockView_onServiceConnected");
                try {
                    tempSleepTime = sleepApi.getSysSleepTime();
                    Log.d("Player", "aidl_LockView_getSysSleepTime:" + tempSleepTime);
                    if (tempSleepTime != -1) {
                        Log.d("Player", "aidl_LockView_setSysSleepTime:" + 604800);
                        sleepApi.setSysSleepTime(604800);
//                        Settings.Global.getInt(getActivity().getContentResolver(), "screen_out_time_seconds" , -1);
                        Log.d("Player", "aidl_LockView_setSysSleepTime-----:" + Settings.Global.getInt(getActivity().getContentResolver(), "screen_out_time_seconds", -1));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d("Player", "aidl_LockView_RemoteException:" + e.toString());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                sleepApi = null;
                Log.d("Player", "aidl_LockView_onServiceDisconnected");
            }
        };

        //BindService
        Intent intent = new Intent("com.nibiru.action.sleepservice");
        intent.setPackage("com.android.nibiru.settings.vr");
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    public void unLockView_resetSleep() {
        if (sleepApi != null) {
            try {
                if (tempSleepTime != -1) {
                    Log.d("Player", "aidl_unLockView_setSysSleepTime:" + tempSleepTime);
                    sleepApi.setSysSleepTime(tempSleepTime);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d("Player", "aidl_unLockView_resetSleep_Exception:" + e.toString());
            }
        }
        //unBindService
        if (serviceConnection != null) {
            try {
                getActivity().unbindService(serviceConnection);
                serviceConnection = null;
                Log.d("Player", "aidl_unbindService");
            } catch (Exception e) {
                Log.d("Player", "Exception：" + e.toString());
            }
        }
    }

}
