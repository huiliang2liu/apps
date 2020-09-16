package com.nibiru.studio.vrscene;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;

import java.util.Arrays;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImageText;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.group.XActorGridView;
import x.core.ui.group.XActorPageView;
import x.core.ui.group.XItem;
import x.core.util.XLog;

/**
 * com.nibiru.studio.vrscene.LauncherMainScene
 */
public class LauncherMainScene extends XBaseScene {
    //粒子特效在某些机器上存在问题，用户可以根据机器取消对Particle注释即可进去粒子示例界面

    String[] name = {
            "Actor",            "Group",                "Label",            "ScrollingLabel",          "Image",
            "ImageText",        "Button",               "EditText",         "ProgressBar",             "ListView",
            "PageView",         "GridView",             "Goto",             "Toast",                   "Dialog",
            "Animation",        "FixView",              "VideoPlayer",      "InputMethod",             "StaticModel", "StaticModelTest",
            "Skeleton",          "SkyBox",               "OS",               "OS API",                "DoubleImage",
            /*"Particle", */         "OpenGL",     /* "Screen"*/     "TexturemanagerScene" ,"VLC",   "ControllerModel"
    };

    @Override
    public void onCreate() {

        final ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        String strResult = Integer.toString(configurationInfo.reqGlEsVersion, 16);

        if(strResult.startsWith("2")) {

            for(int i = 0; i < name.length; i++) {
                if(name[i].equals("Skeleton")) {
                    name[i] = name[name.length - 1];
                }
            }
            name = Arrays.copyOf(name, name.length - 1);
        }

        //创建GridView
        //Create GridView
        initGridView();

    }


    private MyAdapter myAdapter;
    XActorGridView actorGridView;

    //使用adapter生成或更新每一项的内容
    //Use adapter to generate or update each item
    private class MyAdapter extends XItemAdapter {

        @Override
        public int getCount() {
            return name.length;
        }

        @Override
        public Object getObject(int position) {
            return null;
        }

        @Override
        public XItem getXItem(int position, XItem convertItem, XActor parent) {

            //如果需要创建Item，构造Item对象，配置里面的内容
            //If Item needs to be created, construct Item object and configure the content inside
            if (convertItem == null) {
                convertItem = new XItem();

                //设置Item尺寸
                //Set Item size
                convertItem.setSize(0.5f, 0.5f);

                //新建一个ImageText控件显示图片和文本，传入参数为选中图片名称和非选中图片名称，该图片来自于Images.plist
                //Create an ImageText control to display image and text, the passed in parameter is the name of selected image and unselected image, the image is from Images.plist
                XImageText imageText = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");

                //设置选中文本和非选中文本，支持设置resource string ID
                //Set selected text and unselected text, supporting setting resource string ID
                imageText.setTitle(name[position], name[position]);

                //设置控件名称，便于在其他功能逻辑中找到该控件
                //Set control name for other function logics to find the control
                imageText.setName(name[position]);

                //设置图片尺寸
                //Set image size
                imageText.setSizeOfImage(0.5f, 0.5f);

                //设置文本尺寸
                //Set text size
                imageText.setSizeOfTitle(0.5f, 0.2f);

                //设置文本选中颜色和非选中颜色
                //Set the color of selected text and unselected text
                imageText.setTitleColor(Color.RED, Color.WHITE);

                //设置文本在整个ImageText中的位置，坐标为相对坐标，原点在ImageText中心，具体坐标系方向参考开发文档
                //Set the position of text in the whole ImageText, the coordination is relative, the origin is the center of ImageText, please refer to the development guide for coordination directions
                imageText.setTitlePosition(0, -0.35f);

                //设置文本居中布局
                //Set text alignment as center
                imageText.setTitleAlign(XAlign.Center, XAlign.Center);

                //设置文本为单行，非跑马灯
                //Set text as single row, not marquee
                imageText.setTitleArrangementMode(XArrangementMode.SingleRowNotMove, XArrangementMode.SingleRowNotMove);

                //设置整体尺寸
                //Set the overall size
                imageText.setSize(0.5f, 0.5f);

                //设置选中事件监听
                //Set listener for selected event
                imageText.setEventListener(itemActorListener);

                //在Item中加入该层
                //Add the layer in Item
                convertItem.addChild(0, imageText);
            } else {
                //如果Item已经存在，先通过索引获得ImageText，然后直接更新文本内容
                //If Item exists, get ImageText through index first and update text content directly
                XImageText imageText = (XImageText) convertItem.getChild(0);
                imageText.setTitle(name[position], name[position]);
            }
            return convertItem;
        }
    }

    public void initGridView() {

        //创建GridView，设置页的显示类型，宽度，高度，行数，列数，
        //Create GridView, set the display type, width, height, line amount and column amount of page
        actorGridView = new XActorGridView(XActorPageView.PageViewDefaultType.FLAT, 6.5f, 5f, 6, 5);

        //设置控件位置
        //Set control position
        actorGridView.setCenterPosition(0, 0, -1);

        //设置页的背景图片
        //Set background image of page
        actorGridView.setPageBackGroundName("blackxx.png");

        //创建Adapter
        //Create Adapter
        myAdapter = new MyAdapter();

        //设置adapter
        //Set adapter
        actorGridView.setAdapter(myAdapter);

        //添加GridView
        //Add GridView
        addActor(actorGridView);
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

    //加载场景所需的Plist资源，可指定多个，Plist资源只需要覆盖当前场景即可，在其他场景已加载过的Plist不会重复加载
    //Load required Plist resources for scene, several resources can be specified, Plist resources only need to overlap the current scene, loaded Plists in other scenes will not be loaded repeatedly
    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
   }

