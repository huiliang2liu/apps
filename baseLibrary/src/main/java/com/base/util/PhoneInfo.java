package com.base.util;

import android.Manifest;
import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.LocaleList;
import android.provider.Settings;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import dalvik.system.DexFile;

/**
 * 获取手机信息
 *
 * @author jzy 2019/01/14
 */
public final class PhoneInfo {
    private static final String ERROR_MAC = "02:00:00:00:00:00";
    private static long scanner = 0;
    private final static long HALF_HOUR = 30 * 60 * 1000;
    public static final int HTTP = 1;
    public static final int HTTPS = HTTP << 1;
    public static final int FTP = HTTPS << 1;


    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机的型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 生产厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 判断是否有网络连接
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager
                .getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }

        return false;
    }

    public static String uuid(Context context) {
        String mac = getMAC(context);
        if (mac == null || mac.isEmpty() || mac.equals(ERROR_MAC)) {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
//        String device = mac + context.getPackageName();
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
//        UUID deviceUuid = new UUID(androidId.hashCode(), (long) (device.hashCode() << 32));
        UUID deviceUuid = UUID.nameUUIDFromBytes(String.format("%s%s%s", mac, context.getPackageName(), androidId).getBytes());
        return deviceUuid.toString();
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @return 返回boolean ,是否为wifi网络
     */
    public static boolean useWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //是否有网络并且已经连接
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());

    }

    /**
     * 获取当前网络连接的类型信息
     *
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }


    public static String[] wifiArray(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (Build.VERSION.SDK_INT >= 23 && (context.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)) {

                return new String[0];
            }

            try {
                if (System.currentTimeMillis() - scanner > HALF_HOUR) {
                    scanner = System.currentTimeMillis();
                    wm.startScan();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 得到扫描结果
            List<ScanResult> mWifiList = wm.getScanResults();
            if (mWifiList == null || mWifiList.size() <= 0) {

                return new String[0];
            }
            String[] wifiArray = new String[mWifiList.size()];
            for (int i = 0; i < mWifiList.size(); i++) {
                wifiArray[i] = mWifiList.get(i).SSID;
            }
            return wifiArray;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取android版本号int
     *
     * @return
     */
    public static int getAndroidSDK() {
        return Build.VERSION.SDK_INT;
    }
