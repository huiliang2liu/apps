package com.io.sava;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.io.provider.FileProvider;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2018/7/30 10:11
 * instructions：android文件管理
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class AndroidFileUtil {
    private final static String TAG = AndroidFileUtil.class.getName();

    /**
     * 获取第三那方应用
     *
     * @param context
     * @return
     */
    public static Map<String, Apk> selectThreeApk(Context context) {
        Map<String, Map<String, Apk>> map = selectRunApk(context);
        return map == null ? null : map.get("three");
    }

    public static Map<String, Apk> selectSdcApk(Context context) {
        Map<String, Map<String, Apk>> map = selectRunApk(context);
        return map == null ? null : map.get("sdc");
    }

    /**
     * 获取系统应用
     *
     * @param context
     * @return
     */
    public static Map<String, Apk> selectSysApk(Context context) {
        Map<String, Map<String, Apk>> map = selectRunApk(context);
        return map == null ? null : map.get("system");
    }

    /**
     * 获取所有应用
     *
     * @param context
     * @return
     */
    public static Map<String, Apk> selectAllApk(Context context) {
        Map<String, Map<String, Apk>> map = selectRunApk(context);
        if (map == null)
            return null;
        Map<String, Apk> apks = map.get("three");
        if (apks == null)
            apks = map.get("system");
        else
            apks.putAll(map.get("system"));
        return apks;
    }

    private static Map<String, Map<String, Apk>> selectRunApk(Context context) {
        if (context == null)
            return null;
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        Map<String, Map<String, Apk>> map = new HashMap<String, Map<String, Apk>>();
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                Map<String, Apk> apks = map.get("system");
                Apk apk = apk(app, pm);
                if (apks == null) {
                    apks = new HashMap<String, Apk>();
                    apks.put(apk.getPackage_name(), apk);
                    map.put("system", apks);
                } else
                    apks.put(apk.getPackage_name(), apk);
            }
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                Map<String, Apk> apks = map.get("three");
                Apk apk = apk(app, pm);
                if (apks == null) {
                    apks = new HashMap<String, Apk>();
                    apks.put(apk.getPackage_name(), apk);
                    map.put("three", apks);
                } else
                    apks.put(apk.getPackage_name(), apk);
            }
            if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                Map<String, Apk> apks = map.get("three");
                Apk apk = apk(app, pm);
                if (apks == null) {
                    apks = new HashMap<String, Apk>();
                    apks.put(apk.getPackage_name(), apk);
                    map.put("three", apks);
                } else
                    apks.put(apk.getPackage_name(), apk);
                Map<String, Apk> sdk = map.get("sdc");
                if (sdk == null) {
                    sdk = new HashMap<String, Apk>();
                    sdk.put(apk.getPackage_name(), apk);
                    map.put("sdc", sdk);
                } else
                    sdk.put(apk.getPackage_name(), apk);
            }
        }
        return map;
    }

    private static Apk apk(ApplicationInfo app, PackageManager pm) {
        // TODO Auto-generated method stub
        Apk apk = new Apk();
        apk.setName(app.loadLabel(pm).toString());
        apk.setPackage_name(app.packageName);
        apk.setIco(app.loadIcon(pm));
        try {
            PackageInfo info = pm.getPackageInfo(app.packageName, 0);
            apk.setVersionCode(info.versionCode);
            apk.setVersionName(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return apk;
    }

    /**
     * 将安装包路径转换位apk
     *
     * @param file
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static Apk apkFile(String file, Context context) {
        if (file == null || file.isEmpty() || !file.endsWith(".apk"))
            return null;
        Apk apk = new Apk();
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(file,
                PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            apk.setPath(file);
            apk.setVersionName(packageInfo.versionName);
            apk.setVersionCode(packageInfo.versionCode);
            apk.setPackage_name(packageInfo.packageName);
            ApplicationInfo app = packageInfo.applicationInfo;
            if (app != null) {
                apk.setName(app.loadLabel(pm).toString());
                apk.setIco(app.loadIcon(pm));
            }
        }
        return apk;
    }

    /**
     * SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据 设置->应用->应用详情里面的 清除数据
     * xh 2017-3-13 上午9:46:25
     *
     * @param context
     * @return
     */
    public static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null);
    }

    /**
     * SDCard/Android/data/你的应用的包名/ 目录，一般放一些长时间保存的数据 设置->应用->应用详情里面的 清除数据
     * xh 2017-3-13 上午9:46:25
     *
     * @param context
     * @return
     */
    public static File getExternalDir(Context context) {
        return getExternalFilesDir(context).getParentFile();
    }

    /**
     * /mnt/sdcard/Android/data/com.my
     * <p>
     * .app/files/test xh 2017-3-13 上午10:03:38
     *
     * @param context
     * @param file_name
     * @return
     */
    public static File getExternalFilesDir(Context context, String file_name) {
        return context.getExternalFilesDir(file_name);
    }

    /**
     * SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据 设置->应用->应用详情里面的 清除缓存 xh
     * 2017-3-13 上午9:47:59
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 前者获取到的就是 /sdcard/Android/data/<application package>/cache 这个路径，而后者获取到的是
     * /data/data/<application package>/cache 这个路径 xh 2017-3-13 上午9:49:36
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获取data目录 xh 2017-3-13 上午9:52:32
     *
     * @return
     */
    public static File data() {
        return Environment.getDataDirectory();
    }

    /**
     * 获取cache目录 xh 2017-3-13 上午9:53:20
     *
     * @return
     */
    public static File cache() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * 获取mnt/sdcard目录 xh 2017-3-13 上午9:54:05
     *
     * @return
     */
    public static File mntSdcard() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取mnt/sdcard/name文件 xh 2017-3-13 上午9:55:29
     *
     * @param name
     * @return
     */
    public static File mntSdcard(String name) {
        return Environment.getExternalStoragePublicDirectory(name);
    }

    /**
     * system xh 2017-3-13 上午9:56:29
     *
     * @return
     */
    public static File system() {
        return Environment.getRootDirectory();
    }

    /**
     * /data/app/com.my
     * <p>
     * .app-1.apk xh 2017-3-13 上午9:57:28
     *
     * @param context
     * @return
     */
    public static String getPackageCodePath(Context context) {
        return context.getPackageCodePath();
    }

    /**
     * /data/app/com.my
     * <p>
     * .app-1.apk xh 2017-3-13 上午9:58:18
     *
     * @param context
     * @return
     */
    public static String getPackageResourcePath(Context context) {
        return context.getPackageResourcePath();
    }

    /**
     * /data/data/com.my
     * <p>
     * .app/cache xh 2017-3-13 上午9:59:24
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * /data/data/com.my
     * <p>
     * .app/databases/test xh 2017-3-13 上午10:00:11
     *
     * @param context
     * @param file_name
     * @return
     */
    public static File getDatabasePath(Context context, String file_name) {
        return context.getDatabasePath(file_name);
    }

    /**
     * /data/data/com.my
     * <p>
     * .app/app_test xh 2017-3-13 上午10:01:37
     *
     * @param context
     * @param file_name
     * @return
     */
    public static File getDir(Context context, String file_name) {
        return context.getDir(file_name, Context.MODE_PRIVATE);
    }

    /**
     * /data/data/com.my
     * <p>
     * .app/files xh 2017-3-13 上午10:04:51
     *
     * @param context
     * @return
     */
    public static File getFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * lhl 2017-12-25 上午10:03:27 说明：是否安装了sdcard
     *
     * @return boolean
     */
    public static boolean sdcardIs() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * file转化位url xh 2017-3-13 上午10:07:24
     *
     * @param file
     * @return
     */
    @SuppressWarnings("deprecation")
    public static URL file2url(File file) {
        try {
            return file2uri(file).toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * file转化位uri xh 2017-3-13 上午10:07:56
     *
     * @param file
     * @return
     */
    public static URI file2uri(File file) {
        return file.toURI();
    }

    /**
     * url转化位uri xh 2017-3-13 上午10:10:23
     *
     * @param url
     * @return
     */
    public static URI url2uri(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * url转化位file xh 2017-3-13 上午10:10:55
     *
     * @param url
     * @return
     */
    public static File url2file(URL url) {
        URI uri = url2uri(url);
        if (uri == null)
            return null;
        return new File(uri);
    }

    public static Uri file2uri(File file, Context context) {
        if (file == null || context == null)
            return null;
        if (Build.VERSION.SDK_INT > 23) {
            return FileProvider.getUriForFile(context, file);
        } else {
            return Uri.parse("file://" + file.getAbsolutePath());
        }
    }

    @SuppressLint("NewApi")
    public static class Apk implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -8979339748599574778L;
        public String package_name;// 包名
        public String name;// 应用名
        public Drawable ico;// 应用图标
        public int versionCode;// 版本号
        public String versionName;// 版本
        public String path;// 保存路径

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Drawable getIco() {
            return ico;
        }

        public void setIco(Drawable ico) {
            this.ico = ico;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

    }
}
