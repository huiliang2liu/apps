package com.nibiru.studio.vrscene;

import android.graphics.Color;

import com.nibiru.studio.xrdemo.R;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;

/**
 * 演示ImageText控件，该控件支持图片和文本的显示，同时作为Group控件，也支持附加其他Layer显示
 */

/**
 * Show ImageText control, which supports displaying image and text. As a Group control, it also supports displaying other Layers
 */
public class SubSceneXImageText extends XBaseScene implements IXActorEventListener {

    XImage mMaskLayer;
    XImageText mImageText;


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


        XLabel titleLabel = new XLabel("Example：ImageText");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 2.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //设置选中和未选中图片，图片来自于assets
        //Set selected and unselected image, the image is from assets
        mImageText= new XImageText("yejingtu.png", "yejingtu.png");

        //设置选中和未选中图片，图片来自于外部存储
        //Set selected and unselected image, the image is from the external storage
//        mImageText= new XImageText("/sdcard/yejingtu.png", "/sdcard/yejingtu.png", XUI.Location.SDCARD);

        //设置选中和未选中的图片，图片来自于Resource
        //Set selected and unselected image, the image is from Resource
//        mImageText= new XImageText(R.mipmap.icon, R.mipmap.icon);

        //设置选中和未选中的图片，图片来自于Bitmap
        //Set selected and unselected image, the image is from Bitmap
//        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/yejingtu.png");
//        mImageText= new XImageText(bitmap, bitmap);

//        //使用Bitmap创建纹理来初始化控件，首先得到一个Bitmap
//        //Create texture through Bitmap to initialize the control, get as a Bitmap first
//        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/yejingtu.png");
//        //通过createBitmapTexture方法生成名为test_image的纹理，最后一个参数为纹理生成的结果回调。
//        //Generate texture named test_image by createBitmapTexture, the last parameter is the generated texture result callback
//        PicturesUtil.createBitmapTexture(bitmap, "test_image", new IXBitmapTextureCreateSuccessListener() {
//            @Override
//            public void onCreateSuccess(String texName, boolean isSuccess) {
//                Log.d("test", "load texture: "+texName+" result: "+isSuccess);
//            }
//        });
//
//        //用纹理名称作为参数构建UI控件，如果纹理创建成功将自动显示bitmap图片，如果纹理创建失败，则显示为资源加载异常的图片
//        //Build UI control with texture names, if the texture is created successfully, bitmap image will be displayed automatically. If it fails to create the texture, the abnormal loaded image will be displayed
//        mImageText= new XImageText("test_image","test_image");

        mImageText.setCenterPosition(0f, 0f, -4f);
        mImageText.setSize(1.5F, 1.5F);
        mImageText.setSizeOfImage(1.5f, 1.5f);
        mImageText.setRenderOrder(7);

        //支持设置选中/选中文本的资源ID，便于国际化
        //Support setting the resource ID of selected/unselected text for internationalization
        mImageText.setTitle(R.string.imagetext_select_test, R.string.imagetext_unselect_test);

        //支持直接设置选中/未选中文本
        //Support setting selected/unselected text directly
//        mImageText.setTitle("Text: nightscape image, enable marquee when selected", "Text: nightscape image, unselected status");

        //设置选中时开启跑马灯
        //Set enable marquee when it's selected
        mImageText.setSelectedArrangementMode(XArrangementMode.SingleRowMove);

        //设置未选中时左对齐，超出部分显示省略号
        //Set left alignment when it's unselected, the overlength part will be displayed as ellipsis
        mImageText.setUnselectedArrangementMode(XArrangementMode.SingleRowClip);
        mImageText.setUnselectedAlign(XAlign.Left);

        mImageText.setSizeOfTitle(1.5f, 0.2f);
        mImageText.setTitlePosition(0, -0.55f);

        mImageText.setEventListener(this);

        addActor(mImageText);

        //在ImageText基础上添加半透明层区分选中状态
        //Add translucent layer on the basis of ImageText to distinguish the selection status
        mMaskLayer = new XImage("blackxx.png");
        mMaskLayer.setSize(1.5f, 1.5f);
        //先设置为隐藏，在选中时显示
        //Set to hidden status, and display when it's selected
        mMaskLayer.setEnabled(false);
        mImageText.addLayer(mMaskLayer, 0, 0);
    }


    @Override
    public void onGazeEnter(XActor actor) {

        //当选中点移入时显示半透明层
        //Display the translucent layer when the Gaze point enters
        if( actor == mImageText ){
            mMaskLayer.setEnabled(true);
        }

    }

    @Override
    public void onGazeExit(XActor actor) {

        //当选中点移出时隐藏半透明层
        //Hide the translucent layer when the Gaze point exits
        if( actor == mImageText ){
            mMaskLayer.setEnabled(false);
        }

    }

    @Override
    public boolean onGazeTrigger(XActor actor) {
        return true;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

}
