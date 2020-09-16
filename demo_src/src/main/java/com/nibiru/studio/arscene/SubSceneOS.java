package com.nibiru.studio.arscene;

import android.graphics.Color;
import android.util.Log;

import com.nibiru.api.ThemeApiData;
import com.nibiru.lib.vr.listener.NVRScreenShotListener;
import com.nibiru.service.NibiruCameraService;
import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.service.NibiruOSService;
import com.nibiru.service.NibiruSensorEvent;
import com.nibiru.service.NibiruSensorService;
import com.nibiru.service.NibiruService;
import com.nibiru.studio.CalculateUtils;

import java.util.Arrays;
import java.util.List;

import ruiyue.controller.sdk.OnCServiceListener;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.util.XLog;

/**
 * 演示系统提供的API功能接口，该接口依赖Nibiru OS系统和版本，建议在3.5版本之上使用
 */

/**
 * Show OS API function interface provided by system, which depends on Nibiru OS system and version, v3.5 and above is recommended
 */
public class SubSceneOS extends BaseScene {


    XLabel mKeyEventLabel;

    XLabel mStartHMDSensor;

//    XLabel mDisplayMode;

    XLabel mHMDRotMatrixLabel;

    XLabel mScreenShotLabel;

    NibiruOSService mOSService;
    NibiruCameraService mCameraService;
    NibiruSensorService mSensorService;

    boolean isEnableHMDMatrix = false;

    boolean isVRModel = true;

    @Override
    public void onCreate() {
        super.onCreate();
        //Get system pulic service
        mOSService = NibiruService.getNibiruService(NibiruService.TYPE.OS, this);
        //获取摄像头服务
        //Get camera service
        mCameraService = NibiruService.getNibiruService(NibiruService.TYPE.CAMERA, this);
        //获取传感器服务
        //Get sensor service
        mSensorService = NibiruService.getNibiruService(NibiruService.TYPE.SENSOR, this);

        init();

        //获取外设连接相关数据
        //Get peripheral connection data
        getControllerManager().addCServiceListener(new OnCServiceListener() {
            @Override
            public void onCServiceResult(boolean ready) {
                if(ready) {
                    Log.e(XLog.TAG, "getCSupportDevices:" + getControllerManager().getCSupportDevices() + "::getCBikeEvent:" + getControllerManager().getCBikeEvent() + "::getCDevices:" + getControllerManager().getCDevices() + "::getCSensorEvent:" + getControllerManager().getCSensorEvent(0));

                }
            }
        });
        //获取系统设置参数
        //Get system setting parameters
        mOSService.addServerApiReadyListener(new NibiruOSService.IServerApiReadyListener() {
            @Override
            public void onServerApiReady(boolean ready) {
                if(ready) {
                    Log.v(XLog.TAG, "getDeviceName:" + mOSService.getDeviceName() + ":::getVRVersion:" + mOSService.getVRVersion() + "::getCurrentLanguage:" + mOSService.getCurrentLanguage() + ":getCurrentTimezone:" + mOSService.getCurrentTimezone());
                    ThemeApiData currentTheme = mOSService.getCurrentTheme();
                    List<ThemeApiData> allThemes = mOSService.getThemeList();
                }
            }
        });
        //获取系统休眠时间
        //Get system sleep time
        mOSService.addSysSleepApiReadyListener(new NibiruOSService.ISysSleepApiReadyListener() {
            @Override
            public void onSysSleepApiReady(boolean ready) {
                if(ready) {
                    Log.v(XLog.TAG,"getSysSleepTime:" + mOSService.getSysSleepTime());
                }
            }
        });

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

        mSensorService.unregisterSensorListenerAll();
    }

    float mStartY = 0.12f;
    float mStep = -0.05f;

