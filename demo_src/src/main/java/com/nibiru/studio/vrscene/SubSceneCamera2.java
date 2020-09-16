package com.nibiru.studio.vrscene;

import x.core.ui.XBaseScene;

/**
演示SurfaceArea控件显示摄像头拍摄的内容
 */
public class SubSceneCamera2 extends XBaseScene {

//    private NibiruCamera2Device mCamera2Device;
//    private XSurfaceArea mCameraArea;

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

//        //初始化系统摄像头服务
//        final NibiruCamera2Service mCamera2Service = NibiruService.getNibiruService(NibiruService.TYPE.CAMERA2, this);
//
//        //如果当前设备不存在摄像头给出提示
//        if( !mCamera2Service.hasCamera() || mCamera2Service.getCameraNum() == 0 ){
//            XLabel titleLabel = new XLabel("This AIO Device does not support Camera.");
//            titleLabel.setAlignment(XLabel.XAlign.Center);
//            titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
//            titleLabel.setCenterPosition(0, 0f, -4f);
//            titleLabel.setTextColor(Color.WHITE);
//            titleLabel.setSize(0.8f, 0.3f);
//            addActor(titleLabel);
//
//            return;
//        }
//
//
//        //初始化一个SurfaceArea控件，该控件与摄像头服务绑定后可显示摄像头的内容
//        XVec3 center = new XVec3(0f, 0f, -4f);
//        XSpaceRect pose = new XSpaceRect();
//        pose.setFront(0, 0, 1);
//        pose.setUp(0, 1, 0);
//
//        //设置为2D平面模式，并设置参数
//        mCameraArea = new XSurfaceArea.Builder(this, XSurfaceConfig.Mode.MODE_2D, XSurfaceConfig.Format.TYPE_PLANE)
//                .setCenter(center)
//                .setSizeAdaptive(false)
//                .setWidth(1.5f)
//                .setHeight(1.0f)
//                .setPosition(pose)
//                .build();
//
//        //添加控件
//        addActor(mCameraArea);
//
//        //======================== Use HMD Camera =====================================
//        //使用Camera服务打开HMD摄像头
//        mCamera2Service.openCamera2(NibiruCamera2Service.CAMERA_TYPE.HMD, new NibiruCamera2Device.StateCallback() {
//            @Override
//            public void onCameraOpend(NibiruCamera2Device nibiruCamera2Device) {
//                mCamera2Device = nibiruCamera2Device;
//                //开始预览，调用后摄像头服务拍摄到的内容会在SurfaceArea进行显示和更新
//                mCamera2Device.startPreview(mCameraArea.getSurfaceTexture());
//            }
//
//            @Override
//            public void onCameraClosed(NibiruCamera2Device nibiruCamera2Device) {
//
//            }
//
//            @Override
//            public void onCameraError(NibiruCamera2Device nibiruCamera2Device, int i) {
//
//            }
//        });
//
//        //锁定视野到正前方
//        lockTrackerToFront();
//
//        //隐藏选中点
//        hideGaze();

    }

    //在Scene退出时必须关闭摄像头服务，如果有调用锁定视野必须解除锁定
    @Override
    public void onDestroy() {
//        if( mCamera2Device != null ) {
//            mCamera2Device.close();
//            mCamera2Device = null;
//        }
//
//        unlockTracker();
    }

}
