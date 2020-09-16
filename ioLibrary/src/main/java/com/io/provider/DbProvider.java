package com.io.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.io.db.Constants;
import com.io.db.TableEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DbProvider implements IProvider {
    private static final String TAG = "DbProvider";
    private SQLiteDatabase sqLiteDatabase;

    /**
     * description：打开或创建数据库
     *
     * @param context
     * @param dbName
     */
    public DbProvider(Context context, String dbName) {
        sqLiteDatabase = context.openOrCreateDatabase(String.format("%s.db", dbName), Context.MODE_PRIVATE, null);
    }

    /**
     * description：更具数据库文件打开数据库
     * @param context
     * @param dbFile
     */
    public DbProvider(Context context, File dbFile) {
        sqLiteDatabase = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
    }

    public final boolean createTable(String name, List<TableEntity> entities) {
        if (entities == null || entities.size() <= 0)
            return false;
        StringBuffer sb = new StringBuffer();
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            sb.append(entities.get(i).toString());
            if (i != size - 1)
                sb.append(",");
        }
        String create = String.format(Constants.CREATE_TABLE, name, sb.toString());
        Log.e(TAG, create);
        try {
            sqLiteDatabase.execSQL(create);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public final Cursor query(String matcher, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sqLiteDatabase.query(matcher, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public final String getType(String matcher) {
        return "db";
    }

    @Override
    public long insert(String matcher, ContentValues values) {
        return sqLiteDatabase.insert(matcher, null, values);
    }

    @Override
    public int delete(String matcher, String selection, String[] selectionArgs) {
        return sqLiteDatabase.delete(matcher, selection, selectionArgs);
    }

    @Override
    public int update(String matcher, ContentValues values, String selection, String[] selectionArgs) {
        return sqLiteDatabase.update(matcher, values, selection, selectionArgs);
    }
}