//	/**
//	 * app版本getVersionCode
//	 * @param context
//	 * @return
//	 */
//	public static long versionCode(Context context) {
//		PackageManager manager = context.getPackageManager();
//		long code = 0;
//		try {
//			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//			code = info.versionCode();
//		} catch (PackageManager.NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		return code;
//	}


    /**
     * app版本versionName
     *
     * @param context
     * @return
     */
    public static String versionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }


    /**
     * 判断当前是否是debug模式
     *
     * @param context 上下文
     * @return true或false
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean enableAdb(Context context) {
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
    }

    /**
     * app版本versionName
     *
     * @param context
     * @return
     */
    public static int versionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }


    /**
     * 获取imei信息 为空时 返回androidID
     *
     * @param context
     * @return
     */

    public static String getDeviceID(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String imei = null;
            if (Build.VERSION.SDK_INT >= 29) {//androdiQ不能使用getDeviceId getImei
                imei = getAndroidID(context);
            } else if (Build.VERSION.SDK_INT > 22) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT > 25) {
                        imei = telephonyManager.getImei();
                    } else {
                        imei = telephonyManager.getDeviceId();
                    }
                }
            } else {
                imei = telephonyManager.getDeviceId();
            }
            if (imei == null) {
                imei = getAndroidID(context);
            }
            return imei;
        } catch (Exception ex) {

        }
        return "";
    }

    /**
     * 获取 imei
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        return getDeviceID(context);
    }

    public static String getSerial(Context context) {
        if (Build.VERSION.SDK_INT > 28)
            return "";
        if (Build.VERSION.SDK_INT > 25) {
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                return Build.getSerial();
            else
                return "";
        }
        return Build.SERIAL;
    }

    /**
     * 获取imsi信息
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String imsi = "";
            if (Build.VERSION.SDK_INT >= 29) {//android Q不能使用getSubscriberId方法
                imsi = "";
            } else if (Build.VERSION.SDK_INT > 22) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    imsi = telephonyManager.getSubscriberId();
                }
            } else {
                imsi = telephonyManager.getSubscriberId();
            }
            if (imsi == null) {
                imsi = "";
            }
            return imsi;
        } catch (Exception ex) {

        }
        return "";
    }

    /**
     * 获取iccid
     *
     * @param context
     * @return
     */
    public static String getMeid(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String iccid = "";
            if (Build.VERSION.SDK_INT > 29) {//android Q 不能使用getMeid方法
                iccid = "";
            } else if (Build.VERSION.SDK_INT > 25) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    iccid = telephonyManager.getMeid();
            }
            if (iccid != null && iccid.length() > 0) {
                return iccid;
            }
            return "";
        } catch (Exception ex) {

        }
        return "";
    }

    /**
     * 获取iccid
     *
     * @param context
     * @return
     */
    public static String getICCID(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String iccid = "";
            if (Build.VERSION.SDK_INT > 29) {//android Q 不能使用getSimSerialNumber方法
                iccid = "";
            } else if (Build.VERSION.SDK_INT > 22) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    iccid = telephonyManager.getSimSerialNumber();
            } else {
                iccid = telephonyManager.getSimSerialNumber();
            }
            if (iccid != null && iccid.length() > 0) {
                return iccid;
            }
            return "";
        } catch (Exception ex) {

        }
        return "";
    }

    /**
     * 获取设备屏幕分辩密度
     *
     * @param context
     * @return
     */

    public static float getDisplayDensity(Context context) {
        if (context == null)
            return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取设备屏幕分辨率密度dpi
     *
     * @param context
     * @return
     */

    public static int getDisplayDensityDpi(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    /**
     * 获取手机屏幕分辨率
     *
     * @param context
     * @return
     */
    public static int[] getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 获取语言设置
     *
     * @return
     */
    public static String getLanguage() {

        return Locale.getDefault().toString();
    }

    /**
     * 获取用户语言列表
     *
     * @return
     */
    public static String[] getLanguages() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            LocaleList localeList = LocaleList.getDefault();
            if (localeList == null || localeList.size() <= 0)
                return new String[]{};
            String[] locales = new String[localeList.size()];
            for (int i = 0; i < locales.length; i++) {
                locales[i] = localeList.get(i).toString();
            }
        }
        return new String[]{};

    }


    /**
     * 获取国家设置
     *
     * @param context
     * @return
     */
    public static String getCountry(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String zone = telephonyManager.getSimCountryIso();
            if (zone == null) {
                zone = "";
            }
            return zone;
        } catch (Exception ex) {

        }
        return "";
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {    // 当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {    // 当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());    // 得到IPV4地址
                return ipAddress;
            }
        } else {
            // 当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static boolean isTV(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMAC(Context context) {
        String mac = getWlan(context);
        if (mac == null || mac.isEmpty())
            mac = getEth();
        if (mac == null || mac.isEmpty())
            mac = ERROR_MAC;
        mac = mac.toLowerCase();
        Log.e("mac", mac);
        return mac;
    }

    /**
     * description：获取无线mac
     */
    public static String getWlan(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String mac = null;
        if (info != null) {
            mac = info.getMacAddress();
        }
        if (mac == null || mac.isEmpty()) {
            mac = readLine("/sys/class/net/wlan0/address");//6.0-7.0
            if (mac == null || mac.isEmpty()) {
                try {
                    List<NetworkInterface> all =
                            Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface nif : all) {
                        if (!nif.getName().equals("wlan0"))
                            continue;
                        byte macBytes[] = nif.getHardwareAddress();
                        if (macBytes == null) return "";
                        StringBuilder res1 = new StringBuilder();
                        for (Byte b : macBytes) {
                            res1.append(String.format("%02X:", b));
                        }
                        if (res1 != null) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        mac = res1.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mac;
    }

    /**
     * description：获取有线mac
     */
    public static String getEth() {
        String mac = readLine("/sys/class/net/eth0/address");
        try {
            List<NetworkInterface> all =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equals("eth0"))
                    continue;
                byte macBytes[] = nif.getHardwareAddress();
                if (macBytes == null) return "";
                StringBuilder res1 = new StringBuilder();
                for (Byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1 != null) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                mac = res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }


    private static String readLine(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename),
                    256);
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return "";
    }

    /**
     * 获取手机PLMN
     *
     * @param context
     * @return
     */
    public static String getPLMN(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (manager == null) {
                return "";
            }
            String plmn = manager.getSimOperator();
//            String plmn = manager.getNetworkOperator();
            if (plmn == null || plmn.equals("")) {
                return "";
            } else {
                if (plmn.length() > 6) {
                    plmn = plmn.split(",")[0].replace(",", "");
                }
                return plmn.replace(",", "");
            }
        } catch (Exception ex) {

        }
        return "";
    }

    public static String getMNC(Context context) {
        String plmn = getPLMN(context);
        if (plmn.length() >= 5) {
            return plmn.substring(3);
        }
        return "" + context.getResources().getConfiguration().mnc;
    }

    public static String getMCC(Context context) {
        String plmn = getPLMN(context);
        if (plmn.length() >= 5) {
            return plmn.substring(0, 3);
        }
        return "" + context.getResources().getConfiguration().mcc;
    }

    public static String getISOCountryCode(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                return "";
            }
            String icc = null;
            if (tm != null) {
                icc = tm.getSimCountryIso();
            }
            return icc == null ? "" : icc;
        } catch (Exception ex) {

        }
        return "";
    }

    /**
     * 获取手机webview的userAgent
     *
     * @return
     */
    public static String getUserAgent() {
        return System.getProperty("http.agent", "okhttp/3.0");
    }

    /**
     * 获取androidID
     *
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        String androidId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return androidId == null ? "0000000000000000" : androidId;
    }


    /**
     * 获取运营商 46000 46002 46007 代表中国移动 46001 46006 代表中国联通 46003 46005 46011
     * 代表中国电信 46020 代表中国铁通
     *
     * @param context
     * @return
     */

    public static int getOperator(Context context) {
        String imsi = getIMSI(context);
        if (imsi != null && imsi.length() > 0) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")
                    || imsi.startsWith("46007")) {
                return 1;
            }
            if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
                return 2;
            }
            if (imsi.startsWith("46003") || imsi.startsWith("46005")
                    || imsi.startsWith("46011")) {
                return 3;
            }
            if (imsi.startsWith("46020")) {
                return 4;
            }
        }
        return 0;
    }

    public static boolean isChinaMoblie(Context context) {
        String imsi = getIMSI(context);
        if (imsi != null && imsi.length() > 0) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备类型
     *
     * @param context
     * @return 0:phone 1:pad
     */
    public static int getDeviceType(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double inch = Math.sqrt(Math.pow(dm.widthPixels, 2)
                + Math.pow(dm.heightPixels, 2))
                / (160 * dm.density);
        if (inch >= 8.0d) {
            return 1;
        }
        return 0;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getConnectWifiSsid(Context context) {
        if (context == null)
            return "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return "";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null)
            return "";
        String ssid = wifiInfo.getSSID();
        return ssid != null ? ssid.replace("\"", "") : "";
    }

    /**
     * 得到手机产品序列号
     */
    public static String SN() {
        String sn = "NO Search";
        String serial = Build.SERIAL;// 第二种得到序列号的方法
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            Object o = get.invoke(c, "ro.serialno");
            if (o == null) {
                return "NO Search";
            }
            sn = (String) o;
        } catch (Exception e) {
            return "NO Search";
        }
        if (sn == "") {
            return "NO Search";
        }

        return sn;
    }

    /**
     * 获取apk包的签名信息(得到是一串很长的字符  需要上传服务器时一般要用Md5加密处理后再上传)
     */
    public static String getInstalledAPKSignature(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo appInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (null == appInfo || null == appInfo.signatures) {
                return "";
            }
            return appInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断手机是否ROOT
     */
    public static boolean isSystemRoot() {
        boolean isRoot = false;
        try {
            isRoot = (new File("/system/bin/su").exists())
                    || (new File("/system/xbin/su").exists());
            Log.d("TAG", "isRoot  = " + isRoot);
        } catch (Exception e) {

        }
        return isRoot;
    }

    public static boolean isProxy(Context context) {
        String proxyAddress;
        int proxyPort;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return proxyAddress != null && !proxyAddress.isEmpty() && proxyPort != 1;
    }

    public static void noHttpProxy() {
        setHttpProxy("", "-1");
    }

    public static void setHttpProxy(String host, String port) {
        setHttpProxy("", "-1", null);
    }

    public static void setHttpProxy(String host, String port, String extHosts) {
        setProxy(host, port, extHosts, HTTP);
    }

    public static void noHttpsProxy() {
        setHttpsProxy("", "-1");
    }

    public static void setHttpsProxy(String host, String port) {
        setHttpsProxy("", "-1", null);
    }

    public static void setHttpsProxy(String host, String port, String extHosts) {
        setProxy(host, port, extHosts, HTTPS);
    }

    public static void noFtpProxy() {
        setFtpProxy("", "-1");
    }

    public static void setFtpProxy(String host, String port) {
        setFtpProxy("", "-1", null);
    }

    public static void setFtpProxy(String host, String port, String extHosts) {
        setProxy(host, port, extHosts, FTP);
    }


    public static void noProxy() {
        setProxy("", "-1");
    }

    public static void setProxy(String host, String port) {
        setProxy("", "-1", null);
    }

    public static void setProxy(String host, String port, String extHosts) {
        setProxy(host, port, extHosts, HTTP | HTTPS | FTP);
    }

    public static void setProxy(String host, String port, String extHosts, int type) {
        int hport = -1;
        try {
            hport = Integer.valueOf(port);
        } catch (Exception e) {
        }
        if (host == null || host.isEmpty() || hport == -1) {
            if ((type & HTTP) == HTTP) {
                System.getProperties().put("http.proxyHost", "");
                System.getProperties().put("http.proxyPort", "-1");
                System.getProperties().put("http.nonProxyHosts", "");
            }
            if ((type & HTTPS) == HTTPS) {
                System.getProperties().put("https.proxyHost", "");
                System.getProperties().put("https.proxyPort", "-1");
            }
            if ((type & FTP) == FTP) {
                System.getProperties().put("ftp.proxyHost", "");
                System.getProperties().put("ftp.proxyPort", "-1");
                System.getProperties().put("ftp.nonProxyHosts", "");
            }
        } else {
            System.getProperties().put("proxySet", "true");
            if ((type & HTTP) == HTTP) {
                System.getProperties().put("http.proxyHost", host);
                System.getProperties().put("http.proxyPort", port);
                if (extHosts != null && !extHosts.isEmpty()) {
                    System.getProperties().put("http.nonProxyHosts", extHosts);
                }
            }
            if ((type & HTTPS) == HTTPS) {
                System.getProperties().put("https.proxyHost", host);
                System.getProperties().put("https.proxyPort", port);
            }
            if ((type & FTP) == FTP) {
                System.getProperties().put("ftp.proxyHost", host);
                System.getProperties().put("ftp.proxyPort", port);
                if (extHosts != null && !extHosts.isEmpty()) {
                    System.getProperties().put("ftp.nonProxyHosts", extHosts);
                }
            }
        }
    }

    /**
     * 获取无线路由mac
     *
     * @return
     */
    public static String getWlanGatewayMac() {
        List<ARP> arps = getArp();
        String ip = getWlanGatewayIp();
        if (ip != null && !ip.isEmpty() && arps != null && arps.size() > 0) {
            for (ARP arp : arps)
                if (ip.equalsIgnoreCase(arp.ip))
                    return arp.mac;
        }
        return ERROR_MAC;
    }

    /**
     * 获取有线路由mac
     *
     * @return
     */
    public static String getEthGatewayMac() {
        List<ARP> arps = getArp();
        String ip = getEthGatewayIp();
        if (ip != null && !ip.isEmpty() && arps != null && arps.size() > 0) {
            for (ARP arp : arps)
                if (ip.equalsIgnoreCase(arp.ip))
                    return arp.mac;
        }
        return ERROR_MAC;
    }

    /**
     * 获取无线路由ip
     *
     * @return
     */
    public static String getWlanGatewayIp() {
        String cmd = "getprop dhcp.wlan0.gateway";
        return cmd(cmd);
    }

    /**
     * 获取有线路由ip
     *
     * @return
     */

    public static String getEthGatewayIp() {
        String cmd = "getprop dhcp.eth0.gateway";
        return cmd(cmd);
    }

    public static String cmd(String cmd) {
        BufferedReader reader = null;
        String c = "";
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(cmd);
            reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            c = reader.readLine();
            c.trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return c;
    }

    /**
     * 获取设备列表前发送一次udp包用于更新设备
     *
     * @return
     */
    public static boolean sendUdp() {
        try {
            DatagramPacket dp = new DatagramPacket(new byte[0], 0, 0);
            DatagramSocket socket = new DatagramSocket();
            int position = 1;
            while (position < 255) {
                dp.setAddress(InetAddress.getByName("192.168.1." + String.valueOf(position)));
                socket.send(dp);
                position++;
                if (position == 125) {//分两段掉包，一次性发的话，达到236左右，会耗时3秒左右再往下发
                    socket.close();
                    socket = new DatagramSocket();
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static List<String> getAllClassName(Context context) {
        List<String> classNameList = new ArrayList<>();
        try {

            DexFile df = new DexFile(context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = (String) enumeration.nextElement();
                if (className.startsWith("com.google"))
                    continue;
                if (className.startsWith("okhttp3."))
                    continue;
                if (className.startsWith("okio."))
                    continue;
                if (className.startsWith("kotlinx."))
                    continue;
                if (className.startsWith("kotlin."))
                    continue;
                if (className.startsWith("androidx."))
                    continue;
                if (className.startsWith("android."))
                    continue;
                if (className.startsWith("android.support"))
                    continue;
//                if(className.contains("com.google") || className.contains("android.support") || className.contains("javassist.bytecode")
//                        || className.contains("android")|| className.contains("androidx") || className.contains("com.blankj") || className.contains("javassist") || className.contains("kotlin") || className.contains("$") || className.contains("org.reflections") || className.contains("org.intellij") || className.contains("org.jetbrains")){
//                    continue;
//                }
                classNameList.add(className);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classNameList;
    }

    /**
     * 获取arp缓存列表
     *
     * @return
     */
    public static List<ARP> getArp() {
        BufferedReader reader = null;
        List<ARP> arps = new ArrayList<>();
        InputStream is = null;
        Process process = null;
        try {
            is = new FileInputStream("/proc/net/arp");
        } catch (Exception e) {
            try {
                process = Runtime.getRuntime().exec("cat proc/net/arp");
                is = process.getInputStream();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (is == null)
            return arps;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                line.trim();
                if (line.isEmpty() || line.contains("Device") || line.contains("device"))
                    continue;
                if (line.contains("00:00:00:00:00:00"))
                    continue;
                String[] split = line.split(" ");
                List<String> list = new ArrayList<>(split.length);
                for (String s : split) {
                    if (s == null)
                        continue;
                    s = s.trim();
                    if (s.isEmpty())
                        continue;
                    list.add(s);
                }
                int len = list.size();
                if (len < 4)
                    continue;
                ARP arp = new ARP();
                arp.ip = list.get(0);
                arp.type = list.get(1);
                arp.flags = list.get(2);
                arp.mac = list.get(3);
                arp.mask = len > 4 ? list.get(4) : "";
                arp.device = len > 5 ? list.get(5) : "";
                arps.add(arp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return arps;
    }

    public static class ARP {
        public String ip;
        public String type;
        public String flags;
        public String mac;
        public String mask;
        public String device;//eth0有线，wlan0无线

        @NonNull
        @Override
        public String toString() {
            return String.format("ip:%s,type:%s,flags:%s,mac:%s,mask:%s,device:%s", ip, type, flags, mac, mask, device);
        }
    }

}
