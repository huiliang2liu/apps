package com.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DbManager {
    private static final String TAG = "DbManager";
    private SQLiteDatabase sqLiteDatabase;

    public DbManager(Context context, String db) {
        sqLiteDatabase = context.openOrCreateDatabase(String.format("%s.db", db), Context.MODE_PRIVATE, null);
    }

    public boolean createTable(String name, List<TableEntity> entities) {
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

    public boolean dropTable(String name) {
        try {
            sqLiteDatabase.execSQL(String.format(Constants.DROP_TABLE, name));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean alterAdd(String name, TableEntity entity) {
        try {
            sqLiteDatabase.execSQL(String.format(Constants.ALTER_ADD, name, entity.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean alterDrop(String name, String indeName) {
        try {
            sqLiteDatabase.execSQL(String.format(Constants.ALTER_DROP, name, indeName));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean alterAlter(String name, TableEntity entity) {
        try {
            sqLiteDatabase.execSQL(String.format(Constants.ALTER_ALTER, name, entity.toString()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int insert(String name, List<Object> params) {
        if (params == null || params.size() <= 0)
            return 0;
        StringBuffer sb = new StringBuffer();
        int siez = params.size();
        for (int i = 0; i < siez; i++) {
            Object o = params.get(i);
            if (o instanceof String) {
                sb.append("'").append(params.get(i)).append("'");
            } else
                sb.append(params.get(i));
            if (i != siez - 1)
                sb.append(",");
        }
        String insert = String.format(Constants.INSERT1, name, sb.toString());
        Log.e(TAG, insert);
        try {
            sqLiteDatabase.execSQL(insert);
        } catch (SQLException e) {
            return 0;
        }
        return 1;
    }

    public int insert(String name, Map<String, Object> params) {
        if (params == null || params.size() <= 0)
            return 0;
        StringBuffer sbKey = new StringBuffer();
        StringBuffer sbValue = new StringBuffer();
        int size = params.size();
        int i = 0;
        for (String key : params.keySet()) {
            sbKey.append(key);
            Object o = params.get(key);
            if (o instanceof String) {
                sbValue.append("'").append(o).append("'");
            } else
                sbValue.append(o);
            i++;
            if (i != size) {
                sbKey.append(",");
                sbValue.append(",");
            }
        }
        String insert = String.format(Constants.INSERT2, name, sbKey.toString(), sbValue.toString());
        Log.e(TAG, insert);
        try {
            sqLiteDatabase.execSQL(insert);
        } catch (SQLException e) {
            return 0;
        }
        return 1;
    }

    public int delete(String name) {
        return sqLiteDatabase.delete(name, null, null);
    }

    public boolean delete(String name, String where) {
        String deleteOr = String.format(Constants.DELETE_WHERE, name, where);
        Log.d(TAG, deleteOr);
        try {
            sqLiteDatabase.execSQL(deleteOr);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteOr(String name, List<WhereEntity> params) {
        return delete(name, or(params));
    }

    public boolean deleteAnd(String name, List<WhereEntity> params) {
        return delete(name, and(params));
    }


    public boolean updateOr(String name, List<UpdateEntity> params, List<WhereEntity> entities) {
        return update(name, update(params), or(entities));
    }

    public boolean updateAnd(String name, List<UpdateEntity> params, List<WhereEntity> entities) {
        return update(name, update(params), and(entities));
    }

    public boolean updateOr(String name, String update, List<WhereEntity> entities) {
        return update(name, update, or(entities));
    }

    public boolean updateAnd(String name, String update, List<WhereEntity> entities) {
        return update(name, update, and(entities));
    }

    public boolean update(String name, List<UpdateEntity> params, String where) {
        return update(name, update(params), where);
    }

    public boolean update(String name, String params, String where) {
        if (name == null || name.isEmpty())
            return false;
        if (params == null || params.isEmpty())
            return false;
        if (where == null || where.isEmpty())
            return false;
        String update = String.format(Constants.UPDATE, name, params, where);
        Log.e(TAG, update);
        try {
            sqLiteDatabase.execSQL(update);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Cursor selectOr(String name, List<String> likes, List<WhereEntity> where, List<SortEntity> sortEntities) {
        return select(name, like(likes), or(where), order(sortEntities));
    }

    public Cursor selectAnd(String name, List<String> likes, List<WhereEntity> where, List<SortEntity> sortEntities) {
        return select(name, like(likes), and(where), order(sortEntities));
    }


    public Cursor select(String name, List<String> likes, String where, List<SortEntity> sortEntities) {
        return select(name, like(likes), where, order(sortEntities));
    }

    public Cursor selectOr(String name, List<String> likes, List<WhereEntity> where, String order) {
        return select(name, like(likes), or(where), order);
    }

    public Cursor selectAnd(String name, List<String> likes, List<WhereEntity> where, String order) {
        return select(name, like(likes), and(where), order);
    }

    public Cursor selectOr(String name, String like, List<WhereEntity> where, List<SortEntity> sortEntities) {
        return select(name, like, or(where), order(sortEntities));
    }

    public Cursor selectAnd(String name, String like, List<WhereEntity> where, List<SortEntity> sortEntities) {
        return select(name, like, and(where), order(sortEntities));
    }


    public Cursor select(String name, List<String> likes, String where, String order) {
        return select(name, like(likes), where, order);
    }

    public Cursor selectOr(String name, String like, List<WhereEntity> where, String order) {
        return select(name, like, or(where), order);
    }

    public Cursor selectAnd(String name, String like, List<WhereEntity> where, String order) {
        return select(name, like, and(where), order);
    }

    public Cursor select(String name, String like, String where, List<SortEntity> sortEntities) {
        return select(name, like, where, order(sortEntities));
    }

    public Cursor select(String name, String like, String where, String order) {
        if (name == null || name.isEmpty())
            return null;
        if (like == null || like.isEmpty())
            like = "*";
        Cursor cursor;
        if ((where == null || where.isEmpty()) && (order == null || order.isEmpty())) {
            cursor = sqLiteDatabase.rawQuery(String.format(Constants.SELECT, like, name), null);
        } else {
            String select = null;
            if (where != null && !where.isEmpty()) {
                if (order == null || order.isEmpty()) {
                    select = String.format(Constants.SELECT_WHERE, like, name, where, order);
                } else {
                    select = String.format(Constants.SELECT_WHERE_ORDER, like, name, where, order);
                }
            } else {
                select = String.format(Constants.SELECT_ORDER, like, name, order);
            }
            Log.e(TAG, select);
            cursor = sqLiteDatabase.rawQuery(select, null);
        }
        Log.e(TAG, cursor.getCount() + "");
        return cursor;
    }

    private String update(List<UpdateEntity> update) {
        if (update == null || update.size() <= 0)
            return null;
        StringBuffer sb = new StringBuffer();
        int size = update.size() - 1;
        for (int i = 0; i <= size; i++) {
            sb.append(update.get(i).toString());
            if (i < size)
                sb.append(",");
        }
        return sb.toString();
    }

    private String like(List<String> likes) {
        if (likes == null || likes.size() <= 0)
            return "*";
        StringBuffer sb = new StringBuffer();
        int size = likes.size() - 1;
        for (int i = 0; i <= size; i++) {
            sb.append(likes.get(i));
            if (i < size)
                sb.append(",");
        }
        return sb.toString();
    }

    private String order(List<SortEntity> entities) {
        if (entities == null || entities.size() <= 0)
            return null;
        StringBuffer sb = new StringBuffer();
        int size = entities.size() - 1;
        for (int i = 0; i <= size; i++) {
            sb.append(entities.get(i).toString());
            if (i < size)
                sb.append(",");
        }
        return sb.toString();
    }

    private String or(List<WhereEntity> entities) {
        if (entities == null || entities.size() <= 0)
            return null;
        StringBuffer sb = new StringBuffer();
        int size = entities.size() - 1;
        for (int i = 0; i <= size; i++) {
            sb.append(entities.get(i).toString());
            if (i < size)
                sb.append(" or ");
        }
        return sb.toString();
    }

    private String and(List<WhereEntity> entities) {
        if (entities == null || entities.size() <= 0)
            return null;
        StringBuffer sb = new StringBuffer();
        int size = entities.size() - 1;
        for (int i = 0; i <= size; i++) {
            sb.append(entities.get(i).toString());
            if (i < size)
                sb.append(" and ");
        }
        return sb.toString();
    }


}
