package com.nibiru.studio.arscene;

import android.graphics.Color;
import android.util.Log;

import com.nibiru.studio.CalculateUtils;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XLabel;
import x.core.ui.XPanel;
import x.core.util.XLog;

/**
 * 演示Group组合控件基本用法和事件监听
 * Created by zjchai on 2017/3/9.
 */

/**
 * Show Group basic functions
 * Created by zjchai on 2017/3/9.
 */

public class SubSceneGroup extends BaseScene implements IXActorEventListener{

    XPanel panel1, panel2, panel3;
    XLabel label1, label2, label3;;

    XLabel tip;

    @Override
    public void onCreate() {
        super.onCreate();

        XLabel titleLabel = new XLabel("Example：Group");
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.2f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(00.8f, 0.03f);
        titleLabel.setRenderOrder(50);
        addActor(titleLabel);

        //初始化第一个Group
        //Initialize the first Group
        panel1 = new XPanel();
        panel1.setName("G1");
        panel1.setCenterPosition(0, -0.03f, CalculateUtils.CENTER_Z);
        panel1.setSize(0.5f, 0.4f);
        panel1.setAlpha(0.4f);
        panel1.setEventListener(SubSceneGroup.this);

        //设置Group的代理模式
        //Set Group proxy pattern
        //设置后，只要group处于选中状态，则所有子元素都为选中，group未选中时，所有子元素都不选中，以group代替子元素的选中状态
        //After it's set, if group is selected, all the subelements are selecte, if it's unselected, all the subelements are not selected, and group is to replace the select status of subelement
//        panel1.setEnableGazeEventDelegate(true);

        //设置背景图片
        //Set background image
        panel1.setBackGround("blackxx.png", 0);

        //设置panel1中的内容
        //Set contents in Group1
        addPanel1Child();

        addActor(panel1);

        tip = new XLabel("Tip: ");
        tip.setAlignment(XLabel.XAlign.Center);
        tip.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        tip.setCenterPosition(0, -0.25f, CalculateUtils.CENTER_Z + 0.01f);
        tip.setSize(0.3f, 0.03f);
        addActor(tip);
    }

    void addPanel1Child() {
        //初始化第二个Group
        //Initialize the second Group
        panel2 = new XPanel();
        panel2.setName("G2");

        //设置尺寸和透明度
        //Set size and transparency
        panel2.setSize(0.35f, 0.25f);
        panel2.setAlpha(0.5f);

        //设置事件监听
        //Set event listener
        panel2.setEventListener(SubSceneGroup.this);

        //设置背景图片
        //Set background image
        panel2.setBackGround("blackxx.png", 0);
        //设置panel2中的内容
        //Set content in panel2
        addPanel2Child();

        //LayoutParam为相对于Group的坐标系，其中Z轴的值表示相对父元素的值，正值在其表面以上，如果设置为负值可能无法正常显示
        //LayoutParam is the coordination against Group, z value means the value against parent element, the positive value means above the surface, if it's set as negative, it may not be displayed normally
        panel1.addChild(panel2, new XActorGroup.LayoutParam(0, 0.04f, 0.02f));

        //新建一个Label控件放入Group1中
        //Add a Label control into Group1
        label1 = new XLabel("L1: I'm in G1");
        label1.setName("L1");
        label1.setSize(0.4f, 0.05f);
        label1.setEventListener(SubSceneGroup.this);
        label1.setAlignment(XLabel.XAlign.Center);

        //添加Label控件，并指定布局
        //Add Label control and specify the layout
        panel1.addChild(label1, new XActorGroup.LayoutParam(0, -0.14f, 0.02f));
    }

    void addPanel2Child() {
        panel3 = new XPanel();
        panel3.setName("G3");
        panel3.setAlpha(0.6f);
        panel3.setSize(0.2f, 0.15f);
        panel3.setEventListener(SubSceneGroup.this);

        //设置背景图片
        //Set background image
        panel3.setBackGround("blackxx.png", 0);
        //设置panel3内容
        //Set contents in Group3
        addPanel3Child();

        panel2.addChild(panel3, new XActorGroup.LayoutParam(0, 0f, 0.02f));
    }

    void addPanel3Child() {
        //添加两个Label子控件
        //Add two Label subcontrols
        label2 = new XLabel("L2: I'm in G3");
        label2.setName("L2");
        label2.setEventListener(SubSceneGroup.this);

        label2.setSize(0.4f, 0.04f);
        label2.setAlignment(XLabel.XAlign.Center);

        panel3.addChild(label2, new XActorGroup.LayoutParam(0, 0.03f, 0.02f));


        label3 = new XLabel("L3: I'm in G3 too");
        label3.setName("L3");
        label3.setEventListener(SubSceneGroup.this);

        label3.setSize(0.3f, 0.03f);
        label3.setAlignment(XLabel.XAlign.Center);

        panel3.addChild(label3, new XActorGroup.LayoutParam(0, -0.03f, 0.02f));
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

    //选中进入事件回调，Logcat选中控件的名称
    //Call back the selected the entering event, Logcat selects the control name
    @Override
    public void onGazeEnter(XActor actor) {
        Log.i(XLog.TAG, "ActorListener >>>>>>>>>> Enter: "+actor.getName());
        if( tip != null ){
            tip.setTextContent("Tip: Enter "+actor.getName());
        }
    }

    //选中移出事件回调，Logcat选中控件的名称
    //Call bakc the selected exiting event, Logcat selects control name
    @Override
    public void onGazeExit(XActor actor) {
        Log.i(XLog.TAG, "ActorListener >>>>>>>>>> Exit: "+actor.getName());

        if( tip != null ){
            tip.setTextContent("Tip: Exit "+actor.getName());
        }
    }

    //选中点击事件回调，Logcat选中控件的名称
    //Call back selected click event, Logcat selects control name
    @Override
    public boolean onGazeTrigger(XActor actor) {
        Log.i(XLog.TAG, "ActorListener >>>>>>>>>> Trigger: "+actor.getName());

        if( tip != null ){
            tip.setTextContent("Tip: Trigger "+actor.getName());
        }

        return true;
    }

    //回调已废弃
    //The callback has been abandoned
    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

}
