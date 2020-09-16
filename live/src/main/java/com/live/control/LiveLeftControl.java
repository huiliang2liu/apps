package com.live.control;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.base.util.L;
import com.base.widget.RecyclerView;
import com.live.R;
import com.live.activity.PlayActivity;
import com.live.entities.LiveEntity;
import com.live.entities.LiveTypeEntity;
import com.screen.ScreenManager;

import java.util.List;


public class LiveLeftControl implements ILiveControl {
    private static final String TAG="LiveLeftControl";
    private View view;
    private ViewGroup.LayoutParams layoutParams;
    private int tabWidth;
    private boolean show = false;
    private PlayActivity activity;
    private int width;
    private int height;
    private RecyclerView type;
    private RecyclerView channel;
    private RecyclerViewAdapter<LiveTypeEntity> typeAdapter;
    private RecyclerViewAdapter<LiveEntity> liveAdapter;
    private LiveTypeEntity typeEntity;

    public LiveLeftControl(PlayActivity activity) {
        this.activity = activity;
        width = activity.getResources().getDisplayMetrics().widthPixels;
        height = activity.getResources().getDisplayMetrics().heightPixels;
        view = activity.getLayoutInflater().inflate(R.layout.live_left_control, null);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabWidth = ScreenManager.dip2px(activity, 50);
        type = view.findViewById(R.id.live_left_control_type);
        channel = view.findViewById(R.id.live_left_control_channel);
        typeAdapter = new RecyclerViewAdapter<LiveTypeEntity>(type) {
            @Override
            public ViewHolder<LiveTypeEntity> getViewHolder(int itemType) {
                return new ViewHolder<LiveTypeEntity>() {
                    private TextView textView;

                    @Override
                    public void setContext(LiveTypeEntity liveTypeEntity) {
                        textView.setText(liveTypeEntity.name);
                        if (liveTypeEntity.select) {
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
        typeAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                if (typeAdapter.getItem(position).select)
                    return;
                typeEntity.select = false;
                typeEntity = typeAdapter.getItem(position);
                typeEntity.select = true;
                liveAdapter.clean();
                List<LiveEntity> entities=activity.type2entities(typeAdapter.getItem(position).type);
                liveAdapter.addItem(entities);
                int index=0;
                int size=entities.size();
                for (int i=0;i<size;i++){
                    if(entities.get(i).play){
                        index=i;
                        break;
                    }
                }
                channel.scrollToOffset(index);
                typeAdapter.notifyDataSetChanged();
            }
        });
        liveAdapter = new RecyclerViewAdapter<LiveEntity>(channel) {
            @Override
            public ViewHolder<LiveEntity> getViewHolder(int itemType) {
                return new ViewHolder<LiveEntity>() {
                    private TextView textView;

                    @Override
                    public void setContext(LiveEntity liveEntity) {
                        textView.setText(liveEntity.name);
                        if (liveEntity.play) {
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
        liveAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                activity.setLiveEntity(typeEntity, liveAdapter.getItem(position));
                liveAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void dismiss() {
        show = false;
        ViewGroup group = (ViewGroup) view.getParent();
        if (group != null)
            group.removeView(view);
    }

    @Override
    public boolean isShow() {
        return show;
    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {

    }

    @Override
    public void show() {
        show = true;
        activity.addContentView(view, layoutParams);
        if (typeEntity != null)
            typeEntity.select = false;
        typeEntity = activity.getTypeEntity();
        if (typeEntity == null)
            return;
        typeEntity.select = true;
        typeAdapter.clean();
        liveAdapter.clean();
        typeAdapter.addItem(activity.typeEntities());
        type.scrollToOffset(activity.typeEntities().indexOf(typeEntity));
        List<LiveEntity> entities = activity.type2entities(typeEntity.type);
        liveAdapter.addItem(entities);
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            if (entities.get(i).play) {
                channel.scrollToOffset(i);
                break;
            }
        }
    }
}
