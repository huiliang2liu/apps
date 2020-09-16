package com.io.provider;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpProvider implements IProvider {
    private Application application;
    private Map<String, SharedPreferences> map = new HashMap<>();

    public SpProvider(Context context) {
        application = (Application) context.getApplicationContext();
    }

    public void createSp(String name) {
        map.put(name, application.getSharedPreferences(name, Context.MODE_PRIVATE));
    }

    @Override
    public Cursor query(String matcher, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return new SpCursor(map.get(matcher), projection);
    }

    @Override
    public String getType(String matcher) {
        return "sp";
    }

    @Override
    public long insert(String matcher, ContentValues values) {
        return 0;
    }

    @Override
    public int delete(String matcher, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(String matcher, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private class SpCursor implements Cursor {
        List<Object> objects;
        private int position = 0;

        private SpCursor(SharedPreferences sp, String[] projection) {
            objects = new ArrayList<>();
            if (sp == null)
                return;
            if (projection != null && projection.length > 0) {
                Map<String, ?> all = sp.getAll();
                for (String s : projection)
                    objects.add(all.get(s));
            }
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public boolean move(int offset) {
            if (objects.size() > offset) {
                position = offset;
                return true;
            }
            return false;
        }

        @Override
        public boolean moveToPosition(int position) {
            if (objects.size() > position) {
                this.position = position;
                return true;
            }
            return false;
        }

        @Override
        public boolean moveToFirst() {
            position = 0;
            return true;
        }

        @Override
        public boolean moveToLast() {
            position = objects.size() - 1;
            return true;
        }

        @Override
        public boolean moveToNext() {
            if (position < objects.size() - 1) {
                position++;
                return true;
            }
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return position == 0;
        }

        @Override
        public boolean isLast() {
            return position == objects.size() - 1;
        }

        @Override
        public boolean isBeforeFirst() {
            return position == 1;
        }

        @Override
        public boolean isAfterLast() {
            return position == objects.size() - 2;
        }

        @Override
        public int getColumnIndex(String columnName) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return null;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            return new byte[0];
        }

        @Override
        public String getString(int columnIndex) {
            Object o = objects.get(position);
            if (o instanceof String)
                return (String) o;
            return null;
        }

        @Override
        public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

        }

        @Override
        public short getShort(int columnIndex) {
            Object o = objects.get(position);
            if (o instanceof Integer)
                return (Short) o;
            return Short.MIN_VALUE;
        }

        @Override
        public int getInt(int columnIndex) {
            Object o = objects.get(position);
            if (o instanceof Integer)
                return (Integer) o;
            return Integer.MIN_VALUE;
        }

        @Override
        public long getLong(int columnIndex) {
            Object o = objects.get(position);
            if (o instanceof Long)
                return (Long) o;
            return Long.MIN_VALUE;
        }

        @Override
        public float getFloat(int columnIndex) {
            Object o = objects.get(position);
            if (o instanceof Float)
                return (Float) o;
            return Float.MIN_VALUE;
        }

        @Override
        public double getDouble(int columnIndex) {
            Object o = objects.get(position);
            if (o instanceof Float)
                return (Float) o;
            return Float.MIN_VALUE;
        }

        @Override
        public int getType(int columnIndex) {
            return 0;
        }

        @Override
        public boolean isNull(int columnIndex) {
            Object o = objects.get(position);
            return o == null;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {

        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void setNotificationUri(ContentResolver cr, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public void setExtras(Bundle extras) {

        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public Bundle respond(Bundle extras) {
            return null;
        }
    }
}
