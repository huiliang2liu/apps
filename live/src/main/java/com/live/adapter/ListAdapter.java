package com.live.adapter;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.base.adapter.PagerAdapter;
import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.base.thread.Handler;
import com.base.widget.ImageView;
import com.base.widget.ViewPager;
import com.live.R;
import com.live.activity.HotListActivity;
import com.live.activity.MyGoldActivity;
import com.live.activity.PlayActivity;
import com.live.activity.TvStationActivity;
import com.live.entities.ListEntity;
import com.live.entities.LiveEntity;
import com.live.entities.ShowEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ListAdapter extends RecyclerViewAdapter<ListEntity> {
    private int dp1 = 0;

    public ListAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        dp1 = context.getResources().getDimensionPixelSize(R.dimen.dip1);
    }


    @Override
    public ViewHolder<ListEntity> getViewHolder(int itemType) {
        if (itemType == 0)
            return new Holder1();
        if (itemType == 1)
            return new Holder2();
        if (itemType == 2)
            return new Holder3();
        if (itemType == 3)
            return new Holder4();
        if (itemType == 4)
            return new Holder5();
        if (itemType == 5)
            return new Holder6();
        if (itemType == 6)
            return new Holder7();
        if (itemType == 7)
            return new Holder8();
        if (itemType == 8)
            return new Holder9();
        if (itemType == 9)
            return new Holder10();
        if (itemType == 10)
            return new Holder11();
        if (itemType == 11)
            return new Holder12();
        if (itemType == 12)
            return new Holder13();
        if (itemType == 13)
            return new Holder14();
        if (itemType == 14)
            return new Holder15();
        if (itemType == 15)
            return new Holder16();
        if (itemType == 16)
            return new Holder17();
        if (itemType == 17)
            return new Holder18();
        if (itemType == 18)
            return new Holder19();
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public int getView(int itemType) {
        if (itemType == 0)
            return R.layout.holder0;
        if (itemType == 1)
            return R.layout.holder1;
        if (itemType == 2)
            return R.layout.holder2;
        if (itemType == 3)
            return R.layout.holder3;
        if (itemType == 4)
            return R.layout.holder4;
        if (itemType == 5)
            return R.layout.holder5;
        if (itemType == 6)
            return R.layout.holder6;
        if (itemType == 7)
            return R.layout.holder7;
        if (itemType == 8)
            return R.layout.holder8;
        if (itemType == 9)
            return R.layout.holder9;
        if (itemType == 10)
            return R.layout.holder10;
        if (itemType == 11)
            return R.layout.holder11;
        if (itemType == 12)
            return R.layout.holder12;
        if (itemType == 13)
            return R.layout.holder6;
        if (itemType == 14)
            return R.layout.holder7;
        if (itemType == 15)
            return R.layout.holder13;
        if (itemType == 16)
            return R.layout.holder14;
        if (itemType == 17)
            return R.layout.holder15;
        if (itemType == 18)
            return R.layout.holder16;
        return 0;
    }

    private class Holder19 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private TextView title;
        private TextView name;

        @Override
        public void setContext(ListEntity listEntity) {
            ShowEntity entity = (ShowEntity) listEntity.object;
            imageView.setImagePath(entity.url);
            title.setText(entity.type);
            name.setText(entity.name);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder16_iv);
            title = (TextView) findViewById(R.id.holder16_title);
            name = (TextView) findViewById(R.id.holder16_name);
        }
    }

    private class Holder18 extends ViewHolder<ListEntity> {
        private TextView textView;

        @Override
        public void setContext(ListEntity listEntity) {
            textView.setText(listEntity.title);
            if (listEntity.select) {
                textView.setBackgroundResource(R.drawable.holder14_type_select_bg);
                textView.setTextColor(application.getResources().getColor(R.color.color1));
            } else {
                textView.setBackgroundResource(R.drawable.holder14_type_bg);
                textView.setTextColor(application.getResources().getColor(R.color.color15));
            }
        }

        @Override
        public void bindView() {
            textView = (TextView) findViewById(R.id.holder15_tv);
        }
    }

    private class Holder17 extends ViewHolder<ListEntity> {
        private TextView title;
        private TextView time;
        private TextView type;

        @Override
        public void setContext(ListEntity listEntity) {
            ShowEntity entity = (ShowEntity) listEntity.object;
            title.setText(entity.name);
            time.setText(entity.time);
            type.setText(entity.type);
            title.setTextColor(application.getResources().getColor(listEntity.select ? R.color.color1 : R.color.color3));
            time.setTextColor(application.getResources().getColor(listEntity.select ? R.color.color1 : R.color.color3));
            type.setTextColor(application.getResources().getColor(listEntity.select ? R.color.color1 : R.color.color3));
            type.setBackgroundResource(listEntity.select ? R.drawable.holder14_type_select_bg : R.drawable.holder14_type_bg);
        }

        @Override
        public void bindView() {
            title = (TextView) findViewById(R.id.holder14_title);
            time = (TextView) findViewById(R.id.holder14_time);
            type = (TextView) findViewById(R.id.holder14_type);
        }
    }

    private class Holder16 extends ViewHolder<ListEntity> {
        private TextView textView;

        @Override
        public void setContext(ListEntity listEntity) {
            textView.setTextColor(application.getResources().getColor(listEntity.select ? R.color.color1 : R.color.color15));
            textView.setText(listEntity.title);
        }

        @Override
        public void bindView() {
            textView = (TextView) findViewById(R.id.holder13_tv);
        }
    }

    private class Holder15 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private TextView title;
        private TextView time;
        private TextView name;

        @Override
        public void setContext(ListEntity entity) {
            ShowEntity showEntity = (ShowEntity) entity.object;
            imageView.setImagePath(showEntity.url);
            title.setText(showEntity.type);
            time.setText(showEntity.time);
            name.setText(showEntity.name);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_short_video_iv);
            title = (TextView) findViewById(R.id.holder_short_video_title);
            time = (TextView) findViewById(R.id.holder_short_video_time);
            name = (TextView) findViewById(R.id.holder_short_video_name);
        }
    }

    private class Holder14 extends ViewHolder<ListEntity> {
        ImageView imageView;
        TextView name;
        TextView type;

        @Override
        public void setContext(ListEntity entity) {
            ShowEntity showEntity = (ShowEntity) entity.object;
            imageView.setImagePath(showEntity.url);
            name.setText(showEntity.name);
            type.setText(showEntity.type);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_hot_list_iv);
            name = (TextView) findViewById(R.id.holder_hot_list_name);
            type = (TextView) findViewById(R.id.holder_hot_list_type);
        }
    }

    private class Holder13 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private TextView textView;

        @Override
        public void setContext(ListEntity listEntity) {
            ShowEntity entity = (ShowEntity) listEntity.object;
            imageView.setImagePath(entity.url);
            textView.setText(entity.name);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder12_iv);
            textView = (TextView) findViewById(R.id.holder12_tv);
        }
    }

    private class Holder12 extends ViewHolder<ListEntity> {
        private TextView textView;

        @Override
        public void setContext(ListEntity listEntity) {
            textView.setText(listEntity.title);
            if (listEntity.select) {
                textView.setBackgroundColor(application.getResources().getColor(R.color.color13));
                textView.setTextColor(application.getResources().getColor(R.color.color1));
            } else {
                textView.setBackgroundColor(application.getResources().getColor(R.color.color14));
                textView.setTextColor(application.getResources().getColor(R.color.color15));
            }
        }

        @Override
        public void bindView() {
            textView = (TextView) findViewById(R.id.holder11_tv);
        }
    }

    private class Holder11 extends ViewHolder<ListEntity> implements View.OnClickListener {
        private TextView title1;
        private TextView title2;
        private TextView type1;
        private TextView type2;
        private ImageView imageView1;
        private ImageView imageView2;
        private View view;
        private View view1;

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent=new Intent(application,PlayActivity.class);
            ListEntity entity=adapter.getItem(position);
            intent.putExtra(PlayActivity.PLAY_TYPE,entity.title);
            List<ShowEntity> entities = (List<ShowEntity>) entity.object;
            if (id == R.id.holder10_iv1) {
                intent.putExtra(PlayActivity.PLAY_CHANNEL,entities.get(0).name);
            } else {
                intent.putExtra(PlayActivity.PLAY_CHANNEL,entities.get(1).name);
            }
            context.getContext().startActivity(intent);
        }

        @Override
        public void setContext(ListEntity listEntity) {
            List<ShowEntity> entities = (List<ShowEntity>) listEntity.object;
            ShowEntity entity = entities.get(0);
            title1.setText(entity.name);
            type1.setText(entity.type);
            imageView1.setImagePath(entity.url);
            if (entities.size() > 1) {
                ShowEntity entity1 = entities.get(1);
                title2.setVisibility(View.VISIBLE);
                title2.setText(entity1.name);
                type2.setVisibility(View.VISIBLE);
                type2.setText(entity1.type);
                imageView2.setVisibility(View.VISIBLE);
                imageView2.setImagePath(entity1.url);
            }else{
                title2.setVisibility(View.GONE);
                type2.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
            }
//            else
//                view.setVisibility(View.GONE);
        }

        @Override
        public void bindView() {
            title1 = (TextView) findViewById(R.id.holder10_title1);
            title2 = (TextView) findViewById(R.id.holder10_title2);
            type1 = (TextView) findViewById(R.id.holder10_type1);
            type2 = (TextView) findViewById(R.id.holder10_type2);
            imageView1 = (ImageView) findViewById(R.id.holder10_iv1);
            imageView1.setOnClickListener(this);
            imageView2 = (ImageView) findViewById(R.id.holder10_iv2);
            imageView2.setOnClickListener(this);
        }
    }

    private class Holder10 extends ViewHolder<ListEntity> implements View.OnClickListener {
        private TextView title;
        private ImageView imageView1;
        private ImageView imageView2;
        private ImageView imageView3;
        private ImageView imageView4;
        private TextView title1;
        private TextView title2;
        private TextView title3;
        private TextView title4;
        private TextView type1;
        private TextView type2;
        private TextView type3;
        private TextView type4;

        @Override
        public void setContext(ListEntity listEntity) {
            title.setText(listEntity.title);
            List<ShowEntity> entities = (List<ShowEntity>) listEntity.object;
            imageView1.setImagePath(entities.get(listEntity.topPading).url);
            imageView2.setImagePath(entities.get(listEntity.topPading + 1).url);
            imageView3.setImagePath(entities.get(listEntity.topPading + 2).url);
            imageView4.setImagePath(entities.get(listEntity.topPading + 3).url);
            title1.setText(entities.get(listEntity.topPading).name);
            title2.setText(entities.get(listEntity.topPading + 1).name);
            title3.setText(entities.get(listEntity.topPading + 2).name);
            title4.setText(entities.get(listEntity.topPading + 3).name);
            type1.setText(entities.get(listEntity.topPading).type);
            type2.setText(entities.get(listEntity.topPading + 1).type);
            type3.setText(entities.get(listEntity.topPading + 2).type);
            type4.setText(entities.get(listEntity.topPading + 3).type);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.holder9_next) {
                ListEntity entity = adapter.getItem(position);
                entity.topPading += 4;
                if (entity.topPading >= ((List) entity.object).size())
                    entity.topPading = 0;
                adapter.notifyDataSetChanged(position);
            } else if (id == R.id.holder9_iv1) {
                context.getContext().startActivity(new Intent(application, PlayActivity.class));
            } else if (id == R.id.holder9_iv2) {
                context.getContext().startActivity(new Intent(application, PlayActivity.class));
            } else if (id == R.id.holder9_iv3) {
                context.getContext().startActivity(new Intent(application, PlayActivity.class));
            } else if (id == R.id.holder9_iv4) {
                context.getContext().startActivity(new Intent(application, PlayActivity.class));
            }
        }

        @Override
        public void bindView() {
            title = (TextView) findViewById(R.id.holder9_item_title);
            imageView1 = (ImageView) findViewById(R.id.holder9_iv1);
            imageView1.setOnClickListener(this);
            imageView2 = (ImageView) findViewById(R.id.holder9_iv2);
            imageView2.setOnClickListener(this);
            imageView3 = (ImageView) findViewById(R.id.holder9_iv3);
            imageView3.setOnClickListener(this);
            imageView4 = (ImageView) findViewById(R.id.holder9_iv4);
            imageView4.setOnClickListener(this);
            title1 = (TextView) findViewById(R.id.holder9_title1);
            title2 = (TextView) findViewById(R.id.holder9_title2);
            title3 = (TextView) findViewById(R.id.holder9_title3);
            title4 = (TextView) findViewById(R.id.holder9_title4);
            type1 = (TextView) findViewById(R.id.holder9_type1);
            type2 = (TextView) findViewById(R.id.holder9_type2);
            type3 = (TextView) findViewById(R.id.holder9_type3);
            type4 = (TextView) findViewById(R.id.holder9_type4);
            findViewById(R.id.holder9_next).setOnClickListener(this);
        }
    }

    private class Holder9 extends ViewHolder<ListEntity> {
        private ImageView imageView;

        @Override
        public void setContext(ListEntity listEntity) {
            imageView.setImagePath((String) listEntity.object);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder8_iv);
        }
    }

    private class Holder8 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private TextView title;
        private TextView time;
        private TextView name;

        @Override
        public void setContext(ListEntity recommendEntity) {
            ShowEntity showEntity = (ShowEntity) recommendEntity.object;
            imageView.setImagePath(showEntity.url);
            title.setText(showEntity.type);
            time.setText(showEntity.time);
            name.setText(showEntity.name);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_short_video_iv);
            title = (TextView) findViewById(R.id.holder_short_video_title);
            time = (TextView) findViewById(R.id.holder_short_video_time);
            name = (TextView) findViewById(R.id.holder_short_video_name);
        }
    }

    private class Holder7 extends ViewHolder<ListEntity> {
        ImageView imageView;
        TextView name;
        TextView type;

        @Override
        public void setContext(ListEntity recommendEntity) {
            ShowEntity showEntity = (ShowEntity) recommendEntity.object;
            imageView.setImagePath(showEntity.url);
            name.setText(showEntity.name);
            type.setText(showEntity.type);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_hot_list_iv);
            name = (TextView) findViewById(R.id.holder_hot_list_name);
            type = (TextView) findViewById(R.id.holder_hot_list_type);
        }
    }

    private class Holder6 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private ImageView imageView1;
        private TextView textView;
        private TextView textView1;
        private TextView type;
        private TextView type1;

        @Override
        public void setContext(ListEntity recommendEntity) {
            List<ShowEntity> entities = (List<ShowEntity>) recommendEntity.object;
            imageView.setImagePath(entities.get(0).url);
            textView.setText(entities.get(0).name);
            type.setText(entities.get(0).type);
            if (entities.size() < 2) {
                imageView1.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);
                type1.setVisibility(View.GONE);
            } else {
                imageView1.setVisibility(View.VISIBLE);
                imageView1.setImagePath(entities.get(1).url);
                textView1.setVisibility(View.VISIBLE);
                textView1.setText(entities.get(1).name);
                type1.setVisibility(View.VISIBLE);
                type1.setText(entities.get(1).type);
            }
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_recommend6_iv1);
            imageView1 = (ImageView) findViewById(R.id.holder_recommend6_iv2);
            textView = (TextView) findViewById(R.id.holder_recommend6_tv1);
            textView1 = (TextView) findViewById(R.id.holder_recommend6_tv2);
            type = (TextView) findViewById(R.id.holder_recommend6_type1);
            type1 = (TextView) findViewById(R.id.holder_recommend6_type2);
            findViewById(R.id.holder_recommend6_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, PlayActivity.class));
                }
            });
            findViewById(R.id.holder_recommend6_fl1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, PlayActivity.class));
                }
            });
        }
    }

    private class Holder5 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private ImageView imageView1;
        private TextView textView;
        private TextView textView1;

        @Override
        public void setContext(ListEntity recommendEntity) {
            List<ShowEntity> entities = (List<ShowEntity>) recommendEntity.object;
            imageView.setImagePath(entities.get(0).url);
            textView.setText(entities.get(0).name);
            if (entities.size() < 2) {
                imageView1.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);
            } else {
                imageView1.setVisibility(View.VISIBLE);
                textView1.setVisibility(View.VISIBLE);
                imageView1.setImagePath(entities.get(1).url);
                textView1.setText(entities.get(1).name);
            }
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_recommend5_iv1);
            imageView1 = (ImageView) findViewById(R.id.holder_recommend5_iv2);
            textView = (TextView) findViewById(R.id.holder_recommend5_tv1);
            textView1 = (TextView) findViewById(R.id.holder_recommend5_tv2);
            findViewById(R.id.holder_recommend5_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, PlayActivity.class));
                }
            });
            findViewById(R.id.holder_recommend5_fl1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, PlayActivity.class));
                }
            });
        }
    }

    private class Holder4 extends ViewHolder<ListEntity> {
        private ImageView imageView;
        private TextView textView;

        @Override
        public void setContext(ListEntity recommendEntity) {
            ShowEntity entity = (ShowEntity) recommendEntity.object;
            imageView.setImagePath(entity.url);
            textView.setText(entity.name);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_recommend4_tv);
            textView = (TextView) findViewById(R.id.holder_recommend4_iv);
            findViewById(R.id.holder_recommend4_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, PlayActivity.class));
                }
            });
        }
    }

    private class Holder3 extends ViewHolder<ListEntity> {
        private TextView textView;

        @Override
        public void setContext(ListEntity recommendEntity) {
            textView.setText((String) recommendEntity.object);
            context.setPadding(0, recommendEntity.topPading * dp1, 0, 0);
        }

        @Override
        public void bindView() {
            textView = (TextView) findViewById(R.id.holder_recommend3_tv);
            findViewById(R.id.holder_recommend3_move).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post((String) adapter.getItem(position).object);
                }
            });
        }
    }

    private class Holder2 extends ViewHolder<ListEntity> {
        TextView textView;

        @Override
        public void setContext(ListEntity recommendEntity) {
            textView.setText((String) recommendEntity.object);
        }

        @Override
        public void bindView() {
            textView = (TextView) findViewById(R.id.holder_recommend2_tv);
            findViewById(R.id.holder_hot_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, HotListActivity.class));
                }
            });
            findViewById(R.id.holder_gold_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, MyGoldActivity.class));
                }
            });
        }
    }

    private class Holder1 extends ViewHolder<ListEntity> {
        ViewPager recyclerView;
        PagerAdapter<String> adapter;
        private boolean set = false;
        private int selectPosition = 0;
        private int size;
        private Handler handler = new Handler();
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                selectPosition++;
                if (selectPosition > Integer.MAX_VALUE) {
                    selectPosition = 0;
                }
                adapter.setSelection(selectPosition);
                handler.postDelayed(this, 5000);
            }
        };

        @Override
        public void setContext(ListEntity recommendEntity) {
            if (set) {
                adapter.notifyDataSetChanged(position);
                return;
            }
            set = true;
            List<String> url = (List<String>) recommendEntity.object;
            adapter.clean();
            adapter.addItem(url);
            selectPosition = Integer.MAX_VALUE >> 1;
            size = url.size();
            selectPosition -= selectPosition % size;
            adapter.setSelection(selectPosition);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 5000);
        }

        @Override
        public void bindView() {
            set = false;
            recyclerView = (ViewPager) findViewById(R.id.holder_recommend_rv);
            recyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContext().startActivity(new Intent(application, MyGoldActivity.class));
                }
            });
            recyclerView.setOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    if (i == 0) {
                        selectPosition = Integer.MAX_VALUE >> 1;
                        selectPosition -= selectPosition % size;
                    } else if (i == Integer.MAX_VALUE) {
                        selectPosition = Integer.MAX_VALUE >> 1;
                        selectPosition -= selectPosition % size;
                        selectPosition += Integer.MAX_VALUE % size;
                    }
                    adapter.setSelection(selectPosition);

                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            adapter = new PagerAdapter<String>(recyclerView) {

                @Override
                public String getItem(int position) {
                    return super.getItem(position % size);
                }

                @Override
                public int getCount() {
                    return Integer.MAX_VALUE;
                }

                @Override
                public ViewHolder<String> getViewHolder(int itemType) {
                    return new ViewHolder<String>() {
                        private ImageView imageView;

                        @Override
                        public void setContext(String s) {
                            imageView.setImagePath(s);
                        }

                        @Override
                        public void bindView() {
                            imageView = (ImageView) findViewById(R.id.holder_holder_iv);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.getContext().startActivity(new Intent(application, TvStationActivity.class));
                                }
                            });
                        }
                    };
                }

                @Override
                public int getView(int itemType) {
                    return R.layout.holder_recommend_holder;
                }
            };
        }
    }

}