    public XLabel addLabel(String value, int index){
        XLabel infoLabel1 = new XLabel(value);
        infoLabel1.setAlignment(XAlign.Center);
        infoLabel1.setArrangementMode(XArrangementMode.SingleRowNotMove);
        infoLabel1.setCenterPosition(0, mStartY + index * mStep , CalculateUtils.CENTER_Z);
        infoLabel1.setTextColor(Color.WHITE);
        infoLabel1.setSize(0.08f , 0.03f);
        addActor(infoLabel1);

        return infoLabel1;
    }


    public void init() {

        int index = 0;
        //获取设备品类，系统版本号和系统版本码
        //Get device mode, system version and version code
        addLabel("Model: "+mOSService.getModel()+" OS Ver: "+mOSService.getOSVersion()+" Code: "+mOSService.getOSVersionCode(), index++);

        //获取渠道号，系统服务版本号和软件固件版本号
        //Get channel code, system version and system version code
        addLabel("Channel: "+mOSService.getChannelCode()+" Service Ver: "+mOSService.getServiceVersionCode()+" Vendor SW Ver: "+mOSService.getVendorSWVersion(), index++);
//        addLabel("Camera Support: "+mCameraService.hasCamera()+" Count: "+mCameraService.getCameraNum(), index++);

        //新建Label显示按键键值
        //Create Label display key value
        mKeyEventLabel = addLabel("KeyEvent: Press KEY", index++);

        //切换2D/VR模式，该功能用于2D Activity界面使用，Studio界面无效
        //Switch 2D/VR mode, used in 2D Activity, useless in Studio
//        mDisplayMode = addLabel("Switch 2D/VR Display Mode", index++);
//        mDisplayMode.setEnableGazeAnimation(true);
//        mDisplayMode.setEventListener(new IXActorEventListener() {
//            @Override
//            public void onGazeEnter(XActor actor) {
//
//            }
//
//            @Override
//            public void onGazeExit(XActor actor) {
//
//            }
//
//            @Override
//            public boolean onGazeTrigger(XActor actor) {
//
//                if(isVRModel) {
//                    mOSService.setDisplayMode(NibiruOSService.DISPLAY_MODE.MODE_3D);
//                } else {
//                    mOSService.setDisplayMode(NibiruOSService.DISPLAY_MODE.MODE_2D);
//                }
//                isVRModel = !isVRModel;
//
//                /*
//                如果在AndroidManifest.xml对Activity加入了NVR标签则只能是VR模式，如果加入2D标签，则只能是2D模式，只有在未加入NVR/2D标签的应用可以调用切换VR模式的接口
//                这里启动TestActivity进行示例
//                 */
//                /*
//                If NVR Label is added to Activity in AndroidManifest.xml, it must be VR mode. If 2D label is added, it can only be 2D mode. Only when NVR/2D label is not added, the app can call VR mode switching API
//                Here take TestActivity as an example
//                 */
//
////                NibiruStudioUtils utils = NibiruStudioUtils.getInstance(SubSceneOS.this);
////
////                Intent intent = utils.getInternalActivityIntent(TestActivity.class);
////
////                intent.putExtra("request", 1000);
////
////                utils.startInternalActivity(intent);
//                return true;
//            }
//
//            @Override
//            public void onAnimation(XActor actor, boolean isSelected) {
//
//            }
//        });

        /*
        获取头部传感器数据，包括陀螺仪，加速计和地磁传感器
        The following codes depends on Nibiru OS version
         */
         /*
        Get head sensor data, including gyro, accelerometer and geomagnetic sensor
        The following codes depends on Nibiru OS version
         */
        mStartHMDSensor = addLabel("Start HMD Sensor (Print Data in Logcat)", index++);
        mStartHMDSensor.setEnableGazeAnimation(true);
        mStartHMDSensor.setTextColor(Color.GREEN);
        mStartHMDSensor.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            //开启/关闭获取传感器数据
            //Enable/disable getting sensor data
            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( mStartHMDSensor != null ){
                    if( mStartHMDSensor.getTextContent().startsWith("Start") ){
                        mStartHMDSensor.setTextContent("Stop HMD Sensor (Print Data in Logcat)");
                        mStartHMDSensor.setTextColor(Color.MAGENTA);
                        mSensorService.unregisterSensorListener(hmdSensorListener);
                        mSensorService.registerSensorListener(NibiruSensorService.SENSOR_TYPE.GYROSCOPE, NibiruSensorService.SENSOR_LOCATION.HMD, hmdSensorListener);
                        mSensorService.registerSensorListener(NibiruSensorService.SENSOR_TYPE.ACCELEROMETER, NibiruSensorService.SENSOR_LOCATION.HMD, hmdSensorListener);
                        mSensorService.registerSensorListener(NibiruSensorService.SENSOR_TYPE.MAGNETIC_FIELD, NibiruSensorService.SENSOR_LOCATION.HMD, hmdSensorListener);
                    }else if( mStartHMDSensor.getTextContent().startsWith("Stop") ){
                        mStartHMDSensor.setTextContent("Start HMD Sensor (Print Data in Logcat)");
                        mStartHMDSensor.setTextColor(Color.GREEN);
                        mSensorService.unregisterSensorListener(hmdSensorListener);

                    }
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        //获取头部旋转矩阵
        //Get head rotation matrix
        mHMDRotMatrixLabel = addLabel("Start HMD Rot Matrix (Print Data in Logcat)", index++);
        mHMDRotMatrixLabel.setEnableGazeAnimation(true);
        mHMDRotMatrixLabel.setTextColor(Color.GREEN);
        mHMDRotMatrixLabel.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( isEnableHMDMatrix ){
                    mHMDRotMatrixLabel.setTextContent("Start HMD Rot Matrix (Print Data in Logcat)");
                    mHMDRotMatrixLabel.setTextColor(Color.GREEN);
                }else{
                    mHMDRotMatrixLabel.setTextContent("Stop HMD Rot Matrix (Print Data in Logcat)");
                    mHMDRotMatrixLabel.setTextColor(Color.MAGENTA);

                }

                isEnableHMDMatrix = !isEnableHMDMatrix;
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        //需要系统支持
        mScreenShotLabel = addLabel("Start Screen Shot as sdcard/screenShot.png", index++);
        mScreenShotLabel.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                startScreenShot("sdcard/screenShot.png",  new NVRScreenShotListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed() {
                    }
                });
                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });


    }


    float[] mHeadRotationMatrix = new float[16];

    //Call back at each frame
    public void update(float deltaTime) {
        //IMPORTANT! HMD Rot Matrix is only available in Nibiru Stuido App. Normal 2D App cannot get this.
        //获取头部旋转矩阵
        //Get head rotation matrix
        if( isEnableHMDMatrix && mHMDRotMatrixLabel != null ){
            if( getHMDRotationMatrix(mHeadRotationMatrix) ) {
                Log.v(XLog.TAG, "HMD Rot Matrix: " + Arrays.toString(mHeadRotationMatrix));
            }else{
                Log.w(XLog.TAG, "HMD Rot Matrix get failed");
            }
        }
    }


    NibiruSensorService.INibiruSensorDataListener hmdSensorListener = new NibiruSensorService.INibiruSensorDataListener() {
        @Override
        public void onSensorDataChanged(NibiruSensorEvent nibiruSensorEvent) {
            Log.v(XLog.TAG, "[HMD] Sensor Data: "+nibiruSensorEvent);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode) {
        //按键按下时显示键值
        //Display key value when the key is pressed
        if( mKeyEventLabel != null ){
            mKeyEventLabel.setTextContent("KeyEvent: KEY->"+NibiruKeyEvent.getKeyEventDesc(keyCode)+" [KeyCode="+keyCode+"]");
        }
//        if( keyCode == NibiruKeyEvent.KEYCODE_UP ){
//            mOSService.setBrightnessValue(mOSService.getBrightnessValue()+1);
//        }else if( keyCode == NibiruKeyEvent.KEYCODE_DOWN ){
//            mOSService.setBrightnessValue(mOSService.getBrightnessValue()-1);
//        }
        return super.onKeyDown(keyCode);
    }

}
