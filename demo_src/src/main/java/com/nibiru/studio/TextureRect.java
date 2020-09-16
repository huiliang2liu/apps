package com.nibiru.studio;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TextureRect {
    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maTexCoorHandle;

    String mVertexShader;
    String mFragmentShader;

    private FloatBuffer mVertexBuffer;
    FloatBuffer mTexCoorBuffer;
    int vCount;

    float width;
    float height;

    float sEnd;
    float tEnd;

    public TextureRect(Context mv, float width, float height, float sEnd, float tEnd) {

        this.width = width;
        this.height = height;
        this.sEnd = sEnd;
        this.tEnd = tEnd;
        initVertexData();
        initShader(mv);

    }

    public void initVertexData() {
        vCount = 6;
        float vertices[] =
                {
                        -width / 2.0f, height / 2.0f, 0,
                        -width / 2.0f, -height / 2.0f, 0,
                        width / 2.0f, height / 2.0f, 0,

                        -width / 2.0f, -height / 2.0f, 0,
                        width / 2.0f, -height / 2.0f, 0,
                        width / 2.0f, height / 2.0f, 0,
                };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float texCoor[] = new float[]
                {
                        0, 0, 0, tEnd, sEnd, 0,
                        0, tEnd, sEnd, tEnd, sEnd, 0
                };
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);

    }

    public void initShader(Context mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("util/vertex_tex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("util/frag_tex.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
    }

    public void drawSelf(int texId, float[] matrix) {
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, matrix, 0);
        GLES20.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        2 * 4,
                        mTexCoorBuffer
                );
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

}
