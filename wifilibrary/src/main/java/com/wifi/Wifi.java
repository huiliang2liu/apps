package com.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Wifi {
    private static final String TAG = "Wifi";
    WifiManager wifiManager;
    private Context context;
    private BroadcastReceiver wifiReceiver;
    private WifiListChangeListener wifiListChangeListener;
    private WifiEntity wifiEntity;
    private boolean connect = false;
    private WifiConnectListener wifiConnectListener;

    public String getMac() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null)
            return wifiInfo.getBSSID();
        return "";
    }


    public String getIpAddress() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null)
            return ipIntToString(wifiInfo.getIpAddress());
        return "";
    }

    public String getName() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            String name = wifiInfo.getSSID();
            return name.substring(1, name.length() - 1);
        }
        return "";
    }

    public int getLinkSpeed() {//单位Mbps
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null)
            return wifiInfo.getLinkSpeed();
        return 0;
    }

    private String ipIntToString(int ip) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public Wifi(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifiwifi连接状态广播
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        context.registerReceiver(wifiReceiver, filter);
    }

    public void destory() {
        context.unregisterReceiver(wifiReceiver);
    }

    public void setWifiListChangeListener(WifiListChangeListener wifiListChangeListener) {
        this.wifiListChangeListener = wifiListChangeListener;
        scan();
    }

    public void connect(WifiEntity wifiEntity, String password, WifiConnectListener wifiConnectListener) {
        if (this.wifiEntity != null) {
            Log.e(TAG, "connecting.............");
            return;
        }
        int connect = wifiEntity.connect(password, wifiManager);
        Log.e(TAG, String.format("connect:%d", connect));
        if (connect == 0) {
            this.wifiEntity = null;
            if (wifiConnectListener != null)
                wifiConnectListener.onWifiConnected();
            return;
        }
        this.wifiConnectListener = wifiConnectListener;
        this.connect = false;
        this.wifiEntity = wifiEntity;
    }

    // 打开WIFI
    public void openWifi(Context context) {
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }

    // 关闭WIFI
    public void closeWifi(Context context) {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    /**
                     * WIFI_STATE_DISABLED    WLAN已经关闭
                     * WIFI_STATE_DISABLING   WLAN正在关闭
                     * WIFI_STATE_ENABLED     WLAN已经打开
                     * WIFI_STATE_ENABLING    WLAN正在打开
                     * WIFI_STATE_UNKNOWN     未知
                     */
                    case WifiManager.WIFI_STATE_DISABLED: {
                        Log.e(TAG, "已经关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_DISABLING: {
                        Log.e(TAG, "正在关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        Log.e(TAG, "已经打开");
                        scan();
//                        sortScaResult();
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLING: {
                        Log.e(TAG, "正在打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_UNKNOWN: {
                        Log.e(TAG, "未知状态");
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                Log.e(TAG, "--NetworkInfo--" + intent.getExtras());
                if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    Log.e(TAG, "wifi没连接上");
                    if (connect) {
                        if (wifiEntity != null) {
                            Log.e(TAG, "connect failure");
                            wifiManager.removeNetwork(wifiEntity.getNetId());
                            wifiEntity = null;
                            if (wifiConnectListener != null)
                                wifiConnectListener.onWifiConnectFailuer();
                            wifiConnectListener = null;
                        }
                    }
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                    Log.e(TAG, "wifi连接上了");
                    if (wifiEntity != null) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if (wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1).equals(wifiEntity.getName())) {
                            if (wifiConnectListener != null)
                                wifiConnectListener.onWifiConnected();
                        } else {
                            if (wifiConnectListener != null)
                                wifiConnectListener.onWifiConnectFailuer();
                        }
                        wifiConnectListener = null;
                        wifiEntity = null;

                    }
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    Log.e(TAG, "wifi正在连接");
                    connect = true;
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
//                Log.e(TAG, "网络列表变化了");
//                scan();
            }
        }
    }

    private void scan() {
        wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        List<WifiEntity> entities = new ArrayList<>();
        if (results != null && results.size() > 0) {
            for (ScanResult result : results) {
                WifiConfiguration configuration = null;
                if (configurations != null && configurations.size() > 0) {
                    for (WifiConfiguration wifiConfiguration : configurations) {
                        String name = new StringBuffer().append('"').append(result.SSID).append('"').toString();
                        if (wifiConfiguration.SSID.equals(name)) {
                            configuration = wifiConfiguration;
                            break;
                        }
                    }
                }
                WifiEntity entity = new WifiEntity(result, configuration);
                if (entities.indexOf(entity) < 0)
                    entities.add(entity);
            }
        }

        Collections.sort(entities, new Comparator<WifiEntity>() {
            @Override
            public int compare(WifiEntity o1, WifiEntity o2) {
                return o2.getLevel() - o1.getLevel();
            }
        });
        if (wifiListChangeListener != null)
            wifiListChangeListener.onWifiListChange(entities);
    }

    public interface WifiListChangeListener {
        void onWifiListChange(List<WifiEntity> wifis);
    }

    public interface WifiConnectListener {
        void onWifiConnected();

        void onWifiConnectFailuer();
    }
}
