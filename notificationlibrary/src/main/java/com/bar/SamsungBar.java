package com.bar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

class SamsungBar extends ABar {
    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
    private static final String[] CONTENT_PROJECTION = new String[]{"_id", "class"};

    SamsungBar(Context context) {
        super(context);
    }

    @Override
    protected void send(int count) {
        Uri mUri = Uri.parse(CONTENT_URI);
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(mUri, CONTENT_PROJECTION, "package=?", new String[]{mComponentName.getPackageName()}, null);
            if (cursor != null) {
                String entryActivityName = mComponentName.getClassName();
                boolean entryActivityExist = false;
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    ContentValues contentValues = getContentValues(count, false);
                    contentResolver.update(mUri, contentValues, "_id=?", new String[]{String.valueOf(id)});
                    if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                        entryActivityExist = true;
                    }
                }

                if (!entryActivityExist) {
                    ContentValues contentValues = getContentValues(count, true);
                    contentResolver.insert(mUri, contentValues);
                }
            }
        } finally {
            CloseHelper.close(cursor);
        }
    }

    private ContentValues getContentValues(int badgeCount, boolean isInsert) {
        ContentValues contentValues = new ContentValues();
        if (isInsert) {
            contentValues.put("package", mComponentName.getPackageName());
            contentValues.put("class", mComponentName.getClassName());
        }

        contentValues.put("badgecount", badgeCount);

        return contentValues;
    }
}
