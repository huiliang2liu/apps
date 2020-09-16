package com.base.widget;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class EditText extends android.widget.EditText implements TextWatcher {
    private List<TextWatcher> watchers;
    private TextChangeListener textChangeListener;

    {
        super.addTextChangedListener(this);
        watchers = new ArrayList<>();
    }

    public EditText(Context context) {
        super(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        watchers.add(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        watchers.remove(watcher);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.removeTextChangedListener(this);
        super.setText(text, type);
        setSelection(text.length());
        super.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        for (TextWatcher watcher : watchers)
            watcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void afterTextChanged(Editable s) {
        for (TextWatcher watcher : watchers)
            watcher.afterTextChanged(s);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        for (TextWatcher watcher : watchers)
            watcher.onTextChanged(s, start, before, count);
        if (textChangeListener != null) {
            if (s == null || s.length() <= 0)
                textChangeListener.onTextEmpty();
            else
                textChangeListener.onTextChange(s);
        }
    }

    public interface TextChangeListener {
        void onTextEmpty();

        void onTextChange(CharSequence s);
    }
}
