package com.floatW;

import android.content.Context;
import android.view.WindowManager;

public class MediaFloat extends AppFloat{
    public MediaFloat(Context context, WindowManager windowManager) {
        super(context, windowManager);
    }

    @Override
    protected int type() {
        return WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA;
    }
}
