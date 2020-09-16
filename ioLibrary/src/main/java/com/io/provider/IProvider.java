package com.io.provider;

import android.content.ContentValues;
import android.database.Cursor;

public interface IProvider {
    Cursor query(String matcher, String[] projection, String selection, String[] selectionArgs, String sortOrder);

    String getType(String matcher);

    long insert(String matcher, ContentValues values);

    int delete(String matcher, String selection, String[] selectionArgs);

    int update(String matcher, ContentValues values, String selection, String[] selectionArgs);
}
