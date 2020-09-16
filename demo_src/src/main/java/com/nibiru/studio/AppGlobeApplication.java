package com.nibiru.studio;

import android.content.Intent;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import x.core.GlobalApplication;

/**
 * Created by Nibiru on 2017/5/31.
 */

public class AppGlobeApplication extends GlobalApplication {

    //在应用启动时，第一个场景加载之前回调，可初始化一些全局功能，例如ImageLoader
    //When the app is launching, initiate the callback before the first scene is loaded, and some global functions like ImageLoader can be initialized
    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCacheExtraOptions(512, 512)
                // default = device screen dimensions
                .diskCacheExtraOptions(512, 512, null)
                .threadPoolSize(3)
                // default
//                .threadPriority(1)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                .memoryCacheSize(10 * 1024 * 1024)
                // 设定网络连接超时 timeout: 10s 读取网络连接超时read timeout: 60s
                // Set network connect timeout timeout: 10s read network connect timeout read timeout: 60s
                .imageDownloader(new BaseImageDownloader(context, 10000, 60000))
                // default
                .diskCacheSize(100 * 1024 * 1024)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);


//        设置小白点动画
//        List<String> images = new ArrayList<>();
//        images.add("gaze/00000.png");
//        images.add("gaze/00001.png");
//        images.add("gaze/00002.png");
//        images.add("gaze/00003.png");
//        images.add("gaze/00004.png");
//        images.add("gaze/00005.png");
//        XUI.getInstance().getGazeAnimationManager().setGazeAnimationResource(images, XUI.Location.ASSETS);
    }

    //在应用启动/返回/电源键启屏时回调
    //Call back at app launching/returning/screen starting by power button
    @Override
    public void onResume() {
        super.onResume();
    }


    //在渲染准备完成时回调，与渲染相关的功能可以放在这里初始化
    //Call back when the rendering preparation is done, other functions related to rendering can also be intialized here
    @Override
    public void onXRReady() {
        super.onXRReady();
    }

    //在应用切出/电源键熄屏时回调
    //Call back at app cutting out/screen turning off by power button
    @Override
    public void onPause() {
        super.onPause();
    }

    //在应用退出时回调，在这里销毁初始化的服务和功能，例如销毁ImageLoader
    //Call back at app exiting, destroy services and functions of initialization here, e.g. destroy ImageLoader
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().destroy();
    }

    //控制是否使用天空盒
    //Control whether to use skybox
    public boolean isEnableSkybox(){

        //返回false为禁用天空盒
        //Return false: disable skybox
//        return true;

        return super.isEnableSkybox();
    }

    //该方法在运行时决定启动的Scene，常用于不同Intent打开进入不同的Scene，该方法在Scene初始化之前被回调
    //The method decides Scene to be launched at runtime, usually used to enter different Scenes with different Intents, this method is called back at the initialization of Scene
    public Class getLauncherScene(){

        Intent intent = getIntent();

        //示例：如果传入的Intent的goto参数为SubSceneActor，则直接以SubSceneActor启动
        //Sample: If the input goto parameter of Intent is SubSceneActor, then it's launched directly as SubSceneActor
        if( intent != null && TextUtils.equals( intent.getStringExtra("goto"), "SubSceneActor") ){
            if(TextUtils.equals(getPlatfrom(), PLATFORM_VR)) {
                return com.nibiru.studio.vrscene.SubSceneActor.class;
            } else if(TextUtils.equals(getPlatfrom(), PLATFORM_AR)) {
                return com.nibiru.studio.arscene.SubSceneActor.class;
            }
            if(isMR()) {
                return com.nibiru.studio.arscene.SubSceneActor.class;
            } else {
                return com.nibiru.studio.vrscene.SubSceneActor.class;
            }
        }

        //先判断是否声明Platform，再判断是否为AR机器，区分打开VR还是AR场景
        //Determine whether it is an AR machine, distinguish whether to open VR or AR scene
        if(TextUtils.equals(getPlatfrom(), PLATFORM_VR)) {
            return com.nibiru.studio.vrscene.NibiruStudioSampleScene.class;
        } else if(TextUtils.equals(getPlatfrom(), PLATFORM_AR)) {
            return com.nibiru.studio.arscene.NibiruStudioSampleScene.class;
        }
        if(isMR()) {
            return com.nibiru.studio.arscene.NibiruStudioSampleScene.class;
        } else {
            return com.nibiru.studio.vrscene.NibiruStudioSampleScene.class;
        }

        //打开Manifest指定的启动场景
        //Open the launcher scene specified by Manifest
        //return super.getLauncherScene();
    }

//    public String getCustomConfigPath(){
//        return "/sdcard/cubeskybox/";
//    }


    @Override
    public boolean isAdjustFovLevel() {
        return false;
    }
}
