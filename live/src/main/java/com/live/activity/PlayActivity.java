package com.live.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.BaseActivity;
import com.base.adapter.RecyclerViewAdapter;
import com.base.util.L;
import com.base.widget.RecyclerView;
import com.live.Application;
import com.live.R;
import com.live.adapter.ListAdapter;
import com.live.control.IControl;
import com.live.control.LiveControl;
import com.live.entities.ListEntity;
import com.live.entities.LiveEntity;
import com.live.entities.LiveTypeEntity;
import com.live.entities.SourceEntity;
import com.live.provide.ChannelProvide;
import com.live.widget.PlayView;
import com.media.MediaListener;
import com.screen.ScreenManager;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayActivity extends BaseActivity implements PlayView.ScreenListener, View.OnClickListener, MediaListener {
    private static final String TAG = "PlayActivity";
    public static final String PLAY_TYPE = "PLAY_TYPE";
    public static final String PLAY_CHANNEL = "PLAY_CHANNEL";
    private Application application;
    @BindView(R.id.play_pv)
    PlayView playView;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String play = intent.getStringExtra("play");
            if (play == null || play.isEmpty()) {
                String text = intent.getStringExtra("url");
                Log.d(TAG, text);
                LiveEntity liveEntity = LiveEntity.json2entity(text);
                if (liveEntity != null)
                    setLiveEntity(liveEntity);
            } else {
                if (mLiveEntity != null) {
                    mLiveEntity.play = false;
                    mLiveEntity = null;
                }
                play(play);
            }
        }
    };
    @BindView(R.id.play_live_programme)
    TextView playLiveProgramme;
    @BindView(R.id.play_live_related)
    TextView playLiveRelated;
    @BindView(R.id.play_live_programme_time)
    RecyclerView playLiveProgrammeTime;
    @BindView(R.id.play_live_programme_list)
    RecyclerView playLiveProgrammeList;
    @BindView(R.id.play_live_parent)
    FrameLayout playLiveParent;
    @BindView(R.id.play_live_programme_line)
    View playLiveProgrammeLine;
    @BindView(R.id.play_live_related_line)
    View playLiveRelatedLine;
    @BindView(R.id.play_live_related_rv)
    RecyclerView playLiveRelatedRv;
    @BindView(R.id.play_teleplay_title)
    TextView playTeleplayTitle;
    @BindView(R.id.play_teleplay_iv)
    ImageView playTeleplayIv;
    @BindView(R.id.play_teleplay_collection)
    TextView playTeleplayCollection;
    @BindView(R.id.play_teleplay_details)
    TextView playTeleplayDetails;
    @BindView(R.id.play_teleplay_select_rv)
    RecyclerView playTeleplaySelectRv;
    @BindView(R.id.play_teleplay_hot_rv)
    RecyclerView playTeleplayHotRv;
    @BindView(R.id.play_teleplay_parent)
    FrameLayout playTeleplayParent;
    @BindView(R.id.play_hot_title)
    TextView playHotTitle;
    @BindView(R.id.play_hot_iv)
    ImageView playHotIv;
    @BindView(R.id.play_hot_collection)
    TextView playHotCollection;
    @BindView(R.id.play_hot_name)
    TextView playHotName;
    @BindView(R.id.play_hot_rv)
    RecyclerView playHotRv;
    @BindView(R.id.play_hot_parent)
    FrameLayout playHotParent;
    @BindView(R.id.play_movie_title)
    TextView playMovieTitle;
    @BindView(R.id.play_movie_iv)
    ImageView playMovieIv;
    @BindView(R.id.play_movie_collection)
    TextView playMovieCollection;
    @BindView(R.id.play_movie_rv)
    RecyclerView playMovieRv;
    @BindView(R.id.play_movie_parent)
    FrameLayout playMovieParent;
    private ListAdapter programmeTimeAdapter;
    private ListAdapter programmeListAdapter;
    private ListAdapter relatedAdapter;
    private ListAdapter teleplaySelectAdapter;
    private ListAdapter teleplayHotAdapter;
    private ListAdapter hotAdapter;
    private ListAdapter movieAdapter;
    private String type = "live";
    private IControl control;
    private LiveEntity mLiveEntity;
    private List<LiveTypeEntity> liveTypeEntities;
    private List<LiveEntity> list;
    private LiveTypeEntity mTypeEntity;
    private boolean musicPlay=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSLSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new TrustAllHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScreenManager.setStatusBarColor(getWindow(), getResources().getColor(R.color.color2));
        ButterKnife.bind(this);
        application= (Application) baseApplication;
        try {
            if(application.playMusic!=null&&application.playMusic.isPlay()){
                musicPlay=true;
                application.playMusic.pause();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        playView.setScreenListener(this);
        playLiveProgramme.setOnClickListener(this);
        playLiveRelated.setOnClickListener(this);
        playTeleplayIv.setOnClickListener(this);
        playTeleplayDetails.setOnClickListener(this);
        playHotIv.setOnClickListener(this);
        playMovieIv.setOnClickListener(this);
        IntentFilter intent = new IntentFilter();
        intent.addAction("play.activity.url");
        registerReceiver(receiver, intent);
//        playView.play("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
//        playView.play("tvbus://1tTzQsd1BtHiUekzgKfBZAmWTutYusa8hEikfqLm2YjGYsuRqX");
//        playView.play("rtmp://live.hkstv.hk.lxdns.com/live/hks1");
        playView.play("p2p://sdgdklwos.finetv.tv:9906/5a32174e0001f481d4b0ec8f67f06c2b");
//        playView.play("tvbus://12MGYHm2fp2wtTjiGpDsbD1odpShZDSn5ub2y74ok1HX5WB6wk");
        if ("live".equals(type)) {
//            createLive();
        } else if ("teleplay".equals(type)) {
            playLiveParent.setVisibility(View.GONE);
            playTeleplayParent.setVisibility(View.VISIBLE);
            playHotParent.setVisibility(View.GONE);
            playMovieParent.setVisibility(View.GONE);
            createTeleplay();
            playTeleplayIv.setTag(true);
        } else if ("hot".equals(type)) {
            playLiveParent.setVisibility(View.GONE);
            playTeleplayParent.setVisibility(View.GONE);
            playHotParent.setVisibility(View.VISIBLE);
            playMovieParent.setVisibility(View.GONE);
            playHotIv.setTag(true);
            playHotTitle.setText("第四届中国蒙古舞大赛优秀节目展演在呼和 浩特举行！");
            playHotName.setText("上传者用户名");
            createHot();
        } else if ("movie".equals(type)) {
            playLiveParent.setVisibility(View.GONE);
            playTeleplayParent.setVisibility(View.GONE);
            playHotParent.setVisibility(View.GONE);
            playMovieParent.setVisibility(View.VISIBLE);
            playMovieIv.setTag(true);
            createMovie();
        }
        playView.setPlayGestureListener(control);
        playView.setMediaListener(this);
    }

    private void createLive() {
        String type = getIntent().getStringExtra(PLAY_TYPE);
        String channel = getIntent().getStringExtra(PLAY_CHANNEL);
        list = ChannelProvide.getChannels(this);
        L.d(TAG, String.format("type:%s,channel:%s", type, channel));
        liveTypeEntities = ChannelProvide.getType(this);
        int lenSize = liveTypeEntities.size();
        if (lenSize > 0) {
            LiveTypeEntity liveTypeEntity=null;
            int playIndex = 0;
            if (type != null && !type.isEmpty()) {
                for (int i = 0; i < lenSize; i++) {
                    LiveTypeEntity entity = liveTypeEntities.get(i);
                    if (type.equals(entity.name)) {
                        liveTypeEntity = entity;
                    } else if (entity.select) {
                        playIndex = i;
                    }
                }
                if (liveTypeEntity == null) {
                    liveTypeEntity = liveTypeEntities.get(playIndex);
                } else {
                    LiveTypeEntity typeEntity=liveTypeEntities.get(playIndex);
                    typeEntity.select=false;
                    ChannelProvide.updateType(this, false, typeEntity);
                }
            } else {
                for (LiveTypeEntity typeEntity : liveTypeEntities) {
                    if (typeEntity.select) {
                        liveTypeEntity = typeEntity;
                        break;
                    }
                }
            }
            if(liveTypeEntity==null)
                return;
            LiveEntity entity = null;
            if (channel != null && !channel.isEmpty()) {
                int plaIndex = 0;
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    LiveEntity liveEntity = list.get(i);
                    if (channel.equals(liveEntity.name))
                        entity = liveEntity;
                    else {
                        if (liveEntity.play)
                            plaIndex = i;
                    }
                }
                if (entity == null) {
                    entity = list.get(plaIndex);
                } else {
                    LiveEntity e = list.get(plaIndex);
                    if (e.play) {
                        e.play = false;
                        ChannelProvide.updateChannel(e, this);
                    }
                }
            } else {
                List<LiveEntity> entityList = type2entities(liveTypeEntity.type);
                for (LiveEntity liveEntity : entityList) {
                    if (liveEntity.play) {
                        entity = liveEntity;
                        break;
                    }
                }
            }
            setLiveEntity(liveTypeEntity, entity);
        }
        control = new LiveControl(this);
        playLiveParent.setVisibility(View.VISIBLE);
        playTeleplayParent.setVisibility(View.GONE);
        playHotParent.setVisibility(View.GONE);
        playMovieParent.setVisibility(View.GONE);
        createProgrammeAdapter();
    }

    public List<LiveEntity> type2entities(int t) {
        List<LiveEntity> entities = new ArrayList<>();
        for (LiveEntity entity : list) {
            for (int type : entity.types)
                if (type == t && entities.indexOf(entity) < 0) {
                    entities.add(entity);
                    break;
                }
        }
        return entities;
    }

    public void play(String path) {
        playView.play(path);
    }

    public void play(LiveTypeEntity entity, LiveEntity liveEntity) {

    }

    public static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{(TrustManager) new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    public static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private void createMovie() {
        movieAdapter = new ListAdapter(playMovieRv);
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entities.add(ListEntity.entity13());
        }
        movieAdapter.addItem(entities);
    }

    private void createHot() {
        hotAdapter = new ListAdapter(playHotRv);
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entities.add(ListEntity.entity18());
        }
        hotAdapter.addItem(entities);
    }

    private void createTeleplay() {
        teleplayHotAdapter = new ListAdapter(playTeleplayHotRv);
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entities.add(ListEntity.entity13());
        }
        teleplayHotAdapter.addItem(entities);
        teleplayHotAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                startActivity(new Intent(PlayActivity.this, PlayActivity.class));
            }
        });
        teleplaySelectAdapter = new ListAdapter(playTeleplaySelectRv);
        List<ListEntity> entities1 = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            ListEntity entity = new ListEntity();
            entity.type = 17;
            entity.title = "" + i;
            entities1.add(entity);
        }
        entities1.get(2).select = true;
        teleplaySelectAdapter.addItem(entities1);
        teleplaySelectAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                for (int i = 0; i < teleplaySelectAdapter.getCount(); i++) {
                    if (i == position)
                        teleplaySelectAdapter.getItem(i).select = true;
                    else
                        teleplaySelectAdapter.getItem(i).select = false;
                }
                teleplaySelectAdapter.notifyDataSetChanged();
            }
        });
    }

    public List<LiveTypeEntity> typeEntities() {
        return liveTypeEntities;
    }

    public LiveEntity liveEntity() {
        return mLiveEntity;
    }

    public void setLiveEntity(LiveEntity entity) {
        if (mLiveEntity != null) {
            mLiveEntity.play = false;
//            ChannelProvide.updateChannel(mLiveEntity, this);
        }
        mTypeEntity.select = false;
        mLiveEntity = entity;
        mLiveEntity.play = true;
        playView.play(mLiveEntity.sourceEntities.get(mLiveEntity.playIndex).url);
    }



    public void setLiveEntity(LiveTypeEntity typeEntity, LiveEntity entity) {
        if (typeEntity == null || entity == null)
            return;
        if (mTypeEntity != null && mTypeEntity.equals(typeEntity) && mLiveEntity != null && mLiveEntity.equals(entity))
            return;
        if (mTypeEntity != null) {
            if (!mTypeEntity.equals(typeEntity)) {
                mTypeEntity.select = false;
                ChannelProvide.updateType(this, false, mTypeEntity);
                mTypeEntity = typeEntity;
                mTypeEntity.select = true;
                ChannelProvide.updateType(this, true, mTypeEntity);
            }
        } else {
            mTypeEntity = typeEntity;
            mTypeEntity.select = true;
            ChannelProvide.updateType(this, true, mTypeEntity);
        }
        if(mLiveEntity!=null){
            if(!mLiveEntity.equals(entity)){
                mLiveEntity.play=false;
                ChannelProvide.updateChannel(mLiveEntity, this);
                mLiveEntity=entity;
                mLiveEntity.play=true;
                ChannelProvide.updateChannel(mLiveEntity, this);
                playView.play(mLiveEntity.sourceEntities.get(mLiveEntity.playIndex).url);
            }
        }else{
            mLiveEntity=entity;
            mLiveEntity.play=true;
            ChannelProvide.updateChannel(mLiveEntity, this);
            playView.play(mLiveEntity.sourceEntities.get(mLiveEntity.playIndex).url);
        }
    }

    public LiveTypeEntity getTypeEntity() {
        return mTypeEntity;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (control != null && control.isShow())
            control.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_live_programme) {
            liveProgrammeOrRelated(true);
        } else if (id == R.id.play_live_related) {
            liveProgrammeOrRelated(false);
        } else if (id == R.id.play_teleplay_iv) {
            boolean tag = (boolean) playTeleplayIv.getTag();
            tag = !tag;
            playTeleplayIv.setTag(tag);
            playTeleplayIv.setImageResource(tag ? R.mipmap.collection : R.mipmap.uncollection);
            playTeleplayCollection.setText(tag ? R.string.collection : R.string.uncollection);
        } else if (id == R.id.play_teleplay_details) {
            L.d(TAG, "play_teleplay_details");
        } else if (id == R.id.play_hot_iv) {
            boolean tag = (boolean) playHotIv.getTag();
            tag = !tag;
            playHotIv.setTag(tag);
            playHotIv.setImageResource(tag ? R.mipmap.collection : R.mipmap.uncollection);
            playHotCollection.setText(tag ? R.string.collection : R.string.uncollection);
        } else if (id == R.id.play_movie_iv) {
            boolean tag = (boolean) playMovieIv.getTag();
            tag = !tag;
            playMovieIv.setTag(tag);
            playMovieIv.setImageResource(tag ? R.mipmap.collection : R.mipmap.uncollection);
            playMovieCollection.setText(tag ? R.string.collection : R.string.uncollection);
        }
    }


    private void liveProgrammeOrRelated(boolean programme) {
        playLiveProgramme.setTextColor(getResources().getColor(programme ? R.color.color1 : R.color.color15));
        playLiveRelated.setTextColor(getResources().getColor(programme ? R.color.color15 : R.color.color1));
        if (programme) {
            playLiveProgrammeTime.setVisibility(View.VISIBLE);
            playLiveProgrammeList.setVisibility(View.VISIBLE);
            playLiveProgrammeLine.setVisibility(View.VISIBLE);
            playLiveRelatedLine.setVisibility(View.GONE);
            playLiveRelatedRv.setVisibility(View.GONE);
            if (programmeTimeAdapter == null)
                createProgrammeAdapter();
        } else {
            playLiveProgrammeTime.setVisibility(View.GONE);
            playLiveProgrammeList.setVisibility(View.GONE);
            playLiveProgrammeLine.setVisibility(View.GONE);
            playLiveRelatedLine.setVisibility(View.VISIBLE);
            playLiveRelatedRv.setVisibility(View.VISIBLE);
            if (relatedAdapter == null)
                createRelatedAdapter();
        }
    }

    private void createRelatedAdapter() {
        relatedAdapter = new ListAdapter(playLiveRelatedRv);
        relatedAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                startActivity(new Intent(PlayActivity.this, PlayActivity.class));
            }
        });
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entities.add(ListEntity.entity13());
        }
        relatedAdapter.addItem(entities);
    }

    private void createProgrammeAdapter() {
        programmeTimeAdapter = new ListAdapter(playLiveProgrammeTime);
        List<ListEntity> entities = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entities.add(ListEntity.entity15());
        }
        entities.get(0).select = true;
        programmeTimeAdapter.addItem(entities);
        programmeTimeAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                for (int i = 0; i < programmeTimeAdapter.getCount(); i++) {
                    if (i == position) {
                        programmeTimeAdapter.getItem(i).select = true;
                    } else {
                        programmeTimeAdapter.getItem(i).select = false;
                    }
                    programmeTimeAdapter.notifyDataSetChanged();
                    programmeListAdapter.clean();
                    programmeListAdapter.addItem((List) programmeTimeAdapter.getItem(position).object);
                }
            }
        });
        programmeListAdapter = new ListAdapter(playLiveProgrammeList);
        programmeListAdapter.addItem((List) programmeTimeAdapter.getItem(0).object);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(playView.isFullScreen()){
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            ScreenManager.invisibleStatusBar(getWindow());
        }
    }

    public int defaultUi;

    @Override
    public void fullScreen(boolean full) {
        if (full) {
            defaultUi = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            ScreenManager.invisibleStatusBar(getWindow());
        } else {
            window.getDecorView().setSystemUiVisibility(defaultUi);
            ScreenManager.visibleStatusBar(getWindow());
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"destroy");
        playView.destroy();
        unregisterReceiver(receiver);
        if(mLiveEntity!=null)
            ChannelProvide.updateDestroyChannel(mLiveEntity,this);
        if(musicPlay)
            try {
                application.playMusic.play();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (control != null && control.isShow()) {
            control.dismiss();
            return;
        }
        if (playView != null) {
            if (playView.isFullScreen()) {
                playView.exitFullScreen();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void onError(int what, int extra) {
        if (mLiveEntity != null) {
            int dex = mLiveEntity.playIndex;
            dex++;
            if (mLiveEntity.sourceEntities.size() > dex) {
                SourceEntity sourceEntity = mLiveEntity.sourceEntities.get(mLiveEntity.playIndex);
                sourceEntity.select = false;
                mLiveEntity.playIndex = dex;
                sourceEntity = mLiveEntity.sourceEntities.get(dex);
                sourceEntity.select = true;
                ChannelProvide.updateChannel(mLiveEntity, this);
                play(sourceEntity.url);
                L.d(TAG, "切换下一个源");
            } else {
                L.d(TAG, "没有源可以切换了");
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
