package com.im.hx;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.im.IIM;
import com.im.AViewHolder;
import com.t.imlibrary.R;

public class HxRecyclerView extends RecyclerView implements IIM.MessageListener<EMMessage> {
    protected AViewHolder<EMMessage> viewHolder;
    private HxIM hxIM;
    protected EMConversation conversation;
    private String name;
    private HxRecyclerViewAdapter adapter;
    private LinearLayoutManager manager;

    public HxRecyclerView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public HxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getResources().obtainAttributes(attrs, R.styleable.IMViewHolder);
            String className = a.getString(R.styleable.IMViewHolder_className);
            a.recycle();
            if (className == null || className.isEmpty())
                throw new RuntimeException("iViewHolder is empty");
            try {
                viewHolder = (AViewHolder<EMMessage>) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        manager = new LinearLayoutManager(getContext());
        setLayoutManager(manager);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (hxIM != null && name != null && !name.isEmpty())
            hxIM.registerMessageListener(name, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (hxIM != null)
            hxIM.unRegisterMessageListener(this);
    }

    public void setHxIM(String name, HxIM hxIM) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("name is empty");
        }
        if (hxIM == null)
            throw new RuntimeException("hxIM is empty");
        this.hxIM = hxIM;
        hxIM.registerMessageListener(name, this);
        this.name = name;
        conversation = EMClient.getInstance().chatManager().getConversation(name);
        adapter = new HxRecyclerViewAdapter(this,hxIM,name);
    }

    @Override
    public void onMessageReceived(EMMessage message) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCmdMessageReceived(EMMessage message) {

    }

    @Override
    public void onMessageRead(EMMessage message) {
        int index = adapter.index(message);
        if (index > -1) {
            int last = manager.findLastVisibleItemPosition();
            int first = manager.findFirstVisibleItemPosition();
            if (index >= first && index <= last) {
                adapter.notifyItemChanged(index);
            }
        }
    }

    @Override
    public void onMessageDelivered(EMMessage message) {
        onMessageRead(message);
    }

    @Override
    public void onMessageRecalled(EMMessage message) {
        hxIM.delete(name, message.getMsgId());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }
}
