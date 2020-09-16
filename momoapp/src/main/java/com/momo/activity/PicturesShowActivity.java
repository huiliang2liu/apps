package com.momo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.adapter.IAdapter;
import com.momo.R;
import com.momo.adapter.PicturesShowAdapter;
import com.momo.entities.DynamicEntity;
import com.momo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicturesShowActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private static final String FORMAT = "%d/%d";
    List<String> pics;
    List<DynamicEntity> dynamicEntities;
    List<String> picsPath;
    private int showIndex = 0;
    @BindView(R.id.pictures_show_vp)
    ViewPager picturesShowVp;
    @BindView(R.id.pictures_show_size)
    TextView picturesShowSize;
    @BindView(R.id.pictures_show_dynamic)
    TextView picturesShowDynamic;
    private IAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        pics = intent.getStringArrayListExtra(Constants.PICS);
        dynamicEntities = (List<DynamicEntity>) intent.getSerializableExtra(Constants.DYNAMICS);
        showIndex = intent.getIntExtra(Constants.SHOW_INDEX, 0);
        setContentView(R.layout.activity_pictures_show);
        ButterKnife.bind(this);
        picsPath = new ArrayList<>();
        if (pics != null) {
            picturesShowDynamic.setVisibility(View.GONE);
            picsPath.addAll(pics);
        } else {
//            for (DynamicEntity dynamicEntity : dynamicEntities)
//                picsPath.add(dynamicEntity.pic);
        }
        adapter = new PicturesShowAdapter(picturesShowVp);
        adapter.addItem(picsPath);
        picturesShowSize.setText(String.format(FORMAT, showIndex + 1, picsPath.size()));
        if (dynamicEntities != null) {
            picturesShowDynamic.setText(dynamicEntities.get(0).text);
        }
        picturesShowVp.setOnPageChangeListener(this);
        picturesShowVp.setCurrentItem(showIndex);

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        picturesShowSize.setText(String.format(FORMAT, i + 1, picsPath.size()));
        if (dynamicEntities != null) {
            picturesShowDynamic.setText(dynamicEntities.get(i).text);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
