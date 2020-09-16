package com.nibiru.studio.vrscene;

import android.graphics.Color;

import com.nibiru.service.NibiruKeyEvent;

import java.util.ArrayList;
import java.util.List;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XBaseScene;
import x.core.ui.XDialog;
import x.core.ui.XImageText;
import x.core.ui.XLabel;

/**
 * 演示对话框的使用
 * 对话框包含两个ImageText
 * 点击确定键可以控制对话框的显示和隐藏
 */

/**
 * Show the usage of dialog box
 * Dialogue box contains two ImageTexts
 * Click OK to control displaying and hiding dialog box
 */
public class SubSceneDialog extends XBaseScene {


    XDialog xdialog;

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

        XLabel titleLabel = new XLabel("Example：Dialog");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        XLabel helplabel = new XLabel("Press OK to Show Dialog");
        helplabel.setAlignment(XLabel.XAlign.Center);
        helplabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        helplabel.setCenterPosition(0, 0.7f, -4f);
        helplabel.setTextColor(Color.WHITE);
        helplabel.setSize(1.6f, 0.2f);

        addActor(helplabel);

    }

    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
    }

    void dialog() {

        //新建一个List存储Dialog显示的控件List
        //Create a List to store List displayed by Dialog
        List<XActor> listactorDiaglog = new ArrayList<>();

        //初始化一个ImageText
        //Initialize a ImageText
        XImageText panoImageText = new XImageText("yejingtu.png", "yejingtu.png");
        panoImageText.setName("pano");
        //设置ImageText在Dialog中的布局，LayoutPram参数为x, y, z和layout，其中x, y, z值相对于Dialog设置，z为正值表示Dialog之上，负值为Dialog之下，layout参数表示该控件在Dialog中的层级
        //Set the layout of ImageText in Dialog, the LayoutPram parameters are x, y, z and layout. x, y, and z values are set against Dialog, positive value of z value means above Dialog, negative one means below Dialog, layout means the order and level of the control in Dialog
        panoImageText.setLayoutParam(new XActorGroup.LayoutParam(0, 0, 0.01f, 1));
        panoImageText.setSize(1.5f, 1.5f);
        panoImageText.setSizeOfImage(1.5f, 1.5f);
        panoImageText.setRenderOrder(6);

        //放入List
        //Put in List
        listactorDiaglog.add(panoImageText);

        //新建一个关闭图标
        //Create a close icon
        XImageText itenable = new XImageText("delete_focused.png", "delete.png");
        itenable.setName("pano_xxxx");
        itenable.setLayoutParam(new XActorGroup.LayoutParam(0, -1.0f, 0.02f, 1));
        itenable.setSizeOfImage(0.3f, 0.3f);
        itenable.setSize(0.3f, 0.3f);
        itenable.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            //在点击关闭按钮时隐藏dialog
            //Hide dialog when click close icon
            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( xdialog != null ){
                    //移除目前显示的顶层Dialog
                    //Remove the current displayed top level Dialog
                    removeDialog();
                }
                return true;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        itenable.setRenderOrder(6);
        listactorDiaglog.add(itenable);

        //新建Dialog，第一个参数为背景图片，第二个参数为控件List
        //Create Dialog, the first parameter is background image, the second is control List
        xdialog = new XDialog("blackxx.png", listactorDiaglog);
        xdialog.setCenterPosition(0f, 0f, -4f);
        xdialog.setSize(1.5f, 1.5f);
        xdialog.setRenderOrder(5);

        //让dialog出现在Gaze所在位置
        //Make dialog show in the position of Gaze
        xdialog.setEnableShowOnGazePosition(true);

        //是否对dialog进行光学自动调整，如果设置了dialog出现在gaze位置，建议关闭，否则在不同方向可能有不一致的效果、
        //Whether to conduct auto optical adjustment for dialog, if dialog is set to be in the gaze position, please disable it, or there may be different effects for different directions
        xdialog.setEnableFovOffset(false);

        //是否支持对话框以外的区域能够操作，设置为false之后，对话框外的所有控件将不处理Gaze事件
        //Whether to support operations outside of dialog box, when it's set as false, all the controls outside of dialog box will not handle Gaze event
        xdialog.setEnableOutsideSelection(false);

        //是否在对话框外部点击时重置到Gaze所在位置，该模式适用于必须要用户操作的对话框，无论用户朝向哪里，点击OK都会将正在显示的dialog移动过来
        //Whether to reset to the Gaze position when clicking areas outside of dialog box, this mode is for dialog boxes which must be operated by user, wherever user clicks, click OK and the displaying dialog will be moved over
        xdialog.setClickResetPosition(true);

        //返回键也不会消失,并且是否消费掉返回键
        //The return key will not disappear，And whether to consume the back key
        //xdialog.setIgnoreBackEventWithConsumBack(true, true);

        addActor(xdialog);

    }


    public boolean onKeyDown(int key){
        if( key == NibiruKeyEvent.KEYCODE_ENTER ){

            //在按Back键或者调用removeDialog后当前显示的Dialog将被销毁，此时应重新创建
            //The current displaying Dialog will be destroyed after press Back button or call removeDialog, please create again
            if( xdialog == null || !xdialog.isCreated() ){
                dialog();
            }else {
                xdialog.setEnabled(!xdialog.isEnabled());
            }
        }

        return super.onKeyDown(key);
    }

}

