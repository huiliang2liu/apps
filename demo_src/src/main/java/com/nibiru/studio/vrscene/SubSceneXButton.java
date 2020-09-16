package com.nibiru.studio.vrscene;

/**
 * Created by gaoning on 2018/1/23.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;

import com.nibiru.studio.xrdemo.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XButton;
import x.core.ui.XLabel;
import x.core.ui.XToast;
import x.core.ui.XUI;

/**
 * 演示XButton控件
 */

/**
 * Show XButton
 */
public class SubSceneXButton extends XBaseScene implements XButton.IXOnClickListener {

    XButton xButton1, xButton2, xButton3, xButton4, xButton5;

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


        XLabel titleLabel = new XLabel("Example：Button");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 2.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        XLabel textLable = new XLabel("Background from Color, Asset, Resource, SDcard, Bitmap");
        textLable.setCenterPosition(0, 1.5f, -4f);
        textLable.setAlignment(XLabel.XAlign.Center);
        textLable.setSize(5f, 0.18f);
        addActor(textLable);

//        XLabel tipLable = new XLabel("Notice:Please push a png named \"button_background.png\" into sdcard");
//        tipLable.setCenterPosition(0, -1.5f, -4f);
//        tipLable.setAlignment(XLabel.XAlign.Center);
//        tipLable.setArrangementMode(XLabel.XArrangementMode.MultiRow);
//        tipLable.setSize(4f, 0.18f);
//        addActor(tipLable);

        //创建XButton
        //Create XButton
        xButton1 = new XButton("Button1");
        xButton1.setName("button1");
        //设置选中/未选中背景颜色
        //Set selected/unselected background color
        xButton1.setBackgroundColor(0xffff0000, 0xff00ff00);

        //支持设置选中/选中文本的资源ID，便于国际化
        //Support setting resource ID of selected/unselected text for internationalization
//        xButton.setText(R.string.button_title_selected, R.string.button_title_unselected);

        //设置文本选中/未选中颜色
        //Set selected/unselected text color
        xButton1.setTextColor(0xffffffff, 0xff000000);

        //支持直接设置选中/未选中文本
        //Support for setting selected/unchecked text directly
//        xButton.setText("Button Selected", "Button");

        //设置选中时开启跑马灯
        //Enable marquee when it's selected
       // xButton.setSelectedArrangementMode(XLabel.XArrangementMode.SingleRowMove);
        //设置对齐方式
        //Set alignment
        xButton1.setUnselectedAlign(XLabel.XAlign.Center);
        //设置点击事件
        //Set click event
        xButton1.setOnClickListener(this);

        xButton1.setCenterPosition(-0.9f, 0.8f, -4f);
        //设置整个按钮的大小
        //Set button size
        xButton1.setSize(1.5F, 0.5F);
        //设置字体大小
        //Set text size
        xButton1.setTextSize(0.25f);

        addActor(xButton1);


        //创建XButton
        //Create XButton
        xButton2 = new XButton("Button2");
        xButton2.setName("button2");
        //设置选中和未选中图片，图片来自于assets
        //Set selected and unselected image, teh image is from assets
        xButton2.setBackgroundName("Red.png", "Blue.png");

        //设置文本选中/未选中颜色
        //Set selected/unselected text color
        xButton2.setTextColor(0xffffffff, 0xff000000);

        //设置对齐方式
        //Set alignment
        xButton2.setUnselectedAlign(XLabel.XAlign.Center);

        //设置点击事件
        //Set click event
        xButton2.setOnClickListener(this);

        xButton2.setCenterPosition(0.9f, 0.8f, -4f);
        //设置整个按钮的大小
        //Set button size
        xButton2.setSize(1.5F, 0.5F);
        //设置字体大小
        //Set text size
        xButton2.setTextSize(0.25f);

        addActor(xButton2);

