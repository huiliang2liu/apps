package com.nibiru.studio.arscene;

import android.graphics.Color;
import android.view.KeyEvent;

import com.nibiru.studio.CalculateUtils;

import x.core.ui.XLabel;
import x.core.ui.XToast;

/**
 * 演示Toast控件，Toast控件主要用于提示信息，在一段时间后自动消失
 */

/**
 * Show Toast control, Toast control is used for hints, and will disappear for some time
 */
public class SubSceneXToast extends BaseScene {

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

        XLabel titleLabel = new XLabel("Example：Toast");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.1f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);


        XLabel ToastLabel = new XLabel("Press ok to show Toast");
        ToastLabel.setAlignment(XLabel.XAlign.Center);
        ToastLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        ToastLabel.setCenterPosition(0, 0.0f, CalculateUtils.CENTER_Z);
        ToastLabel.setTextColor(Color.WHITE);
        ToastLabel.setSize(0.2f, 0.03f);
        addActor(ToastLabel);
    }

    private long last = 0;

    //按OK键弹出Toast
    //Press OK and Toast is poped up
    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            long current = System.currentTimeMillis();
            if (current - last > 5000) {

                //支持设置文本
                //Support setting text
                XToast toast = XToast.makeToast(this, "Toast Demo, 持续5000ms", 0xffffffff, 5000);

                //支持设置文字资源ID，方便国际化
                //Support setting text resource ID for internationalization
                //XToast toast = XToast.makeToast(this, R.string.toast_test, 1000);

                //设置Toast位置，只在show(false)下有用
                //Set Toast posistion, only useful under show(false)
                toast.setGravity(0.0f, -0.1f, CalculateUtils.CENTER_Z + 0.01f);
                toast.setSize(0.4f, 0.03f);

                //开启toast跟随gaze移动
                //Enable toast following gaze moving
                //toast.setActorFollowGaze(true);

                //保持toast位置在Gaze的gravity设置的位置
                //Keep toast in the position set by gravity of Gaze
                //toast.show(true);

                //toast显示在正中位置
                //Toast is displayed in the middle
                toast.show(false);
                last = current;
            }
        }
        return super.onKeyDown(keyCode);
    }

}

