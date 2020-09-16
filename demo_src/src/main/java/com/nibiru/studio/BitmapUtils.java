package com.nibiru.studio;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by GN on 2017/8/8.
 */

public class BitmapUtils {

    public static Bitmap getBackgourndBitmap(int width, int height, int radius, int color) {
        Bitmap localBitmap = Bitmap.createBitmap(width + 4, height + 4,
                Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setColor(color);
        localCanvas.drawRoundRect(new RectF(2, 2, width + 2, height + 2), radius, radius, localPaint);
        return localBitmap;
    }

    public static Bitmap generateBitmapRect(int width, int height, int color) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setColor(color);
        localCanvas.drawRect(new RectF(0, 0, width, height), localPaint);
        return localBitmap;
    }

    public static Bitmap generateBitmapRoundRect(int width, int height, int color, int round) {
        Bitmap localBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setColor(color);
        localCanvas.drawRoundRect(new RectF(0, 0, width, height), round, round, localPaint);
        return localBitmap;
    }

}
