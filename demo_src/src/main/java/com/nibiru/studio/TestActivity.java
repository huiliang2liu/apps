package com.nibiru.studio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.service.NibiruOSService;
import com.nibiru.studio.xrdemo.R;

import x.core.GlobalApplication;
import x.core.ui.XUI;
import x.core.util.NibiruStudioUtils;


/**
 * 演示Scene与其他Activity共存于同一个应用，Activity将默认采用2D模式显示
 * Created by Steven on 2017/11/30.
 */

/**
 * Present Scene and other gerneral(2D) Activity exist in the same app, Activity adopts 2D mode to display by default
 * Created by Steven on 2017/11/30.
 */

public class TestActivity extends Activity {

    RelativeLayout mRelativeLayout;
    TextView mTip;
    TextView mParam;
    boolean isRequestForResult = false;

    boolean isTestForSceneResult = false;

    boolean isSampleSwitchVRMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_activity);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_main);

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //演示切换2D/3D模式
                //Demostrate switching 2D/3D mode
                if (isSampleSwitchVRMode) {

                    NibiruOSService mOService = NibiruStudioUtils.getInstance(TestActivity.this).getNibiruOService();

                    //切换VR模式和2D模式，在2D模式下，界面会自动分为左右两屏显示，使用与传统Android APP；VR模式下，系统不会自动分屏，适用于VR APP
                    //Switch VR mode and 2D mode, in 2D mode, the interface is displayed as left and right split screen automatically, and it's applied to traditional APP; in VR mode, system will not split the screen automatically, and it's applied to VR APP
                    mOService.setDisplayMode(isVRMode ? NibiruOSService.DISPLAY_MODE.MODE_2D : NibiruOSService.DISPLAY_MODE.MODE_3D);

                    isVRMode = !isVRMode;

                    return;
                }
                //演示设置返回值传递给请求的Scene
                //The demonstrated return value is passed to the request Scene
                else if (isRequestForResult) {
                    //当确定键按下设置Result并返回上一个Scene
                    //When the ok button is pressed, set Result and return to the last Scene
                    //直接设置result最后finish，与Activity类似
                    //Please set result directly and finish, similar to Activity
                    Intent resultData = new Intent();
                    resultData.putExtra("result", "result is OK!");
                    setResult(501, resultData);

                } else {
                    //当确定键按下跳转到SubSceneActor
                    //When ok button is pressed, jump to SubSceneActor
                    //首先获取NibiruStudioUtils实例
                    //Get NibiruStudioUtils instance first
                    NibiruStudioUtils utils = NibiruStudioUtils.getInstance(TestActivity.this);

                    //通过class获取Intnet实例，如果自定义了Studio Activity需要传入Activity的class
                    //Get Intent instance through class, if Studio Activity is customized, Acitivity class should be passed to Activity
                    //          Intent intent = utils.getSceneIntentInternal(XBaseXRActivity.class, SubSceneActor.class);
                    Class sceneClass = null;
                    if(TextUtils.equals(XUI.getInstance().getGlobalApplication().getPlatfrom(), GlobalApplication.PLATFORM_VR)) {
                        sceneClass = com.nibiru.studio.vrscene.SubSceneActor.class;
                    } else if(TextUtils.equals(XUI.getInstance().getGlobalApplication().getPlatfrom(), GlobalApplication.PLATFORM_AR)) {
                        sceneClass = com.nibiru.studio.arscene.SubSceneActor.class;
                    } else {
                        if(XUI.getInstance().getGlobalApplication().isMR()) {
                            sceneClass = com.nibiru.studio.arscene.SubSceneActor.class;
                        } else {
                            sceneClass = com.nibiru.studio.vrscene.SubSceneActor.class;
                        }
                    }
                    Intent intent = utils.getSceneIntentInternal(sceneClass);

                    //可设置参数，该参数将传给Scene
                    //Parameter can be set, and will pass to Scene
                    intent.putExtra("param", "value from TestActivity");

                    //调用接口启动Scene，如果是应用内部定义的Scene，则第二个参数为true
                    //Call API to launch Scene, if the Scene is defined in the app, the second parameter is true
                    utils.startScene(intent, true);

                }

                //结束当前activity
                //Finish the current activity
                finish();
            }
        });

        mTip = (TextView) findViewById(R.id.tip);

        mParam = (TextView) findViewById(R.id.param);

        //判断是result的请求还是简单跳转请求，跳转功能的演示来自于SubSceneGoto，切换2D/3D显示模式来源于SubSceneOS
        //Judge whether it's the request of result or just jumping requst, jumping function demo is from SubSceneGoto, switching 2D/3D mode is from SubSceneOS
        Intent intent = getIntent();

        if (intent != null) {
            int requestCode = intent.getIntExtra("request", -1);

            isRequestForResult = requestCode == 500;

            isTestForSceneResult = requestCode == 700;

            isSampleSwitchVRMode = requestCode == 1000;

            //获取来自Scene的参数
            //Get parameters from Scene
            String p = intent.getStringExtra("param");

            if (mParam != null && p != null) {

                if (!isSampleSwitchVRMode) {
                    mParam.setText("Get Param from Scene: " + p);
                }
            }

            if (isRequestForResult && mTip != null) {
                //请求返回给Scene返回值
                //Request the return value to Scene
                mTip.setText("Press OK to give result and back");
            } else if (isTestForSceneResult && mTip != null) {
                //请求在Activity中请求Scene来获取返回值
                //Request Scene to get return value in Activity
                mTip.setText("Press OK to test start Scene for Result in Activity");
            } else if (isSampleSwitchVRMode && mTip != null) {
                //请求切换2D/3D显示模式
                //Request switching 2D/3D display mode
                mTip.setText("Press OK to switch 2D/VR Mode");
            }
        }
    }

    //TestActivity是普通的Android界面，因此需要用2D模式来显示，在NibiruOS中如果没有特别指定（加入NVR标签）或者切换模式，则默认按2D模式显示
    //TestActivity is normal Android interface, so 2D mode is required for display. If it's not specified (adding NVR label) or switching mode, 2D display mode is used by default
    boolean isVRMode = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == NibiruKeyEvent.KEYCODE_ENTER) {

            //演示切换2D/3D模式
            //Demostrate switching 2D/3D mode
            if (isSampleSwitchVRMode) {

                NibiruOSService mOService = NibiruStudioUtils.getInstance(this).getNibiruOService();

                //切换VR模式和2D模式，在2D模式下，界面会自动分为左右两屏显示，使用与传统Android APP；VR模式下，系统不会自动分屏，适用于VR APP
                //Switch VR mode and 2D mode, in 2D mode, the interface is displayed as left and right split screen automatically, and it's applied to traditional APP; in VR mode, system will not split the screen automatically, and it's applied to VR APP
                mOService.setDisplayMode(isVRMode ? NibiruOSService.DISPLAY_MODE.MODE_2D : NibiruOSService.DISPLAY_MODE.MODE_3D);

                isVRMode = !isVRMode;

                return true;
            }
            //演示设置返回值传递给请求的Scene
            //The demonstrated return value is passed to the request Scene
            else if (isRequestForResult) {
                //当确定键按下设置Result并返回上一个Scene
                //When the ok button is pressed, set Result and return to the last Scene
                //直接设置result最后finish，与Activity类似
                //Please set result directly and finish, similar to Activity
                Intent resultData = new Intent();
                resultData.putExtra("result", "result is OK!");
                setResult(501, resultData);

            } else {
                //当确定键按下跳转到SubSceneActor
                //When ok button is pressed, jump to SubSceneActor
                //首先获取NibiruStudioUtils实例
                //Get NibiruStudioUtils instance first
                NibiruStudioUtils utils = NibiruStudioUtils.getInstance(this);

                //通过class获取Intnet实例，如果自定义了Studio Activity需要传入Activity的class
                //Get Intent instance through class, if Studio Activity is customized, Acitivity class should be passed to Activity
                //          Intent intent = utils.getSceneIntentInternal(XBaseXRActivity.class, XUI.getInstance().getGlobalApplication().isMR()  ? com.nibiru.studio.arscene.SubSceneActor.class : com.nibiru.studio.vrscene.SubSceneActor.class);

                Class sceneClass = null;
                if(TextUtils.equals(XUI.getInstance().getGlobalApplication().getPlatfrom(), GlobalApplication.PLATFORM_VR)) {
                    sceneClass = com.nibiru.studio.vrscene.SubSceneActor.class;
                } else if(TextUtils.equals(XUI.getInstance().getGlobalApplication().getPlatfrom(), GlobalApplication.PLATFORM_AR)) {
                    sceneClass = com.nibiru.studio.arscene.SubSceneActor.class;
                } else {
                    if(XUI.getInstance().getGlobalApplication().isMR()) {
                        sceneClass = com.nibiru.studio.arscene.SubSceneActor.class;
                    } else {
                        sceneClass = com.nibiru.studio.vrscene.SubSceneActor.class;
                    }
                }
                Intent intent = utils.getSceneIntentInternal(sceneClass);

                //可设置参数，该参数将传给Scene
                //Parameter can be set, and will pass to Scene
                intent.putExtra("param", "value from TestActivity");

                //调用接口启动Scene，如果是应用内部定义的Scene，则第二个参数为true
                //Call API to launch Scene, if the Scene is defined in the app, the second parameter is true
                utils.startScene(intent, true);

            }

            //结束当前activity
            //Finish the current activity
            finish();


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //接收到来自跳转Scene的返回值
        //Get return value from the jumping Scene
        if (mParam != null) {
            mParam.setText("Get Result from Scene: requestCode->" + requestCode + " resultCode->" + resultCode + " Intent->" + (data != null ? data.getStringExtra("result") : "null"));
        }
    }

    //演示虚拟鼠标的开启和关闭，仅适用于普通分屏模式下的2D应用使用

    /*

    NibiruVirtualMouseManager mVirtualMouseManager;

    //建议在界面显示时注册虚拟鼠标服务，在界面不显示时销毁服务
    @Override
    protected void onResume() {
        super.onResume();

        //获取系统服务接口
        NibiruOSService service = NibiruService.getNibiruService(NibiruService.TYPE.OS, this);

        //如果未初始化虚拟鼠标服务则初始化
        if( mVirtualMouseManager == null ){
            mVirtualMouseManager= service.getVirtualMouseManager();

            if( mVirtualMouseManager != null ){
                //注册虚拟鼠标服务，第一个参数是context，第二个参数是服务注册监听
                mVirtualMouseManager.registerService(this, new NibiruVirtualMouseManager.VirtualMouseServiceListener() {
                    //回调参数为虚拟鼠标服务注册结果，true为注册成功，false为注册失败
                    @Override
                    public void onServiceRegisterResult(boolean result) {
                        if( result ){
                            //注册成功就开启头盔鼠标显示
                            mVirtualMouseManager.setEnableMouse(true);
                        }
                    }
                });
            }
        }else{
            if( mVirtualMouseManager.isServiceEnable() ){
                //如果服务已注册，则直接调用接口开启
                mVirtualMouseManager.setEnableMouse(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if( mVirtualMouseManager != null ){
            //在界面暂停显示时取消注册
            mVirtualMouseManager.unregisterService();
            mVirtualMouseManager = null;
        }

    }

    */
}
