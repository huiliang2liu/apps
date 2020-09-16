package com.electricity;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.TextView;

public class FloatService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ElectricityApplication application = (ElectricityApplication) getApplication();
        final android.widget.TextView textView = new TextView(this);
        textView.setText("测试");
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textView.setWidth(textView.getWidth() << 2);
//                textView.setHeight(textView.getHeight() << 2);
//            }
//        });
        textView.setBackgroundColor(Color.RED);
        application.floatManager.systemFloat(textView);
    }
}
