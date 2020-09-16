package com.io.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import java.util.List;

public abstract class BaseContentProvider extends ContentProvider {
    private static final String TAG="BaseContentProvider";
    private UriMatcher matcher;
    private List<String> matchers;
    private IProvider provider;

    @Override
    public final boolean onCreate() {
        matchers = matchers();
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        for (int i = 0; i < matchers.size(); i++) {
            matcher.addURI(autohority(), matchers.get(i), i);
        }
        provider = provider();
        Log.e(TAG,"onCreate");
        return true;
    }

    protected abstract String autohority();

    protected abstract List<String> matchers();

    protected abstract IProvider provider();


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return provider.query(matcher(uri), projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return provider.getType(matcher(uri));
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        provider.insert(matcher(uri), values);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return provider.delete(matcher(uri), selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return provider.update(matcher(uri), values, selection, selectionArgs);
    }

    private String matcher(Uri uri) {
        int index = matcher.match(uri);
        return matchers.get(index);
    }
}
