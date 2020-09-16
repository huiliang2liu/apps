package com.nibiru.studio.arscene.SubSceneVideo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

import com.nibiru.studio.CalculateUtils;

import java.io.File;
import java.io.IOException;

import x.core.ui.XBaseScene;
import x.core.ui.XSpaceRect;
import x.core.ui.XToast;
import x.core.ui.surface.XSurfaceArea;
import x.core.ui.surface.XSurfaceConfig;
import x.core.util.MediaPlayerDecodeOpt;
import x.core.util.XLog;
import x.core.util.XVec3;

/**
 * Created by zxf on 2018/2/28.
 * <p>
 * 基础的视频播放场景，不包含控制控件和事件处理
 * Basic video playback scenario without control and event handling
 */

public class SubSceneVideoBase extends XBaseScene {

    private static final String TAG = SubSceneVideoBase.class.getSimpleName();

    //播放参数：
    //Play parameters
    public static final String VIDEO_PATH = "VIDEO_PATH";
    public static final String VIDEO_TYPE = "VIDEO_TYPE";
    public static final String VIDEO_MODEL = "VIDEO_MODEL";

    //播放模式：
    //Play mode
    public static final int TYPE_2D = 0;    //2d
    public static final int TYPE_3D_LEFTRIGHT = 1; //3d left right
    public static final int TYPE_3D_UPDOWN = 2;     //3d up down

    //播放模型：
    //Play model
    public static final int MODEL_PLANE = 0; //平面 plane
    public static final int MODEL_SPHERE = 1;   //球，全景  sphere
    public static final int MODEL_HALFSPHERE = 2;   //半球，半全景  half sphere
    public static final int MODEL_DOME = 3;    //球幕 dome


    protected MediaPlayer mediaPlayer;
    protected XSurfaceArea xSurfaceArea;

    private boolean isNeedOpt;
    private boolean hasPrepared;

    protected String videoPath;   //视频路径  video path
    protected int videoType; //播放模式 默认2d  play mode,default 2d
    protected int videoModel; //播放模型 默认平面  play model, default plane

    private boolean isPlaying;

    @Override
    public void onCreate() {

        hideSkybox();//隐藏天空盒 hide skybox

        handleIntent();

        initPlayer();
    }

    @Override
    public void onResume() {
        startPlay();
    }

    @Override
    public void onPause() {
        pausePlay();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");

        //在Scene销毁时停止播放并释放资源
        //Stop playing and release resources when Scene is destroyed
        if (mediaPlayer != null && hasPrepared) {
            stopPlay();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        hasPrepared = false;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            videoPath = intent.getStringExtra(VIDEO_PATH);
            videoType = intent.getIntExtra(VIDEO_TYPE, TYPE_2D);
            videoModel = intent.getIntExtra(VIDEO_MODEL, MODEL_PLANE);
        }
    }

