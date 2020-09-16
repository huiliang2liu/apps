package com.image.gif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

import androidx.annotation.NonNull;

public class GifDrawable extends BitmapDrawable {
    private boolean gif;
    private GifDecoder mDecoder;
    private int index = 0;
    private Bitmap.Config config = Bitmap.Config.ARGB_8888;

    public GifDrawable(InputStream inputStream) {
        GifDecoder decoder = new GifDecoder();
        gif = decoder.read(inputStream) == GifDecoder.STATUS_OK;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }
}
