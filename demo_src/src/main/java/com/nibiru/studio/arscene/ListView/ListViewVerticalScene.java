package com.nibiru.studio.arscene.ListView;

import android.util.Log;

import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.arscene.BaseScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x.core.adapter.XItemAdapter;
import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XActorGroup;
import x.core.ui.XImage;
import x.core.ui.XImageText;
import x.core.ui.XLabel;
import x.core.ui.XToast;
import x.core.ui.group.XActorListView;
import x.core.ui.group.XItem;

/**
 * 演示ListView列表控件，支持纵向布局
 */

/**
 * Show ListView list control, supporting vertical layout
 */

public class ListViewVerticalScene extends BaseScene implements IXActorEventListener {
    private List<String> datas = new ArrayList<>();
    private MyAdapter adapter;
    private XToast toast;

    public ListViewVerticalScene() {

        //初始化20个数据
        //Initialize 20 data
        for (int i = 0; i < 20; i++) {
            datas.add("" + i);
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
            synchronized (ListViewVerticalScene.this) {
                for (int i = 0; i < 4; i++) {
                    datas.add("hahaha");
                }
                //更新数据后，需要调用notifyDataSetChanged来刷新页面
                //After updating data, call notifyDataChanged to refresh the page
                adapter.notifyDataSetChanged();
                runOnMainThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateArrowState();
                    }
                }, 100);
            }


        } else if ("delete".equals(actor.getName())) {

            //移除第一个数据
            //Move the first data
            synchronized (ListViewVerticalScene.this) {
                if (datas.size() > 0) {
                    datas.remove(0);
                }
            }

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After the data is updated, call notifyDataSetChanged to refresh the page
            adapter.notifyDataSetChanged();
            runOnMainThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    updateArrowState();
                }
            }, 100);
        } else if ("change".equals(actor.getName())) {
            //更新前10个数据
            //Update the first 10 data
            synchronized (ListViewVerticalScene.this) {
                for (int i = 0; i < (datas.size() < 10 ? datas.size() : 10); i++) {
                    datas.set(i, "Update: " + i);
                }
            }

            //更新数据后，需要调用notifyDataSetChanged来刷新页面
            //After updating data, call notifyDataChanged to refresh the page
            adapter.notifyDataSetChanged();
        } else if( "jump".equals(actor.getName())) {

            //跳转到第7项（索引位置6，从0开始）
            //Jumping to the 7th item (index position 6, starting from 0)
            int gotoPosition = 6;
            actorListView.setCurrentShowPosition(gotoPosition);
        }
        return true;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }

    private class MyAdapter extends XItemAdapter {

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
            synchronized (ListViewVerticalScene.class) {
                if (position < 0 || position >= datas.size()) return null;

                data = datas.get(position);
            }

            if (convertItem == null) {
                //如果Item为空，则创建一个新的Item
                //If Item is null, create a new Item
                convertItem = new XItem();

                //设置尺寸
                //Set size
                convertItem.setSize(0.25f, 0.05f);
                //设置背景颜色
                //Set background color
                convertItem.setBackGround(position % 2 == 0 ? "Blue.png" : "Red.png");
                //添加一个Label控件
                //Add a Label control
                XLabel label = new XLabel("文本1：" + data);
                label.setLayoutParam(new XActorGroup.LayoutParam(-0.05f, 0.01f, 0.01f, 3));
                label.setName("title1");
                label.setSize(0.08f, 0.02f);
                label.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
                convertItem.addChild(label);
                XLabel labe2 = new XLabel("文本2：" + data);
                labe2.setLayoutParam(new XActorGroup.LayoutParam(-0.05f, -0.01f, 0.01f, 3));
                labe2.setName("title2");
                labe2.setSize(0.06f, 0.015f);
                labe2.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
                XImage image = new XImage("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
                image.setLayoutParam(new XActorGroup.LayoutParam(0.05f, 0f, 0.01f, 3));
                image.setName("image");
                image.setSize(0.03f, 0.03f);
                convertItem.addChild(label);
                convertItem.addChild(labe2);
                convertItem.addChild(image);
//                convertItem.addLayer(label);
                final XItem finalConvertItem = convertItem;
                //给Item加入选中监听
                //Add listener for Item selection
                convertItem.setEventListener(new IXActorEventListener() {
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
                        toast = XToast.makeToast(ListViewVerticalScene.this, "当前选中：" + ((XLabel)finalConvertItem.getChild("title1")).getTextContent(), 0xffffffff, 3000);
                        toast.setGravity(0.0f, -0.2f, CalculateUtils.CENTER_Z + 0.05f);
                        toast.setSize(0.4f, 0.03f);
                        toast.show();
                        return  true;
                    }

                    @Override
                    public void onAnimation(XActor actor, boolean isSelected) {

                    }
                });
            } else {
                //如果Item已存在，直接更新背景颜色和两个文本的内容
                //If Item exists, update the background color and the two text contents
                convertItem.setBackGround(position % 2 == 0 ? "Blue.png" : "Red.png");
                XLabel title1 = (XLabel) convertItem.getChild("title1");
                title1.setTextContent("文本1：" + data);
                XLabel title2 = (XLabel) convertItem.getChild("title2");
                title2.setTextContent("文本2：" + data);
            }
            return convertItem;
        }
    }

    private XActorListView actorListView;

    @Override
    public void onCreate() {
        super.onCreate();

        //构造竖排ListView
        //Build vertical ListView
        actorListView = new XActorListView(XActorListView.ListViewType.Vertical);

        //设置遮挡层Z值，只有在上下遮挡异常时才需设置，可选
        //Set the z value of mask layer, it requires to be set only when the mask is abnormal
        actorListView.setMaskZoffset(0.025f);

        //设置尺寸
        //Set size
        actorListView.setSize(0.4f, 0.2f);

        //构造Adapter
        //Build Adapter
        adapter = new MyAdapter();

        //设置适配器
        //Set adapter
        actorListView.setAdapter(adapter);

        //设置位置
        //Set position
        actorListView.setCenterPosition(0, -0.05f, CalculateUtils.CENTER_Z);

        //设置背景图片，图片位于assets下
        //Set background image, the image is under assets
        actorListView.setBackGround("trans");

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
                Log.d("test", "ListView Get position range: "+ Arrays.toString( actorListView.getShowItemPosRange()));

                updateArrowState();
            }
        });

        //按页方式切换，在按上下/左右键时整页更新
        //Switch by page mode, press up/down/left/right to update the whole page
        actorListView.setMoveMode(XActorListView.ListViewMoveMode.PAGE);

        addActor(actorListView);

        //初始化顶部按钮
        //Initialize the button on the top
        initTopActor();

        initArrowActor();

    }

    //初始化增/删/改/跳转功能按钮
    //Initialize adding/deleting/modifying/jumping button
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

    //初始化翻页按钮
    //Initialize page turning button
    XImage mPagePrevious;
    XImage mPageNext;

    void initArrowActor(){
        if( actorListView.getListViewType() == XActorListView.ListViewType.Horizontal ) {
            mPagePrevious = new XImage("ic_left_focused.png", "ic_left_default.png");
            mPagePrevious.setCenterPosition(-0.02f, -0.16f, CalculateUtils.CENTER_Z);
        }else{
            mPagePrevious = new XImage("tool_up_s.png");
            mPagePrevious.setCenterPosition(0.15f, -0.03f, CalculateUtils.CENTER_Z);
        }

        mPagePrevious.setSize(0.02f, 0.02f);
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
            mPageNext.setCenterPosition(0.02f, -0.16f, CalculateUtils.CENTER_Z);
        }else{
            mPageNext = new XImage("tool_down_s.png");
            mPageNext.setCenterPosition(0.15f, -0.07f, CalculateUtils.CENTER_Z);
        }

        mPageNext.setSize(0.02f, 0.02f);
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
    public void onResume() {
        updateArrowState();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        //  mXEditText.getIME().setOffset(new XVec3(0, 0, 0));
    }

}

