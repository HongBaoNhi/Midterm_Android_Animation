package com.example.midtermgraphicsdemo

import android.opengl.GLES20
import com.example.midtermgraphicsdemo.MyGLRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle {
    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix; attribute vec4 vPosition; attribute vec4 aColor; varying vec4 vColor; void main() { gl_Position = uMVPMatrix * vPosition; vColor = aColor; }"
    private val fragmentShaderCode =
        "precision mediump float; varying vec4 vColor; void main() { gl_FragColor = vColor; }"

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val mProgram: Int

    init {
        val bb = ByteBuffer.allocateDirect(pyramidCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(pyramidCoords)
        vertexBuffer.position(0)
        val cb = ByteBuffer.allocateDirect(pyramidColors.size * 4)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(pyramidColors)
        colorBuffer.position(0)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES20.glUseProgram(mProgram)
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            COORDS_PER_VERTEX * 4,
            vertexBuffer
        )
        val colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer)
        val vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pyramidCoords.size / COORDS_PER_VERTEX)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    companion object {
        const val COORDS_PER_VERTEX: Int = 3

        var pyramidCoords: FloatArray = floatArrayOf(
            0.0f, 0.7f, 0.0f, -0.6f, -0.5f, 0.6f, 0.6f, -0.5f, 0.6f,
            0.0f, 0.7f, 0.0f, 0.6f, -0.5f, 0.6f, 0.6f, -0.5f, -0.6f,
            0.0f, 0.7f, 0.0f, 0.6f, -0.5f, -0.6f, -0.6f, -0.5f, -0.6f,
            0.0f, 0.7f, 0.0f, -0.6f, -0.5f, -0.6f, -0.6f, -0.5f, 0.6f
        )

        var pyramidColors: FloatArray = floatArrayOf(
            1.0f, 0.2f, 0.3f, 1.0f, 0.0f, 0.9f, 0.8f, 1.0f, 0.6f, 0.2f, 1.0f, 1.0f,
            1.0f, 0.2f, 0.3f, 1.0f, 0.6f, 0.2f, 1.0f, 1.0f, 1.0f, 0.9f, 0.1f, 1.0f,
            1.0f, 0.2f, 0.3f, 1.0f, 1.0f, 0.9f, 0.1f, 1.0f, 0.1f, 0.9f, 0.3f, 1.0f,
            1.0f, 0.2f, 0.3f, 1.0f, 0.1f, 0.9f, 0.3f, 1.0f, 0.0f, 0.9f, 0.8f, 1.0f
        )
    }
}