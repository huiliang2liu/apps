package com.base.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.base.BaseApplication;
import com.base.R;
import com.image.ImageUtil;
import com.image.transform.CircleTransform;
import com.image.transform.DiscolorationTransform;
import com.image.transform.FilmTransform;
import com.image.transform.FuzzyTransform;
import com.image.transform.HeartTransform;
import com.image.transform.HighDegreeSaturationTransform;
import com.image.transform.ITransform;
import com.image.transform.IceTransform;
import com.image.transform.LomoFilterTransform;
import com.image.transform.NostalgiaTransform;
import com.image.transform.OldTimeFilterTransform;
import com.image.transform.PolygonTransfrom;
import com.image.transform.ReflectionTransform;
import com.image.transform.ReliefTransform;
import com.image.transform.RotateTransform;
import com.image.transform.RoundTransform;
import com.image.transform.SharpenTransform;
import com.image.transform.SketchTransform;
import com.image.transform.StarTransform;
import com.image.transform.SunshineTransform;
import com.image.transform.WarmthFilterTransform;

import java.lang.reflect.Field;

public class ImageView extends com.image.widget.ImageView {
    private BaseApplication application;
    private String mPath;
    private int errorId = -1;
    private int startId = -1;

    public ImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        application = (BaseApplication) context.getApplicationContext();
        if (attrs != null) {
            TypedArray a = getResources().obtainAttributes(attrs, R.styleable.ImageView);
            startId = a.getResourceId(R.styleable.ImageView_defaultId, -1);
            errorId = a.getResourceId(R.styleable.ImageView_errorId, -1);
        }

    }


    public void setImagePath(String path) {
        if (path == null || path.isEmpty()) {
            if (errorId > 0)
                setImageResource(errorId);
            else
                setImageDrawable(drawable);
            return;
        }
        if (path.equals(mPath))
            return;
        mPath = path;
        mTransform = transform;
        application.imageLoad.load(startId, errorId, path, this, transform);
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public void setDefaultId(int defaultId) {
        startId = defaultId;
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
        }
        Drawable drawable = getDrawable();
        if (drawable != null
                && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null)
                bitmap.recycle();
        }
    }
}