    /**
     * 初始化播放器
     * init player
     */
    private void initPlayer() {

        //通过scene对象和视频参数初始化XSurfaceArea
        //Initialize XSurfaceArea through scene and video parameters
        xSurfaceArea = new XSurfaceArea.Builder(this, getVideoParameters(videoType, videoModel)).build();

        //默认开启自动隐藏天空盒模式，也就是PLANE平面模式下显示天空盒，而其他模式都隐藏天空盒，如果不需要此功能可关闭
        //Enable auto hiding skybox mode by default, i.e. show skybox in PLANE mode, and hide skybox in other modes. It can be disabled if it's not necessary
        xSurfaceArea.setAutoHideSkybox(false);

        hasPrepared = false;

        if (xSurfaceArea != null && videoPath != null) {

            //如果文件不存在弹出Toast提示
            //If no file exists, pop up Toast hint
            if (!new File(videoPath).exists()) {
                XToast.makeToast(this, "Cannot found video file: " + videoPath, 5000).show(true);
                return;
            }

            //添加SurfaceArea控件
            //Add SurfaceArea control
            addActor(xSurfaceArea);

            //获取SurfaceArea的纹理
            //Get SurfaceArea texture
            Surface surface = new Surface(xSurfaceArea.getSurfaceTexture());

            //初始化Android Media Player
            //Initialize Android Media Player
            mediaPlayer = new MediaPlayer();

            //重要！设置MediaPlayer显示的纹理，这里的纹理必须来自于SurfaceArea，否则将无法显示视频内容
            //Important!Set texture shown by MediaPlayer, which must be from SurfaceArea, or the video cannot be shown
            mediaPlayer.setSurface(surface);

            //设置视频尺寸监听，更新尺寸后会自适应画面大小
            //Set listener for video size, it will adapt the screen automatically after the update
            mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                    if (xSurfaceArea != null) {
                        xSurfaceArea.notifySurfaceSizeChanged(width, height);
                    }
                }
            });

            //在某些平台上对全景视频有专门优化，设置需要开启视频解码优化，平面模式无需开启
            //There are specific optimization for pano videos on some platforms, the video decode optimization should be enabled, but no need to enable it in plane mode
            if (isNeedOpt) {
                //In some platform, start opt decode of MediaPlayer can improve the quality of the 360 video image
                //After play end, remember to stop opt decode mode!
                MediaPlayerDecodeOpt.startVideoDecodeOpt(mediaPlayer);
            }

            //使用MediaPlayer播放视频，具体可参考Android Media Player的用法，这里仅做示例
            //Use MediaPlayer to play video, please refer to the usages of Android Media Player, here only provides the sample
            try {
                mediaPlayer.setDataSource(videoPath);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        hasPrepared = true;

                        if (isRunning()) {
                            //准备完成后开始播放
                            //Play after the preperation is done
                            XLog.logInfo("================ prepare finished, start play");
                            startPlay();
                        }
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        onVideoCompletion();
                    }
                });

                //异步执行准备
                //Prepare asynchronous execution
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 根据播放模式获取初始化参数
     * Get initialized parameters according to playback mode
     */
    private XSurfaceArea.Parameters getVideoParameters(int type, int model) {
        //设置SurfaceArea的中心点坐标
        //Set center coordinate of SurfaceArea
        XVec3 center = new XVec3(0f, 0f, CalculateUtils.Z - 1.5f);

        //设置SurfaceArea的空间朝向，可选
        //Set the space orientation of SurfaceArea, optional
        XSpaceRect pose = new XSpaceRect();
        pose.setFront(0, 0, 1);
        pose.setUp(0, 1, 0);

        //设置全景模式下的SurfaceArea的中心点坐标，为球心位置
        //Set the center coordinate of SurfaceArea under pano mode, i.e. the center of the sphere
        XVec3 center1 = new XVec3(0f, 0f, 0f);

        isNeedOpt = false;

        if (type == TYPE_2D) {
            if (model == MODEL_PLANE) {
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_PLANE)
                        .setCenter(center)
                        //设置宽度
                        //Set width
                        .setWidth(2f)
                        //设置姿态朝向，可选
                        //Set position orientation, optional
//                        .setPosition(pose)
                        //设置是否支持选中，默认关闭
                        //Set whether to support selection, it's disabled by default
                        .setEnableSelection(true)
                        //直接构造参数实体对象
                        //Build parameter objects directly
                        .buildParameters();
            } else if (model == MODEL_SPHERE) {
                isNeedOpt = true;
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_PANORAMA)
                        //设置球心坐标和半径
                        //Set coordinate and radius for the sphere center
                        .setCenter(center1)
                        .setRadius(10)
                        //设置球面网格，数字越大会越精细，但是性能开销也越大
                        //Set sphere mesh, the larger and more accurate the number is , the greater consumption the performance costs
                        .setSphereMesh(50, 30)
                        .buildParameters();
            } else if (model == MODEL_HALFSPHERE) {
                isNeedOpt = true;
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_SEMI_PANORAMA)
                        //设置球心的中心点，与平面模式不同
                        //Set the center point of sphere, different from plane mode
                        .setCenter(center1)
                        //设置球半径
                        //Set radius
                        .setRadius(10)
                        //设置球面网格，数字越大会越精细，但是性能开销也越大
                        //Set sphere mesh, the larger and more accurate the number is , the greater consumption the performance costs
                        .setSphereMesh(100, 50)
                        .buildParameters();
            } else if (model == MODEL_DOME) {
                isNeedOpt = true;
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_SPHERE)
                        .setCenter(center1)
                        .setRadius(10)
                        //设置球幕的显示角度范围，这里设置垂直方向-45度到+45度，水平方向-90度到+90度
                        //Set the angle degree range of displaying the dome, vertical: -45 degree to +45 degree, horizontal: -90 degree to +90 degree
                        .setSphereArea(-22, 33, -50, 50)
                        .buildParameters();

                /*
                如果视频是在经纬度覆盖范围都超过90度，例如从北极点朝下拍摄或者朝正前方拍摄，覆盖超过90度的范围，则采用下面的API
                 */
                 /*
                If the overlapping is over 90 degree in both warp and weft direction, e.g. taking looking down from the north or towards the front, the overlapping is over 90 degree, then adopt the following API
                 */

//                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_SPHERE)
//                            .setCenter(center1)
//                            .setRadius(10)
//                            //覆盖角度范围，这里用110度
//                            .setSubSphereAngle(110.0f)
//                            //是否为正前方开始覆盖
//                            .setSubSphereFront(true)
//                        //设置球面网格，数字越大会越精细，但是性能开销也越大
//                        .setSphereMesh(50, 25)
//                            .buildParameters();


                //如果拍摄原点不在顶部或者正前方，可以调用旋转接口
