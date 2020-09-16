package com.nibiru.studio.arscene;

import android.graphics.Color;

import com.nibiru.studio.CalculateUtils;

import x.core.listener.IXProgressBarListener;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.XProgressBar;

/**
 * 演示进度条控件
 * 包括：矩形进度条，带滑块选择的矩形进度条
 */

/**
 * Show the progress bar control
 * Including: rectangle progress bar, rectangle progress bar with slider
 */
public class SubSceneXProgressBar extends BaseScene {


    private XProgressBar pbar;

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
        XLabel titleLabel = new XLabel("Example：ProgressBar");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.15f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        final XLabel barProcess = new XLabel("Bar Progress: 0%");
        barProcess.setAlignment(XAlign.Center);
        barProcess.setArrangementMode(XArrangementMode.SingleRowNotMove);
        barProcess.setCenterPosition(0, 0.04f, CalculateUtils.CENTER_Z);
        barProcess.setTextColor(Color.WHITE);
        barProcess.setSize(0.08f, 0.03f);
        addActor(barProcess);

        //参数分别为初始百分比[0-1]范围，背景图片，前景图片，滑块图片，默认为滑块选择模式
        //Parameters: initial percentage range [0-1], background image, foreground image, and slider image. the default mode is slider selection, if it's only progress display mode, please refer to the following pbar2 sample
        pbar = new XProgressBar(0.0f, "progress_pp1.png", "progress_pp2.png", "progresspp2_Btn.png");
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
                barProcess.setTextContent("Bar Progress: " + p + "%");
                progressBar.setProcess(progress);
            }
        });
        //设置进度条尺寸
        //Set progerssbar size
        pbar.setSize(0.2f, 0.02f);
        //设置控件位置
        //Set progressbar position
        pbar.setCenterPosition(0, 0.08f, CalculateUtils.CENTER_Z);
        addActor(pbar);


        //只显示百分比，无需设置滑块图片
        //Only percentage is shown, no need to set slider image
        pbar2 = new XProgressBar(0.25f, "progress_pp1.png", "progress_pp2.png");
        //设置为百分比显示模式
        //Set as a percentage display mode
        pbar2.setPrecessBarBtnShowType(XProgressBar.MARK_PERCENT);

        pbar2.setSize(0.2f, 0.02f);
        pbar2.setCenterPosition(0, -0.1f, CalculateUtils.CENTER_Z);
        addActor(pbar2);


        barProcess2 = new XLabel("Bar Progress: 0%");
        barProcess2.setArrangementMode(XArrangementMode.SingleRowNotMove);
        barProcess2.setCenterPosition(0, -0.15f, CalculateUtils.CENTER_Z);
        barProcess2.setSize(0.08f, 0.03f);
        addActor(barProcess2);

        XProgressBar  pbar3 = new XProgressBar(0.25f, "new_progress_pp1_repair.png",
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
        pbar3.setSize(0.2f, 0.01f);
        //设置控件位置
        //Set progressbar position
        pbar3.setCenterPosition(0, 0f, CalculateUtils.CENTER_Z);
        addActor(pbar3);
        barProcess3 = new XLabel("Bar Progress: 25%");
        barProcess3.setArrangementMode(XArrangementMode.SingleRowNotMove);
        barProcess3.setCenterPosition(0, -0.05f, CalculateUtils.CENTER_Z);
        barProcess3.setSize(0.08f, 0.03f);
        addActor(barProcess3);

    }

    XLabel barProcess2, barProcess3;
    XProgressBar pbar2;

    long lastUpdateTime = 0;
    public void update(float deltea){

        //每点击一次进度增加15%
        //The progress adds 15% for each click
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
