package com.fake.cameratest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.camera.Camera;
import com.camera.CameraImpl;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = new CameraImpl(this);
        final SurfaceView surfaceView = findViewById(R.id.sv);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                camera.open(0, null);
                camera.setPictureFormat(Camera.FORMAT_JPEG);
                camera.setSceneMode(Camera.SCENE_MODE_AUTO);
                camera.setPictureSize(surfaceView.getWidth(),surfaceView.getHeight());
                camera.preview(holder.getSurface());
                surfaceView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        camera.takePic(new Camera.TakePictureListener() {
                            @Override
                            public void onTakePicture(byte[] data) {
                                Log.d(TAG, "拍照成功");
                            }
                        }, true);
                    }
                }, 6000);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destory();
    }
}
