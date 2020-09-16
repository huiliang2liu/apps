package com.momo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseActivity;
import com.base.adapter.IAdapter;
import com.base.widget.CardLayoutManager;
import com.momo.R;
import com.momo.adapter.RecommendAdapter;
import com.momo.entities.DynamicEntity;
import com.momo.entities.PeopleEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendActivity extends BaseActivity implements CardLayoutManager.OnSwipeListener {
    @BindView(R.id.recommend_rv)
    RecyclerView recommendRv;
    private CardLayoutManager cardLayoutManager;
    private IAdapter<PeopleEntity> adapter;
    private List<PeopleEntity> peopleEntities;
    String text = "abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ1234567890";

    {
        peopleEntities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PeopleEntity entity = new PeopleEntity();
            entity.age = i + 10;
            entity.city = "北京";
            entity.constellation = "狮子座";
            ArrayList<DynamicEntity> dynamicEntities = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
//                DynamicEntity dynamicEntity = new DynamicEntity();
//                dynamicEntity.pic = "https://f11.baidu.com/it/u=2465775762,1509670197&fm=72";
//                dynamicEntity.text = "测试";
//                dynamicEntities.add(dynamicEntity);
            }
            entity.dynamics = dynamicEntities;
            entity.head = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1036218379,1213399244&fm=26&gp=0.jpg";
            ArrayList<String> label = new ArrayList<>();
            for (int k = 0; k < 10; k++) {
                int r = (int) (Math.random() * 10);
                label.add(text.substring(r, r * 2 + 10));
            }
            int sex = (int) (Math.random() * 10);
            entity.sex = sex % 2;
            entity.label = label;
            entity.name = "name" + i;
            entity.state = 1;
            entity.registTime = "2017-05-06";
            entity.signature = "唯我独尊";
            ArrayList<String> pic = new ArrayList<>();
            pic.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=231620273,2622968107&fm=26&gp=0.jpg");
            pic.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2595508360,28762262&fm=26&gp=0.jpg");
            pic.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3495002897,1124108098&fm=26&gp=0.jpg");
            pic.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1769196384,2228949144&fm=26&gp=0.jpg");
            pic.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=558553267,1442409839&fm=26&gp=0.jpg");
            pic.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=320854467,1081172394&fm=26&gp=0.jpg");
            pic.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2883422974,2939969757&fm=26&gp=0.jpg");
            entity.pic = pic;
            ArrayList<String> pointPic = new ArrayList<>();
            pointPic.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3074348605,4065442562&fm=26&gp=0.jpg");
            pointPic.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2999460979,175312468&fm=26&gp=0.jpg");
            pointPic.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1966645011,1803981512&fm=26&gp=0.jpg");
            pointPic.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2369560882,1700665613&fm=26&gp=0.jpg");
            pointPic.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3860992474,672104378&fm=26&gp=0.jpg");
            pointPic.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3960929628,3097818286&fm=26&gp=0.jpg");
            pointPic.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1232125774,2388954377&fm=26&gp=0.jpg");
            entity.pointPic = pointPic;
            peopleEntities.add(entity);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        ButterKnife.bind(this);
        cardLayoutManager = new CardLayoutManager(recommendRv);
        cardLayoutManager.setSwipeListener(this);
        recommendRv.setItemAnimator(new DefaultItemAnimator());
        recommendRv.setLayoutManager(cardLayoutManager);
        adapter = new RecommendAdapter(recommendRv);
        adapter.addItem(peopleEntities);
    }

    @Override
    public void onSwiped(int adapterPosition, int direction) {

    }

    @Override
    public void onSwipeTo(RecyclerView.ViewHolder viewHolder, float offset) {

    }
}
