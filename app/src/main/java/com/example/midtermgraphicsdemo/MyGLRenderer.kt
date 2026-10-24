package com.example.midtermgraphicsdemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle mPyramid;
    private final float[] vPMatrix = new float[16], projectionMatrix = new float[16], viewMatrix = new float[16], rotationMatrix = new float[16];
    private  float touchAngleX = 0f, touchAngleY = 0f;

    public void addTouchRotation(float dx, float dy) { touchAngleX += dx; touchAngleY += dy; }

    @Override public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.12f, 0.13f, 0.22f, 1.0f); GLES20.glEnable(GLES20.GL_DEPTH_TEST); mPyramid = new Triangle();
    }
    @Override public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0.5f, -3.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        long time = SystemClock.uptimeMillis() % 6000L; float autoAngle = 0.060f * ((int) time);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, autoAngle + touchAngleX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(rotationMatrix, 0, touchAngleY, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0); mPyramid.draw(scratch);
    }
    @Override public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height); float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 2.5f, 10);
    }
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type); GLES20.glShaderSource(shader, shaderCode); GLES20.glCompileShader(shader); return shader;
    }
}