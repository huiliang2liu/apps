package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.util.XLog;

/**
 * 演示控件的基本功能
 * 包括：显示/隐藏，设置平移，旋转，缩放，透明度，开启/关闭选中动画，打开/关闭选中检测
 *
 * 演示重置控件位置的功能，在显示区域外，自动显示提示“点击确定键重置位置”
 */

/**
 * Present basic functions of actor
 * Including: show/hide, set translation, rotation, scaling, transparency, enable/disable the selected animation, and enable/disable the selected detection
 *
 * Present function of resetting control position, the hint "click OK button to reset" hint is displayed outside the display area automatically
 */
public class SubSceneActor extends XBaseScene implements IXActorEventListener {

    float CENTER_Z = -4f;
    public static final float TIP_EXP = 5f;
    XLabel dragLabel;
    @Override
    public void onCreate() {
        //初始化控件
        //Initialize the control
        init();
        isNeedResetPosition = false;
//        getCurrentSkybox().setRenderOrder(10);

        //获取来自于Scene的参数
        //Get parameters from Scene
        Intent intent = getIntent();

        if( intent != null ){
            //获取参数
            //Get parameters
            String param = intent.getStringExtra("param");
            if( param != null ){
                //如果参数不为空，在Logcat中打印出来
                //If the parameter is not null, print it in Logcat
                Log.d(XLog.TAG, "Get intent parameter from other activity or scene: "+param);
            }
        }
    }


