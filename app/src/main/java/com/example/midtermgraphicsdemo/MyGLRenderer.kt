package com.example.midtermgraphicsdemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    private val shapes = mutableMapOf<ShapeType, GLShape>()
    @Volatile
    var currentShapeType: ShapeType = ShapeType.PYRAMID

    @Volatile
    var roughness: Float = 0.3f

    @Volatile
    var shininess: Float = 0.8f

    @Volatile
    var customColor: FloatArray? = null

    @Volatile
    var isRotating: Boolean = true

    private var lastTime: Long = 0L
    private var totalAutoAngle: Float = 0f

    fun addTouchRotation(dx: Float, dy: Float) {
        touchAngleX += dx
        touchAngleY += dy
    }

    fun resetView() {
        touchAngleX = 0f
        touchAngleY = 0f
    }

    fun setShape(shapeType: ShapeType) {
        currentShapeType = shapeType
    }

    fun setMaterialProperties(roughness: Float, shininess: Float) {
        this.roughness = roughness
        this.shininess = shininess
    }

    fun setSurfaceColor(color: FloatArray?) {
        this.customColor = color
    }

    fun toggleRotation(): Boolean {
        isRotating = !isRotating
        return isRotating
    }

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private var touchAngleX = 0f
    private var touchAngleY = 0f

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 0.94f, 0.96f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        shapes[ShapeType.PYRAMID] = GLShape(ShapeType.PYRAMID)
        shapes[ShapeType.CUBE] = GLShape(ShapeType.CUBE)
        shapes[ShapeType.SPHERE] = GLShape(ShapeType.SPHERE)
        shapes[ShapeType.TORUS] = GLShape(ShapeType.TORUS)
    }

    override fun onDrawFrame(unused: GL10?) {
        val mvpMatrix = FloatArray(16)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0.5f, -3.5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val currentTime = SystemClock.uptimeMillis()
        if (lastTime == 0L) lastTime = currentTime
        val delta = currentTime - lastTime
        lastTime = currentTime

        if (isRotating) {
            totalAutoAngle += 0.060f * delta
        }

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, totalAutoAngle + touchAngleX, 0.0f, 1.0f, 0.0f)
        Matrix.rotateM(modelMatrix, 0, touchAngleY, 1.0f, 0.0f, 0.0f)
        Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, modelMatrix, 0)

        shapes[currentShapeType]?.draw(
            mvpMatrix = mvpMatrix,
            modelMatrix = modelMatrix,
            roughness = roughness,
            shininess = shininess,
            customColor = customColor
        )
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
