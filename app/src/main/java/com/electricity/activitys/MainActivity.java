package com.electricity.activitys;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.base.BaseActivity;
import com.base.adapter.PagerAdapter;
import com.base.adapter.tag.ViewHolder;
import com.base.util.IntentUtil;
import com.base.widget.Decoration;
import com.electricity.ElectricityApplication;
import com.electricity.FloatService;
import com.electricity.R;
import com.electricity.adapter.MainAdapter;
import com.electricity.adapter.holder.MainHolder;
import com.electricity.widget.StudyLayoutManage;
import com.http.down.DownListener;
import com.io.db.SortEntity;
import com.io.db.TableEntity;
import com.io.db.WhereEntity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private ElectricityApplication application;

    private List<String> list = new ArrayList<>();
    private PagerAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        downFile.down("http://down.xiaoweizhibo.com/xw/upload/201911/12/23/67797d1d91a88d758329c9ed26635557.apk", new File(getCacheDir() + "test/test.apk"), new DownListener() {
            @Override
            public void downed(String url, File file) {
                installNormal(file);
            }

            @Override
            public void downFailure(String url, File file) {
                if (file.isFile())
                    file.delete();
            }
        });
//        http.
//        initData();
    }

    public boolean installNormal(File filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT > 23) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                Class c = Class.forName("android.support.v4.content.FileProvider");
                Method getUriForFile = c.getDeclaredMethod("getUriForFile", new Class[]{Context.class, String.class, File.class});
                if (!getUriForFile.isAccessible())
                    getUriForFile.setAccessible(true);
                Uri uri = (Uri) getUriForFile.invoke(null, this, getPackageName() + ".com.io.FileProvider", filePath);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else
                intent.setDataAndType(Uri.parse("file://" + filePath.getAbsolutePath()), "application/vnd.android.package-archive");
            startActivity(intent);
            return true;
        } catch (Throwable e) {
        }
        return false;
    }
    private void initView() {
        application = (ElectricityApplication) getApplication();
//        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        Decoration decoration = new Decoration(this, Decoration.Orientation.GRID);
//        decoration.setDrawable(new ColorDrawable(Color.RED));
//        recyclerView.addItemDecoration(decoration);
//        recyclerView.setLayoutManager(new StudyLayoutManage());
        ViewPager viewPager=findViewById(R.id.main_vp);
        adapter=new PagerAdapter(viewPager) {
            @Override
            public ViewHolder getViewHolder(int itemType) {
                return new MainHolder();
            }

            @Override
            public int getView(int itemType) {
                return 0;
            }
        };
//        adapter = new MainAdapter(recyclerView);
//        recyclerView.setAdapter(adapter);
        Log.e(TAG, getWindow().getClass().getName());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        startService(new Intent(this, FloatService.class));
//        new SystemDialog(getApplicationContext()).show();
        try {
            Method method = Toast.class.getDeclaredMethod("getService");
            method.setAccessible(true);
            Object o = method.invoke(null);
            Log.e(TAG, o.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add1(View view) {
        TableEntity entity = new TableEntity();
        entity.setName("head");
        entity.setType(TableEntity.KeyType.STRING);
        application.manager.alterAdd("test", entity);
    }

    public void add2(View view) {
        Map<String, Object> objects = new HashMap<>();
        objects.put("name", "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        objects.put("head", "===============");
        application.manager.insert("test", objects);
    }

    public void select(View view) {
        List<SortEntity> entities = new ArrayList<>();
        entities.add(new SortEntity("id", SortEntity.SortType.DESC));
        List<WhereEntity> whereEntities = new ArrayList<>();
//        whereEntities.add(new WhereEntity("id",7, WhereEntity.BetweenType.EQUALS));
        whereEntities.add(new WhereEntity("id", 9, WhereEntity.BetweenType.GREATER));
//        whereEntities.add(new WhereEntity("id",11, WhereEntity.BetweenType.EQUALS));
        Cursor cursor = application.manager.select("test", "", "", "");
        Log.e(TAG, cursor.getClass().getName());
        cursor.moveToFirst();
        adapter.clean();
        while (!cursor.isAfterLast()) {
            int name = cursor.getColumnIndex("name");
            int id = cursor.getColumnIndex("head");
            Log.e(TAG, "id=" + cursor.getString(id));
            adapter.addItem(cursor.getString(name));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void delete(View view) {
        List<WhereEntity> params = new ArrayList<>();
        params.add(new WhereEntity("id", 4, WhereEntity.BetweenType.EQUALS));
        params.add(new WhereEntity("id", 5, WhereEntity.BetweenType.EQUALS));
        params.add(new WhereEntity("id", 6, WhereEntity.BetweenType.EQUALS));
        application.manager.deleteOr("test", params);
    }

    public void update(View view) {
        application.manager.update("test", "name='https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg'", "id=1 or id=2");
    }

    private void initData() {
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1451330793,2242997567&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2363037083,4182949652&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1451330793,2242997567&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2363037083,4182949652&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1451330793,2242997567&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2363037083,4182949652&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1451330793,2242997567&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2363037083,4182949652&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=234634259,4236876085&fm=27&gp=0.jpg");
        list.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1451330793,2242997567&fm=27&gp=0.jpg");
        list.add("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2873269578,797009742&fm=27&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2363037083,4182949652&fm=27&gp=0.jpg");
        adapter.addItem(list);
    }


}

