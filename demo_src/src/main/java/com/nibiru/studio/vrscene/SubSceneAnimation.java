package com.nibiru.studio.vrscene;


import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import x.core.listener.IXActorAnimationListener;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XLabel;
import x.core.ui.XUI;
import x.core.ui.animation.XAlphaAnimation;
import x.core.ui.animation.XAnimGIFImage;
import x.core.ui.animation.XAnimImage;
import x.core.ui.animation.XAnimSAMImage;
import x.core.ui.animation.XAnimSpriteImage;
import x.core.ui.animation.XAnimation;
import x.core.ui.animation.XFrameImageAnimation;
import x.core.ui.animation.XRotateAnimation;
import x.core.ui.animation.XScaleAnimation;
import x.core.ui.animation.XTranslateAnimation;
import x.core.util.XLog;

/**
 * 演示基础动画功能，包括平移，旋转，缩放，透明度，帧动画
 * 演示选中动画功能，包括平移，旋转，缩放，透明度
 * 演示动画控件，包括GIF，Super Animation，Sprite Animation
 */

/**
 * Show basic functions of animation including translation, rotation, scaling, transparency and frame animation
 * Show selected animation functions, including translation, rotation, scaling and transparency
 * Show animation actor, including GIF,Super Animation, and Sprite Animation
 */

public class SubSceneAnimation extends XBaseScene implements IXActorEventListener {

    XActor mRotActor;
    XActor mScaleActor;
    XActor mTransActor;
    XActor mAlphaActor;
    XActor mFrameActor;

    XAnimGIFImage mGIFActor;
    XAnimSAMImage mSAMActor;
    XAnimSpriteImage mSpritActor;

    XAnimation mRotAnimation = null;
    XAnimation mScaleAnimation = null;
    XAnimation mTranslateAnimation = null;
    XAnimation mAlphaAnimation = null;
    XFrameImageAnimation mFrameAnimation = null;

    List<String> images;

    @Override
    public void onCreate() {

        //初始化标题
        //Initialize title
        XLabel titleLabel = new XLabel("Example：Animation");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //初始化动画控件
        //Initialize animation actor
        initAnimation();

        //初始化选中动画控件
        //Initialize selected animation actor
        initGazeAnimation();

        //初始化其他动画控件
        //Initialize other animation actor
        initImageAnimation();
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

    void initAnimation(){

        XLabel animationLabel = new XLabel("Click Image to Cancel/Start Animation");
        animationLabel.setAlignment(XLabel.XAlign.Center);
        animationLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        animationLabel.setCenterPosition(0, 0.6f, -4f);
        animationLabel.setTextColor(Color.WHITE);
        animationLabel.setSize(3.8f, 0.2f);
        addActor(animationLabel);

        mRotActor = new XImage("ic_image_focused.png");
        mRotActor.setCenterPosition(-2f, 0.0f, -4);
        mRotActor.setSize(0.5f, 0.5f);
        mRotActor.setEventListener(this);
        addActor(mRotActor);

        //设置旋转动画，动画必须在addActor之后
        //Set rotation animation, it should be after addActor
        rotate(mRotActor);

        mScaleActor = new XImage("ic_image_focused.png");
        mScaleActor.setCenterPosition(-1f, 0.0f, -4);
        mScaleActor.setSize(0.5f, 0.5f);
        mScaleActor.setEventListener(this);

        addActor(mScaleActor);

        //设置缩放动画
        //Set scaling animation
        scale(mScaleActor);

        mTransActor = new XImage("ic_image_focused.png");
        mTransActor.setCenterPosition(0f, 0.0f, -4);
        mTransActor.setSize(0.5f, 0.5f);
        mTransActor.setEventListener(this);

        addActor(mTransActor);

        //设置平移动画
        //Set translation animation
        trans(mTransActor);

        mAlphaActor = new XImage("ic_image_focused.png");
        mAlphaActor.setCenterPosition(1f, 0.0f, -4);
        mAlphaActor.setSize(0.5f, 0.5f);
        mAlphaActor.setEventListener(this);

        addActor(mAlphaActor);

        //设置渐变动画
        //Set alpha animation
        alpha(mAlphaActor);

        images = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            images.add("animation/4_0" + i + ".png");
        }

        mFrameActor = new XImage("animation/4_01.png");
        mFrameActor.setCenterPosition(2f, 0.0f, -4);
        mFrameActor.setSize(0.5f, 0.5f);
        mFrameActor.setEventListener(this);

        addActor(mFrameActor);

        //设置帧动画，帧动画只能设置在Image控件上
        //Set frame animation, it can only be set in Image control
        frame(mFrameActor);
    }

