package com.p2p;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Chart {
    private static final String TAG = "Chart";
    private WriteChart writeChart;
    private ReadChart readChart;
    private Closeable socket;

    public Chart(InputStream inputStream, OutputStream outputStream, Closeable socket) {
        if (inputStream != null)
            readChart = new ReadChart(inputStream);
        if (outputStream != null)
            writeChart = new WriteChart(outputStream);
        this.socket = socket;
    }

    public void setChartReadListener(ChartReadListener chartReadListener) {
        if (readChart != null)
            readChart.readListener = chartReadListener;
    }

    public void write(String message) {
        if (writeChart != null)
            writeChart.write(message);
    }

    public void destory() {
        if (writeChart != null)
            writeChart.destory();
    }

    private class WriteChart extends Thread {
        private OutputStream outputStream;
        private static final String WRITE_LOCK = "write_lock";
        private List<String> datas = new ArrayList<>();

        public WriteChart(OutputStream outputStream) {
            this.outputStream = outputStream;
            start();
        }

        //            public void
        @Override
        public void run() {
            super.run();
            while (true) {
                synchronized (WRITE_LOCK) {
                    if (datas.size() > 0) {
                        try {
                            for (String data : datas) {
                                if (data == null || data.isEmpty())
                                    continue;
                                outputStream.write(data.getBytes("utf-8"));
                                outputStream.flush();
                            }
                            datas.clear();
                        } catch (Exception e) {
                            Log.e(TAG, "数据写入错误");
                            e.printStackTrace();
                            break;
                        }
                    }

                    try {
                        WRITE_LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
            Log.e(TAG, "写线程关闭");
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭输出流错误");
                e.printStackTrace();
            }
        }

        public void write(String datas) {
            synchronized (WRITE_LOCK) {
                this.datas.add(datas);
                WRITE_LOCK.notifyAll();
            }
        }

        public void destory() {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            write(null);
        }
    }

    private class ReadChart extends Thread {
        private InputStream inputStream;
        private ChartReadListener readListener;
        private static final int READ_LENGTH = 1024 * 1024;


        public ReadChart(InputStream inputStream) {
            this.inputStream = inputStream;
            start();
        }

        @Override
        public void run() {
            super.run();
            byte[] buff = new byte[READ_LENGTH];
            while (true) {
                try {
                    int len = inputStream.read(buff);
                    if (len == -1) {
                        continue;
                    }
                    String message = new String(buff, 0, len, "utf-8");
                    if (readListener != null)
                        readListener.onRead(message);
                } catch (Exception e) {
                    Log.e(TAG, "读取数据错误");
                    e.printStackTrace();
                    break;
                }
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭输入流错误");
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (Exception e) {
                Log.e(TAG, "套接字关闭失败");
                e.printStackTrace();
            }
            Log.e(TAG, "读线程结束");
        }
    }

    public interface ChartReadListener {
        void onRead(String message);
    }
}
