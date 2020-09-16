package cn.nodemedia;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.media.MediaListener;

import androidx.annotation.NonNull;


public class NodeImpl implements Node, NodePlayerDelegate {
    private static final String TAG = "NodeImpl";
    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int BUFFER_TIME = 10;
    private static final int MAX_BUFFER_TIME = 30 * BUFFER_TIME;
    private static final int RECONNECT_TIMEOUT = CONNECT_TIMEOUT;
    private static final boolean HW_ENABLE = true;
    private NodePlayer nodePlayer;
    private MediaListener listener;
    private Context context;
    private String path;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.what;
            String m = (String) msg.obj;
            if (listener != null) {
                if (event == 1104) {
                    String[] sp = m.split("x");
                    int width = Integer.valueOf(sp[0]);
                    int height = Integer.valueOf(sp[1]);
                    listener.onVideoSizeChanged(width, height);
                } else if (event == 1006) {
                    listener.onError(MediaPlayer.MEDIA_ERROR_SERVER_DIED, MediaPlayer.MEDIA_ERROR_TIMED_OUT);
                } else if (event == 1000) {
                    listener.onInfo(MediaPlayer.MEDIA_INFO_UNKNOWN, 0);
                    Log.d(TAG, "正在连接视频");
                } else if (event == 1001) {
//                    listener.onInfo(MediaPlayer.MEDIA_INFO_UNKNOWN, 1);
                    listener.onPrepared();
                    Log.d(TAG, "视频连接成功");
                } else if (event == 1002) {
                    listener.onError(MediaPlayer.MEDIA_ERROR_SERVER_DIED, MediaPlayer.MEDIA_ERROR_IO);
                } else if (event == 1003) {
                    listener.onInfo(MediaPlayer.MEDIA_INFO_UNKNOWN, 2);
                    Log.d(TAG, "视频开始重连,自动重连总开关");
                } else if (event == 1004) {
                    listener.onCompletion();
                } else if (event == 1005) {
                    listener.onInfo(MediaPlayer.MEDIA_INFO_UNKNOWN, 3);
                    Log.d(TAG, "网络异常,播放中断,播放中途网络异常，回调这里。1秒后重连，如不需要，可停止");
                } else if (event == 1100 || event == 1101) {
                    listener.onInfo(MediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
                    Log.d(TAG, event == 1100 ? "播放缓冲区为空" : "播放缓冲区正在缓冲数据");
                } else if (event == 1102) {
                    listener.onInfo(MediaPlayer.MEDIA_INFO_BUFFERING_END, 0);
                } else if (event == 1103) {
                    listener.onInfo(MediaPlayer.MEDIA_INFO_UNKNOWN, 4);
                    Log.d(TAG, "收到RTMP协议Stream EOF,或 NetStream.Play.UnpublishNotify, 会进行自动重连.");
                } else {
                    Log.d(TAG, String.format("event:%d,msg:%s", event, m));
                }
            } else {
                Log.d(TAG, String.format("event:%d,msg:%s", event, m));
            }
        }
    };

    public NodeImpl(Context context) {
        this.context = context;
        nodePlayer = new NodePlayer(context);
        nodePlayer.setNodePlayerDelegate(this);
        setConnectWaitTimeout(CONNECT_TIMEOUT);
        setHWEnable(HW_ENABLE);
        setSubscribe(true);
        setBufferTime(BUFFER_TIME);
        setMaxBufferTime(MAX_BUFFER_TIME);
        setRtspTransport(NodePlayer.RTSP_TRANSPORT_HTTP);
        setAutoReconnectWaitTimeout(RECONNECT_TIMEOUT);
    }

    @Override
    public void setMedialistener(MediaListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEventCallback(NodePlayer player, int event, String msg) {
        Message message = new Message();
        message.what = event;
        message.obj = msg;
        handler.sendMessage(message);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        nodePlayer.setSurfaceHolder(holder);
    }

    @Override
    public void pause() {
        nodePlayer.stop();
    }

    @Override
    public boolean isPlay() {
        return nodePlayer.isPlaying();
    }

    @Override
    public void play() {
        nodePlayer.start();
    }

    @Override
    public void setPath(String path) {
        nodePlayer.setInputUrl(path);
        nodePlayer.start();
        this.path = path;
    }

    @Override
    public long getDuration() {
        return nodePlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return nodePlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(long seek) {
        nodePlayer.seekTo(seek);
    }

    @Override
    public void setSpeed(float speed) {
        Log.d(TAG, "not support setSpeed");
    }

    @Override
    public void destroy() {
        nodePlayer.stop();
        nodePlayer.release();
    }

    @Override
    public void stop() {
        nodePlayer.stop();
    }

    @Override
    public void reset() {
//        if (isPlay())
//            pause();
//        if (path != null && !path.isEmpty())
//            setPath(path);

    }

    @Override
    public void setPageUrl(@NonNull String pageUrl) {
        nodePlayer.setPageUrl(pageUrl);
    }

    @Override
    public void setSwfUrl(@NonNull String swfUrl) {
        nodePlayer.setSwfUrl(swfUrl);
    }

    @Override
    public void setConnArgs(@NonNull String connArgs) {
        nodePlayer.setConnArgs(connArgs);
    }

    @Override
    public void setRtspTransport(@NonNull String rtspTransport) {
        nodePlayer.setRtspTransport(rtspTransport);
    }

    @Override
    public void setBufferTime(int bufferTime) {
        nodePlayer.setBufferTime(bufferTime);
    }

    @Override
    public void setMaxBufferTime(int maxBufferTime) {
        nodePlayer.setMaxBufferTime(maxBufferTime);
    }

    @Override
    public void setHWEnable(boolean hwEnable) {
        nodePlayer.setHWEnable(hwEnable);
    }

    @Override
    public void setAutoReconnectWaitTimeout(int autoReconnectWaitTimeout) {
        nodePlayer.setAutoReconnectWaitTimeout(autoReconnectWaitTimeout);
    }

    @Override
    public void setConnectWaitTimeout(int connectWaitTimeout) {
        nodePlayer.setConnectWaitTimeout(connectWaitTimeout);
    }

    @Override
    public void setAudioEnable(boolean audioEnable) {
        nodePlayer.setAudioEnable(audioEnable);
    }

    @Override
    public void setVideoEnable(boolean videoEnable) {
        nodePlayer.setVideoEnable(videoEnable);
    }

    @Override
    public void setSubscribe(boolean subscribe) {
        nodePlayer.setSubscribe(subscribe);
    }
}
