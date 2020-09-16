package com.nibiru.studio.vrscene;

import android.graphics.Color;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;

/**
 * 演示天空盒的加载
 * 天空盒在Studio中采用XML配置的方式，也就是在assets目录下新建skybox文件夹，新建skybox.xml配置天空盒资源，具体可参考本项目的配置
 * 支持使用系统天空盒，依赖于系统版本，只有较高版本的系统支持该功能
 * 支持开发者自己设置天空盒，在Scene中提供了一系列设置天空盒的方法，具体可参考开发文档
 */

/**
/**
 * Show loading skybox
 * Skybox uses XML configuring method is Studio, i.e. create skybox folder under assets directory and create skybox.xml to configure skybox resources. Please refer to the configurations of this project for details
 * Support setting skybox by developers. There's a series of setting method in Scene, please refer to the development guide for details
 */
public class SubSceneXSkyBox extends XBaseScene {

    boolean isUseSystemTheme = false;
    XLabel systemTheme;
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

    @Override
    public void onDestroy() {

    }

    public void init() {

        XLabel titleLabel = new XLabel("Example：SkyBox");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(2.8f, 0.3f);
        addActor(titleLabel);

        systemTheme = new XLabel("Click to use System Theme (Depends on OS version)");
        systemTheme.setAlignment(XAlign.Center);
        systemTheme.setArrangementMode(XArrangementMode.SingleRowNotMove);
        systemTheme.setCenterPosition(0, 0.0f, -4f);
        systemTheme.setSize(4.8f, 0.3f);
        systemTheme.setEnableGazeAnimation(true);
        systemTheme.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( isUseSystemTheme ){
                    systemTheme.setTextContent("Click to use System Theme (Depends on OS version)");
                    //设置assets/skybox/skybox.xml配置的天空盒
                    //Set skybox cofigured by assets/skybox/skybox.xml
                    setSkyboxName("main_scene");

                }else{
                    systemTheme.setTextContent("Click to use App SkyBox");
                    //设置系统主题天空盒，依赖系统版本，如果没有成功设置说明系统版本较低
                    //Set system theme skybox, depending on OS version. If it's not set successful, it means the OS version is too low
                    setSkyboxSystemTheme();

                    //演示立方体天空盒
                    //Demonstrate cubic skybox
//                    ArrayMap<String, String> cubeSkybox = new ArrayMap<>();

                    //从SDCard加载
                    //Load from SDCard

//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_UP, "/sdcard/cubeskybox/up.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_DOWN, "/sdcard/cubeskybox/down.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_RIGHT, "/sdcard/cubeskybox/right.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_LEFT, "/sdcard/cubeskybox/left.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_FRONT, "/sdcard/cubeskybox/front.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_BACK, "/sdcard/cubeskybox/back.png");

//                    setCustomSkyboxCubeSdcard("test", cubeSkybox, null);

                    //从Assets加载
                    //Load from Assets
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_UP, "skybox/cube2/up.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_DOWN, "skybox/cube2/down.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_RIGHT, "skybox/cube2/right.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_LEFT, "skybox/cube2/left.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_FRONT, "skybox/cube2/front.png");
//                    cubeSkybox.put(XSkyCubeBox.ORIENTATION_BACK, "skybox/cube2/back.png");
//
//                    setCustomSkyboxCubeAssets("test1", cubeSkybox, null);


                    //从Bitmap加载
                    //Load from Bitmap
//                    ArrayMap<String, Bitmap> cubeSkyboxBitmap = new ArrayMap<>();
//                    cubeSkyboxBitmap.put(XSkyCubeBox.ORIENTATION_UP, BitmapFactory.decodeFile("/sdcard/cubeskybox/up.png"));
//                    cubeSkyboxBitmap.put(XSkyCubeBox.ORIENTATION_DOWN, BitmapFactory.decodeFile("/sdcard/cubeskybox/down.png"));
//                    cubeSkyboxBitmap.put(XSkyCubeBox.ORIENTATION_RIGHT, BitmapFactory.decodeFile("/sdcard/cubeskybox/right.png"));
//                    cubeSkyboxBitmap.put(XSkyCubeBox.ORIENTATION_LEFT, BitmapFactory.decodeFile("/sdcard/cubeskybox/left.png"));
//                    cubeSkyboxBitmap.put(XSkyCubeBox.ORIENTATION_FRONT, BitmapFactory.decodeFile("/sdcard/cubeskybox/front.png"));
//                    cubeSkyboxBitmap.put(XSkyCubeBox.ORIENTATION_BACK, BitmapFactory.decodeFile("/sdcard/cubeskybox/back.png"));
//
//                    setCustomSkyboxCubeBitmap("test2", cubeSkyboxBitmap, null);

                }

                isUseSystemTheme = !isUseSystemTheme;

//                Bitmap bitmap = BitmapFactory.decodeFile("/system/etc/skybox/changjing1.png");
//
//                setCustomSkyboxBitmap("custom", bitmap, new IResourceLoadListener() {
//                    @Override
//                    public void onResourceLoadSuccess(XActor actor) {
//                        XLog.logInfo("custom skybox bitmap update succ");
//                    }
//
//                    @Override
//                    public void onResourceLoadFailed(XActor actor) {
//
//                    }
//                });
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        addActor(systemTheme);

        //Nibiru Studio支持多种方式加载天空盒(assets, sdcard, bigmap）,如果是静态天空盒可以通过assets/skybox/skybox.xml进行配置

        //Nibiru Studio supports many methods to load skybox (assets, sdcard, bitmap). If it's the static skybox, configure through assets/skybox/skybox.xml
        //配置后可直接通过setSkyboxName(name)进行加载
        //After the configuration, load through setSkyboxName(name) directly
        //如果应用运行时需要加载，可以调用setCustomSkyboxXXX系列方法进行加载
        //If the app is required to be loaded at runtime, call the series of setCustomSkyboxXXX method

//        setCustomSkyboxSdcard("test", "/sdcard/tet.png", new IResourceLoadListener() {
//            @Override
//            public void onResourceLoadSuccess(XActor actor) {
//                Log.d("Test", "skybox load success");
//            }
//
//            @Override
//            public void onResourceLoadFailed(XActor actor) {
//                Log.d("Test", "skybox load failed");
//            }
//        });

    }

}
