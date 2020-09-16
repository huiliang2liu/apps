package com.base.net;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class NetworkUtil {
    /**
     * 判断连接的网络是wifi
     * @param context
     * @return boolean
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo == null)
            return false;
        int networkInfoType = networkInfo.getType();
        if (networkInfoType == ConnectivityManager.TYPE_WIFI
                || networkInfoType == ConnectivityManager.TYPE_ETHERNET)
            return networkInfo.isConnected();
        return false;
    }

    /**
     * 判断网络是否连接
     * @param context context
     * @return true/false
     */
    public static boolean isNetConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isConnected())
            return false;
        if (info.getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }

    /**
     * 判断网络是否可用
     * @return
     */
   public static boolean isNetUse(){
        Runtime runtime=Runtime.getRuntime();
       try {
           Process process=runtime.exec("ping -c 3 www.baidu.com");
           return process.waitFor()==0;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
   }
    /**
     * 判断网络是MOBILE
     * @param context
     * @return
     */
    public boolean isMobileConnected(Context context) {
        NetworkInfo mMobileNetworkInfo = getNetworkInfo(context);
        if (mMobileNetworkInfo == null)
            return false;
        if (mMobileNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            return mMobileNetworkInfo.isConnected();
        return false;
    }

    /**
     * 打开设置网络界面
     * @param context void
     */
    public static void setNetworkMethod(Context context) {
        Intent intent = null;
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        // intent = new Intent(Settings.ACTION_APN_SETTINGS);
        // startActivity(intent);
        if (Build.VERSION.SDK_INT > 10) {
            intent = new Intent(
                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            context.startActivity(intent);
            return;
        }
        intent = new Intent();
        ComponentName component = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(component);
        intent.setAction("android.intent.action.VIEW");
        context.startActivity(intent);
    }

    /**
     * 2打开wifi设置
     * @param context void
     */
    public static void setwifi(Context context) {
        // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
        Intent intent = new Intent();
        if (!(context instanceof Activity))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > 10) {
            intent.setAction(android.provider.Settings.ACTION_SETTINGS);

        } else
            intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 打开或者关闭移动数据
     * @param context
     * @param enabled void
     */

    public void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null; // ConnectivityManager类
        Field iConMgrField = null; // ConnectivityManager类中的字段
        Object iConMgr = null; // IConnectivityManager类的引用
        Class<?> iConMgrClass = null; // IConnectivityManager类
        Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法
        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
                    "setMobileDataEnabled", Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取网络类型 2是2G 3是3G 4是4G 0网络不可用
     * @return int
     */
    public static int getNetworkClass(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo == null)
            return 0;
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI
                || type == ConnectivityManager.TYPE_ETHERNET)
            return 1;
        TelephonyManager telecomManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telecomManager == null)
            return 2;
        type = telecomManager.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 2;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18:
            case 19:
                return 4;
            default:
                return 5;
        }
    }

    /**
     * 获取运营商名字
     * @param context
     * @return String
     */
    public static String getOperatorName(Context context) {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // getSimOperatorName就可以直接获取到运营商的名字
        return telephonyManager.getSimOperatorName();
    }

    /***
     * 判断Network具体类型（联通移动wap，电信wap，其他net）
     *
     * */
    @SuppressLint({"NewApi", "DefaultLocale"})
    public static String checkNetworkType(Context mContext) {
        try {
            NetworkInfo mobNetInfoActivity = getNetworkInfo(mContext);
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable())
                // 注意一：
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。
                return "无网络";
            // NetworkInfo不为null开始判断是网络类型
            int netType = mobNetInfoActivity.getType();
            if (netType == ConnectivityManager.TYPE_WIFI)
                // wifi net处理
                return "wifi";
            if (netType == ConnectivityManager.TYPE_MOBILE) {
                // 注意二：
                // 判断是否电信wap:
                // 不要通过getExtraInfo获取接入点名称来判断类型，
                // 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
                // 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
                // 所以可以通过这个进行判断！
                final Cursor c = mContext.getContentResolver().query(
                        Uri.parse("content://telephony/carriers/preferapn"),
                        null, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    final String user = c.getString(c.getColumnIndex("user"));
                    if (user != null && !user.isEmpty()) {
                        if (user.startsWith("ctwap")) {
                            // 互联星空 CTWAP
                        } else {
                            // 互联网CTNET
                        }

                        return "中国电信";
                    }
                }
                c.close();
                // 注意三：
                // 判断是移动联通wap:
                // 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
                // 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
                // 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
                // 所以采用getExtraInfo获取接入点名字进行判断
                String netMode = mobNetInfoActivity.getExtraInfo();
                if (netMode != null) {
                    // 通过apn名称判断是否是联通和移动wap
                    netMode = netMode.toLowerCase();
                    if (netMode.equals("cmwap"))
                        return "中国移动";
                    if (netMode.equals("cmnet"))
                        return "中国移动";
                    if (netMode.equals("3gnet") || netMode.equals("uninet"))
                        return "中国联通";
                    if (netMode.equals("3gwap") || netMode.equals("uniwap"))
                        return "中国联通";
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "其他网络";
        }

        return "其他网络";

    }

    private static NetworkInfo getNetworkInfo(Context context) {
        if (context == null)
            return null;
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            return null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager
                .getActiveNetworkInfo();
    }
}
