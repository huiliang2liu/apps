package com.momo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.widget.WrapLayout;
import com.momo.R;
import com.momo.adapter.PeopleDedailAdapter1;
import com.momo.adapter.PeopleDedailAdapter2;
import com.momo.entities.PeopleEntity;
import com.momo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeopleDetailActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "PeopleDetailActivity";
    private static final String FORMAT = "%d/%d";
    private static final String DYNAMIC_FORMAT = "动态 %d";
    private static final String LABEL_FORMAT = "标签 %d";
    @BindView(R.id.people_detail_vp)
    ViewPager peopleDetailVp;
    @BindView(R.id.people_detail_dd_tv)
    TextView peopleDetailDdTv;
    @BindView(R.id.people_detail_gr_tv)
    TextView peopleDetailGrTv;
    @BindView(R.id.people_detail_size)
    TextView peopleDetailSize;
    @BindView(R.id.people_detail_name)
    TextView peopleDetailName;
    @BindView(R.id.people_detail_state)
    TextView peopleDetailState;
    @BindView(R.id.people_detail_sex)
    ImageView peopleDetailSex;
    @BindView(R.id.people_detail_age)
    TextView peopleDetailAge;
    @BindView(R.id.people_detail_dynamic_size)
    TextView peopleDetailDynamicSize;
    @BindView(R.id.people_detail_dynamic)
    RecyclerView peopleDetailDynamic;
    @BindView(R.id.people_detail_wl)
    WrapLayout peopleDetailWl;
    @BindView(R.id.people_detail_label)
    TextView peopleDetailLabel;
    private PeopleEntity peopleEntity;
    private PeopleDedailAdapter1 vpAdapter;
    private PeopleDedailAdapter2 rvAdapter;
    private boolean diandian = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        peopleEntity = (PeopleEntity) getIntent().getSerializableExtra(Constants.PEOPLE_ENTITY);
        setContentView(R.layout.activity_people_detail);
        ButterKnife.bind(this);
        ViewGroup.LayoutParams layoutParams = peopleDetailVp.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().widthPixels;
        peopleDetailVp.setLayoutParams(layoutParams);
        peopleDetailVp.setOffscreenPageLimit(1);
        vpAdapter = new PeopleDedailAdapter1(peopleDetailVp);
        vpAdapter.addItem(peopleEntity.pointPic);
        diandian = true;
        peopleDetailGrTv.setAlpha(.8f);
        peopleDetailSize.setText(String.format(FORMAT, 1, peopleEntity.pointPic.size()));
        peopleDetailVp.setOnPageChangeListener(this);
        peopleDetailDdTv.setOnClickListener(this);
        peopleDetailGrTv.setOnClickListener(this);
        peopleDetailName.setText(peopleEntity.name);
        peopleDetailState.setText(peopleEntity.state == 0 ? "在线" : "隐身");
        peopleDetailAge.setText(peopleEntity.age + "");
        peopleDetailDynamicSize.setText(String.format(DYNAMIC_FORMAT, peopleEntity.dynamics.size()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        peopleDetailDynamic.setLayoutManager(linearLayoutManager);
        rvAdapter = new PeopleDedailAdapter2(peopleDetailDynamic);
        rvAdapter.addItem(peopleEntity.dynamics);
        peopleDetailWl.setHorizontalSpacing(20);
        peopleDetailWl.setVerticalSpacing(20);
        peopleDetailWl.setGravity(WrapLayout.GRAVITY_CENTER);
        if (peopleEntity.label != null && peopleEntity.label.size() > 0) {
            peopleDetailLabel.setText(String.format(LABEL_FORMAT, peopleEntity.label.size()));
            for (String string : peopleEntity.label) {
                View view = LayoutInflater.from(this).inflate(R.layout.label, null);
                TextView textView = view.findViewById(R.id.label_tv);
                textView.setText(string);
                peopleDetailWl.addView(view);
            }
        }
        peopleDetailSex.setImageResource(peopleEntity.sex == 0 ? R.mipmap.hani_live_home_filter_male_icon : R.mipmap.hani_live_home_filter_female_icon);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.people_detail_dd_tv) {
            if (diandian)
                return;
            diandian = true;
            peopleDetailGrTv.setAlpha(.8f);
//            peopleDetailGrTv.sTS
            peopleDetailGrTv.setTextSize(12);
            peopleDetailDdTv.setAlpha(1);
            peopleDetailDdTv.setTextSize(15);
            vpAdapter.clean();
            vpAdapter.addItem(peopleEntity.pointPic);
            peopleDetailSize.setText(String.format(FORMAT, 1, peopleEntity.pointPic.size()));
            peopleDetailVp.setCurrentItem(0);
//            peopleDetailVp.setAdapter(vpAdapter);
        } else if (id == R.id.people_detail_gr_tv) {
            if (!diandian)
                return;
            diandian = false;
            peopleDetailDdTv.setAlpha(.8f);
            peopleDetailDdTv.setTextSize(12);
            peopleDetailGrTv.setAlpha(1);
            peopleDetailGrTv.setTextSize(15);
            vpAdapter.clean();
            vpAdapter.addItem(peopleEntity.pic);
            peopleDetailVp.setCurrentItem(0);
            peopleDetailSize.setText(String.format(FORMAT, 1, peopleEntity.pic.size()));
//            peopleDetailVp.setAdapter(vpAdapter);
        }
    }

    public void showPics(int position) {
        Intent intent = new Intent(this, PicturesShowActivity.class);
        intent.putStringArrayListExtra(Constants.PICS, diandian ? peopleEntity.pointPic : peopleEntity.pic);
        intent.putExtra(Constants.SHOW_INDEX, position);
        startActivity(intent);
    }

    public void showDynamic(int position) {
        Intent intent = new Intent(this, PicturesShowActivity.class);
        intent.putExtra(Constants.DYNAMICS, peopleEntity.dynamics);
        intent.putExtra(Constants.SHOW_INDEX, position);
        startActivity(intent);
    }

    @Override
    public boolean fullScreen() {
        return true;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        peopleDetailSize.setText(String.format(FORMAT, i + 1, peopleEntity.pointPic.size()));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
