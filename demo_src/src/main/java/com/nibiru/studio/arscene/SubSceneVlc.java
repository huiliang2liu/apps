package com.nibiru.studio.arscene;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;

import com.nibiru.studio.CalculateUtils;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

import x.core.ui.XBaseScene;
import x.core.ui.XSpaceRect;
import x.core.ui.surface.XSurfaceArea;
import x.core.ui.surface.XSurfaceConfig;
import x.core.util.XVec3;


/**
 * 基础的视频播放场景，不包含控制控件和事件处理
 * Basic video playback scenario without control and event handling
 */

public class SubSceneVlc extends XBaseScene implements XSurfaceArea.DragScaleSizeChangeListener {
    private static final String TAG = SubSceneVlc.class.getSimpleName();
    public static final float DISTANCE = 4f;
    public static final int TYPE_2D = 0;    //2d
    public static final int MODEL_PLANE = 0; //平面 plane

    private XSurfaceArea xSurfaceArea;


    @Override
    public void onCreate() {

        hideSkybox();//隐藏天空盒 hide skybox

        initPlayer();

        startPlay();
    }

    @Override
    public void onResume() {
        if(mMediaPlayer != null) {
            mMediaPlayer.play();
        }
    }

    @Override
    public void onPause() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        if (xSurfaceArea!=null){
            xSurfaceArea.changeDragScaleGazeStyle(XSurfaceConfig.DragScaleDirection.NULL);
        }
        stopPlay();
    }



    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    /**
     * 初始化播放器
     * init player
     */
    private void initPlayer() {

        //通过scene对象和视频参数初始化XSurfaceArea
        //Initialize XSurfaceArea through scene and video parameters
        xSurfaceArea = new XSurfaceArea.Builder(this, getVideoParameters(TYPE_2D, MODEL_PLANE)).build();

        //默认开启自动隐藏天空盒模式，也就是PLANE平面模式下显示天空盒，而其他模式都隐藏天空盒，如果不需要此功能可关闭
        //Enable auto hiding skybox mode by default, i.e. show skybox in PLANE mode, and hide skybox in other modes. It can be disabled if it's not necessary
        xSurfaceArea.setAutoHideSkybox(false);

        //添加SurfaceArea控件
        //Add SurfaceArea control
        addActor(xSurfaceArea);

        //设置SurfaceArea可选择
        xSurfaceArea.setCheckSelection(true);
        //打开对surfaceArea的拖拽缩放功能
        xSurfaceArea.openDragScaleFunction();
        //对拖拽缩放尺寸变化的回调
        xSurfaceArea.setDragScaleSizeChangeListener(this);
        //打开边缘检测是小白点图标切换开关,默认是关闭的
        xSurfaceArea.setOpenDragScaleEdgeCheck(true);
//        xSurfaceArea.rotateBy(0, 45, 0);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();

        vlcVout.setVideoSurface(xSurfaceArea.getSurfaceTexture());
        xSurfaceArea.getSurfaceTexture().setDefaultBufferSize(1080, 1080 * 9 / 16);
        mMediaPlayer.getVLCVout().setWindowSize(1080, 1080 * 9 / 16);
        vlcVout.attachViews();

        xSurfaceArea.setCheckSelection(true);

    }

    /**
     * 根据播放模式获取初始化参数
     * Get initialized parameters according to playback mode
     */
    private XSurfaceArea.Parameters getVideoParameters(int type, int model) {
        //设置SurfaceArea的中心点坐标
        //Set center coordinate of SurfaceArea
        XVec3 center = new XVec3(0f, 0f, CalculateUtils.CENTER_Z);

        //设置SurfaceArea的空间朝向，可选
        //Set the space orientation of SurfaceArea, optional
        XSpaceRect pose = new XSpaceRect();
        pose.setFront(0, 0, 1);
        pose.setUp(0, 1, 0);

        if (type == TYPE_2D) {
            if (model == MODEL_PLANE) {
                return new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_PLANE)
                        .setCenter(center)
                        //设置宽度
                        //Set width
                        .setWidth(0.6f)
                        .setHeight(0.4f)
                        //Set position orientation, optional
                        //设置姿态朝向，可选
//                        .setPosition(pose)
                        //设置是否支持选中，默认关闭
                        //Set whether to support selection, it's disabled by default
                        .setEnableSelection(true)
                        //直接构造参数实体对象
                        //Build parameter objects directly
                        .buildParameters();
            }
        }
        return null;
    }
    private static final String ASSET_FILENAME = "bbb.m4v";

    //开始播放
    //Start to play
    protected void startPlay() {

        //开启视频播放模式，开启后Studio将根据视频帧率进行渲染帧率调节，节约功耗
        //Enable video play mode, after it's enabled, Studio will adjust rendering frame rate according to the video fps to save consumption
        enableVideoMode();

        //为显示效果，可隐藏选中点
        //To show the effect, hide the Gaze point
//            hideGaze();
        Media media;
        //开始播放
        //Start to play
        try {

            if(isNetworkConnected(getApplicationContext())) {
                //播放网络URL视频文件，该视频源从网上获取，可能会失效需要开发者替换有效视频源
                //Play a web URL video file, which is obtained from the Internet and may be invalid. Developers need to replace the valid video source.
                media = new Media(mLibVLC, Uri.parse("http://gslb.miaopai.com/stream/nK9hXIZXkWRUdTgGgnlNfsd--NdGnxOyVJZsvw__.mp4?vend=miaopai&ssig=14903a310d873df1407fb765eb6060b9&time_stamp=1548226092862&mpflag=32"));

            } else {
                //播放ASSET目录下视频文件
                //Play video files in the ASSET directory
                media = new Media(mLibVLC, getAssets().openFd(ASSET_FILENAME));
            }
            //播放本地sdcard视频文件
            //Play local sdcard video files
            //final Media media = new Media(mLibVLC, "sdcard/movie.mp4");

            mMediaPlayer.setMedia(media);
        } catch (Exception e) {
            throw new RuntimeException("Invalid asset folder");
        }
        mMediaPlayer.play();

    }


    //停止播放
    //Stop to play
    protected void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            //如果是平面2D模式,如果处于拖拽缩放状态则取消拖拽缩放状态
            if (xSurfaceArea.isClickScaleEnable()) {
                xSurfaceArea.setClickScaleEnable(false);
                return true;
                //如果白点处于检测拖拽区域内则会进入拖拽缩放状态
            } else if (xSurfaceArea.gazeIsCorner()) {
                xSurfaceArea.setClickScaleEnable(true);
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果是平面2D模式,在返回的时候,如果处于拖拽缩放状态则取消拖拽缩放状态
            if (xSurfaceArea.isClickScaleEnable()) {
                xSurfaceArea.setClickScaleEnable(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode);
    }

    /**
     * 拖拽缩放数据的回调
     */
    @Override
    public void changeSizeCallback ( float vertical, float changesizeV, float horizontal, float changesizeH){
        Log.d(TAG, vertical == 1 ? "上边不动" + "--变化增量为==" + changesizeV : "下边不动" + "--变化增量为==" + changesizeV);
        Log.d(TAG, horizontal == 1 ? "右边不动" + "--变化增量为==" + changesizeH : "左边不动" + "--变化增量为==" + changesizeH);
    }
}
