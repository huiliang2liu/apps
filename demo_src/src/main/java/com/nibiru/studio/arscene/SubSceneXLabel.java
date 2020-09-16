package com.nibiru.studio.arscene;

import android.graphics.Color;

import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.xrdemo.R;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.XUI;

/**
 * 演示文本控件，包括设置文本布局，文本大小，样式（粗体/斜体/下划线），颜色，字间距，行间距，跑马灯效果等
 */

/**
 * Show text actor, including setting text layout, text size, style (bold/italic/underline), color, word spacing, line spacing, marquee, etc.
 */

public class SubSceneXLabel extends BaseScene {

    XLabel xLabelUpdate;

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

        //统一使用造梦工坊字体
        //Unified DreamWorks Workshop Font
        //loadFont("MengGongFang.ttf", XUI.Location.ASSETS);
        //清除造梦工坊字体使用
        //Clear Dream Workshop Font Use
        //clearFont();

        XLabel titleLabel = new XLabel("Example：Label");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.1f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f , 0.03f);
        addActor(titleLabel);

        /*

        重要：Studio默认只加载系统语言/中文/英文最多三个字库，如果需要显示除此之外的字体库需要手动加载字库，否则字体将无法正常显示
        下面三种为可选的加载方法，推荐采用前两种方法

         */
         /*

        Important: Studio only loads three fonts: system language/Chinese/English. For other fonts, please load them manually, or they won't be display normally
        Here are three loading methods available, the former two are recommended

         */

        //加载法语字库
        //Load French font
//        loadFont(GlobalIME.LANGUAGE.FRENCH);

        //加载所有字库
        //Load all the fonts
//        loadFontsAll();

        //加载指定字库文件，支持ASSETS或者外部存储
        //Load specified font files, supporing ASSETS or the external storage
