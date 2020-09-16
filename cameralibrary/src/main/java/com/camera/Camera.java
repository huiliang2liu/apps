package com.camera;

import android.hardware.camera2.CameraCharacteristics;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.List;

public interface Camera {
    /**
     * Flash will not be fired.
     */
    int FLASH_MODE_OFF = 0;

    /**
     * Flash will be fired automatically when required. The flash may be fired
     * during preview, auto-focus, or snapshot depending on the driver.
     */
    int FLASH_MODE_AUTO = 1;

    /**
     * Flash will always be fired during snapshot. The flash may also be
     * fired during preview or auto-focus depending on the driver.
     */
    int FLASH_MODE_ON = 2;

    /**
     * Flash will be fired in red-eye reduction mode.
     */
    int FLASH_MODE_RED_EYE = 3;

    /**
     * Constant emission of light during preview, auto-focus and snapshot.
     * This can also be used for video recording.
     */
    int FLASH_MODE_TORCH = 4;
    int FORMAT_TRANSLUCENT = 0;
    int FORMAT_TRANSPARENT = 1;
    int FORMAT_OPAQUE = 2;
    int FORMAT_RGBA_8888 = 3;
    int FORMAT_RGBX_8888 = 4;
    int FORMAT_RGB_888 = 5;
    int FORMAT_RGB_565 = 6;
    int FORMAT_RGBA_5551 = 7;
    int FORMAT_RGBA_4444 = 8;
    int FORMAT_A_8 = 9;
    int FORMAT_L_8 = 10;
    int FORMAT_LA_88 = 11;
    int FORMAT_RGB_332 = 12;
    int FORMAT_YCbCr_422_SP = 13;
    int FORMAT_YCbCr_420_SP = 14;
    int FORMAT_YCbCr_422_I = 15;
    int FORMAT_RGBA_F16 = 16;
    int FORMAT_RGBA_1010102 = 17;
    int FORMAT_JPEG = 18;

    /**
     * Auto-focus mode. Applications should call
     * to start the focus in this mode.
     */
    int FOCUS_MODE_AUTO = 0;

    /**
     * Focus is set at infinity. Applications should not call
     * in this mode.
     */
    int FOCUS_MODE_INFINITY = 1;

    /**
     * Macro (close-up) focus mode. Applications should call
     * to start the focus in this
     * mode.
     */
    int FOCUS_MODE_MACRO = 2;

    /**
     * Focus is fixed. The camera is always in this mode if the focus is not
     * adjustable. If the camera has auto-focus, this mode can fix the
     * focus, which is usually at hyperfocal distance. Applications should
     * not call in this mode.
     */
    int FOCUS_MODE_FIXED = 3;

    /**
     * Extended depth of field (EDOF). Focusing is done digitally and
     * continuously. Applications should not call in this mode.
     */
    int FOCUS_MODE_EDOF = 4;

    /**
     * Continuous auto focus mode intended for video recording. The camera
     * continuously tries to focus. This is the best choice for video
     * recording because the focus changes smoothly . Applications still can
     * call in this mode but the
     * subject may not be in focus. Auto focus starts when the parameter is
     * set.
     *
     * <p>Since API level 14, applications can call in this mode. The focus callback will
     * immediately return with a boolean that indicates whether the focus is
     * sharp or not. The focus position is locked after autoFocus call. If
     * applications want to resume the continuous focus, cancelAutoFocus
     * must be called. Restarting the preview will not resume the continuous
     * autofocus. To stop continuous focus, applications should change the
     * focus mode to other modes.
     *
     * @see #FOCUS_MODE_CONTINUOUS_PICTURE
     */
    int FOCUS_MODE_CONTINUOUS_VIDEO = 5;

