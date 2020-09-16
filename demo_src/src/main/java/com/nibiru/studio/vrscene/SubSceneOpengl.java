package com.nibiru.studio.vrscene;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.nibiru.lib.vr.NVREye;
import com.nibiru.lib.vr.NVRUtils;
import com.nibiru.studio.TextureRect;
import com.nibiru.studio.xrdemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import x.core.ui.XBaseScene;
import x.core.ui.XImage;

/**
 * Created by Administrator on 2018/5/29.
 */

public class SubSceneOpengl extends XBaseScene{

    TextureRect textureRect;
    int textureId, textureIdFocused;

    @Override
    public void onCreate() {

        XImage xImage = new XImage("jpg_test.jpg");

        xImage.setCenterPosition(-3f, -0.2f, -4f);
        xImage.setSize(1f, 1f);
        xImage.setRenderOrder(6);
//        addActor(xImage);


        getNibiruVRService().setTimeWarpEnable(false);
        textureRect = new TextureRect(getActivity(),0.5f, 0.5f, 1, 1);
        textureId = initTexture(getResources(), R.mipmap.icon, true);
        textureIdFocused = initTexture(getResources(), R.mipmap.jpg_test, true);

        Matrix.setLookAtM(matrixCamera, 0, 0.0f, 0.0f, 0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.setIdentityM(matrixModel , 0);
        Matrix.translateM(matrixModel, 0, 0, 0, -4f);
        Matrix.scaleM(matrixModel, 0, 1.5f, 1.5f, 1);

        initCube();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        getNibiruVRService().setTimeWarpEnable(true);
    }

    float[] matrixView = new float[16];
    float[] matrixModel = new float[16];
    float[] matrixProjection = new float[16];
    float[] matrixCamera = new float[16];

    @Override
    public boolean onDrawEye(NVREye eye) {
//        Matrix.multiplyMM(matrixView, 0, eye.getView(), 0, matrixCamera, 0);
//        Matrix.multiplyMM(matrixProjection, 0, matrixView, 0, matrixModel, 0);
//        Matrix.multiplyMM(matrixProjection, 0, eye.getPerspectiveMatrix(), 0, matrixProjection, 0);
//        textureRect.drawSelf(textureId, matrixProjection);

        drawCube(eye);
        return false;
    }

    public int initTexture(Resources r, int drawableId, boolean isMipmap) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, // 产生的纹理id的数量
                textures, // 纹理id的数组
                0 // 偏移量
        );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        if (isMipmap) {// Mipmap纹理采样过滤参数
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR_MIPMAP_LINEAR);
        } else {// 非Mipmap纹理采样过滤参数
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        InputStream is = r.openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 纹理类型
                0, bitmapTmp, // 纹理图像
                0 // 纹理边框尺寸
        );
        if (isMipmap) {
            // 自动生成Mipmap纹理
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }

        bitmapTmp.recycle(); // 纹理加载成功后释放图片
        return textureId;
    }

    public final static class WorldLayoutData {

        public static final float[] CUBE_COORDS = new float[] {
                // Front face
                -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                -1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,

                // Right face
                1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f,
                -1.0f,
                1.0f,
                1.0f,
                -1.0f,

                // Back face
                1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f,
                1.0f,
                -1.0f,

                // Left face
                -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f,
                1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, -1.0f,

                // Bottom face
                1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f, };

        public static final float[] CUBE_COLORS = new float[] {
                // front, green
                0f, 0.5273f, 0.2656f, 1.0f, 0f, 0.5273f, 0.2656f, 1.0f, 0f, 0.5273f, 0.2656f, 1.0f, 0f, 0.5273f,
                0.2656f, 1.0f, 0f, 0.5273f,
                0.2656f,
                1.0f,
                0f,
                0.5273f,
                0.2656f,
                1.0f,

                // right, blue
                0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f,
                0.3398f, 0.9023f, 1.0f, 0.0f, 0.3398f, 0.9023f,
                1.0f,
                0.0f,
                0.3398f,
                0.9023f,
                1.0f,

                // back, also green
                0f, 0.5273f, 0.2656f, 1.0f, 0f, 0.5273f, 0.2656f, 1.0f, 0f, 0.5273f, 0.2656f, 1.0f, 0f, 0.5273f,
                0.2656f, 1.0f, 0f, 0.5273f, 0.2656f, 1.0f,
                0f,
                0.5273f,
                0.2656f,
                1.0f,

                // left, also blue
                0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f,
                0.3398f, 0.9023f, 1.0f, 0.0f, 0.3398f, 0.9023f, 1.0f, 0.0f,
                0.3398f,
                0.9023f,
                1.0f,

                // top, red
                0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f, 0.17578125f,
                0.125f, 1.0f, 0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f,
                0.17578125f, 0.125f,
                1.0f,

                // bottom, also red
                0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f, 0.17578125f,
                0.125f, 1.0f, 0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f, 0.17578125f, 0.125f, 1.0f, 0.8359375f,
                0.17578125f, 0.125f, 1.0f, };

        public static final float[] CUBE_FOUND_COLORS = new float[] {
                // front, yellow
                1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f, 1.0f, 0.6523f, 0.0f, 1.0f,
                1.0f,
                0.6523f,
                0.0f,
                1.0f,

                // right, yellow
                1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f,
                0.6523f,
                0.0f,
                1.0f,

                // back, yellow
                1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f,
                0.0f,
                1.0f,

                // left, yellow
                1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f,

                // top, yellow
                1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f,

                // bottom, yellow
                1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f,
                1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, 1.0f, 0.6523f, 0.0f, 1.0f, };

        public static final float[] CUBE_NORMALS = new float[] {
                // Front face
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f,
                1.0f,
                0.0f,
                0.0f,
                1.0f,

                // Right face
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f,
                1.0f,
                0.0f,
                0.0f,

                // Back face
                0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
                0.0f,
                0.0f,
                -1.0f,

                // Left face
                -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
                0.0f,
                0.0f,

                // Top face
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f,

                // Bottom face
                0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f };

    }

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private static final int COORDS_PER_VERTEX = 3;

    private FloatBuffer cubeVertices;
    private FloatBuffer cubeColors;
    private FloatBuffer cubeFoundColors;
    private FloatBuffer cubeNormals;

    private int cubeProgram;
    private int floorProgram;

    private int cubePositionParam;
    private int cubeNormalParam;
    private int cubeColorParam;
    private int cubeModelParam;
    private int cubeModelViewParam;
    private int cubeModelViewProjectionParam;
    private int cubeLightPosParam;

    private float[] modelCube;
    private float[] camera;
    private float[] view;
    private float[] modelViewProjection;
    private float[] modelView;

    private static final float TIME_DELTA = 0.9f;
    private static final float CAMERA_Z = 0.10f;

    private float rotation = TIME_DELTA;

    private final float[] lightPosInEyeSpace = new float[4];

    float[] view_test = { 0.9964263f, -0.02852817f, 0.07950288f, 0.0f, -0.07836007f, 0.039161384f, 0.9961556f,
            0.0f, -0.031531937f, -0.99882555f, 0.03678596f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

    float[] perspective = { 1.1917536f, 0.0f, 0.0f, 0.0f, 0.0f, 1.1917536f, 0.0f, 0.0f, 0.0f, 0.0f, -1.002002f,
            -1.0f, 0.0f, 0.0f, -0.2002002f, 0.0f
    };

    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 2.0f, 0.0f, 1.0f };

    private void initCube() {

        modelCube = new float[16];
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];

        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        ByteBuffer bbVertices = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        cubeVertices = bbVertices.asFloatBuffer();
        cubeVertices.put(WorldLayoutData.CUBE_COORDS);
        cubeVertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        cubeColors = bbColors.asFloatBuffer();
        cubeColors.put(WorldLayoutData.CUBE_COLORS);
        cubeColors.position(0);

        ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        cubeFoundColors = bbFoundColors.asFloatBuffer();
        cubeFoundColors.put(WorldLayoutData.CUBE_FOUND_COLORS);
        cubeFoundColors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        cubeNormals = bbNormals.asFloatBuffer();
        cubeNormals.put(WorldLayoutData.CUBE_NORMALS);
        cubeNormals.position(0);

        int vertexShader = NVRUtils.loadGLShader(this, GLES30.GL_VERTEX_SHADER, R.raw.light_vertex);
        int passthroughShader = NVRUtils.loadGLShader(this, GLES30.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

        cubeProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(cubeProgram, vertexShader);
        GLES30.glAttachShader(cubeProgram, passthroughShader);
        GLES30.glLinkProgram(cubeProgram);
        GLES30.glUseProgram(cubeProgram);
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(passthroughShader);

        NVRUtils.checkGLError("Cube program");

        cubePositionParam = GLES30.glGetAttribLocation(cubeProgram, "a_Position");
        cubeNormalParam = GLES30.glGetAttribLocation(cubeProgram, "a_Normal");
        cubeColorParam = GLES30.glGetAttribLocation(cubeProgram, "a_Color");

        cubeModelParam = GLES30.glGetUniformLocation(cubeProgram, "u_Model");
        cubeModelViewParam = GLES30.glGetUniformLocation(cubeProgram, "u_MVMatrix");
        cubeModelViewProjectionParam = GLES30.glGetUniformLocation(cubeProgram, "u_MVP");
        cubeLightPosParam = GLES30.glGetUniformLocation(cubeProgram, "u_LightPos");

        GLES30.glEnableVertexAttribArray(cubePositionParam);
        GLES30.glEnableVertexAttribArray(cubeNormalParam);
        GLES30.glEnableVertexAttribArray(cubeColorParam);

        Matrix.setIdentityM(modelCube, 0);
        Matrix.translateM(modelCube, 0, 0, 0, -8f);
    }

    private void drawCube(NVREye eye) {

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);


        rotation += TIME_DELTA;

        view_test = eye.getView();
        perspective = eye.getPerspectiveMatrix();

        Matrix.multiplyMM(view, 0, view_test, 0, camera, 0);

        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // cube 0
        Matrix.setIdentityM(modelCube, 0);
        Matrix.translateM(modelCube, 0, 0, 0, -8f);
        Matrix.rotateM(modelCube, 0, rotation, 0.5f, 0.5f, 1.0f);
        Matrix.multiplyMM(modelView, 0, view, 0, modelCube, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);


        Matrix.multiplyMM(matrixView, 0, eye.getView(), 0, matrixCamera, 0);
        Matrix.multiplyMM(matrixProjection, 0, matrixView, 0, matrixModel, 0);
        Matrix.multiplyMM(matrixProjection, 0, eye.getPerspectiveMatrix(), 0, matrixProjection, 0);

        GLES30.glUseProgram(cubeProgram);

        GLES30.glUniform3fv(cubeLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES30.glUniformMatrix4fv(cubeModelParam, 1, false, modelCube, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES30.glUniformMatrix4fv(cubeModelViewParam, 1, false, modelView, 0);

        GLES30.glEnableVertexAttribArray(cubePositionParam);
        // Set the position of the cube
        GLES30.glVertexAttribPointer(cubePositionParam, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, cubeVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES30.glUniformMatrix4fv(cubeModelViewProjectionParam, 1, false, modelViewProjection, 0);

        GLES30.glEnableVertexAttribArray(cubeNormalParam);
        // Set the normal positions of the cube, again for shading
        GLES30.glVertexAttribPointer(cubeNormalParam, 3, GLES30.GL_FLOAT, false, 0, cubeNormals);

        GLES30.glEnableVertexAttribArray(cubeColorParam);
        GLES30.glVertexAttribPointer(cubeColorParam, 4, GLES30.GL_FLOAT, false, 0,
                isLookingAtObject() ? cubeFoundColors : cubeColors);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        NVRUtils.checkGLError("Drawing cube");
    }
    private float[] headView = new float[16];
    private boolean isLookingAtObject() {
        float[] initVec = { 0, 0, 0, 1.0f };
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from
        // onNewFrame.
        getHMDRotationMatrix(headView);
        Matrix.multiplyMM(modelView, 0, headView, 0, modelCube, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }
}
