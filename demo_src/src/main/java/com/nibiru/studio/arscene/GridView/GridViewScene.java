package com.nibiru.studio.arscene.GridView;

import android.util.Log;

import com.nibiru.studio.CalculateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.XToast;
import x.core.ui.group.XActorGridView;
import x.core.ui.group.XActorPageView;
import x.core.ui.group.XItem;
import x.core.util.XVec3;

/**
 * 演示GridView控件，GridView继承自PageView，为多页网格控件，支持PageView的基本功能
 * Created by Steven on 2017/9/15.
 */

/**
 * Show GridView control, which inherits from PageView. It is multipage grid control and supports basic functions of PageView
 * Created by Steven on 2017/9/15.
 */

public class GridViewScene extends XBaseScene implements IXActorEventListener {

    private MyAdapter myAdapter;
    private List<String> datas = new ArrayList<>();
    private XToast toast;

    @Override
    public void onCreate() {
        //初始化20个数据
        //Initialize 20 data
        for (int i = 0; i < 20; i++) {
            datas.add("good: " + i);
        }

        //初始化GridView
        //Initialize GridView
        init();

        //初始化顶部按钮
        //Initialize the top button
        initTopActor();

        //初始化底部按钮
        //Initialize the bottom button
        initBottomActor();
    }

