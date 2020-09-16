package com.live.provide;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.base.util.L;
import com.io.db.TableEntity;
import com.io.provider.BaseContentProvider;
import com.io.provider.DbProvider;
import com.io.provider.IProvider;
import com.live.entities.LiveEntity;
import com.live.entities.LiveTypeEntity;
import com.live.entities.RadioEntity;
import com.live.entities.SourceEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChannelProvide extends BaseContentProvider {
    private static final String TAG = "ChannelProvide";

    protected String autohority() {
        return getContext().getPackageName() + ".channel";
    }

    @Override
    protected List<String> matchers() {
        List<String> matchers = new ArrayList<>();
        matchers.add("channel");
        matchers.add("type");
        matchers.add("radio");
        return matchers;
    }

    public static void putChannel(String c, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject jo = new JSONObject(c);
            contentValues.put("name", jo.optString("name"));
            contentValues.put("urls", jo.optString("sources"));
            contentValues.put("types", jo.optString("type"));
        } catch (Exception e) {
        }
        contentValues.put("play", 0);
        contentValues.put("playIndex", 0);
        contentValues.put("collection", 0);
        contentValues.put("playTimes", 0);
        contentValues.put("playTime", 0);
        contentResolver.insert(Uri.parse(String.format("content://%s.channel/channel", context.getPackageName())), contentValues);
        L.d(TAG, "插入频道");
    }

    public static boolean updateDestroyChannel(LiveEntity entity, Context context) {
        if (!entity.play)
            return false;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        try {
            JSONArray array = new JSONArray();
            for (int type : entity.types)
                array.put(type);
            contentValues.put("types", array.toString());
            array = new JSONArray();
            for (SourceEntity entity1 : entity.sourceEntities) {
                array.put(entity1.toString());
            }
            contentValues.put("urls", array.toString());
        } catch (Exception e) {
        }
        L.d(TAG, String.format("name:%s,play:%s", entity.name, entity.play));
        contentValues.put("name", entity.name);
        contentValues.put("play", entity.play ? 1 : 0);
        entity.playTime += System.currentTimeMillis() - entity.startTime;
        contentValues.put("playTimes", entity.playTimes);
        contentValues.put("playTime", entity.playTime);
        contentValues.put("playIndex", entity.playIndex);
        contentValues.put("collection", entity.collection ? 1 : 0);
        int c = contentResolver.update(Uri.parse(String.format("content://%s.channel/channel", context.getPackageName())), contentValues, "name = ?", new String[]{entity.name});
        return c > 0;
    }

    public static boolean updateChannel(LiveEntity entity, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        try {
            JSONArray array = new JSONArray();
            for (int type : entity.types)
                array.put(type);
            contentValues.put("types", array.toString());
            array = new JSONArray();
            for (SourceEntity entity1 : entity.sourceEntities) {
                array.put(entity1.toString());
            }
            contentValues.put("urls", array.toString());
        } catch (Exception e) {
        }
        L.d(TAG, String.format("name:%s,play:%s", entity.name, entity.play));
        contentValues.put("name", entity.name);
        contentValues.put("play", entity.play ? 1 : 0);
        if (entity.play) {
            entity.playTimes += 1;
            entity.startTime = System.currentTimeMillis();
        } else {
            entity.playTime += System.currentTimeMillis() - entity.startTime;
        }
        contentValues.put("playTimes", entity.playTimes);
        contentValues.put("playTime", entity.playTime);
        contentValues.put("playIndex", entity.playIndex);
        contentValues.put("collection", entity.collection ? 1 : 0);
        int c = contentResolver.update(Uri.parse(String.format("content://%s.channel/channel", context.getPackageName())), contentValues, "name = ?", new String[]{entity.name});
        return c > 0;
    }

    public static List<LiveEntity> getChannels(Context context, String name) {
        List<LiveEntity> entities = new ArrayList<>();
        ContentResolver contentProvider = context.getContentResolver();
        Cursor cursor = contentProvider.query(Uri.parse(String.format("content://%s.channel/channel", context.getPackageName())), null, String.format("name LIKE %s%s%s", "'%", name, "%'"), null, "playTime DESC,playTimes DESC");
        Log.e(TAG, "search size " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LiveEntity entity = cursor2entity(cursor);
            if (entity != null)
                entities.add(entity);
            cursor.moveToNext();
        }
        return entities;
    }
    public static List<LiveEntity> getHistoryChannels(Context context){
        List<LiveEntity> entities = new ArrayList<>();
        ContentResolver contentProvider = context.getContentResolver();
        Cursor cursor = contentProvider.query(Uri.parse(String.format("content://%s.channel/channel", context.getPackageName())), null, "playTimes > 0", null, "playTime DESC,playTimes DESC");
        Log.e(TAG, "search size " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LiveEntity entity = cursor2entity(cursor);
            if (entity != null)
                entities.add(entity);
            cursor.moveToNext();
        }
        return entities;
    }

    public static List<LiveEntity> getChannels(Context context) {
        List<LiveEntity> entities = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse(String.format("content://%s.channel/channel", context.getPackageName())), null, null, null, "playTime DESC,playTimes DESC");
        Log.e(TAG, "search size " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LiveEntity entity = cursor2entity(cursor);
            if (entity != null)
                entities.add(entity);
            cursor.moveToNext();
        }
        cursor.close();
        return entities;
    }

    private static LiveEntity cursor2entity(Cursor cursor) {
        try {
            int name = cursor.getColumnIndex("name");
            int urls = cursor.getColumnIndex("urls");
            int types = cursor.getColumnIndex("types");
            int collection = cursor.getColumnIndex("collection");
            int play = cursor.getColumnIndex("play");
            int playIndex = cursor.getColumnIndex("playIndex");
            int playTimes = cursor.getColumnIndex("playTimes");
            int playTime = cursor.getColumnIndex("playTime");
            LiveEntity entity = new LiveEntity();
            entity.name = cursor.getString(name);
            entity.play = cursor.getInt(play) == 1;
            entity.playIndex = cursor.getInt(playIndex);
            entity.playTimes = cursor.getInt(playTimes);
            entity.playTime = cursor.getLong(playTime);
            entity.collection = cursor.getInt(collection) == 1;
            List<SourceEntity> sourceEntities = new ArrayList<>();
            JSONArray array = new JSONArray(cursor.getString(urls));
            for (int i = 0; i < array.length(); i++) {
                SourceEntity sourceEntity = SourceEntity.json2entity(array.getString(i));
                if (sourceEntity == null)
                    continue;
                sourceEntities.add(sourceEntity);
            }
            sourceEntities.get(entity.playIndex).select = true;
            entity.sourceEntities = sourceEntities;
            array = new JSONArray(cursor.getString(types));
            List<Integer> typeList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++)
                typeList.add(array.getInt(i));
            entity.types = typeList;
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void putType(String c, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            JSONArray jsonArray = new JSONArray(c);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                int type = jsonObject.getInt("type");
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                contentValues.put("type", type);
                contentValues.put("play", 0);
                contentValues.put("playTimes", 0);
                contentResolver.insert(Uri.parse(String.format("content://%s.channel/type", context.getPackageName())), contentValues);
                Log.d(TAG, "插入类型成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean updateType(Context context, boolean select, LiveTypeEntity typeEntity) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", typeEntity.name);
        contentValues.put("type", typeEntity.type);
        contentValues.put("play", select ? 1 : 0);
        if (select)
            typeEntity.playTimes += 1;
        contentValues.put("playTimes", typeEntity.playTimes);
        int c = contentResolver.update(Uri.parse(String.format("content://%s.channel/type", context.getPackageName())), contentValues, "name = ?", new String[]{typeEntity.name});
        return c > 0;
    }

    public static LiveTypeEntity getType(Context context, String n) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse(String.format("content://%s.channel/type", context.getPackageName())), null, "name = ?", new String[]{n}, null);
            cursor.moveToFirst();
            LiveTypeEntity entity = cursor2type(cursor);
            cursor.close();
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<LiveTypeEntity> getType(Context context) {
        List<LiveTypeEntity> entities = new ArrayList<>();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse(String.format("content://%s.channel/type", context.getPackageName())), null, null, null, "playTimes DESC");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                entities.add(cursor2type(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    private static LiveTypeEntity cursor2type(Cursor cursor) {
        int name = cursor.getColumnIndex("name");
        int type = cursor.getColumnIndex("type");
        int play = cursor.getColumnIndex("play");
        int playTimes = cursor.getColumnIndex("playTimes");
        LiveTypeEntity entity = new LiveTypeEntity();
        entity.name = cursor.getString(name);
        entity.type = cursor.getInt(type);
        entity.select = cursor.getInt(play) == 1;
        entity.playTimes = cursor.getInt(playTimes);
        return entity;
    }

    public static void putRadio(String c, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            JSONObject jsonObject = new JSONObject(c);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String urls = jsonObject.getString(key);
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", key);
                contentValues.put("urls", urls);
                contentValues.put("play", 0);
                contentValues.put("playIndex", 0);
                contentValues.put("playTimes", 0);
                contentResolver.insert(Uri.parse(String.format("content://%s.channel/radio", context.getPackageName())), contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean updateRadio(Context context, RadioEntity typeEntity) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        JSONArray jsonArray = new JSONArray();
        for (String s : typeEntity.strings)
            jsonArray.put(s);
        contentValues.put("name", typeEntity.name);
        contentValues.put("urls", jsonArray.toString());
        contentValues.put("play", typeEntity.play ? 1 : 0);
        if (typeEntity.play)
            typeEntity.playTimes++;
        contentValues.put("playTimes", typeEntity.playTimes);
        contentValues.put("playIndex", typeEntity.playIndex);
        int c = contentResolver.update(Uri.parse(String.format("content://%s.channel/radio", context.getPackageName())), contentValues, "name = ?", new String[]{typeEntity.name});
        return c > 0;
    }

    public static List<RadioEntity> getHistoryRadios(Context context){
        List<RadioEntity> entities = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse(String.format("content://%s.channel/radio", context.getPackageName())), null, "playTimes > 0", null, "playTimes DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RadioEntity entity = cursor2radio(cursor);
            if (entity != null)
                entities.add(entity);
            cursor.moveToNext();
        }
        cursor.close();
        return entities;
    }

    public static List<RadioEntity> getRadios(Context context, String name) {
        List<RadioEntity> entities = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse(String.format("content://%s.channel/radio", context.getPackageName())), null, String.format("name LIKE %s%s%s", "'%", name, "%'"), null, "playTimes DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RadioEntity entity = cursor2radio(cursor);
            if (entity != null)
                entities.add(entity);
            cursor.moveToNext();
        }
        cursor.close();
        return entities;
    }

    public static List<RadioEntity> getRadios(Context context) {
        List<RadioEntity> entities = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse(String.format("content://%s.channel/radio", context.getPackageName())), null, null, null, "playTimes DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RadioEntity entity = cursor2radio(cursor);
            if (entity != null)
                entities.add(entity);
            cursor.moveToNext();
        }
        cursor.close();
        return entities;
    }

    private static RadioEntity cursor2radio(Cursor cursor) {
        try {
            int name = cursor.getColumnIndex("name");
            int urls = cursor.getColumnIndex("urls");
            int play = cursor.getColumnIndex("play");
            int playIndex = cursor.getColumnIndex("playIndex");
            int playTimes = cursor.getColumnIndex("playTimes");
            RadioEntity entity = new RadioEntity();
            entity.name = cursor.getString(name);
            entity.play = cursor.getInt(play) == 1;
            entity.playIndex = cursor.getInt(playIndex);
            entity.playTimes = cursor.getInt(playTimes);
            JSONArray array = new JSONArray(cursor.getString(urls));
            List<String> myUrls = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                myUrls.add(array.getString(i));
            }
            entity.strings = myUrls;
            return entity;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected IProvider provider() {
        return new DbProvider(getContext(), "channel") {
            {
                List<TableEntity> entities = new ArrayList<>();
                entities.add(new TableEntity().setName("id").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.PRIMARY_KEY));
                entities.add(new TableEntity().setName("urls").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.NOT_NULL).setLength(Integer.MAX_VALUE));
                entities.add(new TableEntity().setName("types").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.NOT_NULL).setLength(Integer.MAX_VALUE));
                entities.add(new TableEntity().setName("name").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.UNIQUE));
                entities.add(new TableEntity().setName("collection").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("play").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("playIndex").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("playTimes").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("playTime").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                createTable("channel", entities);
            }

            {
                List<TableEntity> entities = new ArrayList<>();
                entities.add(new TableEntity().setName("id").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.PRIMARY_KEY));
                entities.add(new TableEntity().setName("type").setType(TableEntity.KeyType.INTEGER));
                entities.add(new TableEntity().setName("name").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.UNIQUE));
                entities.add(new TableEntity().setName("play").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("playTimes").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                createTable("type", entities);
            }

            {
                List<TableEntity> entities = new ArrayList<>();
                entities.add(new TableEntity().setName("id").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.PRIMARY_KEY));
                entities.add(new TableEntity().setName("urls").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.NOT_NULL).setLength(Integer.MAX_VALUE));
                entities.add(new TableEntity().setName("name").setType(TableEntity.KeyType.STRING).add(TableEntity.Type.UNIQUE));
                entities.add(new TableEntity().setName("play").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("playIndex").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                entities.add(new TableEntity().setName("playTimes").setType(TableEntity.KeyType.INTEGER).add(TableEntity.Type.NOT_NULL));
                createTable("radio", entities);
            }
        };
    }
}
