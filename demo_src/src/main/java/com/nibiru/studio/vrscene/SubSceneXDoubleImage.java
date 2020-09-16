package com.nibiru.studio.vrscene;

import android.view.KeyEvent;

import x.core.ui.XBaseScene;
import x.core.ui.XDoubleSideImage;
import x.core.ui.XLabel;
import x.core.ui.XUI;

/**
 * 演示图片控件，图片控件支持多种图片来源：assets路径，sdcard路径，Android resource ID, Android Drawable，Bitmap
 * 支持JPG和PNG格式图片
 * 网络路径图片可先下载到存储设备，通过sdcard路径加载，或者直接通过Bitmap加载
 */
public class SubSceneXDoubleImage extends XBaseScene {

    @Override
    public void onCreate() {

        initImageDouble();

    }

    XDoubleSideImage imageDouble;
    void initImageDouble()
    {
        imageDouble = new XDoubleSideImage("Red.png", "Blue.png");
        imageDouble.setSize(0.5f,0.5f);
        imageDouble.setCenterPosition(1,1,-4);

        addActor(imageDouble);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        XUI.getInstance().setOverLayUIEnable(false);
    }

    XLabel titleLabel;

    float x = 0;
    @Override
    public boolean onKeyDown(int keyCode)
    {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

            imageDouble.pageAnimate(x);

            x = x - 15;
        }


        if(keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            x = 0;
            imageDouble.setPageAnimateType( XDoubleSideImage.PageAnimateType.Up);
        }


        return super.onKeyDown(keyCode);
    }

}
