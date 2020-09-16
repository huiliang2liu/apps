package com.nibiru.studio.vrscene;

/**
 * Created by gaoning on 2018/1/23.
 */

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.nibiru.lib.vr.listener.NVRSyncFrameCoordListener;

import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XButton;
import x.core.ui.XLabel;

/**
 * 演示XButton控件
 */

/**
 * Show XButton
 */
public class SubSceneScreen extends XBaseScene implements XButton.IXOnClickListener {

    XButton xButton, xButton11;
    XLabel textLable;
    boolean isOpen = false;

    @Override
    public void onCreate() {
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
        titleLabel.setCenterPosition(0, 2f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        XLabel tip = new XLabel("The system does not support the screen function");
        tip.setAlignment(XLabel.XAlign.Center);
        tip.setCenterPosition(0, 1.5f, 4f);
        tip.setTextColor(Color.WHITE);
        tip.setSize(7f, 0.25f);
        addActor(tip);

        if(isSyncFrameSupported()) {
            tip.setEnabled(false);
        }

        textLable = new XLabel("Addr:" );
        textLable.setCenterPosition(0, 0f, -4f);
        textLable.setAlignment(XLabel.XAlign.Center);
        textLable.setSize(5f, 0.25f);
        addActor(textLable);

        isOpen = isSyncFrameEnabled();

        xButton = new XButton(isOpen ? "Close" : "Open");
        xButton.setName("button1");
        xButton.setBackgroundColor(0xffff0000, 0xff00ff00);
        xButton.setTextColor(0xffffffff, 0xff000000);
        xButton.setUnselectedAlign(XLabel.XAlign.Center);
        xButton.setOnClickListener(this);
        xButton.setCenterPosition(0, -1.5f, -4f);
        xButton.setSize(1.5F, 0.5F);
        xButton.setTextSize(0.25f);

        addActor(xButton);

        xButton11 = new XButton("GOTO");
        xButton11.setName("button111");
        xButton11.setBackgroundColor(0xffff0000, 0xff00ff00);
        xButton11.setTextColor(0xffffffff, 0xff000000);
        xButton11.setUnselectedAlign(XLabel.XAlign.Center);
        xButton11.setOnClickListener(this);
        xButton11.setCenterPosition(0, -0.8f, -4f);
        xButton11.setSize(1.5F, 0.5F);
        xButton11.setTextSize(0.25f);

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

