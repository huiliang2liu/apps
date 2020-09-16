package com.live.entities;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

public class SourceEntity {
    public String name;
    public String url;
    public boolean select = false;

    public static SourceEntity json2entity(String key) {
        try {
            JSONObject jsonObject=new JSONObject(key);
            SourceEntity entity = new SourceEntity();
            entity.url = jsonObject.getString("url");
            entity.name = jsonObject.getString("type");
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", url);
            jsonObject.put("type", name);
            return jsonObject.toString();
        } catch (JSONException e) {
            return super.toString();
        }

    }
}
