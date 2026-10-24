package com.example.midtermgraphicsdemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private var mPyramid: Triangle? = null
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private var touchAngleX = 0f
    private var touchAngleY = 0f

    fun addTouchRotation(dx: Float, dy: Float) {
        touchAngleX += dx
        touchAngleY += dy
    }

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 0.94f, 0.96f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        mPyramid = Triangle()
    }

    override fun onDrawFrame(unused: GL10?) {
        val scratch = FloatArray(16)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0.5f, -3.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        val time = SystemClock.uptimeMillis() % 6000L
        val autoAngle = 0.060f * (time.toInt())
        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.rotateM(rotationMatrix, 0, autoAngle + touchAngleX, 0.0f, 1.0f, 0.0f)
        Matrix.rotateM(rotationMatrix, 0, touchAngleY, 1.0f, 0.0f, 0.0f)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)
        mPyramid!!.draw(scratch)
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2.5f, 10f)
    }

    companion object {
        @JvmStatic
        fun loadShader(type: Int, shaderCode: String?): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }
    }
}