    void rotate(XActor actor) {
        if (mRotAnimation == null) {
            //设置旋转动画，第一个参数为旋转角度，第二个参数为旋转时间，单位毫秒，第三个参数设置重复执行次数，LOOP代表一致循环
            //Set rotation animation, the first parameter is the rotation angle, the second parameter is the rotation time, unit: ms, the third parameter sets the number of repetition, LOOP stands for keeping looping
            mRotAnimation = new XRotateAnimation(360, 500, XAnimation.REPEAT_LOOP);

            //设置指定旋转中心的旋转动画，第一个参数为旋转中心，第二个参数为旋转角度，第三个参数为旋转时间，单位毫秒，第四个参数设置重复执行次数，LOOP代表一致循环
            //Sets rotation animation for the specified rotation center. The first parameter is the rotation center, the second parameter is the rotation angle, the third parameter is the rotation time in milliseconds, and the fourth parameter sets the number of repetition executions. LOOP stands for the uniform rotation.
//            float[] rotCenter = new float[]{0, 0, -4};
//            mRotAnimation = new XRotateAnimation(rotCenter, 360, 500, XAnimation.REPEAT_LOOP);

            //设置动画监听，监听包括开始，进行，结束，重复，取消几个回调
            //Set animation listener, including listen to callbacks like starting, processing, ending, repeating and cancelling
            mRotAnimation.setAnimationListener(new IXActorAnimationListener() {
                @Override
                public void onAnimationStart(XAnimation animation) {
                    Log.d(XLog.TAG, "[Anim] ============== START: ");
                }

                @Override
                public void onAnimationProcess(XAnimation animation, float process) {
//                    Log.d(XLog.TAG, "[Anim] ============== PROCESS: Pro: " + process);
                }

                @Override
                public void onAnimationEnd(XAnimation animation) {
//                    Log.d(XLog.TAG, "[Anim] ============== END: ");
                }

                @Override
                public void onAnimationRepeat(XAnimation animation, int rest) {
//                    Log.d(XLog.TAG, "[Anim] ============== !!REPEAT!!: rest count: " + rest);
                }

                @Override
                public void onAnimationCancel(XAnimation animation) {
//                    Log.d(XLog.TAG, "[Anim] ============== CANCEL: ");
                }
            });

            //开始执行动画
            //Start to perform the animation
            actor.startAnimation(mRotAnimation);
        } else {
            //取消动画
            //Cancel animation
            if (actor != null && mRotAnimation != null) {
                actor.cancelAnimation(mRotAnimation);
                mRotAnimation = null;
            }

        }
    }

    void trans(XActor xImage) {

        if (mTranslateAnimation == null) {
            //设置平移动画，第一个参数为平移动画的位移量，第二个参数为持续时间，第三个参数为重复执行次数，LOOP代表一致循环
            //Set translation animation, the first parameter is the translating value, the second parameter is the time duration, the third is the number of repetition, LOOP stands for keeping looping
            mTranslateAnimation = new XTranslateAnimation(0.3f, 500, XAnimation.REPEAT_LOOP);
            xImage.startAnimation(mTranslateAnimation);
        } else {
            //取消动画
            //Cancel animation
            if (xImage != null && mTranslateAnimation != null) {
                xImage.cancelAnimation(mTranslateAnimation);
                mTranslateAnimation = null;
            }

        }
    }

    void scale(XActor xImage) {

        if (mScaleAnimation == null) {
            //设置缩放动画，第一个参数为起始缩放比例，第二个参数为目标缩放比例，第三个参数为持续时间，第四个参数为重复执行次数，LOOP代表一致循环
            //Set scaling animation, the first parameter is the starting scaling factor, the second is the object scaling factor, the third is the duration, the fourth is the number of repetition, LOOP stands for keeping looping
            mScaleAnimation = new XScaleAnimation(1f, 0.5f, 500, XAnimation.REPEAT_LOOP);
            xImage.startAnimation(mScaleAnimation);
        } else {
            if (xImage != null && mScaleAnimation != null) {
                xImage.cancelAnimation(mScaleAnimation);
                mScaleAnimation = null;
            }

        }
    }

    void alpha(XActor xImage) {

        if (mAlphaAnimation == null) {
            //设置渐变动画，第一个参数为起始透明度，第二个参数为目标透明度，透明度0代表完全透明，1代表完全显示，第三个参数为持续时间，第四个参数为重复执行次数，LOOP代表一致循环
            //Set alpha animation, the first parameter is the starting transparency, the second is the object transparency, 0 means it's totally transparent, 1 means it's fully displayed, the third is the duration, the fourth is the number of repetition, LOOP means keeping looping
            mAlphaAnimation = new XAlphaAnimation(0.1f, 1f, 500, XAnimation.REPEAT_LOOP);
            xImage.startAnimation(mAlphaAnimation);
        } else {
            if (xImage != null && mAlphaAnimation != null) {
                xImage.cancelAnimation(mAlphaAnimation);
                mAlphaAnimation = null;
            }

        }
    }

