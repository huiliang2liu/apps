package com.p2p.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


import com.p2p.Chart;
import com.p2p.ConnectListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class P2PBluetoothManager {
    private static final String TAG = "P2PBluetoothManager";
    private static final String LOG_BLUETOOTH = "address:%s,name:%s,bondState:%b";
    private static final UUID MY_UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter;
    private android.bluetooth.BluetoothManager mBluetoothManager;
    private Handler mHandler = new Handler();
    private boolean destory = false;
    private Context mContext;
    private List<BluetoothSerchListener> mSerchListeners = new Vector<>();
    private BluetoothServer mBluetoothServer;
    private ConnectListener mServerListenet;
    private Runnable mSerchRunnable = new Runnable() {
        @Override
        public void run() {
            discovery();
        }
    };
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.e(TAG, "找到设备");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, String.format(LOG_BLUETOOTH, device.getAddress(), device.getName(), device.getBondState() == BluetoothDevice.BOND_BONDED));
                for (BluetoothSerchListener bluetoothSerchListener : mSerchListeners)
                    bluetoothSerchListener.onSerch(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e(TAG, "搜索完成");
                for (BluetoothSerchListener bluetoothSerchListener : mSerchListeners)
                    bluetoothSerchListener.onSerched();
            } else if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                if (state == BluetoothA2dp.STATE_CONNECTING) {

                } else if (state == BluetoothA2dp.STATE_CONNECTED) {

                } else if (state == BluetoothA2dp.STATE_DISCONNECTING) {

                } else if (state == BluetoothA2dp.STATE_DISCONNECTED) {

                }
            } else if (BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                if (state == BluetoothA2dp.STATE_PLAYING) {

                } else if (state == BluetoothA2dp.STATE_NOT_PLAYING) {

                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (state == BluetoothDevice.BOND_BONDED) {//配对成功

                } else if (state == BluetoothDevice.BOND_BONDING) {//配对中

                } else if (state == BluetoothDevice.BOND_NONE) {//配对失败
                }
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if(state==BluetoothAdapter.STATE_TURNING_ON){

                }else if(state==BluetoothAdapter.STATE_ON){

                }else if(state==BluetoothAdapter.STATE_TURNING_OFF){

                }else if(state==BluetoothAdapter.STATE_OFF){

                }
            }
        }
    };
    private Runnable mServer = new Runnable() {
        @Override
        public void run() {
            server();
        }
    };

    public P2PBluetoothManager(Context context) {
        mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= 18) {
            mBluetoothManager = (android.bluetooth.BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceiver, bluetoothFilter);
    }

    public void startServer(ConnectListener serverListenet) {
        mServerListenet = serverListenet;
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "蓝牙没打开，服务端打开蓝牙");
            mBluetoothAdapter.enable();
        }
        server();
    }

    private void server() {
        Log.e(TAG, "开启服务器");
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothServer = new BluetoothServer();
        } else {
            Log.e(TAG, "开启服务器失败");
            mHandler.postDelayed(mServer, 1000);
        }
    }

    public void startClient() {
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "蓝牙不可用，客户端打开蓝牙");
            mBluetoothAdapter.enable();
        }
        discovery();
    }

    public void startClient(BluetoothDevice device, ConnectListener serverListenet) {
        Log.e(TAG, "打开客户端");
        mServerListenet = serverListenet;
        new OpenClient(device);
    }



    public void discovery() {
        Log.e(TAG, "开始扫描");
        boolean discovery = mBluetoothAdapter.startDiscovery();
        if (discovery)
            Log.e(TAG, "打开搜索成功");
        else {
            Log.e(TAG, "打开搜索失败");
            mHandler.postDelayed(mSerchRunnable, 1000);
        }
    }

    public void disable() {
        mBluetoothAdapter.disable();
    }

    public void cancelDiscovery() {
        Log.e(TAG, "停止扫描");
        mBluetoothAdapter.cancelDiscovery();
    }

    public void destory() {
        destory = true;
        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
        mHandler.removeCallbacksAndMessages(null);
        mContext.unregisterReceiver(mBluetoothReceiver);
        mSerchListeners = null;
        if (mBluetoothServer != null) {
            mBluetoothServer.destroy();
        }
    }

    public void registerBluetoothSerchListener(BluetoothSerchListener bluetoothSerchListener) {
        if (bluetoothSerchListener != null)
            mSerchListeners.add(bluetoothSerchListener);
    }

    public void unRegisterBluetoothSerchListener(BluetoothSerchListener bluetoothSerchListener) {
        if (bluetoothSerchListener != null)
            mSerchListeners.remove(bluetoothSerchListener);
    }

    public interface BluetoothSerchListener {
        void onSerch(BluetoothDevice device);

        void onSerched();
    }


    private class OpenClient extends Thread {
        private BluetoothDevice device;

        OpenClient(BluetoothDevice device) {
            this.device = device;
            start();
        }

        @Override
        public void run() {
            super.run();
            try {
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    try {
                        Method createBondMethod = BluetoothDevice.class.getMethod("cancelPairingUserInput");
                        // cancelBondProcess()
                        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                        byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, "000000");
                        Log.i("pair", Arrays.toString(pin));
                        if (Build.VERSION.SDK_INT > 18) {
                            boolean flag = device.setPin(pin);
                        }
                        Method creMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        Log.e("TAG", "开始配对");
                        creMethod.invoke(device);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                if (mServerListenet != null)
                    mServerListenet.onConnect(new Chart(inputStream, outputStream, socket));
            } catch (IOException e) {
                Log.e(TAG, "客户端打开错误");
                e.printStackTrace();
            }
        }
    }

    private class BluetoothServer extends Thread {
        private BluetoothServerSocket serverSocket;

        public BluetoothServer() {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("小黑", MY_UUID);
                start();
            } catch (IOException e) {
                Log.e(TAG, "打开蓝牙服务器失败");
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            while (!destory) {
                try {
                    Log.e(TAG, "开始等待连接");
                    BluetoothSocket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    if (mServerListenet != null)
                        mServerListenet.onConnect(new Chart(inputStream, outputStream, socket));
                } catch (IOException e) {
                    Log.e(TAG, "获取通道错误");
                    e.printStackTrace();
                }
            }
            if (serverSocket != null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            Log.e(TAG, "停止获取通道");
        }

        public void destroy() {
            try {

                super.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
