package com.nibiru.studio.vrscene;

import android.graphics.Color;

import com.nibiru.service.NibiruCameraDevice;
import com.nibiru.service.NibiruCameraService;
import com.nibiru.service.NibiruService;

import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XSpaceRect;
import x.core.ui.surface.XSurfaceArea;
import x.core.ui.surface.XSurfaceConfig;
import x.core.util.XVec3;

/**
演示SurfaceArea控件显示摄像头拍摄的内容
 */

/**
 Demonstrate SurfaceArea control displays the camera shooting contents
 */
public class SubSceneCamera extends XBaseScene {

    private NibiruCameraDevice mCameraDevice;
    private XSurfaceArea mCameraArea;

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

    public void init() {

        //初始化系统摄像头服务
        //Initialize system camera system
        NibiruCameraService mCameraService = NibiruService.getNibiruService(NibiruService.TYPE.CAMERA, this);

        //如果当前设备不存在摄像头给出提示
        //If the current device has no camera, give the hint
        if( !mCameraService.hasCamera() || mCameraService.getCameraNum() == 0 ){
            XLabel titleLabel = new XLabel("This AIO Device does not support Camera.");
            titleLabel.setAlignment(XLabel.XAlign.Center);
            titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
            titleLabel.setCenterPosition(0, 0f, -4f);
            titleLabel.setTextColor(Color.WHITE);
            titleLabel.setSize(0.8f, 0.3f);
            addActor(titleLabel);

            return;
        }


        //初始化一个SurfaceArea控件，该控件与摄像头服务绑定后可显示摄像头的内容
        //Initialize a SurfaceArea control, the camera shooting content can be displayed after the control binds camera service
        XVec3 center = new XVec3(0f, 0f, -4f);
        XSpaceRect pose = new XSpaceRect();
        pose.setFront(0, 0, 1);
        pose.setUp(0, 1, 0);

        //设置为2D平面模式，并设置参数
        //Set as 2D mode, and set parameters
        mCameraArea = new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_PLANE)
                .setCenter(center)
                .setSizeAdaptive(false)
                .setWidth(1.5f)
                .setHeight(1.0f)
                .setPosition(pose)
                .build();

        //添加控件
        //Add control
        addActor(mCameraArea);

        //======================== Use HMD Camera =====================================
        //使用Camera服务打开HMD摄像头
        //Use Camera service to open HMD camera
        mCameraDevice = mCameraService.openCamera(NibiruCameraService.CAMERA_TYPE.HMD);

        //锁定视野到正前方
        //Lock to the front
        lockTrackerToFront();

        //隐藏选中点
        //Hide selected gaze
        hideGaze();

        //开始预览，调用后摄像头服务拍摄到的内容会在SurfaceArea进行显示和更新
        //Start to preview, all the shot contents will be displayed and updated in SurfaceArea after it's call
        mCameraDevice.startPreview(mCameraArea.getSurfaceTexture());
    }

    //在Scene退出时必须关闭摄像头服务，如果有调用锁定视野必须解除锁定
    //The camera service must be disabled when Scene exits, if it's required to be called, please unlock the tracker
    @Override
    public void onDestroy() {
        if( mCameraDevice != null ) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        unlockTracker();
    }

}