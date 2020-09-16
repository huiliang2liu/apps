package com.nibiru.studio.arscene;

/**
 * Created by gaoning on 2018/1/23.
 */

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.nibiru.lib.vr.listener.NVRSyncFrameCoordListener;
import com.nibiru.studio.CalculateUtils;

import x.core.ui.XActor;
import x.core.ui.XButton;
import x.core.ui.XLabel;

/**
 * 演示XButton控件
 */

/**
 * Show XButton
 */
public class SubSceneScreen extends BaseScene implements XButton.IXOnClickListener {

    XButton xButton, xButton11;
    XLabel textLable;
    boolean isOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void init() {


        XLabel titleLabel = new XLabel("Example：Same Screen");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.2f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        XLabel tip = new XLabel("The system does not support the screen function");
        tip.setAlignment(XLabel.XAlign.Center);
        tip.setCenterPosition(0, 0.15f, CalculateUtils.CENTER_Z);
        tip.setTextColor(Color.WHITE);
        tip.setSize(0.7f, 0.025f);
        addActor(tip);

        if(isSyncFrameSupported()) {
            tip.setEnabled(false);
        }


        textLable = new XLabel("Addr:" );
        textLable.setCenterPosition(0, 0f, CalculateUtils.CENTER_Z);
        textLable.setAlignment(XLabel.XAlign.Center);
        textLable.setSize(0.5f, 0.025f);
        addActor(textLable);

        isOpen = isSyncFrameEnabled();

        xButton = new XButton(isOpen ? "Close" : "Open");
        xButton.setName("button1");
        xButton.setBackgroundColor(0xffff0000, 0xff00ff00);
        xButton.setTextColor(0xffffffff, 0xff000000);
        xButton.setUnselectedAlign(XLabel.XAlign.Center);
        xButton.setOnClickListener(this);
        xButton.setCenterPosition(0, -0.15f, CalculateUtils.CENTER_Z);
        xButton.setSize(0.15F, 0.05F);
        xButton.setTextSize(0.025f);

        addActor(xButton);

        xButton11 = new XButton("GOTO");
        xButton11.setName("button111");
        xButton11.setBackgroundColor(0xffff0000, 0xff00ff00);
        xButton11.setTextColor(0xffffffff, 0xff000000);
        xButton11.setUnselectedAlign(XLabel.XAlign.Center);
        xButton11.setOnClickListener(this);
        xButton11.setCenterPosition(0, -0.08f, CalculateUtils.CENTER_Z);
        xButton11.setSize(0.15F, 0.05F);
        xButton11.setTextSize(0.025f);

        addActor(xButton11);

        setSyncFrameCoordListener(new NVRSyncFrameCoordListener() {
            @Override
            public void onMotionEvent(float v, float v1, int i) {
                Log.e("SyncFrameCoord", "onMotionEvent: X:" + v + ",Y:" + v1 + ",Action:" + i);
            }
        });


    }

    @Override
    public boolean onClick(XActor actor) {
        if(actor.getName().equals("button1")) {
            if(isSyncFrameSupported()) {
                isOpen = !isOpen;
                setEnableSyncFrame(isOpen);
                xButton.setText(isOpen ? "Close" : "Open", isOpen ? "Close" : "Open");
                if (isOpen) {
                    String url = getSyncFrameUrl();
                    textLable.setTextContent("Addr:" + url);

                }
            }
        } else if(actor.getName().equals("button111")) {
            startScene(new Intent(SubSceneScreen.this, SubSceneXButton.class));
        }
        return true;
    }
}

