package com.io.sava;


import android.content.Context;
import android.content.ContextWrapper;
import androidx.annotation.Nullable;


import com.io.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * com.sava
 * 2018/9/28 12:19
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class SharedPreferences implements android.content.SharedPreferences, android.content.SharedPreferences.Editor {
    private volatile static Class cls;
    private volatile static Constructor<? extends android.content.SharedPreferences> constructor;
    private static Map<File, android.content.SharedPreferences> preferencesMap;
    private Editor editor;

    static {
        try {
            cls = Class.forName("android.app.SharedPreferencesImpl");
            constructor = cls.getDeclaredConstructor(File.class, int.class);
            if (!constructor.isAccessible())
                constructor.setAccessible(true);
        } catch (Exception e) {

        }
    }
    public static android.content.SharedPreferences preferences(File file,int mode){
        try {
           Class cls = Class.forName("android.app.SharedPreferencesImpl");
           Constructor<? extends android.content.SharedPreferences>constructor = cls.getDeclaredConstructor(File.class, int.class);
            if (!constructor.isAccessible())
                constructor.setAccessible(true);
            return  constructor.newInstance(file,mode);
        } catch (Exception e) {
            e.printStackTrace();
         return null;
        }
    }
    private android.content.SharedPreferences preferences;

    public SharedPreferences(String file, int mode) {
        this(new File(file), mode);
    }

    public SharedPreferences(String file) {
        this(new File(file), Context.MODE_PRIVATE);
    }

    public SharedPreferences(Context context, String file, int mode) {
        if (context == null)
            throw new NullPointerException("context is null");
        preferences = context.getSharedPreferences(file, mode);
    }

    public SharedPreferences(Context context, String file) {
        this(context, file, Context.MODE_PRIVATE);
    }

    public SharedPreferences(File file, int mode) {
        if (file == null)
            throw new NullPointerException("file is null");
        synchronized (SharedPreferences.class) {
            if (preferencesMap != null && preferencesMap.containsKey(file)) {
                preferences = preferencesMap.get(file);
                return;
            }
            File parant = file.getParentFile();
            if (!parant.exists())
                parant.mkdirs();
            try {
                preferences = constructor.newInstance(file, mode);
                if (preferencesMap == null)
                    preferencesMap = new HashMap<>();
                preferencesMap.put(file, preferences);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public SharedPreferences(File file) {
        this(file, Context.MODE_PRIVATE);
    }

    @Override
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return preferences.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return preferences.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return preferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public Object getSerializable(String key, Serializable defValue) {
        String value = getString(key, "");
        Object serializable = null;
        try {
            InputStream is = StreamUtil.string2stream(value);
            ObjectInputStream ois = new ObjectInputStream(is);
            serializable = ois.readObject();
            StreamUtil.close(ois);
            StreamUtil.close(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serializable;
    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @Override
    public Editor edit() {
        return preferences.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public synchronized Editor putString(String key, @Nullable String value) {
        if (editor == null)
            editor = edit();
        return editor.putString(key, value);
    }

    @Override
    public synchronized Editor putStringSet(String key, @Nullable Set<String> values) {
        if (editor == null)
            editor = edit();
        return editor.putStringSet(key, values);
    }

    @Override
    public synchronized Editor putInt(String key, int value) {
        if (editor == null)
            editor = edit();
        return editor.putInt(key, value);
    }

    @Override
    public Editor putLong(String key, long value) {
        if (editor == null)
            editor = edit();
        return editor.putLong(key, value);
    }

    @Override
    public Editor putFloat(String key, float value) {
        if (editor == null)
            editor = edit();
        return editor.putFloat(key, value);
    }

    public synchronized Editor putSerializable(String key, Serializable serializable) {
        Editor ed = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(serializable);
            ed = putString(key, StreamUtil.byte2string(bos.toByteArray()));
            StreamUtil.close(oos);
            StreamUtil.close(bos);
        } catch (Exception e) {
            e.printStackTrace();
            if (editor == null)
                editor = edit();
            ed = editor;
        }
        return ed;
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        if (editor == null)
            editor = edit();
        return editor.putBoolean(key, value);
    }

    @Override
    public Editor remove(String key) {
        if (editor != null)
            return editor.remove(key);
        return null;
    }

    @Override
    public Editor clear() {
        if (editor != null)
            editor.clear();
        return null;
    }

    @Override
    public boolean commit() {
        if (editor != null)
            return editor.commit();
        return false;
    }

    @Override
    public void apply() {
        if (editor != null)
            editor.apply();
    }

    /**
     * 修改sp的保存路径
     *
     * @param context
     * @param filePath
     * @return boolean
     */
    public static boolean setSharedPrefercencesSavaPath(Context context, String filePath) {
        try {
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            Field field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            Object obj = field.get(context);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = new File(filePath);
            // 修改mPreferencesDir变量的值
            field.set(obj, file);
            return true;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
}
