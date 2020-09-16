package com.nibiru.studio.arscene.SubSceneVideo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.nibiru.studio.BitmapUtils;
import com.nibiru.studio.CalculateUtils;
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
import x.core.ui.animation.XAnimation;

import static x.core.util.PicturesUtil.createBitmapTexture;
import static x.core.util.PicturesUtil.isBitmapTextureCreated;

/**
 * Created by zxf on 2018/3/1.
 * <p>
 * 包含控制控件和事件处理的视频播放场景
 */

/**
 * Video playback scene with control controls and event handling
 */

public class SubSceneVideo extends SubSceneVideoBase implements IXActorEventListener {

    /**
     * 图片纹理资源
     * Picture texture resources
     */
    public String textureBgGrey = "textureBgGrey"; //背景色 Background color
    public String textureBgWhite = "textureBgWhite"; //白色 White
    public String textureLineGrey = "textureLineGrey"; //进度条背景 ProgressBar background
    public String textureBgRoundGrey = "textureBgRoundGrey"; //圆角灰背景 Round gray background
    public String textureBgRoundWhite = "textureBgRoundWhite"; //圆角白背景 Rounded white background
    /**
     * 基础面板
     * Basic panel
     */

    private XPanel controlPanel;    //控制面板 control panel
    private XPanel completePanel;    //播放完成显示的面板 Play completed display panel
    private XPanel zoomPanel;    //画面缩放控制面板 Zoom control panel
    private XPanel morePanel;   //扩展选择面板 Extended selection panel
    /**
     * 控制面板的控件
     * Control Panel Controls
     */
    private XProgressBar progressBar;  //进度条 Progressbar
    private XLabel currentTimeShow;//当前时间显示 CurrentTime
    private XLabel totalTimeShow;//总时间显示 TotalTime
    private XImageText button_play; //播放按钮 Play button
    private XImageText voice;
    private XImageText bright;
    private XProgressBar voiceProgress;
    private XProgressBar brightProgress;
    private XImageText button_type;

    private XImageText control_showDisplay;
    private XImageText control_noSee;
    /**
     * 画面缩放控制面板的控件
     * Zoom Control Panel Controls
     */
    protected XProgressBar[] ctrlXProgressBar = new XProgressBar[2];
    protected XImageText[] btnCtrlDowns = new XImageText[2];
    protected XImageText[] btnCtrlUps = new XImageText[2];
    /**
     * 扩展选择面板的控件
     * Expand the control of the selection panel
     */
    private XImageText more_LoopPlay;//循环播放 Loop
    private XImageText more_notLoopPlay;//非循环播放 Non-looping
    /**
     * 状态和事件
     * Status and events
     */
    private int brightIndex;     //亮度 Brightness
    private int volumeIndex;     //音量 Volume
    private AudioManager mAudioManager;

    protected int moveIndex = 3;       //位置控制 Position Control
    protected int scaleIndex = 3;       //缩放控制 Scale Control

    private boolean isLoop; //循环播放 Loop
    private boolean tag_lockView_range; //锁定视角区域判断 Lock the viewing area judge
    private boolean tag_keyEvent_center;//是否锁定 Is it locked

    @Override
    public void onCreate() {
        super.onCreate();

        initControlPanel();
        initCompletePanel();
        initZoomPanel();
        initMorePanel();

        updateBtnTypeState();
        updateBrightIndex();
        updateVolumeIndex();

        updatePageSize();
        updatePageDistance();

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
        return new String[]{"videoicon.plist"};
    }

