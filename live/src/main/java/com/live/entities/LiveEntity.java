package com.live.entities;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class LiveEntity {
    public String name;
    public boolean collection;
    public int playIndex;
    public boolean play;
    public List<Integer> types;
    public int playTimes;
    public long playTime;
    public long startTime;
    public List<SourceEntity> sourceEntities;

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof LiveEntity))
            return false;
        LiveEntity entity = (LiveEntity) obj;
        return name.equals(entity.name);
    }

    public static LiveEntity json2entity(String s) {
        try {
            LiveEntity entity = new LiveEntity();
            JSONObject jsonObject = new JSONObject(s);
            entity.name = jsonObject.getString("name");
            entity.sourceEntities = new ArrayList<>();
            JSONArray array = jsonObject.getJSONArray("type");
            entity.types = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++)
                entity.types.add(array.getInt(i));
            array = jsonObject.getJSONArray("sources");
            for (int i = 0; i < array.length(); i++) {
                SourceEntity sourceEntity = SourceEntity.json2entity(array.getString(i));
                if (sourceEntity == null)
                    continue;
                entity.sourceEntities.add(sourceEntity);
            }
            if (entity.sourceEntities.size() <= 0)
                return null;
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
