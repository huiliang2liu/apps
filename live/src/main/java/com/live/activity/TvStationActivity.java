package com.live.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.animation.AnimatorFactory;
import com.base.animation.ViewEmbellish;
import com.base.util.L;
import com.base.widget.ImageView;
import com.live.Application;
import com.live.R;
import com.live.entities.RadioEntity;
import com.live.provide.ChannelProvide;
import com.media.IMedia;
import com.media.MediaListener;
import com.media.MusicService;
import com.media.bind.IPlayMusic;
import com.media.exoplayer.ExoMediaImpl;
import com.screen.iface.FullScreen;
import com.screen.iface.StatusBarTextColorWhite;

import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TvStationActivity extends BaseActivity implements View.OnClickListener, FullScreen, StatusBarTextColorWhite, MediaListener {
    private static final String TAG = "TvStationActivity";
    public static final String PLAY_RADIO = "PLAY_RADIO";
    @BindView(R.id.tv_station_iv)
    ImageView tvStationIv;
    @BindView(R.id.tv_station_name)
    TextView tvStationName;
    @BindView(R.id.tv_station_play)
    android.widget.ImageView tvStationPlay;
    @BindView(R.id.tv_station_pause)
    android.widget.ImageView tvStationPause;
    @BindView(R.id.tv_station_last)
    android.widget.ImageView tvStationLast;
    @BindView(R.id.tv_station_next)
    android.widget.ImageView tvStationNext;
    private ObjectAnimator animator;
    private List<RadioEntity> radioEntities;
    private int index = 0;
    private RadioEntity mEntity;
    private Application application;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (Application) baseApplication;
        setContentView(R.layout.activity_tv_station);
        ButterKnife.bind(this);
        radioEntities = ChannelProvide.getRadios(this);
        tvStationName.setText("905交通广播");
        String name = getIntent().getStringExtra(PLAY_RADIO);
        int len = radioEntities.size();
        int playIndex = 0;
        if (name != null && !name.isEmpty()) {
            for (int i = 0; i < len; i++) {
                RadioEntity radioEntity = radioEntities.get(i);
                if (name.equals(radioEntity.name)) {
                    index = i;
                } else {
                    if (radioEntity.play) {
                        playIndex = i;
                    }
                }
            }
            if (index >= 0) {
                if (index != playIndex) {
                    RadioEntity radioEntity = radioEntities.get(playIndex);
                    if (radioEntity.play) {
                        L.d(TAG, "更新就播放" + radioEntity.name);
                        radioEntity.play = false;
                        ChannelProvide.updateRadio(TvStationActivity.this, radioEntity);
                    }
                }
            } else {
                index = playIndex;
            }
        } else {
            L.d(TAG, "没有传名字");
            for (int i = 0; i < len; i++) {
                RadioEntity radioEntity = radioEntities.get(i);
                if (radioEntity.play) {
                    index = i;
                    break;
                }
            }
        }
        if (len > 0)
            play();
        tvStationIv.setImagePath("http://img1.imgtn.bdimg.com/it/u=3667678122,3437809409&fm=26&gp=0.jpg");
        tvStationPlay.setOnClickListener(this);
        tvStationPause.setOnClickListener(this);
        tvStationLast.setOnClickListener(this);
        tvStationNext.setOnClickListener(this);
        animator = AnimatorFactory.rotation(new ViewEmbellish(tvStationIv), 5000, 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        animator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        animator.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animator.pause();
    }

    private void play() {
        if (mEntity != null) {
            mEntity.play = false;
            ChannelProvide.updateRadio(this, mEntity);
        }
        mEntity = radioEntities.get(index);
        mEntity.play = true;
        if (application.play(mEntity)) {
            ChannelProvide.updateRadio(this, mEntity);
        } else {
            if (application.radioEntity != null)
                mEntity = application.radioEntity;
        }
        tvStationName.setText(mEntity.name);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_station_play) {
            L.d(TAG, "play");
            if (application.playMusic != null)
                try {
                    application.playMusic.play();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        } else if (id == R.id.tv_station_pause) {
            L.d(TAG, "pause");
            if (application.playMusic != null)
                try {
                    application.playMusic.pause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        } else if (id == R.id.tv_station_last) {
            L.d(TAG, "last");
            if (index == 0)
                return;
            index--;
            play();
        } else if (id == R.id.tv_station_next) {
            L.d(TAG, "next");
            if (radioEntities == null || index >= radioEntities.size() - 1)
                return;
            index++;
            play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        media.pause();
//        media.stop();
//        media.destroy();
    }

    @Override
    public void onCompletion() {
    }

    @Override
    public void onError(int what, int extra) {
        if (mEntity != null) {
            int index = mEntity.playIndex;
            index++;
            if (mEntity.strings.size() > index) {
                L.d(TAG, "换源");
                mEntity.playIndex = index;
                play();
            } else {
                L.d(TAG, "换节目");
                index = this.index + 1;
                if (radioEntities.size() > index) {
                    mEntity = radioEntities.get(index);
                    this.index = index;
                    play();
                } else {
                    L.d(TAG, "最后一个节目不可换节目");
                }
            }
        }
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public boolean onInfo(int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height) {

    }
}