    @Override
    public void onResume(){

    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {

    }

    //用于添加Label的公共方法
    //Public method to add Label
    float mStartY = 1f;
    float mIntervalY = -0.4f;

    XLabel addLabel(String labelText, int index){
        XLabel xLabel = new XLabel(labelText);
        //垂直方向根据index进行累加
        //Accumulate according to index vertically
        xLabel.setAlignment(XAlign.Center);
        xLabel.setSize(4.5f, 0.3f);

        //添加选中点击事件监听，从而实现不同的功能
        //Add listener for selected click event to realize different functions
        xLabel.setEventListener(this);

        if(labelText.equals("Follow")) {
            //跟随控件z轴和渲染RenderOrder要大点，防止重叠被遮挡
            //Follow the control z-axis and render RenderOrder to be larger, preventing overlap from being occluded
            xLabel.setRenderOrder(50);
            xLabel.setCenterPosition(0, mStartY+mIntervalY*index + 0.3f, CENTER_Z + 1.1f);
        } else if(labelText.equals("Drag")) {
            dragLabel = xLabel;
            //拖拽控件z轴和渲染RenderOrder要大点，防止重叠被遮挡
            //Drag the control z-axis and render the RenderOrder to a larger point to prevent the overlap from being occluded
            dragLabel.setRenderOrder(60);
            xLabel.setCenterPosition(0, mStartY+mIntervalY*index + 0.3f, CENTER_Z + 1.2f);

            //Fov偏移量会对拖拽功能造成影响,需要关闭
            //Fov offset affects drag，needs to be turned off
            dragLabel.setEnableFovOffset(false);

            //设置Actor的拖拽距离范围，如当前位置与Actor距离小于设置值，则将Actor放到最小值，如当前距离大于设置值，则将Actor放到最大值位置
            //Set the Actor's drag distance range. If the current position and the Actor distance are less than the set value,
            // the Actor is placed at the minimum value. If the current distance is greater than the set value, the Actor is placed at the maximum position.
            dragLabel.setDragDistanceRange(2f, 3f);

            //设置Actor的最小拖拽距离，如当前位置与Actor距离小于设置值，则采用设置值作为初始拖拽距离
            //Set the minimum drag distance of the Actor. If the current position and the Actor distance are less than the set value, the set value is used as the initial drag distance.
            //dragLabel.setDragDistanceMin(0.6f);
            //设置Actor的最大拖拽距离，如当前位置与Actor距离大于设置值，则采用设置值作为初始拖拽距离
            //Set the maximum drag distance of the Actor. If the current position   and the Actor distance are greater than the set value, the set value is used as the initial drag distance.
            //dragLabel.setDragDistanceMax(0.7f);

            //设置Actor中心位置拖拽，设置后，无论点击在Actor哪个位置，全部放置在中心点开启拖拽
            //Set the Actor Center position to drag and drop. After setting, no matter where you click on the Actor, all placed at the center point to open and drag.
            dragLabel.setDragFixCenter(true);
        } else {
            xLabel.setRenderOrder(15);
            xLabel.setCenterPosition(0, mStartY+mIntervalY*index, CENTER_Z);
        }

        addActor(xLabel);

        return xLabel;
    }

    public void init() {

        XLabel titleLabel = new XLabel("Example：Actor");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.8f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        titleLabel.setRenderOrder(15);
        addActor(titleLabel);

        XLabel xLabel0 = new XLabel("Tip: Click following label to do basic operation");
        xLabel0.setCenterPosition(0, 1.5f, CENTER_Z);
        xLabel0.setAlignment(XAlign.Center);
        xLabel0.setArrangementMode(XArrangementMode.MultiRow);
        xLabel0.setSize(4.0f, 0.2f);
        xLabel0.setRenderOrder(15);

        addActor(xLabel0);

        int index = 0;

        addLabel("Invisible", index++);
        addLabel("Translate", index++);
        addLabel("Scale", index++);
        addLabel("Rotation", index++);
        addLabel("Alpha", index++);

        XLabel label;
        label = addLabel("Gaze Event", index++);
        //设置控件的名称作为唯一标识，在回调中与其他控件区分
        //Set the control name as the unique identification, to distinguish it from other controls in the callback
        label.setName("GazeEventLabel");
        //设置更匹配文字的尺寸，减少空选中区域
        //Set a more suitable text size, decrease the empty selected areas
        label.setSize(1.7f, 0.3f);

        label = addLabel("Gaze Animation", index++);

        //设置允许Gaze选中动画，默认动画为平移动画，支持多种动画类型，具体可参考Animation演示
        //Set to permit selecting animation, translation animation is default animation, supporting several animation types, please refer to Animation demo for details
        label.setEnableGazeAnimation(true);

        label = addLabel("Disable Gaze Selection", index++);
        //设置控件的名称作为唯一标识，在回调中与其他控件区分
        //Set the control name as the unique identification, and distinguish it from other controls in the callback
        label.setName("GazeDisableLabel");

        label.setEnableGazeAnimation(true);


        //开启选中点不在设置区域时重置位置功能，在非设置区域时显示“点确定将屏幕移到这里”的提示
        //Enable reset position function when the gaze point is not in the setting area, the hint "click OK to move the screen here" is displayed in the non-setting area
        setResetPositioinChecker(new IAreaChecker() {
            @Override
            public boolean isGazeInArea(boolean isTipShow) {
                if(isTipShow) {
                    //不在水平，垂直36度区域内显示重置提示
                    //Display reset hint when it's not in the horizontal and vertical 36 degree area
                    return SubSceneActor.this.isGazeInArea(-36f, 36, -36f, 36f);
                } else {
                    //如果有提示显示，让出一段空间
                    //If there's hint, make out some space
                    return  SubSceneActor.this.isGazeInArea(-36f + TIP_EXP, 36f - TIP_EXP, -36f + TIP_EXP, 36f - TIP_EXP);
                }
            }
        });

        addLabel("Drag", index++);
        addLabel("Follow", index++);

    }

    //当选中点移入时回调
    //Call back when the gaze point enters
    @Override
    public void onGazeEnter(XActor actor) {
        if( "GazeEventLabel".equals(actor.getName()) )  {

            //更新文本内容和颜色
            //Update text content and color
                ((XLabel)actor).setTextContent("Gaze Enter");
                ((XLabel)actor).setTextColor(Color.BLUE);

        }else if( "GazeDisableLabel".equals(actor.getName()) ){

            //更新文本颜色
            //Update text color
            ((XLabel)actor).setTextColor(Color.CYAN);

        }
    }

    //当选中点移出时回调
    //Call back when gaze point exits
    @Override
    public void onGazeExit(XActor actor) {
        if( "GazeEventLabel".equals(actor.getName()) ) {

            //更新文本内容和颜色
            //Update text content and color
                ((XLabel)actor).setTextColor(Color.GREEN);
                ((XLabel)actor).setTextContent("Gaze Exit");

        }else if( "GazeDisableLabel".equals(actor.getName()) ){

            //更新文本颜色
            //Update text color
            ((XLabel)actor).setTextColor(Color.WHITE);

        }
    }

    //当选中点点击控件时回调
    //Call back when the selected gaze point click control
    @Override
    public boolean onGazeTrigger(XActor actor) {
        if( actor instanceof XLabel ){
            String text = ((XLabel)actor).getTextContent();
            if("Follow".equals(text)) {
                //获取并设置Actor的跟随状态，分为开始(START)/暂停(PAUSE)/取消(NONE)，开始表示开启跟随，暂停时控件停留在当前位置，取消后控件返回初始位置
                //Gets and sets the Actor's following state, which is divided into START (START)/Pause (PAUSE)/Cancel (NONE),
                // starting to indicate that the following is enabled, the control stays at the current position when paused,
                // and the control returns to the initial position after cancellation.
                if( actor.getFollowAdaptState() == XActor.FOLLOW_ADAPT_STATE.NONE ) {
                    actor.setFollowAdaptState(XActor.FOLLOW_ADAPT_STATE.START);
                }else if( actor.getFollowAdaptState() == XActor.FOLLOW_ADAPT_STATE.START ) {
                    actor.setFollowAdaptState(XActor.FOLLOW_ADAPT_STATE.PAUSE);
                }else if( actor.getFollowAdaptState() == XActor.FOLLOW_ADAPT_STATE.PAUSE ) {
                    actor.setFollowAdaptState(XActor.FOLLOW_ADAPT_STATE.NONE);
                }

            } else if( "Invisible".equals(text) ){
                //设置为隐藏
                //Set as hidden
                actor.setEnabled(false);
            } else if( "Translate".equals(text) ){
                //按当前位置水平方向左移0.5
                //Translate horizontally 0.5 to the left against the current position
                actor.translateBy(-0.5f, 0, 0);
            } else if( "Scale".equals(text) ){
                //缩小至50%
                //Narrow to 50%
                actor.setScale(0.5f, 0.5f, 0.0f);
            } else if( "Rotation".equals(text) ){
                //绕Y轴逆时针旋转50度（角度）
                //Rotate 50 degree (angle) around y axis
                actor.rotateBy(0f, 50f, 0f);
            } else if( "Alpha".equals(text) ){
                //透明度设置为50%
                //Set transparency to 50%
                actor.setAlpha(0.5f);
            } else if( "GazeEventLabel".equals(actor.getName()) ){
                //更新文本内容
                //Update text content
                ((XLabel)actor).setTextContent("Triggered by Gaze");
            } else if( "Gaze Animation".equals(text) ){
                //取消并关闭Gaze选中动画，关闭后选中控件将不会有动画效果
                //Cancel and close the animation selected by Gaze, there won't be animation effects after the selected control is disabled
                actor.setEnableGazeAnimation(false);
            } else if( "GazeDisableLabel".equals(actor.getName())){
                //更新文本颜色
                //Update text color
                ((XLabel) actor).setTextColor(Color.DKGRAY);
                //设置不可选中，选中动画将取消，控件不再接受Gaze事件
                //Set it as unavailable to be selected, the selected animation will be cancelled, the control will not accept Gaze event
                actor.setEnableSelection(false);
            } else if( "Drag".equals(text)) {
                //判断当前Actor是否启用拖拽
                //Determine if the current Actor is enabled for drag and drop
                if(actor.isDraggingActor()) {
                    //停止拖拽
                    //Stop dragging
                    actor.stopDragActor();
                } else {
                    //开启拖拽
                    //Start dragging
                    actor.startDragActor();
                }
            }
        }
        return true;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    @Override
    public boolean onKeyDown(int keyCode) {
        if(dragLabel.isDraggingActor()) {
            //在拖拽过程中更新拖拽距离，拉近或者拉远，参数offset为基于当前位置的偏移量
            //Update the drag distance in the dragging process, zoom in or out,
            // the parameter offset is the offset based on the current position.
            if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                dragLabel.setDragDistanceOffset(-0.3f);
            } else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                dragLabel.setDragDistanceOffset(0.3f);
            }
        }
        return super.onKeyDown(keyCode);
    }

//    @Override
//    public void update(float deltaTime) {
//        super.update(deltaTime);
        //是否显示重置提示文本
        //Whether to display the reset prompt text
        //isShowResetTip();
//    }
}
