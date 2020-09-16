package com.camera;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.Surface;

import java.util.List;

public class CameraImpl implements Camera, CameraListener {
    private Camera camera;
    private Surface surface;
    private boolean create = false;


    public CameraImpl(Context context) {
//        if (hasCamera2(context))
        camera = new Camera2(context);
//        else
//            camera = new Camera1(context);
    }

    @Override
    public void open(int tag, CameraListener listener) {
        camera.destory();
        camera.open(tag, this);
    }

    @Override
    public void switchCamera(CameraListener listener) {
        camera.destory();
        camera.switchCamera(this);
    }

    @Override
    public void setPictureSize(int width, int height) {
        camera.setPictureSize(width, height);
    }

    @Override
    public void setPictureFormat(int format) {
        camera.setPictureFormat(format);
    }

    @Override
    public void preview(Surface holder) {
        this.surface = holder;
        if (create)
            camera.preview(holder);
    }

    @Override
    public void setPreviewSize(int width, int height) {
        camera.setPreviewSize(width, height);
    }

    @Override
    public List<Size> getPictureSizes() {
        return camera.getPictureSizes();
    }

    @Override
    public void setFlashMode(int flashMode) {
        camera.setFlashMode(flashMode);
    }

    @Override
    public void setFocusMode(int focusMode) {
        camera.setFocusMode(focusMode);
    }

    @Override
    public void setSceneMode(int sceneMode) {
        camera.setSceneMode(sceneMode);
    }

    @Override
    public void destory() {
        camera.destory();
    }

    @Override
    public void takePic(TakePictureListener listener, boolean preview) {
        if (create)
            camera.takePic(listener, preview);
    }

    @Override
    public void onSuccess(Camera camera) {
        create = true;
        if (surface != null)
            this.camera.preview(surface);
    }

    @Override
    public void onFailure() {
        create = false;
    }

    private boolean hasCamera2(Context mContext) {
        if (mContext == null) return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;
        try {
            CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            String[] idList = manager.getCameraIdList();
            boolean notFull = true;
            if (idList.length == 0) {
                notFull = false;
            } else {
                for (final String str : idList) {
                    if (str == null || str.trim().isEmpty()) {
                        notFull = false;
                        break;
                    }
                    final CameraCharacteristics characteristics = manager.getCameraCharacteristics(str);

                    final int supportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        notFull = false;
                        break;
                    }
                }
            }
            return notFull;
        } catch (Throwable ignore) {
            return false;
        }
    }

}
