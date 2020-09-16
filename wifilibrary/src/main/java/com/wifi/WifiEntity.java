package com.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiEntity {
    private static final String STRING = "name:%s,password:%s,level:%d,connect:%b";
    private static final int LEVEL = 4;
    private ScanResult result;
    private boolean connect;
    private int level;
    private WifiConfiguration configuration;
    private WifiType wifiType;
    private String pass = "";
    private int netId;

    public enum WifiType {
        WPA("WPA"), WEP("WEP"), EAP("EAP"), NONE("NONE");
        private String name;

        private WifiType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    WifiEntity(ScanResult scanResult, WifiConfiguration wifiConfiguration) {
        String type = scanResult.capabilities.toUpperCase();
        this.result = scanResult;
        if (type.contains("WPA")) {
            wifiType = WifiType.WPA;
        } else if (type.contains("WEP")) {
            wifiType = WifiType.WEP;
        } else if (type.contains("EAP"))
            wifiType = WifiType.EAP;
        else {
            wifiType = WifiType.NONE;
        }
        level = WifiManager.calculateSignalLevel(scanResult.level, LEVEL);
        if (wifiConfiguration != null) {
            this.configuration = wifiConfiguration;
            pass = wifiConfiguration.preSharedKey;
            connect = true;
        } else {
            this.configuration = new WifiConfiguration();
            configuration.SSID = "\"" + getName() + "\"";
            if (wifiType == WifiType.WPA) {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            } else if (wifiType == WifiType.WEP) {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            } else if (wifiType == WifiType.EAP) {

            } else {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
        }

    }

    public String getName() {
        return result.SSID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WifiEntity) {
            WifiEntity entity = (WifiEntity) obj;
            return getName().equals(entity.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public String getPassword() {
        return pass;
    }

    public int getLevel() {
        return level;
    }

    public boolean isConnect() {
        return connect;
    }

    public int getNetId() {
        return netId;
    }

    public WifiType getWifiType() {
        return wifiType;
    }

    @Override
    public String toString() {
        return String.format(STRING, getName(), getPassword(), getLevel(), isConnect());
    }

    int connect(String password, WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        if (wifiInfo != null) {
            String name = new StringBuffer().append('"').append(result.SSID).append('"').toString();
            if (name.equals(wifiInfo.getSSID())) {
                Log.e("connect", "连接状态");
                return 0;
            }
        }
        Log.e("connect", ".................");
        if (!connect) {
            if (wifiType == WifiType.WEP) {
                Log.e("connect", "wep");
                int length = password.length();
                if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                    configuration.preSharedKey = password;
                } else {
                    configuration.preSharedKey = '"' + password + '"';
                }
            } else if (wifiType == WifiType.WPA) {
                Log.e("connect", "wpa");
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    configuration.preSharedKey = password;
                } else {
                    configuration.preSharedKey = '"' + password + '"';
                }
            } else if (wifiType == WifiType.EAP) {
                Log.e("connect", "wap");
            } else {
                Log.e("connect", "none");
            }
            netId = manager.addNetwork(configuration);
        } else {
            Log.e("connect", "connected");
            netId = configuration.networkId;
        }

        Log.e("connect", "connecting++++++++++++++++++++++");
//        manager.
//        netId = manager.addNetwork(configuration);
        return manager.enableNetwork(netId, true) ? 1 : 2;
    }
}
