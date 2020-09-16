package com.nibiru.studio.arscene;

import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;

import com.nibiru.studio.CalculateUtils;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XImage;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;

/**
 * 演示带有支持上下翻页的多行文本框
 */

/**
 * Demonstrate multiline text box supporting page up and page down
 */
public class SubSceneScrollingLabel extends BaseScene {

    XLabel scrollingLabel;
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

        XLabel titleLabel = new XLabel("Example: Scrolling Label");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.15f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        scrollingLabel = new XLabel("The Nibiru VR/AR system is tailored to the global VR/AR hardware vendors, ODM/OEM and brand vendors, and provides deep customization services. Up to now, the global Nibiru VR/AR system of equipment shipments has reached millions, the market share of more than 80%.");
        scrollingLabel.setAlignment(XAlign.Center);
        scrollingLabel.setArrangementMode(XArrangementMode.MultiRow);
        scrollingLabel.setRectFixType(XLabel.RectFixType.NoStretch);
        scrollingLabel.setCenterPosition(0, 0f, CalculateUtils.CENTER_Z);
        scrollingLabel.setTextColor(Color.WHITE);
        scrollingLabel.setSize(0.28f, 0.19f);
        scrollingLabel.setFontSize(0.03f);
        scrollingLabel.setScrollEnable(true);

        //设置文本滑动监听
        //Set listener for text moving
        scrollingLabel.setTextMoveCallback(new XLabel.TextMoveCallback() {
            //本次滑动结束回调
            //Call back when the moving is done
            @Override
            public void onMoveComplete(float pro) {
                Log.d("Scroll Label",  "Move Complete:" + pro);
            }
            //滑动到达顶部回调
            //Call back when moving to the top
            @Override
            public void onHead() {
                Log.d("Scroll Label",  "onHead");
            }
            //滑动到达底部回调
            //Call back to the bottom
            @Override
            public void onBottom() {
                Log.d("Scroll Label",  "onBottom");
            }
        });
        addActor(scrollingLabel);

        //初始化上下翻页的控件
        //Initialize page up and down controls
        XImage down = new XImage("tool_down_s.png");
        down.setCenterPosition(0.03f, -0.15f, CalculateUtils.CENTER_Z);
        down.setSize(0.03f, 0.03f);
        down.setName("down");
        down.setEventListener(listener);
        addActor(down);

        XImage up = new XImage("tool_up_s.png");
        up.setCenterPosition(-0.03f, -0.15f, CalculateUtils.CENTER_Z);
        up.setSize(0.03f, 0.03f);
        up.setName("up");
        up.setEventListener(listener);
        addActor(up);
    }

    @Override
    public boolean onKeyDown(int keyCode) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            scrollingLabel.scrollUp();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            scrollingLabel.scrollDown();
            return true;
        }
        return super.onKeyDown(keyCode);
    }

    IXActorEventListener listener = new IXActorEventListener() {
        @Override
        public void onGazeEnter(XActor actor) {

        }

        @Override
        public void onGazeExit(XActor actor) {

        }

        @Override
        public boolean onGazeTrigger(XActor actor) {
            if(actor.getName().equals("up")) {
                //向上翻页
                //Scroll up
                scrollingLabel.scrollUp();
            }
            else if(actor.getName().equals("down")) {
                //向下翻页
                //Scroll down
                scrollingLabel.scrollDown();
            }
            return false;
        }

        @Override
        public void onAnimation(XActor actor, boolean isSelected) {

        }
    };

}
