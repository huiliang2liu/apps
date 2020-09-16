package com.nibiru.studio.vrscene;


import android.graphics.Color;
import android.util.Log;

import com.nibiru.studio.xrdemo.R;

import java.util.ArrayList;
import java.util.List;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.group.XActorPageView;
import x.core.ui.group.XItem;

/**
 * 演示PageView，包括显示，更新，删除，跳转，显示当前页数
 */

/**
 * Show PageView, including displaying, updating, deleting, jumping, and showing the current page number
 */

public class SubScenePageViewShow extends XBaseScene implements IXActorEventListener {

    private MyAdapter myAdapter;

    @Override
    public void onCreate() {
        //初始化10个数据
        //Initialize 10 data
        for (int i = 0; i < 10; i++) {
            datas.add("" + i);
        }

        init();

        //初始化顶部按钮
        //Initialize the top button
        initTopActor();

        //初始化底部页显示按钮
        //Initialize the show button at the bottom of the page
        initBottomActor();
    }

    @Override
    public void onResume() {
        //更新页信息
        //Update page info
        updatePageNum();

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


    XActorPageView actorPageView;

    public void init() {

        //从SubScenePageView获取布局类型参数
        //Get layout type from SubScenePageView
        int type = getIntent().getIntExtra("type", 0);

        XActorPageView.PageViewDefaultType t = XActorPageView.PageViewDefaultType.values()[type];

        //初始化PageView，参数为布局类型，凹形，平面，凸形
        //Initialize PageView, parameters are layout types: convex, flat and concave
        actorPageView = new XActorPageView(t);

        //初始化Adapter，负责创建Item和内容刷新
        //Initialize Adapter to create Item and refresh contents
        myAdapter = new MyAdapter();

//        //设置每一页布局控制点，设置控制点可以自定义页的运行轨迹和所在位置
//        //Set layout control point for each page, which can customize the moving track and position of the page
//        List<XVec3> temp = new ArrayList<>();
//        temp.add(new XVec3(-6, -0.5f, 8));
//        temp.add(new XVec3(0, -0.5f, -10));
//        temp.add(new XVec3(6, -0.5f, 8));
//
//        actorPageView.setControlPoints(temp);

        //设置左中右三页布局，可支持左2中右2或左3中右3三种布局方式
        //Set left-center-right layout, left 2 center right 2 or left 3 center right 3
        actorPageView.setVisiblePageLayout(XActorPageView.VisiblePage.LEFT_MIDDLE_RIGHT);

        //设置是否支持每页朝向中心点
        //Set whether to support each page toward the center point
        actorPageView.setEnableFacingCenter(true);

        //设置Adapter
        //Set Adapter
        actorPageView.setAdapter(myAdapter);

        //开启/关闭循环翻页，默认关闭，这里展示FLAT模式下开启
        //Enable/disable page turning circulation, it's disabled by default, here presents enabling it in FLAT mode
        if( t == XActorPageView.PageViewDefaultType.FLAT ) {
            actorPageView.setCirculation(true);
        }

        //开启/关闭点击两侧页面触发翻页的功能（默认开启）
        //Enable/disable turning page function triggered when clicking the two sides of page (enabled by default)
        actorPageView.setEnableSidePageTriggerMove(true);

        //设置两侧页面半透明的alpha值，默认值为0.5，如果取消半透明效果，设置为1.0
        //Set alpha value of the two sides of page, default value 0.5, if the transparency is cancelled, set as 1.0
        actorPageView.setSidePagesTranslucentAlpha(0.3f);

        //设置页切换动画更新，在开始动画和结束动画时回调
        //Set animation updates of page switching, call back at the starting and ending of the animation
        actorPageView.setPageAnimationListener(new XActorPageView.IXPageAnimationListener() {
            @Override
            public void onBegin() {
                Log.d("test", "begin switch Page");
            }

            //onEnd回调参数为当前页状态对象，XPageState包含了当前页布局的基本信息，可以获取目前显示的索引范围
            //The parameter of onEnd callback is the current page status, XPageState contains the basic layout info of the current page
            @Override
            public void onEnd(XActorPageView.XPageState state) {

                //在切页动画运行结束后更新页信息
                //Update page info after the page switching animation is done
                Log.d("test", "End switch Page, Page State: "+state);

                updatePageNum();

            }
        });

        addActor(actorPageView);

    }

    private XLabel mTip;

    //初始化顶部按钮
    //Initialize the top button
    void initTopActor(){
        XImageText buttonAdd = new XImageText("blackxx.png", "blackxx.png");
        buttonAdd.setSize(1f, 0.5f);
        buttonAdd.setSizeOfImage(1f, 0.5f);
        buttonAdd.setName("add");
        buttonAdd.setTitle(R.string.btn_add, R.string.btn_add);
        buttonAdd.setTitleColor(Color.GREEN, Color.WHITE);
        buttonAdd.setTitlePosition(0, 0);
        buttonAdd.setSizeOfTitle(0.4f, 0.2f);
        buttonAdd.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        buttonAdd.setCenterPosition(-2.4f, 2f, -4);
        buttonAdd.setEventListener(this);
        addActor(buttonAdd);

        XImageText buttonDelete = new XImageText("blackxx.png", "blackxx.png");
        buttonDelete.setSize(1f, 0.5f);
        buttonDelete.setSizeOfImage(1f, 0.5f);
        buttonDelete.setName("delete");
        buttonDelete.setTitle(R.string.btn_delete, R.string.btn_delete);
        buttonDelete.setTitleColor(Color.GREEN, Color.WHITE);
        buttonDelete.setTitlePosition(0, 0);
        buttonDelete.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        buttonDelete.setSizeOfTitle(0.4f, 0.2f);
        buttonDelete.setCenterPosition(-0.8f, 2f, -4);
        buttonDelete.setEventListener(this);
        addActor(buttonDelete);

        XImageText buttonChange = new XImageText("blackxx.png", "blackxx.png");
        buttonChange.setSize(1f, 0.5f);
        buttonChange.setSizeOfImage(1f, 0.5f);
        buttonChange.setName("change");
        buttonChange.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        buttonChange.setTitle(R.string.btn_update, R.string.btn_update);
        buttonChange.setTitleColor(Color.GREEN, Color.WHITE);
        buttonChange.setTitlePosition(0, 0);
        buttonChange.setSizeOfTitle(0.4f, 0.2f);
        buttonChange.setCenterPosition(0.8f, 2f, -4);
        buttonChange.setEventListener(this);
        addActor(buttonChange);

        XImageText buttonGoto = new XImageText("blackxx.png", "blackxx.png");
        buttonGoto.setSize(1f, 0.5f);
        buttonGoto.setSizeOfImage(1f, 0.5f);
        buttonGoto.setName("goto");
        buttonGoto.setTitleArrangementMode(XLabel.XArrangementMode.SingleRowNotMove, XLabel.XArrangementMode.SingleRowNotMove);
        buttonGoto.setTitle(R.string.btn_goto, R.string.btn_goto);
        buttonGoto.setTitleColor(Color.GREEN, Color.WHITE);
        buttonGoto.setTitlePosition(0, 0);
        buttonGoto.setSizeOfTitle(0.4f, 0.2f);
        buttonGoto.setCenterPosition(2.4f, 2f, -4);
        buttonGoto.setEventListener(this);
        addActor(buttonGoto);

        mTip = new XLabel("");
        mTip.setAlignment(XLabel.XAlign.Center);
        mTip.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        mTip.setCenterPosition(0, 1.2f, -4f);
        mTip.setSize(0.8f, 0.3f);
        addActor(mTip);
    }


    XLabel mPageNumLabel;
    XImage mPageLeft;
    XImage mPageRight;

    //初始化底部页信息按钮
    //Initialize the info button at the bottom of the page
    void initBottomActor(){
        mPageLeft = new XImage("ic_left_focused.png", "ic_left_default.png");
        mPageLeft.setCenterPosition(-0.5f, -2.5f, -4f);
        mPageLeft.setSize(0.2f, 0.2f);
        mPageLeft.setName("left");
        mPageLeft.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            //向左翻页
            //Page turning to the left
            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( actorPageView != null ){
                    actorPageView.moveLeft();
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(mPageLeft);

        mPageRight = new XImage("ic_right_focused.png", "ic_right_default.png");
        mPageRight.setCenterPosition(0.5f, -2.5f, -4f);
        mPageRight.setSize(0.2f, 0.2f);
        mPageRight.setName("right");
        mPageRight.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            //向右翻页
            //Page turning to the right
            @Override
            public boolean onGazeTrigger(XActor actor) {
                if( actorPageView != null ){
                    actorPageView.moveRight();
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(mPageRight);

        //显示当前页索引
        //Show the current page index
        mPageNumLabel = new XLabel("");
        mPageNumLabel.setCenterPosition(0, -2.5f, -4.0f);
        mPageNumLabel.setSize(0.6f, 0.2f);
        mPageNumLabel.setAlignment(XLabel.XAlign.Center);
        mPageNumLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        addActor(mPageNumLabel);
    }

    void updatePageNum(){
        if( actorPageView == null || !actorPageView.isCreated() )return;

        try {
            //获取当前页状态，如在切页动画运行过程中调用会抛出异常
            //Get the current page status, if it's called when the page switching animation is running, there will be error
            XActorPageView.XPageState state = actorPageView.getXPageState();

            //非循环模式下判断上一页下一页箭头是否显示
            //Judge whether to show the arrows of back and next when it's not in loop mode
            if( !state.isCircle() ){
                if( mPageLeft != null ) {
                    //是否到达第一页
                    // Whether to meet the first page
                    if (state.hasMeetHead()) {
                        mPageLeft.setEnabled(false);
                    } else {
                        mPageLeft.setEnabled(true);
                    }
                }

                if( mPageRight != null ) {
                    //是否到达最后一页
                    //Whether to meet the last page
                    if (state.hasMeetTail()) {
                        mPageRight.setEnabled(false);
                    } else {
                        mPageRight.setEnabled(true);
                    }
                }
            }
            if( mPageNumLabel != null ){
                //页计数从1开始，position根据数组索引从0开始
                //Count page number from 1, position is from 0 according to the array index
                mPageNumLabel.setTextContent((state.getCenterPagePosition() + 1) + " / " + state.getPageCount());
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private List<String> datas = new ArrayList<>();

    @Override
    public void onGazeEnter(XActor actor) {

    }

    @Override
    public void onGazeExit(XActor actor) {

    }

    @Override
    public boolean onGazeTrigger(XActor actor) {
        //演示添加元素
        //Add elements
        if ("add".equals(actor.getName())) {

            //在列表结尾添加4页
            //Add 4 pages at the end of the list
            synchronized (SubSceneXGridViewShow.class) {
                for (int i = 0; i < 4; i++) {
                    datas.add("add: " + i);
                }
            }

            mTip.setTextContent("Add 4 items at tail");

            //通知界面刷新
            //Notify to refresh the page
            myAdapter.notifyDataSetChanged();

            updatePageNum();

        } else if ("delete".equals(actor.getName())) {

            //删除最后一页
            //Delete the last page
            synchronized (SubSceneXGridViewShow.class) {
                if (datas.size() > 0) {
                    datas.remove(datas.size() - 1);
                }
            }

            mTip.setTextContent("Remove the last item");

            //通知界面刷新
            //Notify to refresh the page
            myAdapter.notifyDataSetChanged();

            updatePageNum();

        } else if ("change".equals(actor.getName())) {

            //刷新最后三页
            //Refresh the last 3 pages
            synchronized (SubSceneXGridViewShow.class) {
                for (int i = datas.size() - 1; (i > datas.size() - 4 && i >= 0); i--) {
                    datas.set(i, "update: " + i);
                }
            }
            mTip.setTextContent("Update last 3 items");

            //通知界面刷新
            //Notify to refresh the page
            myAdapter.notifyDataSetChanged();

        }else if( "goto".equals(actor.getName()) ){
            //跳转到最后一页
            //Jump to the last page
            int gotoPos = myAdapter.getCount() - 1;

            if( actorPageView != null ){
                //设置跳转页之后，不要调用notifyDataSetChanged方法，否则会导致跳转错误
                //After page jumping is set, don't call notifyDataSetChanged, or there'll be jumping error
                actorPageView.setCurrentShowPosition(gotoPos);
            }

            mTip.setTextContent("Goto position: "+gotoPos);
        }

        return true;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    private class MyAdapter extends XItemAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getObject(int position) {
            return null;
        }

        @Override
        public XItem getXItem(int position, XItem convertPage, XActor parent) {
            String data;

            synchronized (SubScenePageViewShow.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            //如果Item尚未创建，创建一个新的Item，Item继承自XPanel，是一个Group控件
            //If Item hasn't been created, create an Item, it inherits XPanel and is a Group actor
            if (convertPage == null) {
                convertPage = new XItem();
                //设置Page Item的背景图片
                //Set Page Item background
                convertPage.setBackGround("blackxx.png");
                //设置Page Item的尺寸
                //Set Page Item size
                convertPage.setSize(2, 2);

                //初始化一个Label，放入Item中
                //Initialize a Label and put into Item
                XLabel label = new XLabel("Label: " + data);
                label.setAlignment(XLabel.XAlign.Center);
                label.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
                label.setName("title");
                label.setSize(0.6f, 0.3f);

                convertPage.addChild(label, new XActorGroup.LayoutParam(0, 0, 0.01f, 3));

                //添加选中事件监听
                //Add listener for event selection
                final XItem finalConvertItem = convertPage;
                convertPage.setEventListener(new IXActorEventListener() {
                    @Override
                    public void onGazeEnter(XActor actor) {

                    }

                    @Override
                    public void onGazeExit(XActor actor) {

                    }

                    @Override
                    public boolean onGazeTrigger(XActor actor) {

                        if (finalConvertItem.getChild("title") == null) {
                            return true;
                        }

                        mTip.setTextContent("Sel: " + ((XLabel) finalConvertItem.getChild("title")).getTextContent());

                        return true;
                    }

                    @Override
                    public void onAnimation(XActor actor, boolean isSelected) {

                    }
                });
            }
            //如果Item已经创建，则直接更新其中Label的内容
            //If Item has been created, update the Label content directly
            else{
                if (convertPage.getChild("title") != null) {
//                    XLog.logInfo("update Item: "+data);
                    ((XLabel)convertPage.getChild("title")).setTextContent("Label: " + data);
                }
            }

            return convertPage;
        }
    }

    @Override
    public boolean onGazeTrigger() {
        super.onGazeTrigger();
        return true;
    }

    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
    }
}

