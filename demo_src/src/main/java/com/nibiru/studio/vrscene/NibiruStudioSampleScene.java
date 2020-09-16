package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.util.Log;

import com.nibiru.service.NibiruKeyEvent;
import com.nibiru.studio.xrdemo.R;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;

/**
 * Created by Steven Zhang on 2017/10/31.
 */

//新建一个Scene继承自XBaseScene
//Create a Scene inherits XBaseScene
public class NibiruStudioSampleScene extends XBaseScene implements IXActorEventListener {

    XLabel mEnterLabel;
    XLabel mExitLabel;

//    public void onCreate(){
//        XImage image = new XImage("/sdcard/icon.png", XUI.Location.SDCARD);
//        image.setCenterPosition(0, 0, -10);
//        image.setSize(20, 20);
//
//        addActor(image);
//    }


    //场景创建时回调
    //Call back when the scene is created
    @Override
    public void onCreate() {
        //新建一个Label，并指定显示文本，推荐使用Resource string ID，可自动支持国际化
        //Create a Label, and specify text to be displayed, Resource string ID is recommended, it supports internationalization automatically
        XLabel titleLabel = new XLabel(R.string.sample_scene_title);

        //设置Label的排版方式为，居中，并且不开启跑马灯
        //Set the alignment of Label: center without marquee
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        //设置Label的位置坐标和尺寸
        //Set Label positon coordination and size
        titleLabel.setCenterPosition(0, 1.0f, -4f);
        titleLabel.setSize(0.8f, 0.3f);

        //将Label加入到场景中
        //Put Label into scene
        addActor(titleLabel);

        mEnterLabel = new XLabel(R.string.sample_scene_label);

        mEnterLabel.setAlignment(XLabel.XAlign.Center);
        mEnterLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        mEnterLabel.setCenterPosition(0, 0.0f, -4f);
        mEnterLabel.setSize(1.8f, 0.2f);

        //设置事件监听
        //Set event listener
        mEnterLabel.setEventListener(this);

        //设置选中动画，默认为向前平移动画
        //Set selected animation, translating to the front by default
        mEnterLabel.setEnableGazeAnimation(true);

        addActor(mEnterLabel);


        mExitLabel = new XLabel(R.string.sample_scene_exit_label);

        mExitLabel.setAlignment(XLabel.XAlign.Center);
        mExitLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        mExitLabel.setCenterPosition(0, -1.0f, -4f);
        mExitLabel.setSize(0.4f, 0.2f);

        //设置事件监听
        //Set event listener
        mExitLabel.setEventListener(this);

        //设置选中动画，默认为向前平移动画
        //Set selected animation, translating to the front by default
        mExitLabel.setEnableGazeAnimation(true);

        addActor(mExitLabel);

        if(isEnableSetGazeImage()) {
            //设置选中点动画
            //Set the selected point animation
//            List<String> images = new ArrayList<>();
//            images.add("gaze/00000.png");
//            images.add("gaze/00001.png");
//            images.add("gaze/00002.png");
//            images.add("gaze/00003.png");
//            images.add("gaze/00004.png");
//            images.add("gaze/00005.png");
//            startGazeAnimation(images, XUI.Location.ASSETS);
            //设置白点图片
            //Set the selected point picture
//            setGazeImage("gaze/00003.png", XUI.Location.ASSETS);
            //设置白点缩放大小
            //Set the selected point scale
//            setGazeScale(2);
        }

    }

    //从其他场景返回，或者电源键启屏时回调
    //Return from other scenes or call back when the screen is started by power button
    @Override
    public void onResume() {

    }

    //场景退出/切换到其他场景/电源键熄屏时回调
    //Exit scene/switch to other scenes/call back when the screen is turned off by power button
    @Override
    public void onPause() {

    }

    //场景退出/应用退出时回调
    //Exit scene/call back when the app exits
    @Override
    public void onDestroy() {

    }

    //在选中点移入时回调
    //Call back when the gaze point enters
    @Override
    public void onGazeEnter(XActor actor) {
        Log.d("test", "on gaze enter");
    }

    //在选中点移出时回调
    //Call back when the gaze point exits
    @Override
    public void onGazeExit(XActor actor) {
        Log.d("test", "on gaze exit");
    }

    //在选中点选中时，点击OK键回调
    //Click OK for callback when the gaze is selected
    @Override
    public boolean onGazeTrigger(XActor actor) {
        if( actor == mEnterLabel ){
            //打开新场景，本场景进入pause状态，Intent可设置Extra参数，在目标Scene通过getIntent获取
            //Open new scene, the current scene enters into pause status,Intent can set Extra parameter, Intent can be aquired through object Scene
            startScene(new Intent(this, LauncherMainScene.class));
            //返回true表示已处理完选中点击事件，不再向下层传递
            //Returning true means the selected gaze event has been proceeded and will not pass to the lower layer
            return true;
        }else if( actor == mExitLabel ){
            //由于本场景是第一个场景，因此退出后应用也直接退出
            //Because the current scene is the first scene, the app exits directly after the scene exits
            finish();

            //如果本场景由其他场景跳转而来，此时想直接退出应用，可调用finishAll
            //If the scene is jumping from other scene, if the developer want to exit the app directly, call finishAll
            //finishAll();

            return true;
        }
        return false;
    }

    //遗留接口，请勿使用，Animation回调通过Actor设置Animation监听实现。
    //Legacy API, please don't use, Animation callback is realized through Anmiation listener set in Actor
    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    //指定本场景需要用到的plist资源，plist和对应的png文件需要放在assets中
    //Specify the plist resource to be used in the scene, plist and the corresponding png files should be put in assets
    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
    }

//    //指定本场景需要用到的外部plist资源，plist和对应的png文件需要放在指定路径下
//    //Specify the external plist resources to be used in the scene, plist and the corresponding png files should be put in the specified path
//    @Override
//    public String[] getScenePlistExternal() {
//        return new String[]{"/sdcard/studio/Images.plist"};
//    }

    public boolean onKeyDown(int keycode){

        if( keycode == NibiruKeyEvent.KEYCODE_ENTER ){
            mEnterLabel.setEnabled(!mEnterLabel.isEnabled());
        }

        return super.onKeyDown(keycode);
    }

}
