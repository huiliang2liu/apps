package com.plug.load;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class LoadSo extends ALoad {
    private static final String TAG = "LoadSo";
    private static Field nativeLibraryDirectories;
    private Object objectNativeLibraryDirectories;

    LoadSo(Context context) {
        super(context);
        if (nativeLibraryDirectories == null) {
            try {
                nativeLibraryDirectories = objectPathList.getClass().getDeclaredField("nativeLibraryDirectories");
                if (!nativeLibraryDirectories.isAccessible())
                    nativeLibraryDirectories.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i(TAG, "not found field nativeLibraryDirectories");
            }
        }
        try {
            objectNativeLibraryDirectories = nativeLibraryDirectories.get(objectPathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean load(String plugPath) {
        if (nativeLibraryDirectories == null || objectNativeLibraryDirectories == null)
            return false;
        File file = new File(plugPath);
        if (!file.exists() || !file.isFile())
            return false;
        List<File> sos = new ArrayList<>();
        sos.add(file);
        if (objectNativeLibraryDirectories.getClass().isArray()) {
            Log.i(TAG, "array");
            for (File file1 : (File[]) objectNativeLibraryDirectories) {
                sos.add(file1);
            }
            try {
                nativeLibraryDirectories.set(objectPathList, sos.toArray(new File[sos.size()]));
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "list");
            sos.addAll((List) objectNativeLibraryDirectories);
            try {
                nativeLibraryDirectories.set(objectPathList, sos);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