    @Override
    public void onResume() {
        if( actorGridView != null && actorGridView.isCreated()) {
            try {
                XActorPageView.XPageState state = actorGridView.getXPageState();
                Log.d("demo", "Page State: "+state);
                //更新页信息
                //Update page info
                updatePageNum();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

    }


    XActorGridView actorGridView;

    public void init() {

        int type = getIntent().getIntExtra("type", 0);
        XActorPageView.PageViewDefaultType t = XActorPageView.PageViewDefaultType.values()[type];

        List<XVec3> temp = new ArrayList<>();
        if(t == XActorPageView.PageViewDefaultType.CONCAVE) {
            temp.add(new XVec3(-0.6f, 0, -1.49f));
            temp.add(new XVec3(0, 0, -2));
            temp.add(new XVec3(0.6f, 0, -1.49f));
        } else if(t == XActorPageView.PageViewDefaultType.CONVEX) {
            temp.add(new XVec3(-0.7f, 0, -3f));
            temp.add(new XVec3(0, 0, -1.49f));
            temp.add(new XVec3(0.7f, 0, -3f));
        } else {
            temp.add(new XVec3(-0.7f, 0, -1.49f));
            temp.add(new XVec3(0, 0, -1.49f));
            temp.add(new XVec3(0.7f, 0, -1.49f));
        }

        //构造一个GridView，指定类型、页宽、页高、行数和列数
        //Build a GridView, and specify the type, width and height of page, and the number of line and column
        actorGridView = new XActorGridView(t, 0.2f, 0.15f, 2, 2);

        //初始化Adapter
        //Initialize Adapter
        myAdapter = new MyAdapter();

        //与PageView一样，GridView也支持设置每一页的控制点从而控制每一页显示位置
        //Like PageView, GridView also supports setting the control point on each page to control the displaying position
        actorGridView.setControlPoints(temp);

        //设置适配器
        //Set adapter
        actorGridView.setAdapter(myAdapter);

        //设置显示页的数量和布局方式，默认为左中右三页布局
        //Set to display the page amount and layout, default: left-center-right layout
        //actorGridView.setVisiblePageLayout(XActorPageView.VisiblePage.LEFT_MIDDLE_RIGHT);

        //设置背景图片
        //Set background image
        actorGridView.setPageBackGroundName("blackxx.png");

        //开启/关闭点击两侧页面触发翻页的功能（默认开启）
        //Disable turning page function triggered when clicking the two sides of page, enabled by default
        actorGridView.setEnableSidePageTriggerMove(true);


        //设置两侧页面半透明的alpha值，默认值为0.5，如果取消半透明效果，设置为1.0
        //Sets the semi-transparent alpha value of both sides of the page, the default value is 0.5, if you cancel the translucency effect, set to 1.0
        actorGridView.setSidePagesTranslucentAlpha(0.3f);

        //设置页切换动画更新，在开始动画和结束动画时回调
        //Set animation updates of page switching, call back at the starting and ending of the animation
        actorGridView.setPageAnimationListener(new XActorPageView.IXPageAnimationListener() {
            @Override
            public void onBegin() {
                Log.d("test", "begin switch Page");
            }

            //onEnd回调参数为当前处于正中间的页对象
            //The parameter of onEnd callback is the current page status
            @Override
            public void onEnd(XActorPageView.XPageState state) {

                //获取当前显示页的索引范围
                //Get the index range of the current displayed page
                int[] items = new int[0];
                try {
                    items = actorGridView.getCenterPageItemPosRange();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if( items != null ){
                    Log.d("test", "Current page item position range: "+ Arrays.toString(items));
                }

                updatePageNum();

            }
        });

        addActor(actorGridView);

    }

    void initTopActor(){
        XImageText buttonAdd = new XImageText("btn_focused", "btn_normal");
        buttonAdd.setSize(0.1f, 0.05f);
        buttonAdd.setSizeOfImage(0.1f, 0.05f);
        buttonAdd.setName("add");
        buttonAdd.setTitle("添加", "添加");
        buttonAdd.setTitleColor(0xff000000, 0xffffffff);
        buttonAdd.setTitlePosition(0, 0);
        buttonAdd.setSizeOfTitle(0.1f, 0.03f);
        buttonAdd.setCenterPosition(-0.3f, 0.2f, CalculateUtils.CENTER_Z);
        buttonAdd.setEventListener(this);
        addActor(buttonAdd);

        XImageText buttonDelete = new XImageText("btn_focused", "btn_normal");
        buttonDelete.setSize(0.1f, 0.05f);
        buttonDelete.setSizeOfImage(0.1f, 0.05f);
        buttonDelete.setName("delete");
        buttonDelete.setTitle("删除", "删除");
        buttonDelete.setTitleColor(0xff000000, 0xffffffff);
        buttonDelete.setTitlePosition(0, 0);
        buttonDelete.setSizeOfTitle(0.1f, 0.03f);
        buttonDelete.setCenterPosition(-0.1f, 0.2f, CalculateUtils.CENTER_Z);
        buttonDelete.setEventListener(this);
        addActor(buttonDelete);

        XImageText buttonChange = new XImageText("btn_focused", "btn_normal");
        buttonChange.setSize(0.1f, 0.05f);
        buttonChange.setSizeOfImage(0.1f, 0.05f);
        buttonChange.setName("change");
        buttonChange.setTitle("修改", "修改");
        buttonChange.setTitleColor(0xff000000, 0xffffffff);
        buttonChange.setTitlePosition(0, 0);
        buttonChange.setSizeOfTitle(0.1f, 0.03f);
        buttonChange.setCenterPosition(0.1f, 0.2f, CalculateUtils.CENTER_Z);
        buttonChange.setEventListener(this);
        addActor(buttonChange);

        XImageText buttonJump = new XImageText("btn_focused", "btn_normal");
        buttonJump.setSize(0.1f, 0.05f);
        buttonJump.setSizeOfImage(0.1f, 0.05f);
        buttonJump.setName("jump");
        buttonJump.setTitle("跳转", "跳转");
        buttonJump.setTitleColor(0xff000000, 0xffffffff);
        buttonJump.setTitlePosition(0, 0);
        buttonJump.setSizeOfTitle(0.1f, 0.03f);
        buttonJump.setCenterPosition(0.3f, 0.2f, CalculateUtils.CENTER_Z);
        buttonJump.setEventListener(this);
        addActor(buttonJump);
    }

    XLabel mPageNumLabel;
    XImage mPageLeft;
    XImage mPageRight;

    void initBottomActor(){
        mPageLeft = new XImage("ic_left_focused.png", "ic_left_default.png");
        mPageLeft.setCenterPosition(-0.1f, -0.2f, CalculateUtils.CENTER_Z);
        mPageLeft.setSize(0.04f, 0.04f);
        mPageLeft.setName("left");
        mPageLeft.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( actorGridView != null ){
                    actorGridView.moveLeft();
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(mPageLeft);

        mPageRight = new XImage("ic_right_focused.png", "ic_right_default.png");
        mPageRight.setCenterPosition(0.1f, -0.2f, CalculateUtils.CENTER_Z);
        mPageRight.setSize(0.04f, 0.04f);
        mPageRight.setName("right");
        mPageRight.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( actorGridView != null ){
                    actorGridView.moveRight();
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(mPageRight);

        mPageNumLabel = new XLabel("");
        mPageNumLabel.setCenterPosition(0, -0.2f, CalculateUtils.CENTER_Z);
        mPageNumLabel.setSize(0.06f * 2, 0.02f * 2);
        mPageNumLabel.setAlignment(XLabel.XAlign.Center);
        mPageNumLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        addActor(mPageNumLabel);
    }

    void updatePageNum(){
        if( actorGridView == null )return;

        try {
            XActorPageView.XPageState state = actorGridView.getXPageState();

            //非循环模式下判断上一页下一页箭头是否显示
            //If it's not circulation mode, judge whether to meet the first page and the last page
            if( !state.isCircle() ){
                if( mPageLeft != null ) {
                    //是否到达第一页
                    //If it's the first page, hide the back button
                    if (state.hasMeetHead()) {
                        mPageLeft.setEnabled(false);
                    } else {
                        mPageLeft.setEnabled(true);
                    }
                }

                if( mPageRight != null ) {
                    //如已经是最后一页则隐藏下一页按钮
                    //If it's the last page, hide the next button
                    if (state.hasMeetTail()) {
                        mPageRight.setEnabled(false);
                    } else {
                        mPageRight.setEnabled(true);
                    }
                }
            }
            if( mPageNumLabel != null ){
                //页计数从1开始，position根据数组索引从0开始
                //The page counts from 1, and position counts from 0 according to array index
                mPageNumLabel.setTextContent((state.getCenterPagePosition() + 1) + " / " + state.getPageCount());
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode) {

//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            if (actorGridView != null) actorGridView.moveRight();
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//            if (actorGridView != null) actorGridView.moveLeft();
//        }

        return super.onKeyDown(keyCode);
    }

    @Override
    public void onGazeEnter(XActor actor) {

    }

    @Override
    public void onGazeExit(XActor actor) {

    }

    //顶部按钮点击回调
    //Click the button on the top for callback
    @Override
    public boolean onGazeTrigger(XActor actor) {
        if ("add".equals(actor.getName())) {

            //在尾部添加四个元素
            //Add four elements in tail
            synchronized (GridViewScene.class) {
                for (int i = 0; i < 4; i++) {
                    datas.add("hahaha");
                }
            }

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data update, notifyDataSetChanged is required to be called to refresht the page
            myAdapter.notifyDataSetChanged();
        } else if ("delete".equals(actor.getName())) {

            //删除头部元素
            //Delete head element
            synchronized (GridViewScene.class) {
                if (datas.size() > 0)
                    datas.remove(0);
            }

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data update, notifyDataSetChanged is required to be called to refresht the page
            myAdapter.notifyDataSetChanged();
        } else if ("change".equals(actor.getName())) {

            //更头十个元素
            //Update 10 elements
            synchronized (GridViewScene.class) {
                for (int i = 0; i < (datas.size() < 10 ? datas.size() : 10); i++) {
                    datas.set(i, "update: " + i);
                }
            }

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data update, notifyDataSetChanged is required to be called to refresht the page
            myAdapter.notifyDataSetChanged();
        } else if("jump".equals(actor.getName())) {
            int pos = 3;
            if (actorGridView != null) {

                //设置跳转页，参数为gridview数组中的位置索引，而不是页索引，跳转后保持中间页显示包含给定索引项
                //Set jumping page, parameter: position index of gridview array not page index, after the jumping, please make sure that the specified index item is displayed in the middle page
                //**不要**调用notifyDataSetChanged方法，否则会导致跳转错误
                //**DO NOT**call notifyDataSetChanged method, or there'll be jumping error
                actorGridView.setCurrentShowGridPosition(pos);

                //按页索引跳转，参数为页号，这种跳转不关心gridview的索引项，只关心第几页
                //Jump according to page index, parameter: page number, this jumping doesnot care about the index item of gridview but the page number
//                 actorGridView.setCurrentShowPosition(2);//跳转到第三页

                //将gridview索引转换为页索引，也就是通过数组的索引项得到第几页的索引，比如，一共20个数据，每页显示4个，则第12个数据显示在第三页中
                //Turn gridview index to page intex, i.e. get the page number index through array index item, e.g. there a re 20 bits of data, 4 on each page, then the 12th is on the third page
//                int gridviewPosition = actorGridView.getPagePositionByGridPosition(gotoPos);
            }
        }
        return true;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    private class MyAdapter extends XItemAdapter {

        //返回GridView的Item总数
        //Return the total Item number of GridView
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getObject(int position) {
            return null;
        }

        @Override
        public XItem getXItem(int position, XItem convertItem, XActor parent) {

            String data;

            synchronized (GridViewScene.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            //如果Item尚未创建，新建一个Item
            //If Item is not created, create an Item
            if (convertItem == null) {
                convertItem = new XItem();
                convertItem.setSize(1, 1);

                //添加一个ImageText
                //Add an ImageText
                XImageText imageText = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
                imageText.setTitle(data, data);
                imageText.setName("imageText");
                imageText.setSizeOfImage(0.05f, 0.05f);
                imageText.setSize(0.05f, 0.05f);
                imageText.setSizeOfTitle(0.05f, 0.01f);
                imageText.setTitlePosition(0, -0.03f);
                imageText.setCenterPosition(0, 0, CalculateUtils.CENTER_Z);
                imageText.setEventListener(new IXActorEventListener() {
                    @Override
                    public void onGazeEnter(XActor actor) {

                    }

                    @Override
                    public void onGazeExit(XActor actor) {

                    }

                    //在点击时更新Tip内容
                    //Update Tip at click
                    @Override
                    public boolean onGazeTrigger(XActor actor) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = XToast.makeToast(GridViewScene.this, "当前选中：" + ((XImageText)actor).getTitleSelected(), 0xffffffff, 3000);
                        toast.setGravity(0.0f, -0.15f, CalculateUtils.CENTER_Z + 0.01f);
                        toast.setSize(0.4f, 0.03f);
                        toast.show();
                        return true;
                    }

                    @Override
                    public void onAnimation(XActor actor, boolean isSelected) {

                    }
                });
                convertItem.addLayer(imageText);
            } else {

                //如果Item已经创建，则直接更新内容
                //If Item has been created, update the content directly
                XImageText imageText = (XImageText) convertItem.getChild("imageText");
                imageText.setTitle(data, data);
            }
            return convertItem;
        }
    }

    @Override
    public boolean onGazeTrigger() {
        super.onGazeTrigger();
        return true;
    }
}