//        loadFont("/system/font/Roboto-Regular.ttf", XUI.Location.SDCARD);

        XLabel xLabelLeft = new XLabel("XLabel-Left-Gaze-Select-Move");
        //设置左对齐
        //Set left alignment
        xLabelLeft.setAlignment(XAlign.Left);
        //关闭跑马灯,显示不全的部分用省略号代替
        //Turn off the marquee and show incomplete parts with ellipsis instead
        xLabelLeft.setArrangementMode(XArrangementMode.SingleRowClip);
        xLabelLeft.setCenterPosition(0, 0.03f, CalculateUtils.CENTER_Z);
        xLabelLeft.setSize(0.2f, 0.03f);
        xLabelLeft.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {
                //选中时开启跑马灯
                //Enable marquee when it's selected
                ((XLabel)actor).setArrangementMode(XArrangementMode.SingleRowMove);
            }

            @Override
            public void onGazeExit(XActor actor) {
                //未选中时关闭跑马灯
                //Disable marquee when it's unselected
                ((XLabel)actor).setArrangementMode(XArrangementMode.SingleRowClip);
            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        addActor(xLabelLeft);

        final XLabel xLabel = new XLabel("XLabel_Center_WITH_SingleRowMove");
        //设置居中
        //Set center alignment
        xLabel.setAlignment(XAlign.Center);
        //设置跑马灯
        //Set marquee
        xLabel.setArrangementMode(XArrangementMode.SingleRowMove);
        xLabel.setCenterPosition(0, -0.02f, CalculateUtils.CENTER_Z);
        //通过Color设置文本颜色
        //Set text color through Color
        xLabel.setTextColor(Color.BLUE);
        xLabel.setSize(0.30f, 0.03f);
        addActor(xLabel);

        final XLabel xLabelStyle = new XLabel("Style-Bold-Italic-Underline-SubColor");
        //设置居中
        //Set center alignment
        xLabelStyle.setAlignment(XAlign.Center);
        //设置跑马灯
        //Set marquee
        xLabelStyle.setArrangementMode(XArrangementMode.SingleRowMove);
        xLabelStyle.setCenterPosition(0, -0.07f, CalculateUtils.CENTER_Z);
        xLabelStyle.setSize(0.3f, 0.03f);

        //设置粗斜体，局部文字颜色需要在设置文字内容之后调用
        //Set bold italic, partial text color requires to be called after the text content is set
        //设置粗体，参数为style枚举，起始索引，长度
        //Set bold, parameters: style enemeration, starting index, length
        xLabelStyle.setTextStyle(XLabel.TextStyle.Bold, 6, 4);

        //设置斜体，参数为起始索引位置和作用长度，如果有多段字符需要设置，可以多次调用setTextStyle并设置不同的start和Length参数
        //Set italic, parameters: the position and active length of starting index. If there're several sections of characters are required to be set, call setTextStyle for several times and set different start and Length parameters
        xLabelStyle.setTextStyle(XLabel.TextStyle.Italic, 11, 6);

        //设置下划线，参数定义同上
        //Set underline, the same definition as the above one
        xLabelStyle.setTextStyle(XLabel.TextStyle.Underline, 18, 9);

        //设置部分文字颜色，参数定义同上
        //Set partial text color, the same definition as the above one
        xLabelStyle.setSubTextColor(Color.CYAN, 28, 8);

        xLabelStyle.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {
                //将字符串中SubColor部分的字符设置为红色
                //Set characters of SubColor to red
                xLabelStyle.setSubTextColor(Color.RED, 28, 8);

                //在addActor后需要调用invalidate方法对样式进行刷新
                //After addActor, call invalidate method to refresh the style
                xLabelStyle.invalidateTextStyleColor();
            }

            @Override
            public void onGazeExit(XActor actor) {
                //将字符串中SubColor部分的字符设置为原先颜色
                //Set characters of SubColor to the original color
                xLabelStyle.setSubTextColor(Color.CYAN, 28, 8);

                //在addActor后需要调用invalidate方法对样式进行刷新
                //After addActer, invalidate method should be called to refresh the style
                xLabelStyle.invalidateTextStyleColor();
            }

            @Override
            public boolean onGazeTrigger(XActor actor) {

                //取消所有文字样式
                //Cancel all the text style
//                xLabelStyle.clearTextStyle();

                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(xLabelStyle);

        XLabel xLabelRight = new XLabel(R.string.label_test_right);
        xLabelRight.setName("labelRight");
        //设置右对齐
        //Set right alignment
        xLabelRight.setAlignment(XAlign.Right);
        //关闭跑马灯
        //Disable marquee
        xLabelRight.setArrangementMode(XArrangementMode.SingleRowNotMove);
        xLabelRight.setCenterPosition(0, -0.12f, CalculateUtils.CENTER_Z);
        //通过ARGB设置文本颜色
        //Set text color through ARGB
        xLabelRight.setTextColor(Color.argb(255, 0, 255, 0));
        xLabelRight.setSize(0.24f, 0.03f);

        addActor(xLabelRight);

        xLabelUpdate = new XLabel("XLabel-Click-Update");
        xLabelUpdate.setAlignment(XAlign.Center);
        xLabelUpdate.setArrangementMode(XArrangementMode.SingleRowNotMove);
        xLabelUpdate.setCenterPosition(0, -0.17f, CalculateUtils.CENTER_Z);
        xLabelUpdate.setTextColor(Color.WHITE);
        xLabelUpdate.setSize(0.24f, 0.03f);

        //设置字间距，参数为字高的比例
        //Set word spacing, the parameter is the ratio of text height
        xLabelUpdate.setWordSpace(0.3f);

        xLabelUpdate.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( xLabelUpdate != null && isRunning() ){
                    //更新文本内容
                    //update text content
                    xLabelUpdate.setTextContent("XLabel Text has updated!");
                    //设置字间距，参数为字高的比例
                    //Set word spacing, the parameter is the ratio of text height
                    xLabelUpdate.setWordSpace(0.1f);
                }

                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        addActor(xLabelUpdate);

        XLabel OneTime = new XLabel("Wave font (one-time loading)", XUI.Location.ASSETS, "ZhuLangChuangYi.otf");
        OneTime.setAlignment(XAlign.Center);
        OneTime.setArrangementMode(XArrangementMode.SingleRowNotMove);
        OneTime.setCenterPosition(0, -0.22f, CalculateUtils.CENTER_Z);
        OneTime.setSize(0.24f, 0.03f);
        addActor(OneTime);

        //多行文本
        //Multiline text
        XLabel xLabelMultiLines = new XLabel("Samples to show multi lines text and side alignment. Developers can decides different combination of Alignment and Arrangement Mode");
        //设置两端对齐
        //Set as justified
        xLabelMultiLines.setAlignment(XAlign.Side);
        //开启自动换行
        //Enable wordwrapping
        xLabelMultiLines.setArrangementMode(XArrangementMode.MultiRow);
        xLabelMultiLines.setTextColor(Color.DKGRAY);
        xLabelMultiLines.setCenterPosition(0, -0.27f, CalculateUtils.CENTER_Z);
        xLabelMultiLines.setSize(0.3f, 0.03f);

        //设置字间距
        //Set word spacing
        xLabelMultiLines.setWordSpace(0.2f);

        //设置行间距
        //Set line spacing
        xLabelMultiLines.setLineSpace(0.3f);
        addActor(xLabelMultiLines);



    }
}
