package com.image.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.image.ImageUtil;
import com.image.R;
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

public class ImageView extends android.widget.ImageView {
    private static final int CIRCLE = 0;
    private static final int DISCOKORATION = 1;
    private static final int FILM = 2;
    private static final int FUZZY = 3;
    private static final int HEART = 4;
    private static final int HIGH_DEGREE_SATURATION = 5;
    private static final int ICE = 6;
    private static final int LOMO_FILTER = 7;
    private static final int NOSTALGIA = 8;
    private static final int OLD_TIME_FILTER = 9;
    private static final int POLYGON = 10;
    private static final int REFLECTION = 11;
    private static final int RELIEF = 12;
    private static final int ROTATE = 13;
    private static final int ROUND = 14;
    private static final int SHARPEN = 15;
    private static final int SKETCH = 16;
    private static final int STAR = 17;
    private static final int SUNSHINE = 18;
    private static final int WARMTH_FILTER = 19;
    protected Drawable drawable;
    private Field mDrawable;
    protected ITransform transform;
    protected ITransform mTransform;

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

    @TargetApi(21)
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getResources().obtainAttributes(attrs, R.styleable.ImageView);
            int t = a.getInt(R.styleable.ImageView_transform, -1);
            switch (t) {
                case CIRCLE:
                    transform = new CircleTransform();
                    break;
                case DISCOKORATION:
                    transform = new DiscolorationTransform();
                    break;
                case FILM:
                    transform = new FilmTransform();
                    break;
                case FUZZY:
                    transform = new FuzzyTransform();
                    break;
                case HEART:
                    transform = new HeartTransform();
                    break;
                case HIGH_DEGREE_SATURATION:
                    transform = new HighDegreeSaturationTransform();
                    break;
                case ICE:
                    transform = new IceTransform();
                    break;
                case LOMO_FILTER:
                    transform = new LomoFilterTransform();
                    break;
                case NOSTALGIA:
                    transform = new NostalgiaTransform();
                    break;
                case OLD_TIME_FILTER:
                    transform = new OldTimeFilterTransform();
                    break;
                case POLYGON:
                    int polygon = a.getInt(R.styleable.ImageView_polygon, 5);
                    transform = new PolygonTransfrom(polygon);
                    break;
                case REFLECTION:
                    transform = new ReflectionTransform();
                    break;
                case RELIEF:
                    transform = new ReliefTransform();
                    break;
                case ROTATE:
                    float degree = a.getFloat(R.styleable.ImageView_rotateDegree, 20);
                    transform = new RotateTransform(degree);
                    break;
                case ROUND:
                    float rx = a.getDimensionPixelSize(R.styleable.ImageView_roundRx, 20);
                    float ry = a.getDimensionPixelSize(R.styleable.ImageView_roundRy, 20);
                    transform = new RoundTransform(rx, ry);
                    break;
                case SHARPEN:
                    transform = new SharpenTransform();
                    break;
                case SKETCH:
                    transform = new SketchTransform();
                    break;
                case STAR:
                    transform = new StarTransform();
                    break;
                case SUNSHINE:
                    float sx = a.getDimensionPixelSize(R.styleable.ImageView_sunShineLigthX, 1);
                    float sy = a.getDimensionPixelSize(R.styleable.ImageView_sunShineLigthY, 1);
                    transform = new SunshineTransform(sx, sy);
                    break;
                case WARMTH_FILTER:
                    transform = new WarmthFilterTransform();
                    break;
            }
            a.recycle();
        }
        drawable = getDrawable();
        try {
            mDrawable = android.widget.ImageView.class.getDeclaredField("mDrawable");
            mDrawable.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTransform(ITransform transform) {
        this.transform = transform;
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (transform != null && !transform.equals(mTransform)) {
            mTransform = transform;
            Drawable drawable = getDrawable();
            if (drawable != null) {
                if (drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
                    Bitmap bitmap = ImageUtil.drawable2bitmap(drawable);
                    bitmap = transform.transform(bitmap);
                    try {
                        mDrawable.set(this, new BitmapDrawable(getResources(), bitmap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }     // nothing to draw (empty bounds)
                }
            }
        }
        super.onDraw(canvas);
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