//                    xSurfaceArea.rotateBy(-90f, 0, 0);
            }
        } else {
            if (model == MODEL_PLANE) {
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_3D, XSurfaceConfig.Format.TYPE_PLANE)
                        .setCenter(center)
                        .setWidth(2f)
                        .setPosition(pose)
                        //设置是否支持选中，默认关闭
                        //Set whether to support selection, it's disabled by default
                        .setEnableSelection(true)
                        //设置为左右布局的3D视频，如果是上下布局的3D视频，设置为LAYOUT_VERTICAL
                        //Set as left-right 3D video, if it's top-bottom 3D video, set as LAYOUT_VERTICAL
                        .setVideoLayout(type == TYPE_3D_LEFTRIGHT ? XSurfaceConfig.Layout.LAYOUT_HORIZONTAL : XSurfaceConfig.Layout.LAYOUT_VERTICAL)
                        .buildParameters();
            } else if (model == MODEL_SPHERE) {
                isNeedOpt = true;
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_3D, XSurfaceConfig.Format.TYPE_PANORAMA)
                        .setCenter(center1)
                        .setRadius(10)
                        //设置为上下布局的3D视频，如果是左右布局的3D视频，设置为LAYOUT_HORIZONTAL
                        //Set as top-bottom 3D video, if it's left-right 3D video, set as LAYOUT_HORIZONTAL
                        .setVideoLayout(type == TYPE_3D_LEFTRIGHT ? XSurfaceConfig.Layout.LAYOUT_HORIZONTAL : XSurfaceConfig.Layout.LAYOUT_VERTICAL)
                        .buildParameters();
            } else if (model == MODEL_HALFSPHERE) {
                isNeedOpt = true;
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_3D, XSurfaceConfig.Format.TYPE_SEMI_PANORAMA)
                        //设置球心和半径
                        //Set sphere center and radius
                        .setCenter(center1)
                        .setRadius(10)
                        //设置为左右布局的3D视频，如果是上下布局的3D视频，设置为LAYOUT_VERTICAL
                        //Set as left-right 3D video,, if it's top-bottom 3D video, set as LAYOUT_VERTICAL
                        .setVideoLayout(type == TYPE_3D_LEFTRIGHT ? XSurfaceConfig.Layout.LAYOUT_HORIZONTAL : XSurfaceConfig.Layout.LAYOUT_VERTICAL)
                        .buildParameters();
            } else if (model == MODEL_DOME) {
                isNeedOpt = true;
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_3D, XSurfaceConfig.Format.TYPE_SPHERE)
                        .setCenter(center1)
                        .setRadius(10)
                        //设置球幕的显示角度范围，这里设置垂直方向-45度到+45度，水平方向-90度到+90度
                        //Set the angle degree range of displaying the dome, vertical: -45 degree to +45 degree, horizontal: -90 degree to +90 degree
                        .setSphereArea(-22, 33, -50, 50)
                        //设置为上下布局的3D视频，如果是左右布局的3D视频，设置为LAYOUT_HORIZONTAL
                        //Set as top-bottom 3D video, if it's left-right 3D video, set as LAYOUT_HORIZONTAL
                        .setVideoLayout(type == TYPE_3D_LEFTRIGHT ? XSurfaceConfig.Layout.LAYOUT_HORIZONTAL : XSurfaceConfig.Layout.LAYOUT_VERTICAL)
                        .buildParameters();
            }
        }
        return null;
    }


    //开始播放
    //Start to play
    protected void startPlay() {
        if (mediaPlayer != null && hasPrepared) {
            if (isNeedOpt) {
                MediaPlayerDecodeOpt.startVideoDecodeOpt(mediaPlayer);
            }

            //开启视频播放模式，开启后Studio将根据视频帧率进行渲染帧率调节，节约功耗
            //Enable video play mode, after it's enabled, Studio will adjust rendering frame rate according to the video fps to save consumption
            enableVideoMode();

            //为显示效果，可隐藏选中点
            //To show the effect, hide the Gaze point
//            hideGaze();

            //开始播放
            //Start to play
            mediaPlayer.start();

            isPlaying = true;
        }
    }

    //暂停播放
    //Pause the play
    protected void pausePlay() {
        if (mediaPlayer != null && hasPrepared) {
            //禁用视频播放模式，恢复正常帧率渲染
            //Disable video play mode, resume the normal rendering frame rate
            disableVideoMode();
            //显示选中点
            //Show the Gaze
//            showGaze();
            mediaPlayer.pause();

            isPlaying = false;
        }
    }

    //停止播放
    //Stop to play
    protected void stopPlay() {
        if (mediaPlayer != null && hasPrepared) {
            disableVideoMode();
//            showGaze();
            mediaPlayer.stop();

            //关闭视频优化功能
            //Disable video optimization
            if (isNeedOpt) {
                MediaPlayerDecodeOpt.stopVideoDecodeOpt(mediaPlayer);
            }

            isPlaying = false;
        }
    }

    //重播
    //Replay
    protected void handleReplay() {
        mediaPlayer.seekTo(0);
        startPlay();
    }

    private boolean isUpdateParameters = false;

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            isUpdateParameters = false;
        }
    };

    protected void changeVideoType() {
        if (!isUpdateParameters) {
            isUpdateParameters = true;
            XSurfaceArea.Parameters param = getVideoParameters(videoType, videoModel);
            //更新播放模式参数
            //Update playback mode parameters
            if (xSurfaceArea != null) {
                xSurfaceArea.updateParameters(param, updateRunnable);
            }
        }
    }

    protected void onVideoCompletion() {

    }

    protected void changeVideoPlayState() {
        if (isPlaying) {
            pausePlay();
        } else {
            startPlay();
        }
    }

    protected boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPrepared() {
        return hasPrepared;
    }
}