    /**
     * Continuous auto focus mode intended for taking pictures. The camera
     * continuously tries to focus. The speed of focus change is more
     * aggressive than {@link #FOCUS_MODE_CONTINUOUS_VIDEO}. Auto focus
     * starts when the parameter is set.
     * this mode. If the autofocus is in the middle of scanning, the focus
     * callback will return when it completes. If the autofocus is not
     * scanning, the focus callback will immediately return with a boolean
     * that indicates whether the focus is sharp or not. The apps can then
     * decide if they want to take a picture immediately or to change the
     * focus mode to auto, and run a full autofocus cycle. The focus
     * position is locked after autoFocus call. If applications want to
     * resume the continuous focus, cancelAutoFocus must be called.
     * Restarting the preview will not resume the continuous autofocus. To
     * stop continuous focus, applications should change the focus mode to
     * other modes.
     *
     * @see #FOCUS_MODE_CONTINUOUS_VIDEO
     */
    int FOCUS_MODE_CONTINUOUS_PICTURE = 6;

    /**
     * Scene mode is off.
     */
    int SCENE_MODE_AUTO = 0;

    /**
     * Take photos of fast moving objects. Same as {@link
     * #SCENE_MODE_SPORTS}.
     */
    int SCENE_MODE_ACTION = 1;

    /**
     * Take people pictures.
     */
    int SCENE_MODE_PORTRAIT = 2;

    /**
     * Take pictures on distant objects.
     */
    int SCENE_MODE_LANDSCAPE = 3;

    /**
     * Take photos at night.
     */
    int SCENE_MODE_NIGHT = 4;

    /**
     * Take people pictures at night.
     */
    int SCENE_MODE_NIGHT_PORTRAIT = 5;

    /**
     * Take photos in a theater. Flash light is off.
     */
    int SCENE_MODE_THEATRE = 6;

    /**
     * Take pictures on the beach.
     */
    int SCENE_MODE_BEACH = 7;

    /**
     * Take pictures on the snow.
     */
    int SCENE_MODE_SNOW = 8;

    /**
     * Take sunset photos.
     */
    int SCENE_MODE_SUNSET = 9;

    /**
     * Avoid blurry pictures (for example, due to hand shake).
     */
    int SCENE_MODE_STEADYPHOTO = 10;

    /**
     * For shooting firework displays.
     */
    int SCENE_MODE_FIREWORKS = 11;

    /**
     * Take photos of fast moving objects. Same as {@link
     * #SCENE_MODE_ACTION}.
     */
    int SCENE_MODE_SPORTS = 12;

    /**
     * Take indoor low-light shot.
     */
    int SCENE_MODE_PARTY = 13;

    /**
     * Capture the naturally warm color of scenes lit by candles.
     */
    int SCENE_MODE_CANDLELIGHT = 14;

    /**
     * Applications are looking for a barcode. Camera driver will be
     * optimized for barcode reading.
     */
    int SCENE_MODE_BARCODE = 15;

    /**
     * Capture a scene using high dynamic range imaging techniques. The
     * camera will return an image that has an extended dynamic range
     * compared to a regular capture. Capturing such an image may take
     * longer than a regular capture.
     */
    int SCENE_MODE_HDR = 16;
    /**
     * <p>The camera device faces the same direction as the device's screen.</p>
     *
     * @see CameraCharacteristics#LENS_FACING
     */
    int LENS_FACING_FRONT = 0;

    /**
     * <p>The camera device faces the opposite direction as the device's screen.</p>
     *
     * @see CameraCharacteristics#LENS_FACING
     */
    int LENS_FACING_BACK = 1;

    /**
     * <p>The camera device is an external camera, and has no fixed facing relative to the
     * device's screen.</p>
     *
     * @see CameraCharacteristics#LENS_FACING
     */
    int LENS_FACING_EXTERNAL = 2;

    void open(int tag,CameraListener listener);

    void switchCamera(CameraListener listener);

    void setPictureSize(int width, int height);
    void setPreviewSize(int width, int height);

    void setPictureFormat(int format);

    void preview(Surface holder);

    List<Size> getPictureSizes();

    void setFlashMode(int flashMode);

    void setFocusMode(int focusMode);

    void setSceneMode(int sceneMode);

    void destory();
    void takePic(final TakePictureListener listener,final boolean preview);
    interface TakePictureListener {
        void onTakePicture(byte[] data);
    }
}
