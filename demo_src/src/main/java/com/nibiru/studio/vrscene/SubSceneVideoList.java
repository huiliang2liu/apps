package com.nibiru.studio.vrscene;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.nibiru.service.NibiruSelectionTask;

import java.io.File;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XToast;
import x.core.util.XLog;

/**
 * Created by zxf on 2018/3/9.
 */

public class SubSceneVideoList extends XBaseScene implements IXActorEventListener {

    private XLabel selectXLabel, pathXLabel, playXLabel;

    @Override
    public void onCreate() {

        selectXLabel = new XLabel("Select Video File");
        selectXLabel.setName("btn_select");
        selectXLabel.setAlignment(XLabel.XAlign.Center);
        selectXLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        selectXLabel.setCenterPosition(0, 0.6f, -4f);
        selectXLabel.setTextColor(Color.WHITE);
        selectXLabel.setEnableGazeAnimation(true);
        selectXLabel.setEventListener(this);
        selectXLabel.setSize(0.8f, 0.3f);
        addActor(selectXLabel);

        pathXLabel = new XLabel("");
        pathXLabel.setAlignment(XLabel.XAlign.Center);
        pathXLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        pathXLabel.setCenterPosition(0, 0f, -4f);
        pathXLabel.setTextColor(Color.WHITE);
        pathXLabel.setSize(1.5f, 0.3f);
        addActor(pathXLabel);

        playXLabel = new XLabel("Play Selected Video");
        playXLabel.setName("btn_play");
        playXLabel.setAlignment(XLabel.XAlign.Center);
        playXLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        playXLabel.setCenterPosition(0, -0.6f, -4f);
        playXLabel.setTextColor(Color.WHITE);
        playXLabel.setEnableGazeAnimation(true);
        playXLabel.setEventListener(this);
        playXLabel.setSize(0.8f, 0.3f);
        addActor(playXLabel);

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }


    @Override
    public void onGazeEnter(XActor xActor) {

    }

    @Override
    public void onGazeExit(XActor xActor) {

    }

    @Override
    public boolean onGazeTrigger(XActor xActor) {
        String name = xActor.getName();

        //演示选择视频文件
        //Demo selection video file
        if ("btn_select".equals(name)) {
            selectFile(NibiruSelectionTask.FILE_TYPE_VIDEO);
            return true;
        } else if ("btn_play".equals(name)) {
            if (TextUtils.isEmpty(selectedVideoPath) || !new File(selectedVideoPath).exists() || !isVideo(selectedVideoPath)) {
                XToast toast = XToast.makeToast(this, "Select a valid video file first", 2000);
                toast.setGravity(0.0f, 0f, -3.0f);
                toast.show(true);
                return true;
            } else {
                Intent intent = new Intent(this, SubSceneVideo.class);
                intent.putExtra(SubSceneVideo.VIDEO_PATH, selectedVideoPath);
                startScene(intent);
            }
        }
        return false;
    }

    private boolean isVideo(String path) {
        return (path.endsWith(".mp4") || path.endsWith(".mkv") || path.endsWith(".3gp") || path.endsWith(".wmv") || path.endsWith(".avi"));
    }

    @Override
    public void onAnimation(XActor xActor, boolean b) {

    }

    String selectedVideoPath = null;

    //选择文件的回调
    //Select file callback
    class FileSelectionRunnable implements NibiruSelectionTask.NibiruSelectionCallback {

        int type;

        FileSelectionRunnable(int type) {
            this.type = type;
        }

        @Override
        public void onSelectionResult(NibiruSelectionTask nibiruSelectionTask) {

            //获取返回码
            //Get return code
            NibiruSelectionTask.SELECTION_RESULT resultCode = nibiruSelectionTask.getResultCode();

            //获取用户选择的文件路径
            //Get user selected file path
            final String filepath = nibiruSelectionTask.getResultValue().get(NibiruSelectionTask.FILE_KEY_SELECTION_RESULT);

            Log.d(XLog.TAG, "File Select Result: " + resultCode + " path: " + filepath);

            //如果返回码为OK，表示选择成功，保存路径
            //If the return code is OK, the selection is successful, save the path
            if (resultCode == NibiruSelectionTask.SELECTION_RESULT.OK) {
                if (type == NibiruSelectionTask.FILE_TYPE_VIDEO) {
                    selectedVideoPath = filepath;

                    runOnRenderThread(new Runnable() {
                        @Override
                        public void run() {
                            pathXLabel.setTextContent("Get Video Path: " + filepath);
                        }
                    });
                }

            } else {
                if (type == NibiruSelectionTask.FILE_TYPE_VIDEO) {
                    selectedVideoPath = null;

                    runOnRenderThread(new Runnable() {
                        @Override
                        public void run() {
                            pathXLabel.setTextContent("Get Video Path: " + null);
                        }
                    });
                }
            }
        }
    }

    private void selectFile(int type) {
        NibiruSelectionTask nibiruSelectionTask = new NibiruSelectionTask(NibiruSelectionTask.TASK_SELECTION_ACTION_FILE);

        //选择文件的类型
        //Select the type of file
        nibiruSelectionTask.addParameter(NibiruSelectionTask.FILE_KEY_TYPE, type);

        //起始路径
        //Starting path
        nibiruSelectionTask.addParameter(NibiruSelectionTask.FILE_KEY_PATH, "/sdcard");

        //设置选择文件完成回调
        //Set selection file to complete callback
        nibiruSelectionTask.setCallback(new FileSelectionRunnable(type));

        startNibiruTask(nibiruSelectionTask);
    }
}
