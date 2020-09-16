package com.im.hx;

import android.content.Context;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.im.AIM;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HxIM extends AIM<EMMessage> implements EMConnectionListener, EMMessageListener {
    private static final String TAG = "HxIM";
    private EMClient emClient;
    private EMChatManager chatManager;

    public HxIM(Context context) {
        super(context);
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        options.setAutoLogin(true);//取消自动登录
        emClient = EMClient.getInstance();
        emClient.init(context.getApplicationContext(), options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        emClient.setDebugMode(true);
        emClient.addConnectionListener(this);
        chatManager = emClient.chatManager();
        chatManager.addMessageListener(this);
    }

    @Override
    public void sendFile(String filePath, boolean group, String name, Callback callback) {
        sendMesage(createFile(filePath), name, group, callback);
    }

    @Override
    public void sendLocation(double latitude, double longitude, String locationAddress, boolean group, String name, Callback callback) {
        sendMesage(createLocation(latitude, longitude, locationAddress), name, group, callback);
    }

    @Override
    public void sendImage(String imagePath, boolean group, String name, Callback callback) {
        sendMesage(createImage(imagePath, true), name, group, callback);
    }

    @Override
    public void sendVideo(String videoPath, String thumPath, int length, String name, boolean group, Callback callback) {
        sendMesage(createVideo(videoPath, thumPath, length), name, group, callback);
    }

    @Override
    public void sendVoice(String voicPath, int length, String name, boolean group, Callback callback) {
        sendMesage(createVoice(voicPath, length), name, group, callback);
    }

    @Override
    public void sendText(String context, String name, boolean group, Callback callback) {
        sendMesage(createTxtMessage(context), name, group, callback);
    }

    @Override
    public void sendMesage(EMMessage message, String name, boolean group, final Callback callback) {
        if (message == null) {
            Log.e(TAG, "========");
            return;
        }
        if (group)
            message.setChatType(EMMessage.ChatType.GroupChat);
        message.setTo(name);
        Log.e(TAG, "发送消息");
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int code, String error) {
                if (callback != null)
                    callback.onError(code, error);
            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null)
                    callback.onProgress(progress, status);
            }
        });
        chatManager.sendMessage(message);
    }

    @Override
    public void sendCmd(String name, String action, Map<String, Object> params, final Callback callback) {
        EMMessage cmd = createPassthrough();
        cmd = addExpand(cmd, params);
        EMCmdMessageBody body = new EMCmdMessageBody(action);
        cmd.addBody(body);
        sendMesage(cmd, name, false, callback);
    }

    public static EMMessage createTxtMessage(String content) {
        return EMMessage.createTxtSendMessage(content == null || content.isEmpty() ? "null" : content, "");
    }


    public static EMMessage createVoice(String filePath, int length) {
        //filePath为语音文件路径，length为录音时间(秒)
        return EMMessage.createVoiceSendMessage(filePath, length, "");
    }


    public static EMMessage createVideo(String videoPath, String thumbPath, int videoLength) {
        //videoPath为视频本地路径，thumbPath为视频预览图路径，videoLength为视频时间长度
        return EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, "");
    }


    public static EMMessage createImage(String imagePath, boolean original) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        return EMMessage.createImageSendMessage(imagePath, original, "");
    }


    public static EMMessage createImage(String imagePath, boolean original, String s1) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage emMessage = EMMessage.createImageSendMessage(imagePath, original, s1);
        return EMMessage.createImageSendMessage(imagePath, original, s1);
    }


    public static EMMessage createLocation(double latitude, double longitude, String locationAddress) {
        //latitude为纬度，longitude为经度，locationAddress为具体位置内容
        return EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, "");
    }


    public static EMMessage createFile(String filePath) {
        return EMMessage.createFileSendMessage(filePath, "");
    }


    public static EMMessage createPassthrough() {
        return EMMessage.createSendMessage(EMMessage.Type.CMD);
    }

    public static EMMessage addExpand(EMMessage message, Map<String, Object> expands) {
        if (expands == null || expands.isEmpty() || message == null)
            return message;
        Set<Map.Entry<String, Object>> iterator = expands.entrySet();
        for (Map.Entry<String, Object> entry : iterator) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                message.setAttribute(key, (String) value);
            } else if (value instanceof Integer) {
                message.setAttribute(key, (Integer) value);
            } else if (value instanceof Long) {
                message.setAttribute(key, (Long) value);
            } else if (value instanceof Boolean) {
                message.setAttribute(key, (Boolean) value);
            } else if (value instanceof JSONObject) {
                message.setAttribute(key, (JSONObject) value);
            } else if (value instanceof JSONArray) {
                message.setAttribute(key, (JSONArray) value);
            }
        }
        return message;
    }

    @Override
    public void delete(String name) {
        chatManager.deleteConversation(name, true);
    }

    @Override
    public void delete(String name, String msgId) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return;
        conversation.removeMessage(msgId);
    }

    @Override
    public int count(String name) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return 0;
        return conversation.getAllMessages().size();
    }

    @Override
    public int count() {
        Map<String, EMConversation> conversationMap = chatManager.getAllConversations();
        if (conversationMap == null || conversationMap.size() <= 0)
            return 0;
        int size = 0;
        for (EMConversation conversation : conversationMap.values()) {
            size += conversation.getAllMessages().size();
        }
        return size;
    }

    @Override
    public int unReadCount(String name) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return 0;
        return conversation.getUnreadMsgCount();
    }

    @Override
    public int unReadCount() {
        Map<String, EMConversation> conversationMap = chatManager.getAllConversations();
        if (conversationMap == null || conversationMap.size() <= 0)
            return 0;
        int size = 0;
        for (EMConversation conversation : conversationMap.values()) {
            size += conversation.getUnreadMsgCount();
        }
        return size;
    }

    @Override
    public List<String> conversations() {
        List<String> conversations = new ArrayList<>();
        Map<String, EMConversation> conversationMap = chatManager.getAllConversations();
        if (conversationMap == null || conversationMap.size() <= 0)
            return conversations;
        for (String key : conversationMap.keySet())
            conversations.add(key);
        return conversations;
    }

    @Override
    public void read() {
        chatManager.markAllConversationsAsRead();
    }

    @Override
    public void read(String name) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return;
        conversation.markAllMessagesAsRead();
    }

    @Override
    public void read(String name, String msgId) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return;
        conversation.markMessageAsRead(msgId);
    }

    @Override
    public List<EMMessage> getMessages(String name) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return new ArrayList<>();
        return conversation.getAllMessages();
    }

    @Override
    public List<EMMessage> getMessages(String name, int size) {
        List<EMMessage> messages = getMessages(name);
        List<EMMessage> list = new ArrayList<>();
        if (messages == null || messages.size() <= 0)
            return list;
        if (messages.size() > size) {
            for (int i = 0; i < size; i++) {
                list.add(messages.get(i));
            }
        } else
            list.addAll(getMessages(name, messages.get(0).getMsgId(), size));
        return list;
    }

    @Override
    public List<EMMessage> getMessages(String name, String msgId, int size) {
        EMConversation conversation = chatManager.getConversation(name);
        if (conversation == null)
            return new ArrayList<>();
        return conversation.loadMoreMsgFromDB(msgId, size);
    }

    @Override
    public void login(String name, final String pass) {
        super.login(name, pass);
        emClient.login(name, pass, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "登陆成功");
                emClient.groupManager().loadAllGroups();
                emClient.chatManager().loadAllConversations();
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG, "登陆失败");
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.e(TAG, String.format("%d,%s", progress, status));
            }
        });
    }

    @Override
    public void logout() {
        super.logout();
        emClient.logout(true);
    }

    @Override
    public boolean isLogin() {
        return emClient.isLoggedInBefore();
    }

    @Override
    public void onConnected() {
        Log.e(TAG, "onConnected");
    }

    @Override
    public void onDisconnected(int error) {
        if (error == EMError.USER_REMOVED) {
            // 显示帐号已经被移除
        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
            logout();
        } else {
            if (NetUtils.hasNetwork(context)) {
                Log.e(TAG, "连接不到聊天服务器");
            } else {
                Log.e(TAG, "当前网络不可用，请检查网络设置");
            }
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void recallMessage(EMMessage message) {
        try {
            emClient.chatManager().recallMessage(message);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(final List<EMMessage> messages) {
        Log.e(TAG, "收到消息");
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (EMMessage message : messages) {
                    for (MessageListener listener : messageListeners)
                        listener.onMessageReceived(message);
                    MessageListener listener = messageListenerMap.get(message.getChatType() == EMMessage.ChatType.Chat ? message.getFrom() : message.getTo());
                    if (listener != null)
                        listener.onMessageReceived(message);
                }

            }
        });
    }

    @Override
    public void onCmdMessageReceived(final List<EMMessage> messages) {
        Log.e(TAG, "收到透传消息");
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (EMMessage message : messages) {
                    for (MessageListener listener : messageListeners)
                        listener.onCmdMessageReceived(message);
                    MessageListener listener = messageListenerMap.get(message.getChatType() == EMMessage.ChatType.Chat ? message.getFrom() : message.getTo());
                    if (listener != null)
                        listener.onCmdMessageReceived(message);
                }

            }
        });
    }

    @Override
    public void onMessageRead(final List<EMMessage> messages) {
        Log.e(TAG, "收到已读回执");
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (EMMessage message : messages) {
                    for (MessageListener listener : messageListeners)
                        listener.onMessageRead(message);
                    MessageListener listener = messageListenerMap.get(message.getTo());
                    if (listener != null)
                        listener.onMessageRead(message);
                }

            }
        });
    }

    @Override
    public void onMessageDelivered(final List<EMMessage> messages) {
        Log.e(TAG, "收到已送达回执");
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (EMMessage message : messages) {
                    for (MessageListener listener : messageListeners)
                        listener.onMessageDelivered(message);
                    MessageListener listener = messageListenerMap.get(message.getTo());
                    if (listener != null)
                        listener.onMessageDelivered(message);
                }

            }
        });
    }

    @Override
    public void onMessageRecalled(final List<EMMessage> messages) {
        Log.e(TAG, "消息被撤回");
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (EMMessage message : messages) {
                    for (MessageListener listener : messageListeners)
                        listener.onMessageRecalled(message);
                    MessageListener listener = messageListenerMap.get(message.getChatType() == EMMessage.ChatType.Chat ? message.getFrom() : message.getTo());
                    if (listener != null)
                        listener.onMessageRecalled(message);
                }

            }
        });
    }

    @Override
    public void onMessageChanged(final EMMessage message, final Object change) {
        Log.e(TAG, "消息状态变动");
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (MessageListener listener : messageListeners)
                    listener.onMessageChanged(message, change);
                MessageListener listener = messageListenerMap.get(message.getChatType() == EMMessage.ChatType.Chat ? message.getFrom() : message.getTo());
                if (listener != null)
                    listener.onMessageChanged(message, change);

            }
        });
    }
}
