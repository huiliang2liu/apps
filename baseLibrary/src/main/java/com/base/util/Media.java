package com.base.util;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;

import java.io.File;

/**
 * com.video.util
 * 2019/1/17 17:55
 * instructions：
 * author:liuhuiliang  email:825378291@qq.com
 **/
public class Media {
    private static final String TAG = "Media";
    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private MediaListener mListener;
    private static final int BASE = 1;
    private static final int SPACE = 200;// 间隔取样时间
//    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;
    private boolean stop = false;
    private long start_time;
    public long time;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListener == null)
                return;
            mListener.mediaUpdate(msg.what);
        }
    };

    public void setMediaListener(MediaListener listener) {
        mListener = listener;
    }


    public void startVoice(File file) {
        if (isRecording)
            return;
        Log.e(TAG, "开始录音");
        isRecording = true;
        stop = false;
        try {
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
//            file = new File(file, System.currentTimeMillis() + ".amr");
            mediaRecorder = new MediaRecorder();
            // 设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制音频的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置录制音频文件输出文件路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());
//            mediaRecorder.setMaxDuration(MAX_LENGTH);
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
//                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                    stop = true;
                    Log.e(TAG, "录音失败");
                    if (mListener != null)
                        mListener.mediaFailure();
                }
            });
            // 准备、开始
            mediaRecorder.prepare();
            mediaRecorder.start();
            start_time = System.currentTimeMillis();
            new Thread(new Update()).start();
        } catch (Exception e) {
            Log.e(TAG, "录音失败");
            stop();
        }
    }


    /**
     * 2019/1/17 18:37
     * annotation：录制视频
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public void startVideo(String parentPath, SurfaceView surfaceView) {
        if (isRecording)
            return;
        Log.e(TAG, "开始录制视频");
        isRecording = true;
        stop = false;
        try {
            File file = new File(parentPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, System.currentTimeMillis() + ".mp4");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            // 设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置视频图像的录入源
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 设置录入媒体的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            // 设置视频的编码格式
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            // 设置视频的采样率，每秒4帧
            mediaRecorder.setVideoFrameRate(4);
            // 设置录制视频文件的输出路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置捕获视频图像的预览界面
            if (surfaceView != null)
                mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
//                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                    stop = true;
                    Log.e(TAG, "录制视频失败");
                    if (mListener != null)
                        mListener.mediaFailure();
                }
            });
            // 准备、开始
            mediaRecorder.prepare();
            mediaRecorder.start();
            start_time = System.currentTimeMillis();
            new Thread(new Update()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 2019/1/17 18:30
     * annotation：停止录制
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public void stop() {
        Log.e(TAG, "停止录制");
        mListener = null;
        if (isRecording) {
            stop = true;
            time = System.currentTimeMillis() - start_time;
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Update implements Runnable {
        @Override
        public void run() {
            while (!stop) {
                if (mediaRecorder == null)
                    return;
                int ratio = mediaRecorder.getMaxAmplitude() / BASE;
                int db = 0;// 分贝
                if (ratio > 1)
                    db = (int) (20 * Math.log10(ratio));
                mHandler.sendEmptyMessage(db);
                try {
                    Thread.sleep(SPACE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 2019/1/17 18:14
     * annotation：录制回调
     * author：liuhuiliang
     * email ：825378291@qq.com
     */
    public interface MediaListener {


        /**
         * 2019/1/17 18:15
         * annotation：录制失败
         * author：liuhuiliang
         * email ：825378291@qq.com
         */
        void mediaFailure();

        /**
         * 听筒收入的声音大小
         *
         * @param update
         */
        void mediaUpdate(int update);
    }
}
