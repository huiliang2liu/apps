package com.nibiru.studio.arscene.PageView;

import android.util.Log;

import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.arscene.BaseScene;

import java.util.ArrayList;
import java.util.List;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.XToast;
import x.core.ui.group.XActorPageView;
import x.core.ui.group.XItem;
import x.core.util.XVec3;

/**
 * 演示PageView，包括显示，更新，删除，跳转，显示当前页数
 */

/**
 * Show PageView, including displaying, updating, deleting, jumping, and showing the current page number
 */

public class PageViewTestScene extends BaseScene implements IXActorEventListener {

    private MyAdapter myAdapter;
    private XToast toast;


    @Override
    public void onCreate() {
        super.onCreate();

        //初始化20个数据
        //Initialize 20 data
        for (int i = 0; i < 20; i++) {
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

        ////更新页信息
        //Update page info
        if( actorPageView != null && actorPageView.isCreated()) {
            try {
                XActorPageView.XPageState state = actorPageView.getXPageState();
                Log.d("demo", "Page State: "+state);

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


    XActorPageView actorPageView;

    public void init() {

        //从SubScenePageView获取布局类型参数
        //Get layout type from SubScenePageView
        int type = getIntent().getIntExtra("type", 0);
        XActorPageView.PageViewDefaultType t = XActorPageView.PageViewDefaultType.values()[type];


        List<XVec3> temp = new ArrayList<>();
        if(t == XActorPageView.PageViewDefaultType.CONCAVE) {
            temp.add(new XVec3(-0.4f, -0f, -0.6f));
            temp.add(new XVec3(0, -0f, -1.2f));
            temp.add(new XVec3(0.4f, -0f, -0.6f));
        } else if(t == XActorPageView.PageViewDefaultType.CONVEX) {
            temp.add(new XVec3(-0.4f, -0f, -1.2f));
            temp.add(new XVec3(0, -0f, -0.6f));
            temp.add(new XVec3(0.4f, -0f, -1.2f));
        } else {
            temp.add(new XVec3(-0.4f, -0f, -1f));
            temp.add(new XVec3(0, -0f, -1f));
            temp.add(new XVec3(0.4f, -0f, -1f));
        }

        //初始化PageView，参数为布局类型，凹形，平面，凸形
        //Initialize PageView, parameters are layout types: convex, flat and concave
        actorPageView = new XActorPageView(t);

        //初始化Adapter，负责创建Item和内容刷新
        //Initialize Adapter to create Item and refresh contents
        myAdapter = new MyAdapter();
        
        //设置左中右三页布局，可支持左2中右2或左3中右3三种布局方式
        //Set left-center-right layout, left 2 center right 2 or left 3 center right 3
        actorPageView.setVisiblePageLayout(XActorPageView.VisiblePage.LEFT_MIDDLE_RIGHT);

        //设置每一页布局控制点，设置控制点可以自定义页的运行轨迹和所在位置
        //Set layout control point for each page, which can customize the moving track and position of the page
        actorPageView.setControlPoints(temp);

        actorPageView.setSize(0f, 0f);

        //设置Adapter
        //Set Adapter
        actorPageView.setAdapter(myAdapter);

        //开启/关闭循环翻页，默认关闭
        //Enable/disable page turning circulation, it's disabled by default
        actorPageView.setCirculation(false);

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

                Log.d("test", "End switch Page, Page State: "+state);

                updatePageNum();

            }
        });
        //设置是否支持每页朝向中心点
        //Set whether to support each page toward the center point
        //actorPageView.setEnableFacingCenter(true);
        addActor(actorPageView);

    }

    //初始化顶部按钮
    //Initialize the top button
    void initTopActor() {
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

    //初始化底部页信息按钮
    //Initialize the info button at the bottom of the page
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
        mPageNumLabel.setCenterPosition(0, -0.2f, CalculateUtils.CENTER_Z);
        mPageNumLabel.setSize(0.06f * 2, 0.02f * 2);
        mPageNumLabel.setAlignment(XLabel.XAlign.Center);
        mPageNumLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        addActor(mPageNumLabel);
    }

    void updatePageNum(){
        if( actorPageView == null )return;

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

    float ratio = 0.5f;

    @Override
    public boolean onKeyDown(int keyCode) {

//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            if (actorPageView != null) actorPageView.moveRight();
//            ratio += 0.05f;
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//            if (actorPageView != null) actorPageView.moveLeft();
//            ratio -= 0.05f;
//        }

//        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//            datas.clear();
//            for (int i = 0; i < 18; i++) {
//                datas.add("notify: " + i);
//            }
//
//            myAdapter.notifyDataSetChanged();
//        }

        return super.onKeyDown(keyCode);
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
        if ("add".equals(actor.getName())) {


            //在列表结尾添加4页
            //Add 4 pages at the end of the list
            synchronized (PageViewTestScene.class) {
                for (int i = 0; i < 4; i++) {
                    datas.add("ha");
                }
            }

            //通知界面刷新
            //Notify to refresh the page
            myAdapter.notifyDataSetChanged();
        } else if ("delete".equals(actor.getName())) {

            //删除最后一页
            //Delete the last page
            synchronized (PageViewTestScene.class) {
                if (datas.size() > 0)
                    datas.remove(datas.size() - 1);
            }

            //通知界面刷新
            //Notify to refresh the page
            myAdapter.notifyDataSetChanged();
        } else if ("change".equals(actor.getName())) {
            //刷新最后三页
            //Refresh the last 3 pages
            synchronized (PageViewTestScene.class) {
                for (int i = datas.size() - 1; (i > datas.size() - 4 && i >= 0); i--) {
                    datas.set(i, "update: " + i);
                }
            }

            //通知界面刷新
            //Notify to refresh the page
            myAdapter.notifyDataSetChanged();
        } else if("jump".equals(actor.getName())) {

            //跳转到最后一页
            //Jump to the last page
            int pos = myAdapter.getCount() - 1;
            if( actorPageView != null ){
                //设置跳转页之后，不要调用notifyDataSetChanged方法，否则会导致跳转错误
                //After page jumping is set, don't call notifyDataSetChanged, or there'll be jumping error
                actorPageView.setCurrentShowPosition(pos);
            }
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
            synchronized (PageViewTestScene.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            if (convertPage == null) {
                //如果Item尚未创建，创建一个新的Item，Item继承自XPanel，是一个Group控件
                //If Item hasn't been created, create an Item, it inherits XPanel and is a Group actor
                convertPage = new XItem();
                //设置Page Item的尺寸
                //Set Page Item size
                convertPage.setSize(0.1f, 0.1f);
                //设置Page Item的背景图片
                //Set Page Item background
                convertPage.setBackGround("blackxx.png");

                XLabel label = new XLabel(data.contains("update") ? data : "Label: " + data);
                label.setAlignment(XLabel.XAlign.Center);
                label.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
                label.setName("title");
                label.setSize(0.1f, 0.02f);
                convertPage.addChild(label, new XActorGroup.LayoutParam(0, 0, 0.01f, 3));

                final XItem finalConvertItem = convertPage;

                //添加选中事件监听
                //Add listener for event selection
                convertPage.setEventListener(new IXActorEventListener() {
                    @Override
                    public void onGazeEnter(XActor actor) {

                    }

                    @Override
                    public void onGazeExit(XActor actor) {

                    }

                    @Override
                    public boolean onGazeTrigger(XActor actor) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        if (finalConvertItem.getChild("title") == null) {
                            return true;
                        }
                        toast = XToast.makeToast(PageViewTestScene.this, "当前选中：" + ((XLabel) finalConvertItem.getChild("title")).getTextContent(), 0xffffffff, 3000);
                        toast.setGravity(0.0f, -0.15f, CalculateUtils.CENTER_Z + 0.05f);
                        toast.setSize(0.4f, 0.03f);
                        toast.show();
                        return true;
                    }

                    @Override
                    public void onAnimation(XActor actor, boolean isSelected) {

                    }
                });
                //如果Item已经创建，则直接更新其中Label的内容
                //If Item has been created, update the Label content directly
            } else{
                    if (convertPage.getChild("title") != null) {
//                    XLog.logInfo("update Item: "+data);
                        ((XLabel)convertPage.getChild("title")).setTextContent(data.contains("update") ? data : "Label: " + data);
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
}

