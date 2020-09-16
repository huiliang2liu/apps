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
import x.core.ui.group.XActorListView;
import x.core.ui.group.XItem;

/**
 * 演示ListView列表控件，支持横向布局和纵向布局
 */

/**
 * Show ListView list control, supporting horizontal and vertical layout
 */

public class SubSceneXListViewShow extends XBaseScene implements IXActorEventListener {
    private List<String> datas = new ArrayList<>();
    private XItemAdapter adapter;

    private XActorListView actorListView;

    @Override
    public void onCreate() {

        //从SubSceneXListView获取用户选择的布局类型
        //Get layout type selected by user from SubSceneXListView
        int type = getIntent().getIntExtra("type", 1);

        XActorListView.ListViewType t = XActorListView.ListViewType.values()[type];

        //初始化20个数据
        //Initialize 20 data
        for (int i = 0; i < 20; i++) {
            datas.add("" + i);
        }

        //构造竖排ListView
        //Build vertical ListView
        actorListView = new XActorListView(t);

        //设置遮挡层Z值，只有在上下遮挡异常时才需设置，可选
        //Set the z value of mask layer, it requires to be set only when the mask is abnormal
        actorListView.setMaskZoffset(0.025f);

        if( t == XActorListView.ListViewType.Vertical ) {
            //设置尺寸
            //Set size
            actorListView.setSize(2.5f, 3.15f);

            //设置位置
            //Set position
            actorListView.setCenterPosition(0, -0.5f, -4.2f);

            //设置Item之间的间隔
            //Set divider of item
            actorListView.setDividerHeight(0.03f);
            //构造Adapter
            //Build Adapter
            adapter = new MyVerticalAdapter();

        }else{
            //设置横排的尺寸和中心位置
            //Set horizontal size and the center postion
            actorListView.setSize(4.5f, 2f);
            actorListView.setCenterPosition(0, 0, -4.2f);

            //初始化横排的Adapter
            //Initialize the horizontal Adapter
            adapter = new MyHorizontalAdapter();
        }

        //设置适配器
        //Set adapter
        actorListView.setAdapter(adapter);

        //设置移动监听
        //Set listener for moving
        actorListView.setItemMoveListener(new XActorListView.IXListViewItemMoveListener() {
            @Override
            public void onMoveStart() {
                Log.d("test", "ListView Move Start");
            }

            @Override
            public void onMoveEnd(int startPosition, int endPosition) {
                //获得当前显示的Position范围
                //Get the range of the current displayed Position
                Log.d("test", "ListView Move End: start->"+startPosition+" end->"+endPosition);

                //判断是否到达列表头部或者尾部
                Log.d("test", "ListView Has Meet Head? "+actorListView.hasMeetListHead()+" Has Meet Tail? "+actorListView.hasMeetListTail());

                //获得当前显示的Position范围
                Log.d("test", "ListView Get position range: "+Arrays.toString( actorListView.getShowItemPosRange()));

                updateArrowState();
            }
        });

        //设置背景图片，图片位于assets下
        //Set background image, the image is under assets
        actorListView.setBackGround("blackxx.png");

        //按页方式切换，在按上下/左右键时整页更新
        //Switch by page mode, press up/down/left/right to update the whole page
        actorListView.setMoveMode(XActorListView.ListViewMoveMode.PAGE);

        addActor(actorListView);


        //初始化顶部按钮
        //Initialize the button on the top
        initTopActor();

        //初始化翻页按钮
        //Initialize page turning button
        initArrowActor();
    }


    //初始化增/删/改/跳转功能按钮
    //Initialize adding/deleting/modifying/jumping button
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

    //初始化翻页按钮
    //Initialize page turning button
    XImage mPagePrevious;
    XImage mPageNext;

