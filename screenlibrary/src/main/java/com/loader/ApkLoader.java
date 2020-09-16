package com.loader;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.BaseDexClassLoader;

class ApkLoader extends DexLoader {
    private static final String TAG = "ApkLoader";
    private static final String CPU_ARMEABI = "armeabi";
    private static final String CPU_X86 = "x86";
    private static final String CPU_MIPS = "mips";
    private static final String PREFERENCE_NAME = "dynamic_load_configs";
    private File osDirectory;
    private final static String SO = "so";
    private final static String DEX = "dex";

    public ApkLoader(String dexPath, ClassLoader parent, Context context) {
        super(dexPath, parent, context);
        osDirectory = context.getDir(SO, Context.MODE_PRIVATE);
        Log.d(TAG, osDirectory.getAbsolutePath());
        compress2soPath(new File(dexPath), context);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    protected URL findResource(String name) {
        return super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) {
        return super.findResources(name);
    }

    @Override
    public String findLibrary(String libname) {
        Log.d(TAG, libname);
        if (osDirectory.isDirectory()) {
            File[] files = osDirectory.listFiles();
            if (files != null && files.length > 0)
                for (File file : files) {
                    String path = file.getAbsolutePath();
                    if (path.endsWith(String.format("lib%s.so", libname))) {
                        return path;
                    }
                }
        }
        return null;
    }

    @Override
    protected synchronized Package getPackage(String name) {
        return super.getPackage(name);
    }

    private List<String> compress2soPath(File file, Context context) {
        Log.d(TAG, "复制so文件");
        String cpuArchitect = getCpuArch();
        Log.d(TAG, cpuArchitect);
        List<String> fileName = new ArrayList<>();
        try {
            ZipInputStream zipInputStream = new ZipInputStream(
                    new FileInputStream(file));
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                if (zipEntryName.endsWith(".so")) {
                    Log.d(TAG, zipEntryName);
                    if (zipEntryName.contains(cpuArchitect)) {
                        final long lastModify = zipEntry.getTime();
                        if (lastModify == getSoLastModifiedTime(context,
                                zipEntryName)) {
                            continue;
                        }
                        String[] files = zipEntryName.split("/");
                        fileName.add(copySo(files[files.length - 1], zipInputStream));
                        setSoLastModifiedTime(context, zipEntryName, lastModify);
                    }
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private String copySo(String file) {
        File file1 = new File(file);
        if (!file1.exists() || file1.isDirectory())
            return null;
        try {
            return copySo(file1.getName(), new FileInputStream(file1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String copySo(String fileName, InputStream is) {
        File os = new File(osDirectory, fileName);
        Log.i(TAG, String.format("copy so file name %s", fileName));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(os);
            byte[] buff = new byte[1024];
            int len = -1;
            while ((len = is.read(buff)) > 0) {
                fos.write(buff, 0, len);
            }

            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        os.setReadOnly();
        return os.getAbsolutePath();
    }

    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            br.close();
            String[] array = text.split(":\\s+", 2);
            if (array.length >= 2) {
                return array[1];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCpuArch() {
        String cpuArchitect = CPU_ARMEABI;
        String cpuName = getCpuName();
        if (cpuName.toLowerCase().contains("arm")) {
            cpuArchitect = CPU_ARMEABI;
        } else if (cpuName.toLowerCase().contains("x86")) {
            cpuArchitect = CPU_X86;
        } else if (cpuName.toLowerCase().contains("mips")) {
            cpuArchitect = CPU_MIPS;
        }
        return cpuArchitect;
    }

    public static void setSoLastModifiedTime(Context cxt, String soName,
                                             long time) {
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        prefs.edit().putLong(soName, time).apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static long getSoLastModifiedTime(Context cxt, String soName) {
        SharedPreferences prefs = cxt.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        return prefs.getLong(soName, 0);
    }
}
