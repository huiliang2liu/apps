package com.tv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    private Handler handler=new Handler(this);
    private static final int TAG=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(){
            @Override
            public void run() {
                super.run();
                int i=1;
                while (i<100){
                    i++;
                    handler.removeMessages(TAG);
                    handler.sendEmptyMessageDelayed(TAG,3000);

                }
            }
        }.start();

    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.e("MainActivity","收到消息");
        return true;
    }
}
