package com.p2p.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.p2p.Chart;
import com.p2p.ConnectListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
public class WifiP2PManager implements WifiP2pManager.ChannelListener {
    private static final String TAG = "WifiP2PManager";
    private static final String IS_ENABLE = "enable:%b";
    private static final String CONNECT = "connect:%b";
    private static final String DEVICE_MESSAGE = "address:%s,name:%s,status:%d";
    private static final String DISCOVERY_STARTED = "start:%b";
    private static final String SERCH_ERROR = "搜索失败，失败码：%d";
    private static final String CONNECT_ERROR = "连接失败，失败码：%d";
    private Context context;
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {//用于指示WifiP2P是否可用SS
                boolean p2pIsEnable = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED) == WifiP2pManager.WIFI_P2P_STATE_ENABLED;
                Log.e(TAG, String.format(IS_ENABLE, p2pIsEnable));
            } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {//peers列表发生变化
                WifiP2pDeviceList mPeers = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                for (WifiP2pDevice device : mPeers.getDeviceList()) {
                    Log.e(TAG, String.format(DEVICE_MESSAGE, device.deviceAddress, device.deviceName, device.status));
                    if (listener != null)
                        listener.onWifiSerch(device);
                }

            } else if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {//WifiP2P的连接状态发生了改变
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                WifiP2pGroup wifiP2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                Log.e(TAG, String.format(CONNECT, networkInfo.isConnected()));
            } else if (action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)) {//本设备的设备信息发生了变化
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                Log.e(TAG, String.format(DEVICE_MESSAGE, device.deviceAddress, device.deviceName, device.status));
            } else if (action.equals(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)) {//开始搜索和结束搜索SS
                boolean discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED;
                Log.e(TAG, String.format(DISCOVERY_STARTED, discoveryState));
            }
        }
    };
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private boolean destory = false;
    private ConnectListener connectListener;
    private WifiSerchListener listener;
    private WifiP2pInfo wifiP2pInfo;
    private Handler handler = new Handler();
    private Runnable serchRunnable = new Runnable() {
        @Override
        public void run() {
            serch(listener);
        }
    };

    public WifiP2PManager(Context context) {
        this.context = context.getApplicationContext();
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this.context, Looper.getMainLooper(), this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        this.context.registerReceiver(mWifiReceiver, intentFilter);
        if (!wifiState(context)) {
            Log.e(TAG, "打开wifi");
            openWifi(context);
        }
        setDeviceName("xiaohei");
    }

    public void setDeviceName(String deviceName) {
        try {
            Method m = wifiP2pManager.getClass().getMethod(
                    "setDeviceName",
                    new Class[]{WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class});

            m.invoke(wifiP2pManager, channel, deviceName, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    //Code for Success in changing name

                    Log.e(TAG, "setDeviceName onSuccess");
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                    Log.e(TAG, "setDeviceName onFailure" + reason);
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void openWifi(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wm == null)
            return;
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "没有权限打开Wi-Fi失败");
            return;
        }

        if (wm.isWifiEnabled()) {
            Log.e(TAG, "wifi已经打开");
        } else {
            Log.e(TAG, "打开wifi。。。。。。。");
            wm.setWifiEnabled(true);
        }
    }

    // 检查当前WIFI状态
    public boolean wifiState(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wm == null)
            return false;
        if (Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            return false;
        return wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    public void serch(WifiSerchListener wifiSerchListener) {
        listener = wifiSerchListener;
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "启动搜索成功");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, String.format(SERCH_ERROR, reason));
                handler.postDelayed(serchRunnable, 1000);
            }
        });
    }

    public void stopSerch() {
        if (Build.VERSION.SDK_INT >= 16)
            wifiP2pManager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {

                }
            });
    }

    public void startServer(ConnectListener connectListener) {
        Log.e(TAG, "开启服务");
        this.connectListener = connectListener;
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "关闭群组成功");
                createGroup();
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "关闭群组失败");
                createGroup();
            }
        });


    }

    private void createGroup() {
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "打开群组成功");
                new ServerThread();
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "打开群组失败");
            }
        });
    }

    public void destory() {
        destory = false;
        wifiP2pManager.cancelConnect(channel, null);
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "关闭群组成功");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "关闭群组失败");
            }
        });
    }

    public void startClient(final ConnectListener connectListener, final WifiP2pDevice device) {
        this.connectListener = connectListener;
        Log.e(TAG, "开启客户端");
        if (device.status == WifiP2pDevice.AVAILABLE) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "连接成功");
                    new ClienThread();
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, String.format(CONNECT_ERROR, reason));
                }
            });
        } else if (device.status == WifiP2pDevice.CONNECTED) {
            Log.e(TAG, "已经连接");
            new ClienThread();

        } else if (device.status == WifiP2pDevice.INVITED) {
            Log.e(TAG, "请求还没处理");
            wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    Log.e(TAG, "取消请求成功");
                    startClient(connectListener, device);
                }

                public void onFailure(int reason) {
                    Log.e(TAG, "取消请求失败");
                }
            });
        }


    }


    @Override
    public void onChannelDisconnected() {

    }

    public interface WifiSerchListener {
        void onWifiSerch(WifiP2pDevice device);
    }

    private class ServerThread extends Thread {
        ServerThread() {
            start();
        }

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            super.run();
            try {
                serverSocket = new ServerSocket(8000);
                Log.e(TAG, "服务端开启成功");
                while (!destory) {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    if (connectListener != null && Build.VERSION.SDK_INT > 18)
                        connectListener.onConnect(new Chart(inputStream, outputStream, socket));
                    Log.e(TAG, "接受到连接请求");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null)
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    private class ClienThread extends Thread {


        ClienThread() {

            start();
        }

        @Override
        public void run() {
            super.run();
            try {
                while (true) {
                    if (wifiP2pInfo == null) {
                        Log.e(TAG, "wifiP2pInfo is null");
                        try {
                            sleep(300);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    if (wifiP2pInfo.groupOwnerAddress == null) {
                        Log.e(TAG, "wifiP2pInfo.groupOwnerAddress is null");
                        try {
                            sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    break;
                }

                Socket socket = new Socket(wifiP2pInfo.groupOwnerAddress.getHostAddress(), 8000);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                if (connectListener != null && Build.VERSION.SDK_INT > 18)
                    connectListener.onConnect(new Chart(inputStream, outputStream, socket));
                Log.e(TAG, "客户端开启成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