    //支持从SD卡加载plist文件
    // Support loading plist file from SD Card
//   public String[] getScenePlistExternal(){
//       return new String[]{"/sdcard/Images.plist"};
//   }

    //选中事件监听，根据actor的名称决定跳转到不同的Scene中
    //Listener for selected event, jumping to different scenes accordingto the actor name
    IXActorEventListener itemActorListener = new IXActorEventListener() {

        @Override
        public void onGazeEnter(XActor actor) {

        }

        @Override
        public void onGazeExit(XActor actor) {

        }

        @Override
        public boolean onGazeTrigger(XActor actor) {
            XLog.logInfo("on gaze trigger: "+actor.getName());

            if (actor.getName().equals("Actor")) {
                //通过Intent方式实现Scene的跳转，Intent可设置参数，在目标Scene中通过getIntent获取
                //Realize Scene jumping through Intent, parameters can be set in Intent which is obtained through getIntent in object Scene
                startScene(new Intent(LauncherMainScene.this, SubSceneActor.class));
            } else if (actor.getName().equals("Label")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXLabel.class));
            } else if (actor.getName().equals("Image")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXImage.class));
            } else if (actor.getName().equals("ImageText")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXImageText.class));
            } else if (actor.getName().equals("Button")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXButton.class));
            } else if (actor.getName().equals("EditText")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXEditText.class));
            } else if (actor.getName().equals("SkyBox")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXSkyBox.class));
            } else if (actor.getName().equals("ProgressBar")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXProgressBar.class));
            } else if (actor.getName().equals("GridView")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXGridView.class));
            } else if (actor.getName().equals("ListView")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXListView.class));
            } else if (actor.getName().equals("PageView")) {
                startScene(new Intent(LauncherMainScene.this, SubScenePageView.class));
            } else if (actor.getName().equals("VideoPlayer")) {
//                startScene(new Intent(LauncherMainScene.this, SubSceneVideoPlayer.class));
                startScene(new Intent(LauncherMainScene.this, SubSceneVideoList.class));
            } else if (actor.getName().equals("Toast")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneXToast.class));
            } else if (actor.getName().equals("Dialog")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneDialog.class));
            } else if (actor.getName().equals("FixView")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneFixView.class));
            } else if (actor.getName().equals("Group")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneGroup.class));
            } else if (actor.getName().equals("Animation")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneAnimation.class));
            } else if(actor.getName().equals("InputMethod")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneIME.class));
            } else if("StaticModel".equals(actor.getName())){
                startScene(new Intent(LauncherMainScene.this, SubSceneXModel.class));
            }else if ("StaticModelTest".equals(actor.getName())){
                startScene(new Intent(LauncherMainScene.this, StaticModelTest.class));
            } else if("OS".equals(actor.getName())){
                startScene(new Intent(LauncherMainScene.this, SubSceneOS.class));
            } else if("ScrollingLabel".equals(actor.getName())){
                startScene(new Intent(LauncherMainScene.this, SubSceneScrollingLabel.class));
            } else if("Camera".equals(actor.getName())){
                startScene(new Intent(LauncherMainScene.this, SubSceneCamera.class));
            } else if( "Skeleton".equals(actor.getName()) ){
                startScene(new Intent(LauncherMainScene.this, SubSceneXSkeleton.class));
            } else if("OS API".equals(actor.getName())) {
                startScene(new Intent(LauncherMainScene.this, SubSceneOSAPI.class));
            } else if( "Goto".equals(actor.getName()) ){
                startScene(new Intent(LauncherMainScene.this, SubSceneGoto.class));
            } else if("DoubleImage".equals(actor.getName())) {
                startScene(new Intent(LauncherMainScene.this, SubSceneDoubleImage.class));
            } else if( "Particle".equals(actor.getName()) ) {
                startScene(new Intent(LauncherMainScene.this, SubSceneParticleSystem.class));
            } else if("OpenGL".equals(actor.getName())) {
                startScene(new Intent(LauncherMainScene.this, SubSceneOpengl.class));
            } else if("Screen".equals(actor.getName())) {
                startScene(new Intent(LauncherMainScene.this, SubSceneScreen.class));
            } else if("VLC".equals(actor.getName())) {
                startScene(new Intent(LauncherMainScene.this, SubSceneVlc.class));
            }else if(actor.getName().equals("ControllerModel")) {
                startScene(new Intent(LauncherMainScene.this, SubSceneControllerModel.class));
            }else if (actor.getName().equals("TexturemanagerScene")){
                startScene(new Intent(LauncherMainScene.this, TexturemanagerScene.class));
            }


                return true;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }

    };


}
