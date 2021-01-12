package com.image.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.image.AImageLoad;
import com.image.transform.ITransform;

public class GlideImpl extends AImageLoad {
    private Context context;
    private Bitmap.Config config;
    private Glide glide;
    private GlideGlideModule glideGlideModule;
    private static final int CACHE_SIXE = 1024 * 1023 * 100;

    public GlideImpl(Bitmap.Config config, Context context, String path) {
        this.config = config;
        this.context = context;
        if (path == null || path.isEmpty())
            path = context.getExternalFilesDir(null).getParent() + "/glide";
        switch (config) {
            case ALPHA_8:
            case RGB_565:
            case HARDWARE:
            case RGBA_F16:
            case ARGB_4444:
                GlideGlideModule.decodeFormat = DecodeFormat.PREFER_RGB_565;
                break;
            case ARGB_8888:
                GlideGlideModule.decodeFormat = DecodeFormat.PREFER_ARGB_8888;
                break;

        }
        GlideGlideModule.mPath = path;
        GlideGlideModule.mSize = CACHE_SIXE;
        this.glide = Glide.get(context);
    }

    @Override
    public void load(int defaultImg, int error, String path, ImageView imageView, ITransform transform) {
        DrawableRequestBuilder drawableRequestBuilder = Glide.with(context).load(path).transform(new GlideTransformation(context, path, transform));
        if (defaultImg > 0)
            drawableRequestBuilder.placeholder(defaultImg);
        if (error > 0)
            drawableRequestBuilder.error(error);
        drawableRequestBuilder.into(imageView);
    }

    @Override
    public void loadBg(int defaultImg, int error, String path, View view, ITransform transform) {
        DrawableRequestBuilder drawableRequestBuilder = Glide.with(context).load(path).transform(new GlideTransformation(context, path, transform));
        if (defaultImg > 0)
            drawableRequestBuilder.placeholder(defaultImg);
        if (error > 0)
            drawableRequestBuilder.error(error);
        drawableRequestBuilder.into(new GlideViewTarget(view));
    }
}
