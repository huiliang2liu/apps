package com.t.dbtext;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;



public class MainActivity extends FragmentActivity {
    private static final Uri STUDENT_ALL_URI = Uri.parse("content://com.t.dbtext/student");
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", "刘慧良");
                Log.e(TAG, "insert size " + contentResolver.insert(STUDENT_ALL_URI, contentValues));
            }
        });
        findViewById(R.id.main_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(STUDENT_ALL_URI, null, null, null, null);
                Log.e(TAG, "search size " + cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int name = cursor.getColumnIndex("name");
                    int id = cursor.getColumnIndex("id");
                    Log.e(TAG, "name=" + cursor.getString(name));
                    cursor.moveToNext();
                }
            }
        });


    }

    private String imei() {
        try {
            TelephonyManager telecomManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String imei = telecomManager.getDeviceId();
            if (imei == null)
                imei = "";
            Log.d(TAG,imei);
            return imei;
        } catch (Exception e) {
            Log.d(TAG,"error",e);
        }
        return "";
    }
}
