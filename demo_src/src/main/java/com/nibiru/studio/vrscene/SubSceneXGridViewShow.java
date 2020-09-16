package com.nibiru.studio.vrscene;


import android.graphics.Color;
import android.util.Log;

import com.nibiru.studio.xrdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XBaseScene;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.group.XActorGridView;
import x.core.ui.group.XActorPageView;
import x.core.ui.group.XItem;
import x.core.util.XLog;
import x.core.util.XVec3;

/**
 * 演示GridView控件，GridView继承自PageView，为多页网格控件，支持PageView的基本功能
 * Created by Steven on 2017/9/15.
 */

/**
 * Show GridView control, which inherits from PageView. It is multipage grid control and supports basic functions of PageView
 * Created by Steven on 2017/9/15.
 */

public class SubSceneXGridViewShow extends XBaseScene implements IXActorEventListener {

    private MyAdapter myAdapter;
    private List<String> datas = new ArrayList<>();

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


    XActorGridView actorGridView;

    public void init() {
        int type = getIntent().getIntExtra("type", 0);

        XActorPageView.PageViewDefaultType t = XActorPageView.PageViewDefaultType.values()[type];

        //构造一个GridView，指定类型、页宽、页高、行数和列数
        //Build a GridView, and specify the type, width and height of page, and the number of line and column
        actorGridView = new XActorGridView(t, 2.5f, 2.5f, 2, 2);

        //初始化Adapter
        //Initialize Adapter
        myAdapter = new MyAdapter();

        //设置位置坐标，默认居中以默认控制点位置为准
        //Set position coordinate, it's centered based on the default control point by default
//        actorGridView.setCenterPosition(0, 0, CalculateUtils.CENTER_Z);

        //与PageView一样，GridView也支持设置每一页的控制点从而控制每一页显示位置
        //Like PageView, GridView also supports setting the control point on each page to control the displaying position

//        List<XVec3> temp = new ArrayList<>();
//        temp.add(new XVec3(-0.6f, 0, 0));
//        temp.add(new XVec3(0, 0, -2f));
//        temp.add(new XVec3(0.6f, 0, 0));
//        actorGridView.setControlPoints(temp);

        //设置适配器
        //Set adapter
        actorGridView.setAdapter(myAdapter);

        //设置显示页的数量和布局方式，默认为左中右三页布局
        //Set to display the page amount and layout, default: left-center-right layout
//        actorGridView.setVisiblePageLayout(XActorPageView.VisiblePage.LEFT_MIDDLE_RIGHT);

        //设置背景图片
        //Set background image
        actorGridView.setPageBackGroundName("blackxx.png");

        //开启循环翻页，默认关闭
        //Enable page turning circulation, disabled by default
        actorGridView.setCirculation(true);

        actorGridView.setOnPageTriggerListener(new XActorPageView.OnPageTriggerListener() {
            @Override
            public void onPageTrigger(XActorPageView.PAGE_LOCATION location, XItem item) {
                XLog.logErrorInfo("trigger page location: "+location+" item: "+item);
            }
        });

        //关闭点击两侧页面触发翻页的功能，默认开启
        //Disable turning page function triggered when clicking the two sides of page, enabled by default
//        actorPageView.setEnableSidePageTriggerMove(false);

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

//                Log.d("test", "end switch Page, center position: "+centerItem.getPosition()+" all pages: "+actorGridView.getPageCount());
//
//                //获得当前显示页队列的索引值范围，所以值-1表示当前位置没有页显示
//                //Get the index range of the current displayed page queue, -1 means there's no page displaying in the current position
//                Log.d("test", "Current show page position arrange: "+ Arrays.toString( actorGridView.getShowPageIndex()));
//
//                //获取当前显示页对象
//                //Get the current displayed page object
//                XItem item = actorGridView.getCenterPage();
//
//                //获取当前显示页对象内容的索引
//                //Get the index of the current displayed page
//                int position = actorGridView.getCenterPagePosition();
//
//                //判断是否显示为最后一页
//                //Judge whether it's the last page
//                Log.d("test", "Last Page? "+actorGridView.hasMeetLastPage()+" First Page? "+actorGridView.hasMeetFirstPage());

                //获取当前显示页的索引范围
                //Get the index range of the current displayed page
                int[] items = new int[0];
                try {
                    items = actorGridView.getCenterPageItemPosRange();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if( items != null ){
                    Log.d("test", "Current page item position range: "+Arrays.toString(items));
                }


                updatePageNum();
            }
        });

        addActor(actorGridView);

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
            synchronized (SubSceneXGridViewShow.class) {
                for (int i = 0; i < 4; i++) {
                    datas.add("add: " + i);
                }
            }

            mTip.setTextContent("Add 4 items at tail");

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data update, notifyDataSetChanged is required to be called to refresht the page
            myAdapter.notifyDataSetChanged();
            updatePageNum();

        } else if ("delete".equals(actor.getName())) {
            //删除头部元素
            //Delete head element
            synchronized (SubSceneXGridViewShow.class) {
                if (datas.size() > 0) {
                    datas.remove(0);
                }
            }

            mTip.setTextContent("Remove the first item");

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data update, notifyDataSetChanged is required to be called to refresht the page
            myAdapter.notifyDataSetChanged();

            updatePageNum();

        } else if ("change".equals(actor.getName())) {
            //更头十个元素
            //Update 10 elements
            synchronized (SubSceneXGridViewShow.class) {
                for (int i = 0; i < (datas.size() < 10 ? datas.size() : 10); i++) {
                    datas.set(i, "update: " + i);
                }
            }
            mTip.setTextContent("Update first 10 items");

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data update, notifyDataSetChanged is required to be called to refresht the page
            myAdapter.notifyDataSetChanged();

        }else if( "goto".equals(actor.getName()) ){
            int gotoPos = 8;

            if( actorGridView != null ){
                //设置跳转页，参数为gridview数组中的位置索引，而不是页索引，跳转后保持中间页显示包含给定索引项
                //Set jumping page, parameter: position index of gridview array not page index, after the jumping, please make sure that the specified index item is displayed in the middle page
                //**不要**调用notifyDataSetChanged方法，否则会导致跳转错误
                //**DO NOT**call notifyDataSetChanged method, or there'll be jumping error
                actorGridView.setCurrentShowGridPosition(gotoPos);

                //按页索引跳转，参数为页号，这种跳转不关心gridview的索引项，只关心第几页
                //Jump according to page index, parameter: page number, this jumping doesnot care about the index item of gridview but the page number
//                 actorGridView.setCurrentShowPosition(2);//跳转到第三页

                //将gridview索引转换为页索引，也就是通过数组的索引项得到第几页的索引，比如，一共20个数据，每页显示4个，则第12个数据显示在第三页中
                //Turn gridview index to page intex, i.e. get the page number index through array index item, e.g. there a re 20 bits of data, 4 on each page, then the 12th is on the third page
//                int gridviewPosition = actorGridView.getPagePositionByGridPosition(gotoPos);

            }

            mTip.setTextContent("Goto position: "+gotoPos);
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

            synchronized (SubSceneXGridViewShow.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            //如果Item尚未创建，新建一个Item
            //If Item is not created, create an Item
            if (convertItem == null) {
                convertItem = new XItem();
//                convertItem.setSize(2, 2);

                //添加一个ImageText
                //Add an ImageText
                XImageText imageText = new XImageText("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
                imageText.setTitle(data, data);
                imageText.setName("imageText");
                imageText.setSizeOfImage(0.5f, 0.5f);
                imageText.setSize(0.5f, 0.5f);
                imageText.setSizeOfTitle(1.0f, 0.2f);
                imageText.setTitlePosition(0, -0.35f);
                imageText.setCenterPosition(0, 0, -4);
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

                        if (!(actor instanceof XImageText) || ((XImageText)actor).getTitleSelected() == null) {
                            return true;
                        }
                        mTip.setTextContent("Sel: "+((XImageText)actor).getTitleSelected());
                        XVec3 vec3 = new XVec3(0, 0, 0);
                        if( actor.getParent() != null && actor.getParent() instanceof XActorGroup && ((XActorGroup)actor.getParent()).getChildGlobalLocation(actor, vec3) ){
                            XLog.logInfo("Image Loc: x: "+vec3.m_X+", y: "+vec3.m_Y+", z: "+vec3.m_Z);
                        }
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

    //初始化顶部按钮
    //Initialize the button on the top
    private XLabel mTip;
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

    //初始化底部按钮，左右翻页和当前页数显示
    //Initialize the button at the bottom, left and right page turning and the current page display
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

            //左翻页
            //Page turning to the left
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

            //右翻页
            //Page turning to the right
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
        mPageNumLabel.setCenterPosition(0, -2.5f, -4.0f);
        mPageNumLabel.setSize(0.6f, 0.2f);
        mPageNumLabel.setAlignment(XLabel.XAlign.Center);
        mPageNumLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

        addActor(mPageNumLabel);
    }

    void updatePageNum(){
        if( actorGridView == null || !actorGridView.isCreated() )return;

        try {
            //获取页状态信息，如在切页动画运行中调用会抛出异常
            //Get the current page status, if it's called when the page switching animation is running, there will be error
            XActorPageView.XPageState state = actorGridView.getXPageState();

            //如果当前不是循环模式，判断是否到达第一页和最后一页
            //If it's not circulation mode, judge whether to meet the first page and the last page
            if( !state.isCircle() ){
                if( mPageLeft != null ) {
                    //如已经是第一页则隐藏上一页按钮
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
                mPageNumLabel.setTextContent((state.getCenterPagePosition() + 1) +" / "+state.getPageCount());
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
    }
}
