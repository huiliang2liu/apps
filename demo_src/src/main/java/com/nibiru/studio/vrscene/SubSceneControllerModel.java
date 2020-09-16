package com.nibiru.studio.vrscene;

/**
 * Created by gaoning on 2018/1/23.
 */

import android.graphics.Color;

import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XButton;
import x.core.ui.XLabel;
import x.core.ui.XUI;


public class SubSceneControllerModel extends XBaseScene implements XButton.IXOnClickListener {

    XButton xButton1, xButton2;

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
        XLabel titleLabel = new XLabel("Example：ControllerModel");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 2f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        XLabel textLable = new XLabel("No Controller Connected!!!");
        textLable.setCenterPosition(0, 0f, -4f);
        textLable.setAlignment(XLabel.XAlign.Center);
        textLable.setSize(5f, 0.3f);
        addActor(textLable);

        xButton1 = new XButton("Change Model");
        xButton1.setName("button1");
        xButton1.setBackgroundColor(0xffff0000, 0xff00ff00);
        xButton1.setTextColor(0xffffffff, 0xff000000);
        xButton1.setUnselectedAlign(XLabel.XAlign.Center);
        xButton1.setOnClickListener(this);
        xButton1.setCenterPosition(0, 0.6f, -4f);
        xButton1.setSize(3F, 0.5F);
        xButton1.setTextSize(0.25f);
        addActor(xButton1);

        xButton2 = new XButton("Change Model Position");
        xButton2.setName("button2");
        xButton2.setBackgroundColor(0xffff0000, 0xff00ff00);
        xButton2.setTextColor(0xffffffff, 0xff000000);
        xButton2.setUnselectedAlign(XLabel.XAlign.Center);
        xButton2.setOnClickListener(this);
        xButton2.setCenterPosition(0f, -0.8f, -4f);
        xButton2.setSize(3F, 0.5F);
        xButton2.setTextSize(0.25f);
        addActor(xButton2);

        if (getControllerManager().hasDeviceConntected()) {
            textLable.setEnabled(false);
        } else {
            xButton1.setEnabled(false);
            xButton2.setEnabled(false);
        }
    }

    private int index = 0;
    private long lastclicktime=0;
    private long curclicktime=0;
    @Override
    public boolean onClick(XActor actor) {
        if (actor.getName().equals("button1")) {
            curclicktime=System.currentTimeMillis();
            if (curclicktime-lastclicktime>500) {
                lastclicktime = curclicktime;
                index++;
                if (index % 3 == 0) {
                    setControllerModel("3DOF_ID_Model.obj", XUI.Location.ASSETS, 25, -0.018f,-0.06f);
                } else if (index % 3 == 1) {
                    setControllerModel("JoyStick.obj", XUI.Location.ASSETS, 1, 0,0);
                } else {
                    setControllerModel("housing_bott.obj", XUI.Location.ASSETS, 25, -0.01f,-0.07f);
                }
            }
        } else if (actor.getName().equals("button2")) {
            setControllerInitPosition(0, 0.03f, -1);
        }
        return true;
    }
}