    void frame(XActor xImage) {

        if( mFrameAnimation == null ){
            //设置帧动画，帧动画只能设置在XImage控件上，第一个参数为图片路径数组，第二个参数为图片来源，第三个参数为帧更新时间，第四个参数为重复执行次数，LOOP代表一致循环
            //Set frame animation, frame animation can only be set in XImage control, the first parameter is the array of image path, the second is the image source, the third is the frame updating time, the fourth is the number of repetition, LOOP means keeping looping
            mFrameAnimation = new XFrameImageAnimation(images, XUI.Location.ASSETS, 300, XAnimation.REPEAT_LOOP);
            ((XImage)xImage).startImageFrameAnimation(mFrameAnimation);
        }else{
            if( xImage != null && mFrameAnimation != null ){
                ((XImage)xImage).cancelImageFrameAnimation(mFrameAnimation);
                mFrameAnimation = null;
            }
        }


    }

    @Override
    public void onGazeEnter(XActor actor) {

    }

    @Override
    public void onGazeExit(XActor actor) {

    }

    //选中点击开启或者关闭动画
    //Select and click to enable or disable the animation
    @Override
    public boolean onGazeTrigger(XActor actor) {
        if( actor == mRotActor ){
            rotate(actor);
        }else if( actor == mTransActor ){
            trans(actor);
        }else if( actor == mScaleActor ){
            scale(actor);
        }else if( actor ==  mAlphaActor ){
            alpha(actor);
        }else if( actor == mFrameActor ){
            frame(actor);
        }
        return false;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }


    //初始化选中动画，动画只在瞄准点选中时执行，选中点移出后恢复
    //Initialize the selected animation, only proceeded when the gaze is selected, it restores when the gaze point exits
    void initGazeAnimation(){
        XLabel animationLabel = new XLabel("Gaze Focus to Trigger Animation");
        animationLabel.setAlignment(XLabel.XAlign.Center);
        animationLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        animationLabel.setCenterPosition(0, -0.5f, -4f);
        animationLabel.setTextColor(Color.WHITE);
        animationLabel.setSize(3.8f, 0.2f);
        addActor(animationLabel);


        XImage mImage1 = new XImage("photo1.png");
        mImage1.setCenterPosition(-2.0f, -1.0f, -4);
        mImage1.setSize(0.5f, 0.5f);
        //开启默认选中动画，默认为平移动画
        //Enable selected animation by default, the translation animation is the default animation
        mImage1.setEnableGazeAnimation(true);
        addActor(mImage1);

        XImage mImage2 = new XImage("photo1.png");
        mImage2.setCenterPosition(-1.0f, -1.0f, -4);
        mImage2.setSize(0.5f, 0.5f);

        //设置选中平移动画，第一个参数为类型，第二个参数为平移量，平移方向为Z轴方向，正值为朝向眼睛，负值为远离眼睛，第三个参数动画执行时间，单位：毫秒
        //Set selected translation animation, the first parameter is type, the second is the translation value, z-direction, positive value means close to eyes, negative value means far from eyes, the third parameter is duration, unit: ms
        mImage2.setGazeAnimation(XAnimation.AnimationType.TRANSLATE, 0.2f, 200);
        addActor(mImage2);

        XImage mImage3 = new XImage("photo1.png");
        mImage3.setCenterPosition(0f, -1.0f, -4);
        mImage3.setSize(0.5f, 0.5f);

        //设置选中缩放动画，第一个参数为类型，第二个参数为缩放值，第三个参数为执行时间，单位：毫秒
        //Set selected scaling animation, the first parameter is the type, the second is the scaling factor, the third is the duration, unit: ms
        mImage3.setGazeAnimation(XAnimation.AnimationType.SCALE, 0.3f, 200);
        addActor(mImage3);

        XImage mImage4 = new XImage("photo1.png");
        mImage4.setCenterPosition(1.0f, -1.0f, -4);
        mImage4.setSize(0.5f, 0.5f);

        //设置选中旋转动画，旋转轴为Z轴，第一个参数为类型，第二个参数为旋转角度，第三个参数为持续时间
        //Set selected rotation animation, the rotation axis is z axis, the first parameter is type, the second is rotating degree, the third is duration
        mImage4.setGazeAnimation(XAnimation.AnimationType.ROTATE, 30f, 200);
        addActor(mImage4);

        XImage mImage5 = new XImage("photo1.png");
        mImage5.setCenterPosition(2.0f, -1.0f, -4);
        mImage5.setSize(0.5f, 0.5f);

        //设置选中透明度动画，第一个参数为类型，第二个参数为起始透明度，第三个参数为目标透明度，第四个参数为执行时间
        //Set selected alpha animation, the first parameter is type, the second is starting transparency, the third is object transparency, the fourth is duration
        mImage5.setGazeAnimation(XAnimation.AnimationType.ALPHA, 1.0f, 0.5f, 200);
        addActor(mImage5);
    }

