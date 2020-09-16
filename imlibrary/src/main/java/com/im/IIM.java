package com.im;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;
import java.util.Map;

public interface IIM<T> {
    enum MessageType {
        FILE, LOCATION, IMAGE, VIDEO, VOICE, TEXT
    }

    void sendFile(String filePath, boolean group, String name, Callback callback);

    void sendFile(String filePath, boolean group, String name);

    void sendLocation(double latitude, double longitude, String locationAddress, boolean group, String name, Callback callback);

    void sendLocation(double latitude, double longitude, String locationAddress, boolean group, String name);

    void sendImage(String imagePath, boolean group, String name, Callback callback);

    void sendImage(String imagePath, boolean group, String name);

    void sendVideo(String videoPath, String thumPath, int length, String name, boolean group, Callback callback);

    void sendVideo(String videoPath, String thumPath, int length, String name, boolean group);

    void sendVoice(String voicPath, int length, String name, boolean group, Callback callback);

    void sendVoice(String voicPath, int length, String name, boolean group);

    void sendText(String context, String name, boolean group, Callback callback);

    void sendText(String context, String name, boolean group);

    void sendMesage(T t, String name, boolean group, Callback callback);

    void sendMesage(T t, String name, boolean group);

    void sendCmd(String name, String action, Map<String, Object> params, Callback callback);

    void sendCmd(String name, String action, Map<String, Object> params);

    void delete(String name);

    void delete(String name, String msgId);

    void recallMessage(T t);

    int count(String name);

    int count();

    int unReadCount(String name);

    int unReadCount();

    void read();

    void read(String name);

    void read(String name, String msgId);

    void login(String name, String pass);

    List<String> conversations();


    void logout();

    boolean isLogin();

    List<T> getMessages(String name);

    List<T> getMessages(String name, int size);

    List<T> getMessages(String name, String msgId, int size);

    void registerMessageListener(MessageListener<T> messageListener);

    void registerMessageListener(String name, MessageListener<T> messageListener);

    void unRegisterMessageListener(MessageListener<T> messageListener);


    interface Callback {
        void onSuccess();

        void onError(int i, String msg);

        void onProgress(int i, String msg);
    }

    interface MessageListener<T> {

        void onMessageReceived(T message);


        void onCmdMessageReceived(T message);


        void onMessageRead(T message);


        void onMessageDelivered(T message);


        void onMessageRecalled(T message);


        void onMessageChanged(T message, Object change);
    }
}
