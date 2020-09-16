package com.image.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.image.AImageLoad;
import com.image.IImageLoad;
import com.image.transform.ITransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PicassoImpl extends AImageLoad {
    private Picasso mPicasso;

    public PicassoImpl(Bitmap.Config bitmapConfig, Context context) {
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.defaultBitmapConfig(bitmapConfig);
        mPicasso = builder.build();
        Picasso.setSingletonInstance(mPicasso);
    }

    @Override
    public void load(int defaultImg, int error, String path, ImageView imageView, ITransform transform) {
        RequestCreator requestCreator = mPicasso.load(path).transform(new PicassoTransformation(transform, null, path));
        if (defaultImg > 0)
            requestCreator.placeholder(defaultImg);
        if (error > 0)
            requestCreator.error(error);
        requestCreator.into(imageView);
    }

    @Override
    public void loadBg(int defaultImg, int error, String path, View view, ITransform transform) {
        ImageView imageView = new ImageView(view.getContext().getApplicationContext());
        imageView.setLayoutParams(view.getLayoutParams());
        RequestCreator requestCreator = mPicasso.load(path).transform(new PicassoTransformation(transform, null, path));
        if (defaultImg > 0)
            requestCreator.placeholder(defaultImg);
        if (error > 0)
            requestCreator.error(error);
        requestCreator.into(imageView);
    }
}
