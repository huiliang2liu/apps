package com.nibiru.studio.arscene.SubSceneVideo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.studio.CalculateUtils;

import java.io.File;
import java.io.IOException;

import x.core.ui.XBaseScene;
import x.core.ui.XSkyBox;
import x.core.ui.XSpaceRect;
import x.core.ui.XToast;
import x.core.ui.surface.XSurfaceArea;
import x.core.ui.surface.XSurfaceConfig;
import x.core.util.MediaPlayerDecodeOpt;
import x.core.util.XLog;
import x.core.util.XVec3;


/**
 * 演示视频播放功能实现
 * 包括2D/3D模式，平面/180/360/球幕等视频类型，示例中使用Android MediaPlayer作为解码框架，开发者可根据需要决定解码框架
 * @author Steven
 */

/**
 * Show realizing video play function
 * Including 2D/3D mode, plane/180/360/fulldome formats. Android MediaPlayer is used as decode frame in the sample, and the developer can decide the frame according to demands
 * @author Steven
 */
public class SubSceneVideoAll2 extends XBaseScene {

    private static final String TAG = SubSceneVideoAll2.class.getSimpleName();

    public static final String PLAY_MODE = "PLAY_MODE";

    String[] mModeArray = {"2DNormal", /*"2D180", "2D360", "2DSphere", */"3DNormal", /*"3D180", "3D360", "3DSphere"*/};

    public static final int NORMAL_2D   = 0;
//    public static final int HALF_2D     = 1;
//    public static final int CIRCLE_2D   = 2;
//    public static final int SPHERE_2D   = 3;
    public static final int NORMAL_3D   = 1;
//    public static final int HALF_3D     = 5;
//    public static final int CIRCLE_3D   = 6;
//    public static final int SPHERE_3D   = 7;

    public int mCurrentPlayMode = NORMAL_2D;


    MediaPlayer mPlayer;
    XSurfaceArea m_VideoArea;

    boolean isNeedOpt = false;

    String videoPath = null;
    boolean hasPrepared = false;


    @Override
    public void onCreate() {
        //获取由SubSceneVideoPlayer传入的播放模式的参数
        //Get playback mode parameters passing by SubSceneVideoPlayer
        loadExtra();

        //初始化SurfaceArea和MediaPlayer
        //Initialize SurfaceArea and MediaPlayer
        init();
    }

