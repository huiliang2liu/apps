package com.momo.activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.adapter.FragmentAdapter;
import com.momo.R;
import com.momo.fragment.FocusFragment;
import com.momo.fragment.LiveFragment;
import com.momo.fragment.MainFragment;
import com.momo.fragment.MessageFragment;
import com.momo.fragment.MoreFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.main_vp)
    ViewPager mainVp;
    @BindView(R.id.main_home_iv)
    ImageView mainHomeIv;
    @BindView(R.id.main_home_tv)
    TextView mainHomeTv;
    @BindView(R.id.main_home_ll)
    LinearLayout mainHomeLl;
    @BindView(R.id.main_live_iv)
    ImageView mainLiveIv;
    @BindView(R.id.main_live_tv)
    TextView mainLiveTv;
    @BindView(R.id.main_live_ll)
    LinearLayout mainLiveLl;
    @BindView(R.id.main_message_iv)
    ImageView mainMessageIv;
    @BindView(R.id.main_message_tv)
    TextView mainMessageTv;
    @BindView(R.id.main_message_size)
    TextView mainMessageSize;
    @BindView(R.id.main_message_fl)
    FrameLayout mainMessageFl;
    @BindView(R.id.main_focus_iv)
    ImageView mainFocusIv;
    @BindView(R.id.main_focus_tv)
    TextView mainFocusTv;
    @BindView(R.id.main_focus_ll)
    LinearLayout mainFocusLl;
    @BindView(R.id.main_more_iv)
    ImageView mainMoreIv;
    @BindView(R.id.main_more_tv)
    TextView mainMoreTv;
    @BindView(R.id.main_more_ll)
    LinearLayout mainMoreLl;
    FragmentAdapter adapter;
    private List<Fragment> fragments=new ArrayList<>();
    private int page = 0;
    {
        fragments.add(new MainFragment());
        fragments.add(new LiveFragment());
        fragments.add(new MessageFragment());
        fragments.add(new FocusFragment());
        fragments.add(new MoreFragment());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainHomeLl.setOnClickListener(this);
        mainLiveLl.setOnClickListener(this);
        mainMessageFl.setOnClickListener(this);
        mainFocusLl.setOnClickListener(this);
        mainMoreLl.setOnClickListener(this);
        adapter=new FragmentAdapter(this,fragments);
        mainVp.setAdapter(adapter);
        page();
        Log.e("MainActivity","onCreate");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.main_home_ll) {
            if (page != 0) {
                page = 0;
                page();
            }
        } else if (id == R.id.main_live_ll) {
            if (page != 1) {
                page = 1;
                page();
            }
        } else if (id == R.id.main_message_fl) {
            if (page != 2) {
                page = 2;
                page();
            }
        } else if (id == R.id.main_focus_ll) {
            if (page != 3) {
                page = 3;
                page();
            }
        } else if (id == R.id.main_more_ll) {
            if (page != 4) {
                page = 4;
                page();
            }
        }
    }

    private void page() {
        mainVp.setCurrentItem(page,false);
        mainHomeIv.setImageResource(page == 0 ? R.mipmap.ic_nav_nearby_active : R.mipmap.ic_nav_nearby_normal);
        mainLiveIv.setImageResource(page == 1 ? R.mipmap.ic_nav_live_active : R.mipmap.ic_nav_live_normal);
        mainMessageIv.setImageResource(page == 2 ? R.mipmap.ic_nav_chat_active : R.mipmap.ic_nav_chat_normal);
        mainFocusIv.setImageResource(page == 3 ? R.mipmap.ic_nav_follow_active : R.mipmap.ic_nav_follow_normal);
        mainMoreIv.setImageResource(page == 4 ? R.mipmap.ic_nav_profile_active : R.mipmap.ic_nav_profile_normal);
        mainHomeTv.setTextColor(page==0?Color.rgb(0x72,0xdf,0xe5):Color.argb(0x80,0x00,0x00,0x00));
        mainLiveTv.setTextColor(page==1?Color.rgb(0xf0,0x91,0x84):Color.argb(0x80,0x00,0x00,0x00));
        mainMessageTv.setTextColor(page==2?Color.rgb(0x7e,0xe3,0xd1):Color.argb(0x80,0x00,0x00,0x00));
        mainFocusTv.setTextColor(page==3?Color.rgb(0xd7,0x7a,0xf5):Color.argb(0x80,0x00,0x00,0x00));
        mainMoreTv.setTextColor(page==4?Color.rgb(0xf8,0xcf,0x47):Color.argb(0x80,0x00,0x00,0x00));
    }
}
