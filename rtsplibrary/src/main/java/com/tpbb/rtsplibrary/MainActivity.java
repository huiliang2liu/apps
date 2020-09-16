package com.tpbb.rtsplibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;

import com.aaronhan.rtspclient.RtspClient;

public class MainActivity extends AppCompatActivity {
    private RtspClient mRtspClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SurfaceView sv=findViewById(R.id.test_sv);
         mRtspClient = new RtspClient("rtsp://www.mym9.com/101065");
        mRtspClient.setSurfaceView(sv);

//开始显示
        mRtspClient.start();

//关闭,请在Activity销毁时调用此方法
//在UDP模式下即使销毁Activity某些RTSP服务器也会继续发送报文

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRtspClient.shutdown();
    }
}
