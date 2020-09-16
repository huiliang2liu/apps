package com.nibiru.studio.arscene;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import x.core.listener.IXBitmapTextureCreateSuccessListener;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XLabel;
import x.core.util.PicturesUtil;

public class TexturemanagerScene extends XBaseScene {
    @Override
    public void onCreate() {
        XLabel label=new XLabel("External texture management");
        label.setCenterPosition(0,0.6f,-3);
        label.setSize(2,0.12f);
        addActor(label);
        createview();
    }

    private void createview() {
        for (int i = 0; i < 5; i++) {
            Bitmap imageFromAssetsFile = getImageFromAssetsFile("skybox/changjing1.png", this);
            final int finalI = i;
            PicturesUtil.createBitmapTexture(imageFromAssetsFile, "changjing" + Math.random() + "abc", new IXBitmapTextureCreateSuccessListener() {
                @Override
                public void onCreateSuccess(String texName, boolean isSuccess) {
                    XImage one = new XImage(texName);
                    one.setName(texName);
                    one.setCenterPosition(0.1f * finalI, -0.1f * finalI, -3f + finalI * 0.1f);
                    one.setSize(0.4f, 0.3f);
                    addActor(one);
                }
            });
        }
    }

    public Bitmap getImageFromAssetsFile(String fileName, Context context) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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