    /**
     * @param keyCode
     * @return 处理按键事件 Key Down Event
     */
    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == 255) {
            if (tag_keyEvent_center) {
                control_noSee.setEnabled(true);
                nibiruVRView.unlockTracker();
                tag_keyEvent_center = false;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            //锁定区域判断
            //Locked area judgment
            tag_lockView_range = XUI.getInstance().isGazeInArea(-13, 13, -9, 9);
            if (!tag_keyEvent_center && !zoomPanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled() && tag_lockView_range) {
                control_noSee.setEnabled(false);
                tag_keyEvent_center = true;
                cancelRotateView();
                nibiruVRView.lockTrackerToFront();
                return true;
            } else if (tag_keyEvent_center) {
                control_noSee.setEnabled(true);
                nibiruVRView.unlockTracker();
                tag_keyEvent_center = false;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeAdd();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDec();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP) {
            brightAdd();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN) {
            brightDec();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            pageLeave();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            pageNear();
            return true;
        }
        return super.onKeyDown(keyCode);
    }

    @Override
    public void onGazeEnter(XActor xActor) {
        if (!zoomPanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
            if (xActor.getName().equals("control_noSee")) {
                control_showDisplay.setEnabled(true);
                showGaze();
            }
            if (xActor.getName().equals("control_showDisplay")) {
                getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        control_showDisplay.setEnabled(false);
                        showControlPanel();
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

    @Override
    public void onGazeExit(XActor xActor) {
        if (!zoomPanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
            if (xActor.getName().equals("controlPanel")) {
                autoHideControlPanel();
            }
        }
    }

    /**
     * @param xActor
     * @return 处理控件点击事件 Handling control click events
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
                button_play.setImgName("btn_pause_s.png", "btn_pause.png");
            } else {
                button_play.setImgName("btn_play_s.png", "btn_play.png");
            }
            return true;
        } else if ("button_type".equals(name)) {
            if (videoType == TYPE_2D) {
                videoType = TYPE_3D_LEFTRIGHT;
            } else {
                videoType = TYPE_2D;
            }
            changeVideoType();
            updateBtnTypeState();
            return true;
        } else if ("button_zoom".equals(name)) {
            controlPanel.setEnabled(false);
            showZoomPanel();
            return true;
        } else if ("button_more".equals(name)) {
            controlPanel.setEnabled(false);
            showMorePanel();
            return true;
        } else if ("button_exit".equals(name)) {
            finish();
            return true;
        } else if (name.equals("complete_rePlay")) {
            handleReplay();
            completePanel.setEnabled(false);
            hideGaze();
            return true;
        } else if (name.equals("complete_back")) {
            finish();
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
        } else if (name.equals("zoomSure")) {
            zoomPanel.setEnabled(false);
            hideGaze();
            return true;
        } else if (name.equals("zoomDown0")) {
            pageZoomDown();
            return true;
        } else if (name.equals("zoomDown1")) {
            pageLeave();
            return true;
        } else if (name.equals("zoomUp0")) {
            pageZoomUp();
            return true;
        } else if (name.equals("zoomUp1")) {
            pageNear();
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
        if (!isBitmapTextureCreated(textureBgGrey)) {
            Bitmap bmpBgGrey = BitmapUtils.generateBitmapRect(32, 32, Color.parseColor("#99e2e2ff"));
            createBitmapTexture(bmpBgGrey, textureBgGrey);
        }

        if (!isBitmapTextureCreated(textureBgWhite)) {
            Bitmap bmpBgGrey = BitmapUtils.generateBitmapRect(32, 32, Color.parseColor("#ffffffff"));
            createBitmapTexture(bmpBgGrey, textureBgWhite);
        }

        if (!isBitmapTextureCreated(textureLineGrey)) {
            Bitmap bmpBgGrey = BitmapUtils.generateBitmapRect(32, 32, Color.parseColor("#ff757575"));
            createBitmapTexture(bmpBgGrey, textureLineGrey);
        }

        if (!isBitmapTextureCreated(textureBgRoundGrey)) {
            Bitmap bmpBgGrey = BitmapUtils.generateBitmapRoundRect(160, 60, Color.parseColor("#ff757575"), 30);
            createBitmapTexture(bmpBgGrey, textureBgRoundGrey);
        }

        if (!isBitmapTextureCreated(textureBgRoundWhite)) {
            Bitmap bmpBgGrey = BitmapUtils.generateBitmapRoundRect(160, 60, Color.parseColor("#ffffffff"), 30);
            createBitmapTexture(bmpBgGrey, textureBgRoundWhite);
        }

        controlPanel = new XPanel();
        controlPanel.setBackGround(textureBgGrey, 0);
        controlPanel.setCenterPosition(0f, CalculateUtils.transformSize(-350), CalculateUtils.Z);
        controlPanel.setSize(CalculateUtils.transformSize(1200), CalculateUtils.transformSize(200));
        controlPanel.setEventListener(this);
        controlPanel.setName("controlPanel");
        addActor(controlPanel);
        controlPanel.setEnabled(false);

        progressBar = new XProgressBar(0.0f, textureLineGrey, textureBgWhite, "circle_white.png");
        progressBar.setSize(CalculateUtils.transformSize(1140), CalculateUtils.transformSize(6));
        progressBar.setBarSizeRatio(0.02f, 3.8f);       //相对于长条的宽度和高度比例
        progressBar.setName("progressBar");
        progressBar.setProgressBarListener(progressBarListener);
        progressBar.setEventListener(this);
        controlPanel.addChild(progressBar, new XActorGroup.LayoutParam(0f, CalculateUtils.transformSize(70), 0.002f));

        currentTimeShow = new XLabel("00:00:00");
        currentTimeShow.setSize(CalculateUtils.transformSize(120), CalculateUtils.transformSize(24));
        currentTimeShow.setAlignment(XLabel.XAlign.Center);
        controlPanel.addChild(currentTimeShow, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-510), CalculateUtils.transformSize(40), 0.001f, 5));

        totalTimeShow = new XLabel("00:00:00");
        totalTimeShow.setSize(CalculateUtils.transformSize(120), CalculateUtils.transformSize(24));
        totalTimeShow.setAlignment(XLabel.XAlign.Center);
        controlPanel.addChild(totalTimeShow, new XActorGroup.LayoutParam(CalculateUtils.transformSize(510), CalculateUtils.transformSize(40), 0.001f, 5));

        button_play = new XImageText("btn_pause_s.png", "btn_pause.png");
        button_play.setSize(CalculateUtils.transformSize(130), CalculateUtils.transformSize(130));
        button_play.setSizeOfImage(CalculateUtils.transformSize(130), CalculateUtils.transformSize(130));
        button_play.setName("button_play");
        button_play.setEventListener(this);
        controlPanel.addChild(button_play, new XActorGroup.LayoutParam(0f, CalculateUtils.transformSize(-20), 0.001f));

        XImageText button_addVoice = new XImageText("btn_add_s.png", "btn_add.png");
        button_addVoice.setSize(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_addVoice.setSizeOfImage(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_addVoice.setName("button_addVoice");
        button_addVoice.setEventListener(this);
        controlPanel.addChild(button_addVoice, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-200), CalculateUtils.transformSize(-10), 0.001f));

        voice = new XImageText("btn_volume.png", "btn_volume.png");
        voice.setSize(CalculateUtils.transformSize(40), CalculateUtils.transformSize(40));
        voice.setSizeOfImage(CalculateUtils.transformSize(40), CalculateUtils.transformSize(40));
        controlPanel.addChild(voice, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-265), CalculateUtils.transformSize(-10), 0.001f));

        XImageText button_decVoice = new XImageText("btn_dec_s.png", "btn_dec.png");
        button_decVoice.setSize(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_decVoice.setSizeOfImage(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_decVoice.setName("button_decVoice");
        button_decVoice.setEventListener(this);
        controlPanel.addChild(button_decVoice, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-330), CalculateUtils.transformSize(-10), 0.001f));

        voiceProgress = new XProgressBar(0.0f, textureLineGrey, textureBgWhite);
        voiceProgress.setSize(CalculateUtils.transformSize(160f), CalculateUtils.transformSize(6f));
        controlPanel.addChild(voiceProgress, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-265), CalculateUtils.transformSize(-40), 0.001f));

        XImageText button_addLight = new XImageText("btn_add_s.png", "btn_add.png");
        button_addLight.setSize(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_addLight.setSizeOfImage(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_addLight.setName("button_addLight");
        button_addLight.setEventListener(this);
        controlPanel.addChild(button_addLight, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-415), CalculateUtils.transformSize(-10), 0.001f));

        bright = new XImageText("btn_bright.png", "btn_bright.png");
        bright.setSize(CalculateUtils.transformSize(40), CalculateUtils.transformSize(40));
        bright.setSizeOfImage(CalculateUtils.transformSize(40), CalculateUtils.transformSize(40));
        controlPanel.addChild(bright, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-480), CalculateUtils.transformSize(-10), 0.001f));

        XImageText button_decLight = new XImageText("btn_dec_s.png", "btn_dec.png");
        button_decLight.setSize(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_decLight.setSizeOfImage(CalculateUtils.transformSize(36), CalculateUtils.transformSize(36));
        button_decLight.setName("button_decLight");
        button_decLight.setEventListener(this);
        controlPanel.addChild(button_decLight, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-545), CalculateUtils.transformSize(-10), 0.001f));

        brightProgress = new XProgressBar(0.0f, textureLineGrey, textureBgWhite);
        brightProgress.setSize(CalculateUtils.transformSize(160f), CalculateUtils.transformSize(6f));
        controlPanel.addChild(brightProgress, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-480), CalculateUtils.transformSize(-40), 0.001f));

        button_type = new XImageText("btn_2d_s.png", "btn_2d.png");
        button_type.setSize(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_type.setSizeOfImage(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_type.setName("button_type");
        button_type.setEventListener(this);
        controlPanel.addChild(button_type, new XActorGroup.LayoutParam(CalculateUtils.transformSize(170), CalculateUtils.transformSize(-20), 0.001f));

        XImageText button_zoom = new XImageText("btn_zoom_s.png", "btn_zoom.png");
        button_zoom.setSize(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_zoom.setSizeOfImage(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_zoom.setName("button_zoom");
        button_zoom.setEventListener(this);
        controlPanel.addChild(button_zoom, new XActorGroup.LayoutParam(CalculateUtils.transformSize(290), CalculateUtils.transformSize(-20), 0.001f));

        XImageText button_more = new XImageText("btn_more_s.png", "btn_more.png");
        button_more.setSize(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_more.setSizeOfImage(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_more.setName("button_more");
        button_more.setEventListener(this);
        controlPanel.addChild(button_more, new XActorGroup.LayoutParam(CalculateUtils.transformSize(410), CalculateUtils.transformSize(-20), 0.001f));

        XImageText button_exit = new XImageText("btn_back_s.png", "btn_back.png");
        button_exit.setSize(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_exit.setSizeOfImage(CalculateUtils.transformSize(80), CalculateUtils.transformSize(80));
        button_exit.setName("button_exit");
        button_exit.setEventListener(this);
        controlPanel.addChild(button_exit, new XActorGroup.LayoutParam(CalculateUtils.transformSize(530), CalculateUtils.transformSize(-20), 0.001f));

        //播控条显隐延时控制按钮
        // Control panel display hidden delay control button
        control_showDisplay = new XImageText("btn_show_s.png", "btn_show.png");
        control_showDisplay.setSize(CalculateUtils.transformSize(100), CalculateUtils.transformSize(100));
        control_showDisplay.setSizeOfImage(CalculateUtils.transformSize(100), CalculateUtils.transformSize(100));
        control_showDisplay.setCenterPosition(0f, CalculateUtils.transformSize(-350), CalculateUtils.Z - CalculateUtils.transformSize(5));
        control_showDisplay.setName("control_showDisplay");
        control_showDisplay.setEventListener(this);
        addActor(control_showDisplay);
        control_showDisplay.setEnabled(false);

        //不可见的矩形控制位（当瞄准时，出现control_showDisplay）
        //Invisible rectangle control bit (control_showDisplay appears when aiming)
        control_noSee = new XImageText("transparent.png", "transparent.png");
        control_noSee.setSize(CalculateUtils.transformSize(1200), CalculateUtils.transformSize(250));
        control_noSee.setSizeOfImage(CalculateUtils.transformSize(1200), CalculateUtils.transformSize(250));
        control_noSee.setCenterPosition(0f, CalculateUtils.transformSize(-350), CalculateUtils.Z - CalculateUtils.transformSize(10));
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
        completePanel.setBackGround(textureBgGrey, 0);
        completePanel.setCenterPosition(0f, 0f, CalculateUtils.Z);
        completePanel.setSize(CalculateUtils.transformSize(400), CalculateUtils.transformSize(300));
        completePanel.setEventListener(this);
        addActor(completePanel);
        completePanel.setEnabled(false);

        XImageText complete_rePlay = new XImageText("btn_replay_s.png", "btn_replay.png");
        complete_rePlay.setTitle(getString(R.string.video_replay), getString(R.string.video_replay));//重播
        complete_rePlay.setTitleColor(Color.WHITE, Color.GRAY);
        complete_rePlay.setSize(CalculateUtils.transformSize(100), CalculateUtils.transformSize(100));
        complete_rePlay.setSizeOfImage(CalculateUtils.transformSize(100), CalculateUtils.transformSize(100));
        complete_rePlay.setTitlePosition(0, CalculateUtils.transformSize(-100));
        complete_rePlay.setSizeOfTitle(CalculateUtils.transformSize(200), CalculateUtils.transformSize(30));
        complete_rePlay.setSelectedAlign(XLabel.XAlign.Center);
        complete_rePlay.setUnselectedAlign(XLabel.XAlign.Center);
        complete_rePlay.setName("complete_rePlay");
        complete_rePlay.setEventListener(this);
        completePanel.addChild(complete_rePlay, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-100), 0f, 0.001f));

        XImageText complete_back = new XImageText("btn_exit_s.png", "btn_exit.png");
        complete_back.setTitle(getString(R.string.video_finish), getString(R.string.video_finish));//退出
        complete_back.setTitleColor(Color.WHITE, Color.GRAY);
        complete_back.setSize(CalculateUtils.transformSize(100), CalculateUtils.transformSize(100));
        complete_back.setSizeOfImage(CalculateUtils.transformSize(100), CalculateUtils.transformSize(100));
        complete_back.setTitlePosition(0.0f, CalculateUtils.transformSize(-100));
        complete_back.setSizeOfTitle(CalculateUtils.transformSize(200), CalculateUtils.transformSize(30));
        complete_back.setSelectedAlign(XLabel.XAlign.Center);
        complete_back.setUnselectedAlign(XLabel.XAlign.Center);
        complete_back.setName("complete_back");
        complete_back.setEventListener(this);
        completePanel.addChild(complete_back, new XActorGroup.LayoutParam(CalculateUtils.transformSize(100), 0f, 0.001f));
    }

    private void initZoomPanel() {
        zoomPanel = new XPanel();
        zoomPanel.setBackGround(textureBgGrey, 0);
        zoomPanel.setCenterPosition(0f, 0f, CalculateUtils.Z);
        zoomPanel.setSize(CalculateUtils.transformSize(500), CalculateUtils.transformSize(400));
        zoomPanel.setEventListener(this);
        addActor(zoomPanel);
        zoomPanel.setEnabled(false);

        int[] txtZoom = {R.string.video_scale, R.string.video_move};

        for (int i = 0; i < 2; i++) {
            btnCtrlDowns[i] = new XImageText("btn_dec_s.png", "btn_dec.png");
            btnCtrlDowns[i].setName("zoomDown" + i);
            btnCtrlDowns[i].setSize(CalculateUtils.transformSize(50), CalculateUtils.transformSize(50));
            btnCtrlDowns[i].setSizeOfImage(CalculateUtils.transformSize(50), CalculateUtils.transformSize(50));
            btnCtrlDowns[i].setEventListener(this);
            zoomPanel.addChild(btnCtrlDowns[i], new XActorGroup.LayoutParam(CalculateUtils.transformSize(-150), CalculateUtils.transformSize(40 + (-i + 0.5f) * 120), 0.001f));

            ctrlXProgressBar[i] = new XProgressBar(0, textureLineGrey, textureBgWhite);
            ctrlXProgressBar[i].setSize(CalculateUtils.transformSize(164), CalculateUtils.transformSize(6));
            zoomPanel.addChild(ctrlXProgressBar[i], new XActorGroup.LayoutParam(0, CalculateUtils.transformSize(40 + (-i + 0.5f) * 120), 0.001f));


            btnCtrlUps[i] = new XImageText("btn_add_s.png", "btn_add.png");
            btnCtrlUps[i].setName("zoomUp" + i);
            btnCtrlUps[i].setSize(CalculateUtils.transformSize(50), CalculateUtils.transformSize(50));
            btnCtrlUps[i].setSizeOfImage(CalculateUtils.transformSize(50), CalculateUtils.transformSize(50));
            btnCtrlUps[i].setEventListener(this);
            zoomPanel.addChild(btnCtrlUps[i], new XActorGroup.LayoutParam(CalculateUtils.transformSize(150), CalculateUtils.transformSize(40 + (-i + 0.5f) * 120), 0.001f));

            XLabel xLabelCtrl = new XLabel(txtZoom[i]);
            xLabelCtrl.setSize(CalculateUtils.transformSize(180), CalculateUtils.transformSize(40));
            xLabelCtrl.setAlignment(XLabel.XAlign.Center);
            xLabelCtrl.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
            zoomPanel.addChild(xLabelCtrl, new XActorGroup.LayoutParam(0, CalculateUtils.transformSize(80 + (-i + 0.5f) * 120), 0.001f));
        }

        String txtSure = getString(R.string.video_sure);
        XImageText btnSure = new XImageText(textureBgRoundWhite, textureBgRoundGrey);
        btnSure.setName("zoomSure");
        btnSure.setSize(CalculateUtils.transformSize(164), CalculateUtils.transformSize(64));
        btnSure.setSizeOfImage(CalculateUtils.transformSize(164), CalculateUtils.transformSize(64));
        btnSure.setTitle(txtSure, txtSure);
        btnSure.setTitlePosition(0f, 0f);
        btnSure.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        btnSure.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        btnSure.setTitleColor(Color.BLACK, Color.WHITE);
        btnSure.setSizeOfTitle(CalculateUtils.transformSize(160), CalculateUtils.transformSize(45));
        btnSure.setEventListener(this);
        zoomPanel.addChild(btnSure, new XActorGroup.LayoutParam(0, CalculateUtils.transformSize(-130), 0.001f));
    }

    /**
     * 初始化扩展选择面板
     * Initialize the expansion selection panel
     */
    private void initMorePanel() {

        morePanel = new XPanel();
        morePanel.setBackGround(textureBgGrey, 0);
        morePanel.setCenterPosition(0f, 0f, CalculateUtils.Z);
        morePanel.setSize(CalculateUtils.transformSize(400), CalculateUtils.transformSize(300));
        morePanel.setEventListener(this);
        addActor(morePanel);
        morePanel.setEnabled(false);

        XLabel more_loop = new XLabel(getString(R.string.video_loop));//循环播放
        more_loop.setSize(CalculateUtils.transformSize(400), CalculateUtils.transformSize(30));
        more_loop.setAlignment(XLabel.XAlign.Center);
        morePanel.addChild(more_loop, new XActorGroup.LayoutParam(0f, CalculateUtils.transformSize(80), 0.001f));

        more_LoopPlay = new XImageText(textureBgWhite, textureLineGrey);
        more_LoopPlay.setTitle(getString(R.string.video_yes), getString(R.string.video_yes));//开
        more_LoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
        more_LoopPlay.setSize(CalculateUtils.transformSize(120), CalculateUtils.transformSize(40));
        more_LoopPlay.setSizeOfImage(CalculateUtils.transformSize(120), CalculateUtils.transformSize(40));
        more_LoopPlay.setTitlePosition(0, 0);
        more_LoopPlay.setSizeOfTitle(CalculateUtils.transformSize(120), CalculateUtils.transformSize(24));
        more_LoopPlay.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        more_LoopPlay.setName("more_LoopPlay");
        more_LoopPlay.setEventListener(this);
        morePanel.addChild(more_LoopPlay, new XActorGroup.LayoutParam(CalculateUtils.transformSize(-80), 0, 0.001f));

        more_notLoopPlay = new XImageText(textureBgWhite, textureLineGrey);
        more_notLoopPlay.setTitle(getString(R.string.video_no), getString(R.string.video_no));//开
        more_notLoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
        more_notLoopPlay.setSize(CalculateUtils.transformSize(120), CalculateUtils.transformSize(40));
        more_notLoopPlay.setSizeOfImage(CalculateUtils.transformSize(120), CalculateUtils.transformSize(40));
        more_notLoopPlay.setTitlePosition(0, 0);
        more_notLoopPlay.setSizeOfTitle(CalculateUtils.transformSize(120), CalculateUtils.transformSize(24));
        more_notLoopPlay.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        more_notLoopPlay.setName("more_notLoopPlay");
        more_notLoopPlay.setEventListener(this);
        morePanel.addChild(more_notLoopPlay, new XActorGroup.LayoutParam(CalculateUtils.transformSize(80), 0, 0.001f));

        XImageText sure = new XImageText(textureBgRoundWhite, textureBgRoundGrey);
        sure.setTitle(getString(R.string.video_sure), getString(R.string.video_sure));//确定
        sure.setTitleColor(Color.BLACK, Color.WHITE);
        sure.setSize(CalculateUtils.transformSize(160), CalculateUtils.transformSize(60));
        sure.setSizeOfImage(CalculateUtils.transformSize(160), CalculateUtils.transformSize(60));
        sure.setTitlePosition(0, 0);
        sure.setSizeOfTitle(CalculateUtils.transformSize(150), CalculateUtils.transformSize(34));
        sure.setTitleAlign(XLabel.XAlign.Center, XLabel.XAlign.Center);
        sure.setName("more_sure");
        sure.setEventListener(this);
        morePanel.addChild(sure, new XActorGroup.LayoutParam(0f, CalculateUtils.transformSize(-80), 0.001f));
    }

    /**
     * 更新扩展面板控件的选中状态
     * Update the expanded panel control's selected state
     */
    private void updateMorePanelState() {
        if (isLoop) {
            more_LoopPlay.setImgName(textureBgWhite, textureBgWhite);
            more_LoopPlay.setTitleColor(Color.BLACK, Color.BLACK);
            more_notLoopPlay.setImgName(textureBgWhite, textureLineGrey);
            more_notLoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
        } else {
            more_LoopPlay.setImgName(textureBgWhite, textureLineGrey);
            more_LoopPlay.setTitleColor(Color.BLACK, Color.WHITE);
            more_notLoopPlay.setImgName(textureBgWhite, textureBgWhite);
            more_notLoopPlay.setTitleColor(Color.BLACK, Color.BLACK);
        }
    }

    /**
     * 更新循环模式选择按钮的选中状态
     * Update cycle mode selection button selection
     */
    private void updateBtnTypeState() {
        if (videoType == TYPE_2D) {
            button_type.setImgName("btn_2d_s.png", "btn_2d.png");
        } else {
            button_type.setImgName("btn_3d_s.png", "btn_3d.png");
        }
    }

    private void showControlPanel() {
        showGaze();
        controlPanel.setEnabled(true);
    }

    private void showCompletePanel() {
        showGaze();
        completePanel.setEnabled(true);
    }

    private void showMorePanel() {
        showGaze();
        morePanel.setEnabled(true);
        updateMorePanelState();
    }

    private void hideAllPanel() {
        controlPanel.setEnabled(false);
        completePanel.setEnabled(false);
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
                if (!zoomPanel.isEnabled() && !morePanel.isEnabled() && !completePanel.isEnabled()) {
                    hideGaze();
                }
            }
        };
        getMainHandler().postDelayed(hideControl, 3000);
    }

    private void showZoomPanel() {
        showGaze();
        zoomPanel.setEnabled(true);
        updateZoomPanelState();
    }

    protected void updateZoomPanelState() {
        float scaleProgress = scaleIndex / 6f;
        float moveProgress = moveIndex / 6f;
        ctrlXProgressBar[0].setProcess(scaleProgress);
        ctrlXProgressBar[1].setProcess(moveProgress);

        if (scaleIndex == 0) {
            btnCtrlDowns[0].setImgName("btn_dec_no.png", "btn_dec_no.png");
            btnCtrlDowns[0].setEnableGazeAnimation(false);
        } else {
            btnCtrlDowns[0].setImgName("btn_dec_s.png", "btn_dec.png");
            btnCtrlDowns[0].setGazeAnimation(XAnimation.AnimationType.TRANSLATE, CalculateUtils.transformSize(30), 150);
        }
        if (scaleIndex == 6) {
            btnCtrlUps[0].setImgName("btn_add_no.png", "btn_add_no.png");
            btnCtrlUps[0].setEnableGazeAnimation(false);
        } else {
            btnCtrlUps[0].setImgName("btn_add_s.png", "btn_add.png");
            btnCtrlUps[0].setGazeAnimation(XAnimation.AnimationType.TRANSLATE, CalculateUtils.transformSize(30), 150);
        }

        if (moveIndex == 0) {
            btnCtrlDowns[1].setImgName("btn_dec_no.png", "btn_dec_no.png");
            btnCtrlDowns[1].setEnableGazeAnimation(false);
        } else {
            btnCtrlDowns[1].setImgName("btn_dec_s.png", "btn_dec.png");
            btnCtrlDowns[1].setGazeAnimation(XAnimation.AnimationType.TRANSLATE, CalculateUtils.transformSize(30), 150);
        }
        if (moveIndex == 6) {
            btnCtrlUps[1].setImgName("btn_add_no.png", "btn_add_no.png");
            btnCtrlUps[1].setEnableGazeAnimation(false);
        } else {
            btnCtrlUps[1].setImgName("btn_add_s.png", "btn_add.png");
            btnCtrlUps[1].setGazeAnimation(XAnimation.AnimationType.TRANSLATE, CalculateUtils.transformSize(30), 150);
        }
    }

    public void pageZoomUp() {
        if (scaleIndex < 6) {
            scaleIndex += 1;
            updatePageSize();
            updateZoomPanelState();
        }
    }

    public void pageZoomDown() {
        if (scaleIndex > 0) {
            scaleIndex -= 1;
            updatePageSize();
            updateZoomPanelState();
        }
    }

    public void pageNear() {
        if (moveIndex < 6) {
            moveIndex += 1;
            updatePageDistance();
            updateZoomPanelState();
        }
    }

    public void pageLeave() {
        if (moveIndex > 0) {
            moveIndex -= 1;
            updatePageDistance();
            updateZoomPanelState();
        }
    }

    protected void updatePageSize() {
        xSurfaceArea.getLeftSurface().setCenterZ(-3f + 0.45f * (moveIndex - 3));
        xSurfaceArea.getRightSurface().setCenterZ(-3f + 0.45f * (moveIndex - 3));
    }

    protected void updatePageDistance() {
        xSurfaceArea.resize(2f * ((float) Math.pow(1.26f, scaleIndex - 3f)), 1.125f * ((float) Math.pow(1.26f, scaleIndex - 3f)));
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
            handleReplay();
        } else {
            if (tag_keyEvent_center) {
                control_noSee.setEnabled(true);
                nibiruVRView.unlockTracker();
                tag_keyEvent_center = false;
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
     * @param time
     * @return 时间格式转换 Time format conversion
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
     * 更新音量控件 Update volume controls
     */
    private void changeVoiceImage(int tempVolume) {
        voiceProgress.setProcess(tempVolume / 6f);
        if (tempVolume == 0) {
            voice.setImgName("btn_volume_no.png", "btn_volume_no.png");
        } else {
            voice.setImgName("btn_volume.png", "btn_volume.png");
        }
    }

    /**
     * 更新亮度控件 Update brightness control
     */
    private void changeBrightImage(int tempBrightness) {
        brightProgress.setProcess(tempBrightness / 6f);
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
     * Volumn up
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
     * Volumn down
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
     *Brightness monitoring
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
            changeBrightImage(brightIndex);
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 亮度调节
     * Set Screen Brightness
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
     * Brigheness up
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

}