        //创建XButton
        //Create XButton
        xButton3 = new XButton("Button3");
        xButton3.setName("button3");
        //设置选中和未选中的图片，图片来自于Resource
        //Set selected and unselected image, the image is from Resource
        xButton3.setBackgroundResourceId(R.mipmap.black_70, R.mipmap.black_70);

        //设置文本选中/未选中颜色
        //Set selected/unselected text color
        xButton3.setTextColor(0xffffffff, 0xffffffff);

        //设置对齐方式
        //Set alignment
        xButton3.setUnselectedAlign(XLabel.XAlign.Center);

        //设置点击事件
        //Set click event
        xButton3.setOnClickListener(this);

        xButton3.setCenterPosition(-0.9f, 0f, -4f);
        //设置整个按钮的大小
        //Set the button size
        xButton3.setSize(1.5F, 0.5F);
        //设置字体大小
        //Set text size
        xButton3.setTextSize(0.25f);

        addActor(xButton3);

        String filePath = Environment.getExternalStorageDirectory() + "/" + "button_background.png";
        File file = new File(filePath);

        if( !file.exists() ){
            //当SD卡没有图片存在时，从Assets拷贝一张默认图片到SD路径用于显示
            //When there' no image in SD Card, copy the default image from Assets to SD Card for display
            copyAssetsToSdcard("bluexx.png", filePath);
        }

        //创建XButton
        //Create XButton
        xButton4 = new XButton("Button4");
        xButton4.setName("button4");
        //设置选中和未选中图片，图片来自于外部存储
        //Set selected and unselected image, the image is from the external storage
        xButton4.setBackgroundName("sdcard/button_background.png", "sdcard/button_background.png", XUI.Location.SDCARD);

        //设置文本选中/未选中颜色
        //Set selecte/unselected text color
        xButton4.setTextColor(0xffffffff, 0xff000000);

        //设置对齐方式
        //Set alignment
        xButton4.setUnselectedAlign(XLabel.XAlign.Center);

        //设置点击事件
        //Set click event
        xButton4.setOnClickListener(this);

        xButton4.setCenterPosition(0.9f, 0f, -4f);
        //设置整个按钮的大小
        //Set the button size
        xButton4.setSize(1.5F, 0.5F);
        //设置字体大小
        //Set text size
        xButton4.setTextSize(0.25f);

        addActor(xButton4);

        //创建XButton
        //Create XButton
        xButton5 = new XButton("Button5");
        xButton5.setName("button5");
        //设置选中和未选中的图片，图片来自于Bitmap
        //Set selected and unselected image, the image is from Bitmap
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/button_background.png");
        xButton5.setBackgroundBitmap(bitmap, bitmap);

        //设置文本选中/未选中颜色
        //Set selected/unselected text color
        xButton5.setTextColor(0xffffffff, 0xff000000);

        //设置对齐方式
        //Set alignment
        xButton5.setUnselectedAlign(XLabel.XAlign.Center);

        //设置点击事件
        //Set click event
        xButton5.setOnClickListener(this);

        xButton5.setCenterPosition(-0.9f, -0.8f, -4f);
        //设置整个按钮的大小
        //Set button size
        xButton5.setSize(1.5F, 0.5F);
        //设置字体大小
        //Set text size
        xButton5.setTextSize(0.25f);

        addActor(xButton5);


    }


    @Override
    public boolean onClick(XActor actor) {
        XToast toast = XToast.makeToast(this,"Click " + actor.getName(), 1000);
        toast.setGravity(0.0f, -0.6f, -3f);
        toast.setSize(4f, 0.2f);
        toast.show(false);
        return true;
    }

    //拷贝Assets图片到SD卡中，这里用于SD卡图片路径不存在时拷贝Assets路径下的默认图片
    //Copy the default image in Assets to SD Card when there's no image in SD Card
    private void copyAssetsToSdcard(String from, String to){

        try {
            int bytesum = 0;
            int byteread = 0;

            InputStream inStream = getResources().getAssets().open(from);
            OutputStream fs = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();

        } catch (Exception e) {
        }

    }
}

