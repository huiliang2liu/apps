package com.electricity;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.base.BaseDialog;

public class SystemDialog extends BaseDialog {
    public SystemDialog(Context context) {
        super(context);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    @Override
    protected void bindView() {

    }

    @Override
    public View layoutView() {
        final TextView textView = new TextView(getContext());
        textView.setText("测试");
        return textView;
    }
}
