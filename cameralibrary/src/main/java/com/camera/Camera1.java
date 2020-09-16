package com.camera;


import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class Camera1 implements Camera {
    private static final String TAG = "Camera1";
    private int tag = -1;
    private android.hardware.Camera mCamera;
    private static Method setPreviewSurface;
    private WindowManager manager;

    static {
        try {
            setPreviewSurface = android.hardware.Camera.class.getDeclaredMethod("setPreviewSurface", Surface.class);
            setPreviewSurface.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera1(Context context) {
        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void open(int facing, CameraListener listener) {
        if (facing == LENS_FACING_FRONT)
            facing = android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
        else
            facing = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
        int cameraCount = android.hardware.Camera.getNumberOfCameras();
        Log.d(TAG, String.format("相机数量%d", cameraCount));
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                try {
                    mCamera = android.hardware.Camera.open(i);
                    tag = facing;
                    initCamera(info);
                    if (listener != null)
                        listener.onSuccess(this);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onFailure();
                }

            }
        }
    }

    @Override
    public void switchCamera(CameraListener listener) {
        int cameraCount = android.hardware.Camera.getNumberOfCameras();
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing != tag) {
                try {
                    mCamera = android.hardware.Camera.open(i);
                    tag = info.facing;
                    initCamera(info);
                    if (listener != null)
                        listener.onSuccess(this);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onFailure();
                }

            }
        }

    }

    private void initCamera(android.hardware.Camera.CameraInfo cameraInfo) {
        final int rotation = manager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }
        int cameraRotationOffset = cameraInfo.orientation;
        int displayRotation = 0;
        setPictureFormat(FORMAT_JPEG);
        setSceneMode(SCENE_MODE_AUTO);
        //根据前置与后置摄像头的不同，设置预览方向，否则会发生预览图像倒过来的情况。
        if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayRotation = (cameraRotationOffset + degrees) % 360;
            displayRotation = (360 - displayRotation) % 360; // compensate
        } else {
            displayRotation = (cameraRotationOffset - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(displayRotation);
    }

    @Override
    public void takePic(final TakePictureListener listener, final boolean preview) {
        mCamera.takePicture(new android.hardware.Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

            }
        }, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

            }
        }, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                if (listener != null)
                    listener.onTakePicture(data);
                if (preview)
                    mCamera.startPreview();
            }
        });
    }

    @Override
    public void setPictureSize(int width, int height) {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        android.hardware.Camera.Size size = getFitSize(width, height, parameters.getSupportedPictureSizes());
        if (size != null)
            parameters.setPictureSize(size.width, size.height);
        mCamera.setParameters(parameters);
    }

    @Override
    public void setPreviewSize(int width, int height) {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        android.hardware.Camera.Size size = getFitSize(width, height, parameters.getSupportedPreviewSizes());
        if (size != null)
            parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);
    }

    private android.hardware.Camera.Size getFitSize(int width, int height, List<android.hardware.Camera.Size> sizes) {
        if (sizes == null || sizes.size() <= 0)
            return null;
        float f = 1000;
        android.hardware.Camera.Size size = null;
        float mf = height * 1.0f / width;
        Log.e(TAG, String.format("width:%d,height:%d", width, height));
        for (android.hardware.Camera.Size size1 : sizes) {
            float a = size1.width * 1.0f / size1.height;
            if (Math.abs(mf - f) > Math.abs(mf - a)) {
                f = a;
                size = size1;
            }
            Log.d(TAG, String.format("width:%d,height:%d", size1.width, size1.height));
        }
        return size;
    }

    @Override
    public void setPictureFormat(int format) {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        if (format == FORMAT_TRANSLUCENT)
            parameters.setPictureFormat(PixelFormat.TRANSLUCENT);
        else if (format == FORMAT_TRANSPARENT)
            parameters.setPictureFormat(PixelFormat.TRANSPARENT);
        else if (format == FORMAT_OPAQUE)
            parameters.setPictureFormat(PixelFormat.OPAQUE);
        else if (format == FORMAT_RGBA_8888)
            parameters.setPictureFormat(PixelFormat.RGBA_8888);
        else if (format == FORMAT_RGBX_8888)
            parameters.setPictureFormat(PixelFormat.RGBX_8888);
        else if (format == FORMAT_RGB_888)
            parameters.setPictureFormat(PixelFormat.RGB_888);
        else if (format == FORMAT_RGB_565)
            parameters.setPictureFormat(PixelFormat.RGB_565);
        else if (format == FORMAT_RGBA_5551)
            parameters.setPictureFormat(PixelFormat.RGBA_5551);
        else if (format == FORMAT_RGBA_4444)
            parameters.setPictureFormat(PixelFormat.RGBA_4444);
        else if (format == FORMAT_A_8)
            parameters.setPictureFormat(PixelFormat.A_8);
        else if (format == FORMAT_L_8)
            parameters.setPictureFormat(PixelFormat.L_8);
        else if (format == FORMAT_LA_88)
            parameters.setPictureFormat(PixelFormat.LA_88);
        else if (format == FORMAT_RGB_332)
            parameters.setPictureFormat(PixelFormat.RGB_332);
        else if (format == FORMAT_YCbCr_422_SP)
            parameters.setPictureFormat(PixelFormat.YCbCr_422_SP);
        else if (format == FORMAT_YCbCr_420_SP)
            parameters.setPictureFormat(PixelFormat.YCbCr_420_SP);
        else if (format == FORMAT_YCbCr_422_I)
            parameters.setPictureFormat(PixelFormat.YCbCr_422_I);
        else if (format == FORMAT_RGBA_F16)
            parameters.setPictureFormat(PixelFormat.RGBA_F16);
        else if (format == FORMAT_RGBA_1010102)
            parameters.setPictureFormat(PixelFormat.RGBA_1010102);
        else
            parameters.setPictureFormat(PixelFormat.JPEG);
//        mCamera.setParameters(parameters);

    }

    @Override
    public void preview(Surface holder) {
        try {
            setPreviewSurface.invoke(mCamera, holder);
            mCamera.setPreviewCallback(new android.hardware.Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {

                }
            });
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Size> getPictureSizes() {
        List<android.hardware.Camera.Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
        List<Size> sizes1 = new ArrayList<>(sizes.size());
        for (android.hardware.Camera.Size size : sizes) {
            sizes1.add(new Size(size.width, size.height));
        }
        return sizes1;
    }

    @Override
    public void setFlashMode(int flashMode) {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        if (flashMode == FLASH_MODE_OFF)
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
        else if (flashMode == FLASH_MODE_AUTO)
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_AUTO);
        else if (flashMode == FLASH_MODE_ON)
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_ON);
        else if (flashMode == FLASH_MODE_RED_EYE)
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_RED_EYE);
        else
            parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
    }

    @Override
    public void setFocusMode(int focusMode) {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        if (focusMode == FOCUS_MODE_AUTO)
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
        else if (focusMode == FOCUS_MODE_INFINITY)
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_INFINITY);
        else if (focusMode == FOCUS_MODE_MACRO)
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_MACRO);
        else if (focusMode == FOCUS_MODE_FIXED)
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_FIXED);
        else if (focusMode == FOCUS_MODE_EDOF)
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_EDOF);
        else if (focusMode == FOCUS_MODE_CONTINUOUS_VIDEO)
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        else
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    @Override
    public void setSceneMode(int sceneMode) {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        if (sceneMode == SCENE_MODE_AUTO)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_AUTO);
        else if (sceneMode == SCENE_MODE_ACTION)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_ACTION);
        else if (sceneMode == SCENE_MODE_PORTRAIT)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_PORTRAIT);
        else if (sceneMode == SCENE_MODE_LANDSCAPE)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_LANDSCAPE);
        else if (sceneMode == SCENE_MODE_NIGHT)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_NIGHT);
        else if (sceneMode == SCENE_MODE_NIGHT_PORTRAIT)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT);
        else if (sceneMode == SCENE_MODE_THEATRE)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_THEATRE);
        else if (sceneMode == SCENE_MODE_BEACH)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_BEACH);
        else if (sceneMode == SCENE_MODE_SNOW)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_SNOW);
        else if (sceneMode == SCENE_MODE_SUNSET)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_SUNSET);
        else if (sceneMode == SCENE_MODE_STEADYPHOTO)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_STEADYPHOTO);
        else if (sceneMode == SCENE_MODE_FIREWORKS)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_FIREWORKS);
        else if (sceneMode == SCENE_MODE_SPORTS)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_SPORTS);
        else if (sceneMode == SCENE_MODE_PARTY)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_PARTY);
        else if (sceneMode == SCENE_MODE_CANDLELIGHT)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_CANDLELIGHT);
        else if (sceneMode == SCENE_MODE_BARCODE)
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_BARCODE);
        else
            parameters.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_HDR);
        mCamera.setParameters(parameters);
    }

    @Override
    public void destory() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }
}
