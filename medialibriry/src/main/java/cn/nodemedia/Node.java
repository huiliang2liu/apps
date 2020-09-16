package cn.nodemedia;

import com.media.IMedia;

import androidx.annotation.NonNull;

public interface Node extends IMedia {
    void setPageUrl(@NonNull String pageUrl);

    void setSwfUrl(@NonNull String swfUrl);

    void setConnArgs(@NonNull String connArgs);

    void setRtspTransport(@NonNull String rtspTransport);

    void setBufferTime(int bufferTime);

    void setMaxBufferTime(int maxBufferTime);

    void setHWEnable(boolean hwEnable);

    void setAutoReconnectWaitTimeout(int autoReconnectWaitTimeout);

    void setConnectWaitTimeout(int connectWaitTimeout);

    void setAudioEnable(boolean audioEnable);

    void setVideoEnable(boolean videoEnable);

    void setSubscribe(boolean subscribe);

}
