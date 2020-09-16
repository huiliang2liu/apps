package com.nibiru.studio.vrscene;

import android.graphics.Color;

import x.core.listener.IXActorEventListener;
import x.core.listener.IXProgressBarListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XCircleProgressBar;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.XProgressBar;

/**
 * 演示进度条控件
 * 包括：矩形进度条，带滑块选择的矩形进度条，圆形进度条
 */

/**
 * Show the progress bar control
 * Including: rectangle progress bar, rectangle progress bar with slider, and circle progress bar
 */
public class SubSceneXProgressBar extends XBaseScene {

    XLabel barProcess, barProcess2, barProcess3;
    XProgressBar pbar, pbar2, pbar3;
    XCircleProgressBar bar1, bar2;


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

        XLabel titleLabel = new XLabel("Example：ProgressBar");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //参数分别为初始百分比[0-1]范围，背景图片，前景图片，滑块图片，默认为滑块选择模式，如果仅为进度显示模式参考下面pbar2的示例
        //Parameters: initial percentage range [0-1], background image, foreground image, and slider image. the default mode is slider selection, if it's only progress display mode, please refer to the following pbar2 sample
        pbar = new XProgressBar(0.25f, "progress_pp1.png", "progress_pp2.png", "progresspp2_Btn.png");

        //设置监听，在滑块选中位置变化时回调
        //Set listener to callback when the selected position of the slider changes
        pbar.setProgressBarListener(new IXProgressBarListener() {
            @Override
            public void onProgress(XProgressBar progressBar, float progress) {

                //滑块选择回调，更新进度条内容
                //Slider selection for callback, and update the content of progress bar
                if(progress > 0.99f) {
                    progress = 1;
                }
                int p = (int) (progress * 100);
                progressBar.setProcess(progress);

                if( barProcess != null ){
                    barProcess.setTextContent("Bar Progress: " + p + "%");
                }
            }
        });
        //设置进度条尺寸
        //Set progerssbar size
        pbar.setSize(2, 0.1f);
        //设置控件位置
        //Set progressbar position
        pbar.setCenterPosition(0, 0.4f, -4);
        addActor(pbar);

        barProcess = new XLabel("Bar Progress: 25%");
        barProcess.setCenterPosition(0, 0f, -4f);
        barProcess.setSize(2.0f, 0.2f);
        addActor(barProcess);

        pbar3 = new XProgressBar(0.25f, "new_progress_pp1_repair.png",
                "progress_pp2.png", "new_progress_pp1.png", "progresspp2_Btn.png");
        //设置第二进度
        //Set the second progress
        pbar3.setProcessSecond(0.9f);
        //设置监听，在滑块选中位置变化时回调
        //Set listener to callback when the selected position of the slider changes
        pbar3.setProgressBarListener(new IXProgressBarListener() {
            @Override
            public void onProgress(XProgressBar progressBar, float progress) {

                //滑块选择回调，更新进度条内容
                //Slider selection for callback, and update the content of progress bar
                if(progress > 0.99f) {
                    progress = 1;
                }
                int p = (int) (progress * 100);
                progressBar.setProcess(progress);

                if( barProcess3 != null ){
                    barProcess3.setTextContent("Bar Progress: " + p + "%");
                }
            }
        });
        //设置进度条尺寸
        //Set progerssbar size
        pbar3.setSize(2, 0.1f);
        //设置控件位置
        //Set progressbar position
        pbar3.setCenterPosition(0, -1.1f, -4);
        addActor(pbar3);

        barProcess3 = new XLabel("Bar Progress: 25%");
        barProcess3.setCenterPosition(0, -1.4f, -4f);
        barProcess3.setSize(2.0f, 0.2f);
        addActor(barProcess3);


        //只显示百分比，无需设置滑块图片
        //Only percentage is shown, no need to set slider image
        pbar2 = new XProgressBar(0.25f, "progress_pp1.png", "progress_pp2.png");
        //设置为百分比显示模式
        //Set as a percentage display mode
        pbar2.setPrecessBarBtnShowType(XProgressBar.MARK_PERCENT);

        pbar2.setSize(2, 0.1f);
        pbar2.setCenterPosition(0, -0.4f, -4);
        addActor(pbar2);

        barProcess2 = new XLabel("Bar Progress: 0%");
        barProcess2.setCenterPosition(0, -0.8f, -4f);
        barProcess2.setSize(2.5f, 0.2f);
        addActor(barProcess2);


        bar1 = new XCircleProgressBar(0.5f, "download_percent.png", 30, 100);
        bar1.setCenterPosition(-1.5f, -1.9f, -4f);
        bar1.setRenderOrder(15);
        addActor(bar1);

        //每点击一次进度增加15%
        //The progress adds 15% for each click
        XLabel circleTip1 = new XLabel("Click me to plus process");
        circleTip1.setCenterPosition(-1.5f, -2.7f, -4f);
        circleTip1.setSize(2.5f, 0.2f);

        circleTip1.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                //更新当前进度，如果控件是隐藏状态，更新进度无效
                //Update the current progress, if the control is in hidden status, the update is ineffective
                if( bar1 != null ){
                    bar1.setCurrentProcess( ( bar1.getCurrentProcess()+15 ) % 100);
                }
                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        addActor(circleTip1);

        //圆形进度条控件，第一个参数为尺寸，第二个参数为图片，第三个参数为当前进度值，第四个参数为最大进度值
        //Circle progress bar control, the first parameter is size, the second is image, the third is the current progress value, and the fourth is the maximum progress value
        bar2 = new XCircleProgressBar(0.5f, "download_percent.png", 50, 100);
        bar2.setCenterPosition(1.5f, -1.9f, -4f);
        bar2.setRenderOrder(15);
        addActor(bar2);

        //点击开始加载，再次点击停止加载
        //Click to load, and click again to stop the loading
        final XLabel circleTip2 = new XLabel("Start Loading");
        circleTip2.setCenterPosition(2.0f, -2.7f, -4f);
        circleTip2.setSize(2.5f, 0.2f);
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
                if( bar2 != null ){
                    if( bar2.isLoading() ){
                        circleTip2.setTextContent("Start Loading");
                        //停止加载
                        //Stop the loading
                        bar2.stopLoading();
                    }else{
                        circleTip2.setTextContent("Stop Loading");
                        //开始加载，第一个参数为步长，第二个参数为更新间隔时间，单位：毫秒
                        //Start to load, the first parameter is the step length, the second is the interval time for updating, unit: ms
                        bar2.startLoading(5, 100);
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

    long lastUpdateTime = 0;
    public void update(float deltea){

        //每一秒增加进度5%
        //Add 5% of the progress per second
        if( pbar2 != null && pbar2.isCreated() && System.currentTimeMillis() - lastUpdateTime >= 1000 ){
            lastUpdateTime = System.currentTimeMillis();

            float target = pbar2.getProcess()+0.05f;

            pbar2.setProcess( ((int)(target * 100)) > 100 ? 0 : target);

            if( barProcess2 != null ){
                barProcess2.setTextContent("Current Progress: " + (((int)(target * 100)) > 100 ? 0 : (int)(target * 100))+ "%");
            }
        }
    }

}
