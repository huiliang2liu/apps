package com.live.activity;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.base.BaseActivity;
import com.live.R;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.NodePlayer;

public class TestActivity extends BaseActivity {
    @BindView(R.id.su)
    SurfaceView su;
    private NodePlayer nodePlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        nodePlayer=new NodePlayer(this);
        su.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                nodePlayer.setSurfaceHolder(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                nodePlayer.surfaceCreated(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
//                nodePlayer.surfaceDestroyed(holder);
            }
        });
        nodePlayer.setInputUrl("http://ivi.bupt.edu.cn/hls/dntv.m3u8");
        nodePlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nodePlayer.stop();
        nodePlayer.release();
    }
}
