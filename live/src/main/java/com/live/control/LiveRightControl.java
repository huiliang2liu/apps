package com.live.control;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.base.widget.RecyclerView;
import com.live.R;
import com.live.activity.PlayActivity;
import com.live.entities.SourceEntity;
import com.live.provide.ChannelProvide;
import com.screen.ScreenManager;

import java.util.List;

public class LiveRightControl implements ILiveControl {
    private PlayActivity activity;
    private View view;
    private ViewGroup.LayoutParams layoutParams;
    private boolean show = false;
    private RecyclerView recyclerView;
    private int tabWidth;
    private static final int MAX_TAB = 5;
    private int maxWidth;
    private RecyclerViewAdapter<SourceEntity> adapter;
    private ViewGroup.LayoutParams recyclerParams;
    private ViewGroup.LayoutParams parantParams;
    private int screenwidth;

    LiveRightControl(PlayActivity activity) {
        this.activity = activity;
        DisplayMetrics metrics=activity.getResources().getDisplayMetrics();
        screenwidth=metrics.widthPixels;
        maxWidth = metrics.heightPixels * 2 / 3;
        view = activity.getLayoutInflater().inflate(R.layout.live_right_control, null);
        parantParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView = view.findViewById(R.id.live_right_control_rv);
        recyclerParams = recyclerView.getLayoutParams();
        adapter = new RecyclerViewAdapter<SourceEntity>(recyclerView) {
            @Override
            public ViewHolder getViewHolder(int itemType) {
                return new ViewHolder<SourceEntity>() {
                    private TextView textView;

                    @Override
                    public void setContext(SourceEntity entity) {
                        textView.setText(entity.name);
                        if (entity.select) {
                            textView.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                        } else {
                            textView.setTextColor(activity.getResources().getColor(R.color.color13));
                        }
                    }

                    @Override
                    public void bindView() {
                        textView = findViewById(R.id.holder_live_right_control_tv);
                    }
                };
            }

            @Override
            public int getView(int itemType) {
                return R.layout.holder_live_right_control;
            }
        };
        tabWidth = ScreenManager.dip2px(activity, 100);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void show() {
        if (show)
            return;
        show = true;
        adapter.clean();
        List<SourceEntity> entities = activity.liveEntity().sourceEntities;
        int width = entities.size() * tabWidth;
        if (width > maxWidth)
            width = maxWidth;
        recyclerParams.width = screenwidth;
        recyclerView.setLayoutParams(recyclerParams);
        adapter.addItem(entities);
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).select) {
                recyclerView.toCentre(i, false);
                break;
            }
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                for (int i = 0; i < adapter.getCount(); i++)
                    adapter.getItem(i).select = false;
                SourceEntity entity = adapter.getItem(position);
                entity.select = true;
                activity.liveEntity().playIndex = position;
                ChannelProvide.updateChannel(activity.liveEntity(), activity);
                activity.play(entity.url);
                recyclerView.toCentre(position);
                adapter.notifyDataSetChanged();
            }
        });
        activity.addContentView(view, parantParams);
    }

    public boolean isShow() {
        return show;
    }


    @Override
    public void dispatchTouchEvent(MotionEvent ev) {


    }

    @Override
    public void dismiss() {
        show = false;
        ViewGroup group = (ViewGroup) view.getParent();
        if (group != null)
            group.removeView(view);
    }
}
