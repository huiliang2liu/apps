package com.nibiru.studio.arscene;

import android.graphics.Bitmap;

import com.nibiru.studio.BitmapUtils;
import com.nibiru.studio.CalculateUtils;

import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.util.PicturesUtil;

/**
 * 统一绘制背景框
 * Draw a background frame uniformly
 * Created by GN on 2017/9/29.
 */

public class BaseScene extends XBaseScene {

    @Override
    public void onCreate() {
        if(!PicturesUtil.isBitmapTextureCreated("scence_bk")) {
            Bitmap bitmap = BitmapUtils.getBackgourndBitmap(1440 / 2, 1080 / 2, 2, 0x99e2e2ff);
            PicturesUtil.createBitmapTexture(bitmap, "scence_bk");
        }
        if(!PicturesUtil.isBitmapTextureCreated("trans")) {
            Bitmap bitmap = BitmapUtils.getBackgourndBitmap(200, 200, 2, 0x00000000);
            PicturesUtil.createBitmapTexture(bitmap, "trans");
        }
        if(!PicturesUtil.isBitmapTextureCreated("btn_normal")) {
            Bitmap bitmap = BitmapUtils.getBackgourndBitmap(100, 50, 5, 0x80ffffff);
            PicturesUtil.createBitmapTexture(bitmap, "btn_normal");
        }
        if(!PicturesUtil.isBitmapTextureCreated("btn_focused")) {
            Bitmap bitmap = BitmapUtils.getBackgourndBitmap(100, 50, 5, 0xffffffff);
            PicturesUtil.createBitmapTexture(bitmap, "btn_focused");
        }
        float bg_x = CalculateUtils.transformSize(1440);
        float bg_y = CalculateUtils.transformSize(1080);
        XImage xImage = new XImage("scence_bk");
        xImage.setSize(bg_x, bg_y);
        xImage.setCenterPosition(0, 0, CalculateUtils.CENTER_Z_BG);
        xImage.setRenderOrder(2);
        addActor(xImage);

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
}
