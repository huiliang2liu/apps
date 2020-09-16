package com.nagasoft.player;

import android.util.Log;

import java.lang.ref.WeakReference;

public class VJPlayer {
    public static final int PLAYER_NOTIFY_URL = 0x1011;
    protected static final String TAG = "NGPlayer";
    private static boolean gbload = false;

    private int mNativePlayer = 0;
    private int mNativeListener = 0;
    private String mStrVideoURL;
    private UrlChanged mUCCallback = null;
    private OnVJMSErrorListener mVELCallback = null;

    static {
        try {
            System.loadLibrary("p2pcore");
            System.loadLibrary("vjplayer_jni");
            gbload = true;
            native_init();
        } catch (Throwable e) {
            gbload = false;
            Log.e(TAG, "loadLibrary libvjplayer_jni error: " + e.toString());
        }
    }

    public VJPlayer(UrlChanged uc) {
        if (gbload)
            native_setup(new WeakReference<VJPlayer>(this));
        mUCCallback = uc;
    }

    public void setOnVJMSErrorListener(OnVJMSErrorListener vel) {
        mVELCallback = vel;
    }

    // notify player server url
    public void notifyPlayURL(String strURL) {
        Log.d(TAG, "from native string is " + strURL);
        mStrVideoURL = strURL;
        notifyUI(strURL);
    }

    public void notifyError(int nErrorCode) {
        Log.d(TAG, "from native error is " + nErrorCode);
        notifyUIError(nErrorCode);
    }

    protected void notifyUI(String strURL) {
        if (mUCCallback != null) {
            mUCCallback.onUrlChanged(strURL);
        }
    }

    protected void notifyUIError(int nCode) {
        if (mVELCallback != null)
            mVELCallback.onVJMSError(nCode);
    }

    public String getVideoURL() {
        return mStrVideoURL;
    }

    // init
    public static native final void native_init();

    // finalize
    public native final void native_finalize();

    // setup
    public native final void native_setup(Object vjplayer_this);

    // release
    public native void _release();

    public boolean play(String url) {
        if (!gbload)
            return false;
        try {
            setURL(url);
            start();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            notifyUIError(0);
        }
        return false;
    }

    public void stopAndRelease() {
        if (gbload) {
            stop();
            _release();
        }
    }

    // set url
    public native void setURL(String strURL);

    // start
    public native boolean start();

    // stop
    public native void stop();

    // setvjmsbuffertimeout
    public native void setVJMSBufferTimeout(int nTime);

    // islivestream
    public native boolean isLiveStream();

    // isvodfile
    public native boolean isVodFile();

    // isplaybackstream
    public native boolean isPlayBackStream();

    // getplaybackduration
    public native long getPlayBackDuration();

    // islivestream
    public native boolean seekPlayBack(long lTime);

}