    void initArrowActor(){
        if( actorListView.getListViewType() == XActorListView.ListViewType.Horizontal ) {
            mPagePrevious = new XImage("ic_left_focused.png", "ic_left_default.png");
            mPagePrevious.setCenterPosition(-0.2f, -1.6f, -4f);
        }else{
            mPagePrevious = new XImage("tool_up_s.png");
            mPagePrevious.setCenterPosition(1.5f, -0.3f, -4f);
        }

        mPagePrevious.setSize(0.2f, 0.2f);
        mPagePrevious.setName("next");
        mPagePrevious.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                //移动至下一页
                //Move to the next page
                if( actorListView != null ){
                    actorListView.moveToPrevious();
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(mPagePrevious);

        if( actorListView.getListViewType() == XActorListView.ListViewType.Horizontal ) {
            mPageNext = new XImage("ic_right_focused.png", "ic_right_default.png");
            mPageNext.setCenterPosition(0.2f, -1.6f, -4f);
        }else{
            mPageNext = new XImage("tool_down_s.png");
            mPageNext.setCenterPosition(1.5f, -0.7f, -4f);
        }

        mPageNext.setSize(0.2f, 0.2f);
        mPageNext.setName("previous");
        mPageNext.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                //移动至上一页
                //Move to the previous page
                if( actorListView != null ){
                    actorListView.moveToNext();
                }
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });

        addActor(mPageNext);
    }

    void updateArrowState(){
        if( mPagePrevious != null ){
            //到达列表头部时，隐藏上一页按钮
            //Previous page button
            if( actorListView.hasMeetListHead() ){
                mPagePrevious.setEnabled(false);
            }else{
                mPagePrevious.setEnabled(true);
            }
        }

        if( mPageNext != null ){
            //到达列表尾部时，隐藏下一页按钮
            //Hide the next page button when it meets the list bottom
            if( actorListView.hasMeetListTail() ){
                mPageNext.setEnabled(false);
            }else{
                mPageNext.setEnabled(true);
            }
        }
    }

    @Override
    public void onGazeEnter(XActor actor) {

    }

    @Override
    public void onGazeExit(XActor actor) {

    }

    @Override
    public boolean onGazeTrigger(XActor actor) {

        if ("add".equals(actor.getName())) {
            //尾部添加4个数据
            //Add 4 elements in tail
            for (int i = 0; i < 4; i++) {
                datas.add("add: "+i);
            }
            mTip.setTextContent("Add 4 items at tail");

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After updating data, call notifyDataChanged to refresh the page
            adapter.notifyDataSetChanged();
            updateArrowState();
        } else if ("delete".equals(actor.getName())) {
            //移除第一个数据
            //Move the first data
            if (datas.size() > 0) {
                datas.remove(0);
            }
            mTip.setTextContent("Remove the first item");

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data is updated, call notifyDataSetChanged to refresh the page
            adapter.notifyDataSetChanged();

            updateArrowState();
        } else if ("change".equals(actor.getName())) {
            //更新前10个数据
            //Update the first 10 data
            for (int i = 0; i < ( datas.size() < 10 ? datas.size() : 10); i++) {
                datas.set(i, "Update: " + i);
            }
            mTip.setTextContent("Update first 10 items");

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After updating data, call notifyDataChanged to refresh the page
            adapter.notifyDataSetChanged();
        } else if( "goto".equals(actor.getName())){

            //跳转到第7项（索引位置6，从0开始）
            //Jumping to the 7th item (index position 6, starting from 0)
            int gotoPosition = 6;

            actorListView.setCurrentShowPosition(gotoPosition);

            mTip.setTextContent("Set start from position "+gotoPosition);
        }

        return true;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    //定义纵向布局的Adapter
    //Define the Adapter of the vertical layout
    private class MyVerticalAdapter extends XItemAdapter {

        //获取总数
        //Get the total number
        @Override
        public int getCount() {
            return datas.size();
        }

        //获取给定位置的对象
        //Get the object with specified position
        @Override
        public Object getObject(int position) {
            return datas.get(position);
        }

        @Override
        public XItem getXItem(int position, XItem convertItem, XActor parent) {
            String data;
            synchronized (SubSceneXListViewShow.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            //如果Item为空，则创建一个新的Item
            //If Item is null, create a new Item
            if (convertItem == null) {

                convertItem = new XItem();

                //设置尺寸
                //Set size
                convertItem.setSize(2.5f, 0.5f);

                //设置背景颜色
                //Set background color
                convertItem.setBackGround(position % 2 == 0 ? "Blue.png" : "Red.png");

                //添加一个Label控件
                //Add a Label control
                XLabel label = new XLabel("Text1: " + data);
                label.setLayoutParam(new XActorGroup.LayoutParam(-0.3f, 0.1f, 0.01f, 3));
                label.setName("title");
                label.setSize(0.5f, 0.18f);
                label.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

                convertItem.addChild(label);

                //再添加一个Label控件
                //Add another Label control
                XLabel labe2 = new XLabel("Text2: " + data);
                labe2.setLayoutParam(new XActorGroup.LayoutParam(-0.3f, -0.1f, 0.01f, 3));
                labe2.setName("title2");
                labe2.setSize(0.3f, 0.12f);
                labe2.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);

                //添加一个图片控件
                //Add an image control
                XImage image = new XImage("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
                image.setLayoutParam(new XActorGroup.LayoutParam(0.8f, 0f, 0.01f, 3));
                image.setName("image");
                image.setSize(0.4f, 0.4f);


                convertItem.addChild(label);
                convertItem.addChild(labe2);
                convertItem.addChild(image);


                //给Item加入选中监听
                //Add listener for Item selection
                final XItem finalConvertItem = convertItem;
                convertItem.setEventListener(new IXActorEventListener() {
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

                        mTip.setTextContent("Sel: "+((XLabel) finalConvertItem.getChild("title")).getTextContent());

                        return true;
                    }

                    @Override
                    public void onAnimation(XActor actor, boolean isSelected) {

                    }
                });
            } else {
                //如果Item已存在，直接更新背景颜色和两个文本的内容
                //If Item exists, update the background color and the two text contents
                convertItem.setBackGround(position % 2 == 0 ? "Blue.png" : "Red.png");
                XLabel title1 = (XLabel) convertItem.getChild("title");
                title1.setTextContent("Text1：" + data);
                XLabel title2 = (XLabel) convertItem.getChild("title2");
                title2.setTextContent("Text2：" + data);
            }
            return convertItem;
        }
    }


    //定义横向布局的Adapter
    //Define horizontal layout Adapter
    private class MyHorizontalAdapter extends XItemAdapter {

        //获取总数
        //Get the total number
        @Override
        public int getCount() {
            return datas.size();
        }

        //获取给定位置的对象
        //Get the object with specified position
        @Override
        public Object getObject(int position) {
            return datas.get(position);
        }

        @Override
        public XItem getXItem(int position, XItem convertItem, XActor parent) {

            String data;
            synchronized (SubSceneXListViewShow.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            //如果Item尚未创建，创建一个新的Item
            //If Item has not been created, created a new Item
            if (convertItem == null) {

                convertItem = new XItem();

                //设置尺寸和背景色
                //Set size and background color convertItem.setSize(1.5f, 2f);
                convertItem.setSize(1.5f, 2f);
                convertItem.setBackGround(position % 2 == 0 ? "Blue.png" : "Red.png");

                //添加一个图片和一个文本控件
                //Add an image and a text control
                XImage image = new XImage("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
                image.setLayoutParam(new XActorGroup.LayoutParam(0, 0.2f, 0.01f, 3));
                image.setName("image");
                image.setSize(0.4f, 0.4f);


                XLabel label = new XLabel("Text: " + data);
                label.setLayoutParam(new XActorGroup.LayoutParam(0, -0.2f, 0.01f, 3));
                label.setName("title");
                label.setSize(0.6f, 0.18f);
                label.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);


                convertItem.addChild(label);
                convertItem.addChild(image);

                //设置选中监听
                //Set listener for selection
                final XItem finalConvertItem = convertItem;
                convertItem.setEventListener(new IXActorEventListener() {
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

                        mTip.setTextContent("Sel: "+((XLabel) finalConvertItem.getChild("title")).getTextContent());

                        return true;
                    }

                    @Override
                    public void onAnimation(XActor actor, boolean isSelected) {

                    }
                });
            } else {
                //如果Item已经存在，则直接更新背景色和文本内容
                //If Item has existed, directly update background color and text content
                convertItem.setBackGround(position % 2 == 0 ? "Blue.png" : "Red.png");
                XLabel title = (XLabel) convertItem.getChild("title");

                title.setTextContent("Text: " + data);
            }
            return convertItem;
        }
    }

    @Override
    public void onResume() {

        //更新当前翻页信息
        //Update the current page turning info
        updateArrowState();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
    }


    public String[] getScenePlist(){
        return new String[]{"Images.plist"};
    }
}

