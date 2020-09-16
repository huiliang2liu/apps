package com.plug.load;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.plug.entities.ApkEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LoadImpl implements ILoadApk {
    private final static String TAG = "LoadImpl";
    private final static String SO = "so";
    private final static String[] APK_END = {".zip", ".apk"};
    private final static String[] SO_END = {".so"};
    private final static String[] DEX_END = {".dex", ".jar"};
    private static final String CPU_ARMEABI = "armeabi";
    private static final String CPU_X86 = "x86";
    private static final String CPU_MIPS = "mips";
    private static final String PREFERENCE_NAME = "dynamic_load_configs";
    private File optimizedDirectory;
    LoadApk apk;
    ILoad dex;
    ILoad so;
    Context context;

    public LoadImpl(Context context) {
        apk = new LoadApk(context);
        dex = new LoadDex(context);
        so = new LoadSo(context);
        this.context = context;
        optimizedDirectory = new File(context.getFilesDir().getParent(), SO);
        if (!optimizedDirectory.exists())
            optimizedDirectory.mkdirs();
    }

    @Override
    public boolean load(String plugPath) {
        if (plugPath == null || plugPath.isEmpty())
            return false;
        for (String apk : APK_END) {
            if (plugPath.equals(apk)) {
                this.apk.load(plugPath);
                this.dex.load(plugPath);
                List<String> soPath = compress2soPath(new File(plugPath), context);
                for (String so : soPath)
                    this.so.load(so);
                return true;
            }

        }
        for (String dex : DEX_END) {
            if (plugPath.endsWith(dex)) {
                this.dex.load(plugPath);
                return true;
            }
        }
        for (String so : SO_END) {
            if (plugPath.endsWith(so)) {
                String soPath = copySo(plugPath);
                if (soPath != null)
                    this.so.load(soPath);
                return true;
            }
        }
        return false;
    }

    @Override
    public ApkEntity name2apkEntity(String packageName) {
        return apk.name2apkEntity(packageName);
    }

    public void load(String... paths) {
        StringBuffer dexPaths = new StringBuffer();
        for (String path : paths) {
            boolean is = false;
            for (String apk : APK_END) {
                if (path.endsWith(apk)) {
                    this.apk.load(path);
                    dexPaths.append(path).append(":");
                    List<String> soPath = compress2soPath(new File(path), context);
                    for (String so : soPath)
                        this.so.load(so);
                    is = true;
                    break;
                }
            }
            if (is)
                continue;
            for (String dex : DEX_END) {
                if (path.endsWith(dex)) {
                    dexPaths.append(path).append(":");
                    is = true;
                    break;
                }
            }
            if (is)
                continue;
            for (String so : SO_END) {
                if (path.endsWith(so)) {
                    String soPath = copySo(path);
                    if (soPath != null)
                        this.so.load(soPath);
                    break;
                }
            }
        }
        if (dexPaths.length() > 1) {
            dex.load(dexPaths.substring(0, dexPaths.length() - 1));
        }
    }

    public void loadSo(String... sos) {
        if (optimizedDirectory == null || sos.length <= 0)
            return;
        for (String so : sos)
            System.load(optimizedDirectory.getAbsolutePath() + File.separator + String.format("lib%s.so", so));
    }

    private List<String> compress2soPath(File file, Context context) {
        Log.e(TAG, "复制so文件");
        String cpuArchitect = getCpuArch();
        List<String> fileName = new ArrayList<>();
        try {
            ZipInputStream zipInputStream = new ZipInputStream(
                    new FileInputStream(file));
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                if (zipEntryName.endsWith(".so")
                        && zipEntryName.contains(cpuArchitect)) {
                    final long lastModify = zipEntry.getTime();
                    if (lastModify == getSoLastModifiedTime(context,
                            zipEntryName)) {
                        continue;
                    }
                    String[] files = zipEntryName.split("/");
                    fileName.add(copySo(files[files.length - 1], zipInputStream));
                    setSoLastModifiedTime(context, zipEntryName, lastModify);
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
        Log.i(TAG, String.format("copy so file name %s", fileName));
        File os = new File(optimizedDirectory, fileName);
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
