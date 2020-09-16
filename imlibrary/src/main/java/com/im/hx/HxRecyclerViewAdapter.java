package com.im.hx;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.im.IIM;
import com.im.AViewHolder;

class HxRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int TEXT = 0;
    private static final int MY_TEXT = 1;
    private static final int IMAGE = 2;
    private static final int MY_IMAGE = 3;
    private static final int VOICE = 4;
    private static final int MY_VOICE = 5;
    private static final int VIDEO = 6;
    private static final int MY_VIDEO = 7;
    private static final int LOCATION = 8;
    private static final int MY_LOCATION = 9;
    private static final int FILE = 10;
    private static final int MY_FILE = 11;

    HxRecyclerView recyclerView;
    private HxIM hxIM;
    private String name;

    public HxRecyclerViewAdapter(HxRecyclerView recyclerView, HxIM hxIM,String name) {
        super();
        this.recyclerView = recyclerView;
        recyclerView.setAdapter(this);
        this.hxIM = hxIM;
        this.name=name;
    }

    @Override
    public int getItemCount() {
        return hxIM.count(name);
    }

    public int index(EMMessage message) {
        for (int i = 0; i < getItemCount(); i++) {
            if (message.getMsgId().equals(getItem(i).getMsgId()))
                return i;
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        switch (message.getType()) {
            case TXT:
                return message.direct() == EMMessage.Direct.RECEIVE ? TEXT : MY_TEXT;
            case IMAGE:
                return message.direct() == EMMessage.Direct.RECEIVE ? IMAGE : MY_IMAGE;
            case VOICE:
                return message.direct() == EMMessage.Direct.RECEIVE ? VOICE : MY_VOICE;
            case VIDEO:
                return message.direct() == EMMessage.Direct.RECEIVE ? VIDEO : MY_VIDEO;
            case LOCATION:
                return message.direct() == EMMessage.Direct.RECEIVE ? LOCATION : MY_LOCATION;
            case FILE:
                return message.direct() == EMMessage.Direct.RECEIVE ? FILE : MY_FILE;

        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private EMMessage getItem(int position) {
        return recyclerView.conversation.getAllMessages().get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        IIM.MessageType messageType = IIM.MessageType.TEXT;
        switch (viewType) {
            case TEXT:
            case MY_TEXT:
                messageType = IIM.MessageType.TEXT;
                break;
            case IMAGE:
            case MY_IMAGE:
                messageType = IIM.MessageType.IMAGE;
                break;
            case VOICE:
            case MY_VOICE:
                messageType = IIM.MessageType.VOICE;
                break;
            case VIDEO:
            case MY_VIDEO:
                messageType = IIM.MessageType.VIDEO;
                break;
            case LOCATION:
            case MY_LOCATION:
                messageType = IIM.MessageType.LOCATION;
                break;
            case FILE:
            case MY_FILE:
                messageType = IIM.MessageType.FILE;
                break;
        }
        return new VH(recyclerView.viewHolder, viewType % 2 == 1, messageType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VH vh = (VH) viewHolder;
        vh.bindView(position);
    }

    private class VH extends RecyclerView.ViewHolder {
        private AViewHolder<EMMessage> viewHolder;

        public VH(AViewHolder<EMMessage> viewHolder, boolean my, IIM.MessageType type) {
            super(viewHolder.layout(recyclerView.getContext(), my, type));
            this.viewHolder = viewHolder;
        }

        private void bindView(int position) {
            if (position > 0)
                viewHolder.setOn(getItem(position - 1));
            if (position < getItemCount() - 1)
                viewHolder.setNext(getItem(position + 1));
            this.viewHolder.bind(getItem(position));
        }
    }
}
