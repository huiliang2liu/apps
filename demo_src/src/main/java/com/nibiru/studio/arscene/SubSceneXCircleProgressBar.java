package com.nibiru.studio.arscene;

import android.graphics.Color;

import com.nibiru.studio.CalculateUtils;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XCircleProgressBar;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;

/**
 * 演示进度条控件
 * 圆形进度条
 */

/**
 * Show the progress bar control
 * circle progress bar
 */
public class SubSceneXCircleProgressBar extends BaseScene {

    private float i = 0;

    XCircleProgressBar bar = null;

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

        XLabel titleLabel = new XLabel("Example：CircleProgressBar");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.10f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        //圆形进度条控件，第一个参数为尺寸，第二个参数为图片，第三个参数为当前进度值，第四个参数为最大进度值
        //Circle progress bar control, the first parameter is size, the second is image, the third is the current progress value, and the fourth is the maximum progress value
        bar = new XCircleProgressBar(0.05f, "download_percent.png", 30, 100);
        bar.setCenterPosition(0f, 0f, CalculateUtils.CENTER_Z);
        bar.setRenderOrder(15);
        addActor(bar);

        //点击开始加载，再次点击停止加载
        //Click to load, and click again to stop the loading
        final XLabel circleTip2 = new XLabel("Start Loading");
        circleTip2.setCenterPosition(0f, -0.15f, CalculateUtils.CENTER_Z);
        circleTip2.setSize(0.25f, 0.03f);
        circleTip2.setEnableGazeAnimation(true);
        circleTip2.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( bar != null ){
                    if( bar.isLoading() ){
                        circleTip2.setTextContent("Start Loading");
                        //停止加载
                        //Stop the loading
                        bar.stopLoading();
                    }else{
                        circleTip2.setTextContent("Stop Loading");
                        //开始加载，第一个参数为步长，第二个参数为更新间隔时间，单位：毫秒
                        //Start to load, the first parameter is the step length, the second is the interval time for updating, unit: ms
                        bar.startLoading(5, 100);
                    }
                }
                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        addActor(circleTip2);
    }


}
