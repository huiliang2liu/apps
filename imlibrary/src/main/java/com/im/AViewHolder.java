package com.im;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

public abstract class AViewHolder<T> {
    protected T on;
    protected T next;

    public final void setOn(T on) {
        ListView listView;
        this.on = on;
    }

    public final void setNext(T next) {
        this.next = next;
    }

    public abstract View layout(Context context, boolean my, IIM.MessageType type);

    public abstract void bind(T t);
}