    //初始化GIF，SAM，Sprite动画控件
    //Initialize GIF, SAM, and Sprite animation controls
    public void initImageAnimation(){

        XLabel animationLabel = new XLabel("GIF/Super Animation/Sprite Animation");
        animationLabel.setAlignment(XLabel.XAlign.Center);
        animationLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        animationLabel.setCenterPosition(0, -1.5f, -4f);
        animationLabel.setTextColor(Color.WHITE);
        animationLabel.setSize(3.8f, 0.2f);
        addActor(animationLabel);

        //初始化GIF动画控件
        //Initialize GIF animation control

        //支持加载SD卡路径
        //Support loading SD Card path
//        mGIFActor = new XAnimGIFImage("/sdcard/gif/40000306.gif", XUI.Location.SDCARD);

        //支持加载Assets文件
        //Support loading Assets
        mGIFActor = new XAnimGIFImage("animation/40000306.gif", XUI.Location.ASSETS);
        mGIFActor.setCenterPosition(-2.0f, -2.5f, -4);
        mGIFActor.setSize(1.5f, 1.5f);

        //设置开启循环
        //Set to enable loop
        mGIFActor.setLoop(true);

        //设置GIF动画播放帧间隔时间，默认为60ms
        //Set GIF animation play interval time between frames, default: 60ms
        mGIFActor.setIntervalTime(60);

        //设置动画开始结束的监听
        //Set listener for animation starting and ending
        mGIFActor.setAnimationStateListener(new XAnimImage.OnAnimationStateListener() {
            @Override
            public void onAnimationStart(XAnimImage image) {
                Log.d(XLog.TAG, "on GIF Animation START: "+image);
            }

            @Override
            public void onAnimationFinish(XAnimImage image) {
                Log.d(XLog.TAG, "on GIF Animation FIN: "+image);

            }
        });
        addActor(mGIFActor);


        //初始化Super Animation动画控件
        //Initialize Super Animation control

        //支持SD卡路径，参数分别为：sam文件路径，Section名称，音频Mp3路径（无音频可设置为null），资源位置
        //Support SD Card path, parameters: sam file path, Section name, MP3 path (set as null when there's no audio), resource location
//        mSAMActor = new XAnimSAMImage("/sdcard/sam/bmwi8.sam", "audio1", "/sdcard/sam/audio1.mp3", XUI.Location.SDCARD);

        //设置Assets路径下的SAM文件,"animation/sam/audio1.mp3"
        //Set SAM file under Assets, "animation/sam/audio1.mp3"
        mSAMActor = new XAnimSAMImage("animation/sam/bmwi8.sam", "audio1", "animation/sam/audio1.mp3", XUI.Location.ASSETS);
        mSAMActor.setCenterPosition(-1.0f, -2.5f, -4);
        mSAMActor.setSize(1.5f, 1.5f);

        //设置循环开关
        //Set loop switch
        mSAMActor.setLoop(true);

        //设置动画开始结束监听
        //Set listener for animation staring and ending
        mSAMActor.setAnimationStateListener(new XAnimImage.OnAnimationStateListener() {
            @Override
            public void onAnimationStart(XAnimImage image) {
                Log.d(XLog.TAG, "on SAM Animation START: "+image);
            }

            @Override
            public void onAnimationFinish(XAnimImage image) {
                Log.d(XLog.TAG, "on SAM Animation FIN: "+image);

            }
        });
        addActor(mSAMActor);


        //初始化Sprite动画控件
        //Initialize Sprite animation control

        //加载Sprite动画，参数分别为：xml路径（对应的图片资源需要放在同级目录下），资源位置
        //Load Sprite animation, parameters: xml path (the corresponding image resources should be put under the directory with the same level), resource location
        mSpritActor = new XAnimSpriteImage("animation/sprite/gold.xml", XUI.Location.ASSETS);
        mSpritActor.setCenterPosition(1.5f, -2.5f, -4);
        mSpritActor.setSize(1.5f, 1.5f);

        //其他接口定义与GIF,SAM动画类似
        //Other definitions of APIs are similar to GIF and SAM animation
        mSpritActor.setLoop(true);
        mSpritActor.setAnimationStateListener(new XAnimImage.OnAnimationStateListener() {
            @Override
            public void onAnimationStart(XAnimImage image) {
                Log.d(XLog.TAG, "on Sprite Animation START: "+image);
            }

            @Override
            public void onAnimationFinish(XAnimImage image) {
                Log.d(XLog.TAG, "on Sprite Animation FIN: "+image);

            }
        });

        addActor(mSpritActor);

    }

}