    private void loadExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            int playMode = intent.getIntExtra(PLAY_MODE, NORMAL_2D);
            setPlayMode(playMode);
        }else{
            setPlayMode(NORMAL_2D);
        }
    }

    public void setPlayMode(int mode) {
        mCurrentPlayMode = mode;
    }

    //根据播放模式获取初始化参数
    //Get initialized parameters according to playback mode
    public XSurfaceArea.Parameters getVideoParameters(int mode){
        //设置SurfaceArea的中心点坐标
        //Set center coordinate of SurfaceArea
        XVec3 center = new XVec3(0f, 0f, CalculateUtils.CENTER_Z);

        //设置SurfaceArea的空间朝向
        //Set the space orientation of SurfaceArea, optional
        XSpaceRect pose = new XSpaceRect();
        pose.setFront(0, 0, 1);
        pose.setUp(0, 1, 0);

        //设置全景模式下的SurfaceArea的中心点坐标，为球心位置
        //Set the center coordinate of SurfaceArea under pano mode, i.e. the center of the sphere
        XVec3 center1 = new XVec3(0f, 0f, 0f);

        isNeedOpt = false;

        switch(mode){
            case NORMAL_2D:{
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_PLANE)
                        .setCenter(center)
                        //设置宽度
                        //Set width
                        //设置宽度
                        .setWidth(0.5f)
                        .setPosition(pose)
                        .buildParameters();

            }
            case NORMAL_3D:{
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_3D, XSurfaceConfig.Format.TYPE_PLANE)
                        .setCenter(center)
                        .setWidth(0.5f)
                        .setPosition(pose)
                        //设置为左右布局的3D视频，如果是上下布局的3D视频，设置为LAYOUT_VERTICAL
                        //Set as left-right 3D video, if it's top-bottom 3D video, set as LAYOUT_VERTICAL
                        .setVideoLayout(XSurfaceConfig.Layout.LAYOUT_HORIZONTAL)
                        .buildParameters();

            }
            default:
                return null;

        }

    }

    public void init() {

        //通过scene对象和视频参数初始化XSurfaceArea
        //Initialize XSurfaceArea through scene and video parameters
        m_VideoArea = new XSurfaceArea.Builder(this, getVideoParameters(mCurrentPlayMode)).build();

        if( mCurrentPlayMode < 0 || mCurrentPlayMode >= mModeArray.length ){
            return;
        }

        videoPath = "/sdcard/"+mModeArray[mCurrentPlayMode]+".mp4";

        hasPrepared = false;

        if( m_VideoArea != null && videoPath != null ) {

            //如果文件不存在弹出Toast提示
            //If no file exists, pop up Toast hint
            if( !new File(videoPath).exists() ){
                XToast.makeToast(this, "Cannot found video file: "+videoPath, 5000).show(true);
                return;
            }

            //添加SurfaceArea控件
            //Add SurfaceArea control
            addActor(m_VideoArea);

            //获取SurfaceArea的纹理
            //Get SurfaceArea texture
            Surface surface = new Surface(m_VideoArea.getSurfaceTexture());


            //初始化Android Media Player
            //Initialize Android Media Player
            mPlayer = new MediaPlayer();

            //重要！设置MediaPlayer显示的纹理，这里的纹理必须来自于SurfaceArea，否则将无法显示视频内容
            //Important!Set texture shown by MediaPlayer, which must be from SurfaceArea, or the video cannot be shown
            mPlayer.setSurface(surface);

            //设置视频尺寸监听，更新尺寸后会自适应画面大小
            //Set listener for video size, it will adapt the screen automatically after the update
            mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                    if( m_VideoArea != null ){
                        m_VideoArea.notifySurfaceSizeChanged(width, height);
                    }
                }
            });

            //在某些平台上对全景视频有专门优化，设置需要开启视频解码优化，平面模式无需开启
            //There are specific optimization for pano videos on some platforms, the video decode optimization should be enabled, but no need to enable it in plane mode
            if( isNeedOpt ){
                //In some platform, start opt decode of MediaPlayer can improve the quality of the 360 video image
                //After play end, remember to stop opt decode mode!
                MediaPlayerDecodeOpt.startVideoDecodeOpt(mPlayer);
            }

            //使用MediaPlayer播放视频，具体可参考Android Media Player的用法，这里仅做示例
            //Use MediaPlayer to play video, please refer to the usages of Android Media Player, here only provides the sample
            try {
                mPlayer.setDataSource(videoPath);
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        hasPrepared = true;

                        if( isRunning() ) {
                            //准备完成后开始播放
                            //Play after the preperation is done
                            XLog.logInfo("================ prepare finished, start play");
                            startPlay();
                        }
                    }
                });


                //异步执行准备
                //Prepare asynchronous execution
                mPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        showCurrentPlayMode();
    }



    //开始播放
    //Start to play
    void startPlay(){
        if (mPlayer != null && hasPrepared ) {
            if( isNeedOpt ){
                MediaPlayerDecodeOpt.startVideoDecodeOpt(mPlayer);
            }

            //开启视频播放模式，开启后Studio将根据视频帧率进行渲染帧率调节，节约功耗
            //Enable video play mode, after it's enabled, Studio will adjust rendering frame rate according to the video fps to save consumption
            enableVideoMode();

            //为显示效果，可隐藏选中点
            //To show the effect, hide the Gaze point
            hideGaze();

            //开始播放
            //Start to play
            mPlayer.start();
        }
    }

    //暂停播放
    //Pause the play
    void pausePlay(){
        if (mPlayer != null && hasPrepared ) {
            //禁用视频播放模式，恢复正常帧率渲染
            //Disable video play mode, resume the normal rendering frame rate
            disableVideoMode();
            //显示选中点
            //Show the Gaze
            showGaze();
            mPlayer.pause();
        }
    }

    //停止播放
    //Stop to play
    void stopPlay(){
        if (mPlayer != null && hasPrepared ) {
            disableVideoMode();
            showGaze();
            mPlayer.stop();

            //关闭视频优化功能
            //Disable video optimization
            if( isNeedOpt ){
                MediaPlayerDecodeOpt.stopVideoDecodeOpt(mPlayer);
            }
        }
    }
    @Override
    public void onPause() {
        pausePlay();
    }

    @Override
    public void onResume() {
        startPlay();
    }

    //在Scene销毁时停止播放并释放资源
    //Stop to play and release the resource when Scene is destroyed
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        if (mPlayer != null && hasPrepared ) {
            stopPlay();
        }

        if(mPlayer != null ) {
            mPlayer.release();
        }
        hasPrepared = false;
    }

    boolean isUpdateParameters = false;

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            isUpdateParameters = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode) {

        if( keyCode == NibiruKeyEvent.KEYCODE_ENTER ){
            //暂停/恢复播放
            //Pause/resume the play
            if( mPlayer != null && mPlayer.isPlaying() ){
                pausePlay();
            }else if( mPlayer != null && !mPlayer.isPlaying() ){
                startPlay();
            }

        }
        //上下方向键循环更新当前播放模式，如机器没有上下方向键，可用改为其他按键触发
        //Up and down direction keys cycle the current play mode, if there's no up and down direction key, other keys can replace them
        else if( keyCode == NibiruKeyEvent.KEYCODE_UP ){
            if(!isUpdateParameters) {
                isUpdateParameters = true;
                --mCurrentPlayMode;
                if (mCurrentPlayMode < 0) {
                    mCurrentPlayMode = mModeArray.length - 1;
                }

                XSurfaceArea.Parameters param = getVideoParameters(mCurrentPlayMode);

                //更新播放模式参数
                //Update play mode parameters
                if (m_VideoArea != null) {
                    m_VideoArea.updateParameters(param, updateRunnable);
                }

                showCurrentPlayMode();
            }


        }else if( keyCode == NibiruKeyEvent.KEYCODE_DOWN ){
            if(!isUpdateParameters) {
                isUpdateParameters = true;
                ++mCurrentPlayMode;
                if (mCurrentPlayMode >= mModeArray.length) {
                    mCurrentPlayMode = 0;
                }

                XSurfaceArea.Parameters param = getVideoParameters(mCurrentPlayMode);

                //更新播放模式参数
                //Update play mode parameters
                if (m_VideoArea != null) {
                    m_VideoArea.updateParameters(param, updateRunnable);
                }


                showCurrentPlayMode();
            }
        }

        return super.onKeyDown(keyCode);
    }

    void showCurrentPlayMode(){
        //弹出提示显示当前播放模式
        //Pop up hint to show the current playback mode
        XToast toast = XToast.makeToast(this, "Current Play Mode: "+mModeArray[mCurrentPlayMode], 2000);

        if( mCurrentPlayMode == NORMAL_2D || mCurrentPlayMode == NORMAL_3D ) {
            //设置坐标，显示在画面下方
            //Set coordinate, shown below the screen
            toast.setGravity(0.0f, -0.18f, CalculateUtils.CENTER_Z + 0.05f);
            toast.setSize(0.4f, 0.03f);
            //平面模式保持在场景正前方显示
            //Plane mode kept to be shown in the front of scene
            toast.show(false);
        }else{
            //全景模式在选中点所在位置显示
            //The pano mode is shown in the gaze position
            toast.show(true);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        XSkyBox skybox = getCurrentSkybox();
        if( skybox != null ) {
            XLog.logErrorInfo("update: "+skybox.getName()+" enable: "+skybox.isEnabled()+" video mode: "+getNibiruVRService().isVideoMode());
        }
    }
}
