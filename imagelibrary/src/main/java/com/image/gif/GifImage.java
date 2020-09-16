package com.image.gif;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import com.image.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;

public class GifImage extends ImageView implements android.os.Handler.Callback {
    private GifDecoder.GifFrame[] frames;
    private boolean gif;
    private int index = 0;
    private boolean isAdd;
    private Handler handler = new Handler(Looper.getMainLooper(), this);

    public GifImage(Context context) {
        super(context);
    }

    @Override
    public boolean handleMessage(Message msg) {
        index++;
        if (index >= frames.length)
            index = 0;
        setImageBitmap(frames[index].image);
        if (gif)
            handler.sendEmptyMessageDelayed(0, frames[index].delay);
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAdd = true;
        if (gif)
            handler.sendEmptyMessageDelayed(0, frames[index].delay);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAdd = false;
        if (gif)
            handler.removeCallbacksAndMessages(null);
    }

    public GifImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GifImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public GifImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setImageResource(int resId) {
        GifDecoder decoder = new GifDecoder();
        InputStream stream = getResources().openRawResource(resId);
        decoder.read(stream);
        if (decoder.err()) {
            handler.removeCallbacksAndMessages(null);
            gif = false;
            frames = null;
            super.setImageResource(resId);
        } else {
            frames = decoder.getFrames();
            index = 0;
            gif = true;
            setImageBitmap(frames[index].image);
        }
    }

    public void setImageAssets(String name) {
        try {
            GifDecoder decoder = new GifDecoder();
            InputStream stream = getResources().getAssets().open(name);
            decoder.read(stream);
            if (decoder.err()) {
                handler.removeCallbacksAndMessages(null);
                gif = false;
                frames = null;
                setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(name)));
            } else {
                frames = decoder.getFrames();
                index = 0;
                gif = true;
                setImageBitmap(frames[index].image);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        try {
            GifDecoder decoder = new GifDecoder();
            InputStream stream = getContext().getContentResolver().openInputStream(uri);
            decoder.read(stream);
            if (decoder.err()) {
                handler.removeCallbacksAndMessages(null);
                gif = false;
                frames = null;
                super.setImageURI(uri);
            } else {
                frames = decoder.getFrames();
                index = 0;
                gif = true;
                setImageBitmap(frames[index].image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageFile(File file) {
        try {
            GifDecoder decoder = new GifDecoder();
            InputStream stream = new FileInputStream(file);
            decoder.read(stream);
            if (decoder.err()) {
                handler.removeCallbacksAndMessages(null);
                gif = false;
                frames = null;
                super.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(file)));
            } else {
                frames = decoder.getFrames();
                index = 0;
                gif = true;
                setImageBitmap(frames[index].image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageFile(String filePath) {
        setImageFile(new File(filePath));
    }

    /**
     * description：最好把图片下载下来避免每次区网上请求
     */
    @Deprecated
    public void setImageNet(final String path) {
        new Thread() {
            @Override
            public void run() {
                try {
                    final HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
                    if (connection.getHeaderField("content-type").toUpperCase().contains("GIF")) {
                        GifDecoder decoder = new GifDecoder();
                        decoder.read(connection.getInputStream());
                        if (!decoder.err()) {
                            frames = decoder.getFrames();
                            index = 0;
                            gif = true;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setImageBitmap(frames[index].image);
                                    if (isAdd)
                                        handler.sendEmptyMessageDelayed(0, frames[index].delay);

                                }
                            });
                        }
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setImageBitmap(BitmapFactory.decodeStream(connection.getInputStream()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void finalize() {
        super.finalize();
        if (frames != null)
            for (GifDecoder.GifFrame frame : frames) {
                if (!frame.image.isRecycled())
                    frame.image.recycle();
            }
    }
}
