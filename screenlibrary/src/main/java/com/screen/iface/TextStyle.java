package com.screen.iface;

import android.widget.TextView;
/**
 * User：liuhuiliang
 * Date：2019-12-20
 * Time：12:31
 * Descripotion：设置文字样式，application、activity实现，最好在application实现
 */
public interface TextStyle {
    void setTextStyle(TextView textView, String textStyle);
}
