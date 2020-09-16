package com.momo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.base.BaseActivity;
import com.momo.R;
import com.momo.adapter.SplashAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.splash_vp)
    ViewPager splashVp;
    private SplashAdapter adapter;
    private List<View> views;
    private int time = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            time++;
            if (time >= 5) {
                startActivity(new Intent(baseApplication, MainActivity.class));
                finish();
            } else {
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        handler.sendEmptyMessageDelayed(0, 1000);
        adapter = new SplashAdapter(splashVp);
        adapter.addItem("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        adapter.addItem("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg");
        adapter.addItem("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1451330793,2242997567&fm=27&gp=0.jpg");
        adapter.addItem("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        adapter.addItem("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2363037083,4182949652&fm=27&gp=0.jpg");
        views = new ArrayList<>();
        views.add(findViewById(R.id.splash_point1));
        views.add(findViewById(R.id.splash_point2));
        views.add(findViewById(R.id.splash_point3));
        views.add(findViewById(R.id.splash_point4));
        views.add(findViewById(R.id.splash_point5));
        splashVp.setOnPageChangeListener(this);
//        baseApplicati
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < views.size(); i++) {
            if (i == position)
                views.get(i).setBackgroundResource(R.drawable.point);
            else
                views.get(i).setBackgroundResource(R.drawable.point2);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
