package com.example.midtermgraphicsdemo

import android.opengl.GLES20
import com.example.midtermgraphicsdemo.MyGLRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

enum class ShapeType {
    PYRAMID, CUBE, SPHERE, TORUS
}

class GLShape(val shapeType: ShapeType) {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        attribute vec4 vPosition;
        attribute vec3 aNormal;
        attribute vec4 aColor;
        varying vec3 vPositionWorld;
        varying vec3 vNormalWorld;
        varying vec4 vColor;
        void main() {
            vec4 worldPos = uModelMatrix * vPosition;
            vPositionWorld = worldPos.xyz;
            vNormalWorld = mat3(uModelMatrix) * aNormal;
            vColor = aColor;
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec3 uLightPos;
        uniform vec3 uViewPos;
        uniform vec4 uBaseColor;
        uniform float uUseCustomColor;
        uniform float uRoughness;
        uniform float uShininess;
        varying vec3 vPositionWorld;
        varying vec3 vNormalWorld;
        varying vec4 vColor;
        void main() {
            vec3 N = normalize(vNormalWorld);
            vec3 L = normalize(uLightPos - vPositionWorld);
            vec3 V = normalize(uViewPos - vPositionWorld);
            vec3 H = normalize(L + V);

            vec4 surfaceColor = (uUseCustomColor > 0.5) ? uBaseColor : vColor;

            vec3 ambient = 0.3 * surfaceColor.rgb;
            float diff = max(dot(N, L), 0.0);
            vec3 diffuse = 0.6 * diff * surfaceColor.rgb;

            float specPower = mix(256.0, 2.0, clamp(uRoughness, 0.01, 1.0));
            float spec = pow(max(dot(N, H), 0.0), specPower);
            vec3 specular = vec3(1.0, 1.0, 1.0) * spec * uShininess;

            vec3 finalColor = ambient + diffuse + specular;
            gl_FragColor = vec4(finalColor, surfaceColor.a);
        }
    """.trimIndent()

    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val mProgram: Int
    private val vertexCount: Int

    init {
        val (coords, normals, colors) = when (shapeType) {
            ShapeType.PYRAMID -> generatePyramid()
            ShapeType.CUBE -> generateCube()
            ShapeType.SPHERE -> generateSphere(24, 24, 0.65f)
            ShapeType.TORUS -> generateTorus(0.45f, 0.20f, 32, 20)
        }

        vertexCount = coords.size / 3

        val bb = ByteBuffer.allocateDirect(coords.size * 4).order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer().put(coords)
        vertexBuffer.position(0)

        val nb = ByteBuffer.allocateDirect(normals.size * 4).order(ByteOrder.nativeOrder())
        normalBuffer = nb.asFloatBuffer().put(normals)
        normalBuffer.position(0)

        val cb = ByteBuffer.allocateDirect(colors.size * 4).order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer().put(colors)
        colorBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
    }

    fun draw(
        mvpMatrix: FloatArray,
        modelMatrix: FloatArray,
        roughness: Float,
        shininess: Float,
        customColor: FloatArray?
    ) {
        GLES20.glUseProgram(mProgram)

        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)

        val normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal")
        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer)

        val colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val modelMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uModelMatrix")
        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0)

        val lightPosHandle = GLES20.glGetUniformLocation(mProgram, "uLightPos")
        GLES20.glUniform3f(lightPosHandle, 2.0f, 3.5f, 3.0f)

        val viewPosHandle = GLES20.glGetUniformLocation(mProgram, "uViewPos")
        GLES20.glUniform3f(viewPosHandle, 0.0f, 0.5f, -3.5f)

        val roughnessHandle = GLES20.glGetUniformLocation(mProgram, "uRoughness")
        GLES20.glUniform1f(roughnessHandle, roughness)

        val shininessHandle = GLES20.glGetUniformLocation(mProgram, "uShininess")
        GLES20.glUniform1f(shininessHandle, shininess)

        val useCustomColorHandle = GLES20.glGetUniformLocation(mProgram, "uUseCustomColor")
        val baseColorHandle = GLES20.glGetUniformLocation(mProgram, "uBaseColor")

        if (customColor != null) {
            GLES20.glUniform1f(useCustomColorHandle, 1.0f)
            GLES20.glUniform4fv(baseColorHandle, 1, customColor, 0)
        } else {
            GLES20.glUniform1f(useCustomColorHandle, 0.0f)
            GLES20.glUniform4f(baseColorHandle, 1.0f, 1.0f, 1.0f, 1.0f)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    companion object {
        private fun generatePyramid(): Triple<FloatArray, FloatArray, FloatArray> {
            val coordsList = mutableListOf<Float>()
            val normalsList = mutableListOf<Float>()
            val colorsList = mutableListOf<Float>()

            val apex = floatArrayOf(0.0f, 0.7f, 0.0f)
            val p0 = floatArrayOf(-0.6f, -0.5f, 0.6f)
            val p1 = floatArrayOf(0.6f, -0.5f, 0.6f)
            val p2 = floatArrayOf(0.6f, -0.5f, -0.6f)
            val p3 = floatArrayOf(-0.6f, -0.5f, -0.6f)

            val faceColors = arrayOf(
                floatArrayOf(1.0f, 0.3f, 0.4f, 1.0f),
                floatArrayOf(0.2f, 0.8f, 0.7f, 1.0f),
                floatArrayOf(0.9f, 0.8f, 0.2f, 1.0f),
                floatArrayOf(0.6f, 0.4f, 0.9f, 1.0f),
                floatArrayOf(0.4f, 0.7f, 0.9f, 1.0f),
                floatArrayOf(0.4f, 0.7f, 0.9f, 1.0f)
            )

            val triangles = listOf(
                Triple(apex, p0, p1),
                Triple(apex, p1, p2),
                Triple(apex, p2, p3),
                Triple(apex, p3, p0),
                Triple(p0, p2, p1),
                Triple(p0, p3, p2)
            )

            triangles.forEachIndexed { idx, (a, b, c) ->
                val ux = b[0] - a[0]; val uy = b[1] - a[1]; val uz = b[2] - a[2]
                val vx = c[0] - a[0]; val vy = c[1] - a[1]; val vz = c[2] - a[2]
                var nx = uy * vz - uz * vy
                var ny = uz * vx - ux * vz
                var nz = ux * vy - uy * vx
                val len = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
                if (len > 0.0001f) { nx /= len; ny /= len; nz /= len }

                val col = faceColors[idx]

                fun addV(v: FloatArray) {
                    coordsList.add(v[0]); coordsList.add(v[1]); coordsList.add(v[2])
                    normalsList.add(nx); normalsList.add(ny); normalsList.add(nz)
                    colorsList.add(col[0]); colorsList.add(col[1]); colorsList.add(col[2]); colorsList.add(col[3])
                }

                addV(a); addV(b); addV(c)
            }

            return Triple(coordsList.toFloatArray(), normalsList.toFloatArray(), colorsList.toFloatArray())
        }

        private fun generateCube(): Triple<FloatArray, FloatArray, FloatArray> {
            val coordsList = mutableListOf<Float>()
            val normalsList = mutableListOf<Float>()
            val colorsList = mutableListOf<Float>()

            val faceNormals = arrayOf(
                floatArrayOf(0f, 0f, 1f),
                floatArrayOf(0f, 0f, -1f),
                floatArrayOf(0f, 1f, 0f),
                floatArrayOf(0f, -1f, 0f),
                floatArrayOf(1f, 0f, 0f),
                floatArrayOf(-1f, 0f, 0f)
            )

            val faceVertices = arrayOf(
                floatArrayOf(-0.5f, -0.5f, 0.5f,  0.5f, -0.5f, 0.5f,  0.5f, 0.5f, 0.5f,
                           -0.5f, -0.5f, 0.5f,  0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f),
                floatArrayOf(-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,  0.5f, 0.5f, -0.5f,
                           -0.5f, -0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, -0.5f, -0.5f),
                floatArrayOf(-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f,  0.5f, 0.5f, 0.5f,
                           -0.5f, 0.5f, -0.5f,  0.5f, 0.5f, 0.5f,  0.5f, 0.5f, -0.5f),
                floatArrayOf(-0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, 0.5f,
                           -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f),
                floatArrayOf(0.5f, -0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, 0.5f, 0.5f,
                           0.5f, -0.5f, -0.5f,  0.5f, 0.5f, 0.5f,  0.5f, -0.5f, 0.5f),
                floatArrayOf(-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
                           -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f)
            )

            val faceColors = arrayOf(
                floatArrayOf(1.0f, 0.4f, 0.5f, 1.0f),
                floatArrayOf(0.2f, 0.8f, 0.6f, 1.0f),
                floatArrayOf(1.0f, 0.8f, 0.2f, 1.0f),
                floatArrayOf(0.6f, 0.4f, 0.9f, 1.0f),
                floatArrayOf(1.0f, 0.5f, 0.2f, 1.0f),
                floatArrayOf(0.2f, 0.6f, 1.0f, 1.0f)
            )

            for (face in 0 until 6) {
                val norm = faceNormals[face]
                val verts = faceVertices[face]
                val col = faceColors[face]
                var i = 0
                while (i < verts.size) {
                    coordsList.add(verts[i]); coordsList.add(verts[i+1]); coordsList.add(verts[i+2])
                    normalsList.add(norm[0]); normalsList.add(norm[1]); normalsList.add(norm[2])
                    colorsList.add(col[0]); colorsList.add(col[1]); colorsList.add(col[2]); colorsList.add(col[3])
                    i += 3
                }
            }

            return Triple(coordsList.toFloatArray(), normalsList.toFloatArray(), colorsList.toFloatArray())
        }

        private fun generateSphere(stacks: Int, slices: Int, radius: Float): Triple<FloatArray, FloatArray, FloatArray> {
            val coordsList = mutableListOf<Float>()
            val normalsList = mutableListOf<Float>()
            val colorsList = mutableListOf<Float>()

            val pi = Math.PI.toFloat()

            for (i in 0 until stacks) {
                val phi1 = pi * i / stacks - pi / 2
                val phi2 = pi * (i + 1) / stacks - pi / 2

                val y1 = radius * sin(phi1)
                val r1 = radius * cos(phi1)
                val y2 = radius * sin(phi2)
                val r2 = radius * cos(phi2)

                for (j in 0 until slices) {
                    val theta1 = 2 * pi * j / slices
                    val theta2 = 2 * pi * (j + 1) / slices

                    val x11 = r1 * cos(theta1); val z11 = r1 * sin(theta1)
                    val x12 = r1 * cos(theta2); val z12 = r1 * sin(theta2)
                    val x21 = r2 * cos(theta1); val z21 = r2 * sin(theta1)
                    val x22 = r2 * cos(theta2); val z22 = r2 * sin(theta2)

                    fun addVertex(x: Float, y: Float, z: Float) {
                        coordsList.add(x); coordsList.add(y); coordsList.add(z)

                        val nx = x / radius
                        val ny = y / radius
                        val nz = z / radius
                        normalsList.add(nx); normalsList.add(ny); normalsList.add(nz)

                        val r = (x / radius + 1f) / 2f
                        val g = (y / radius + 1f) / 2f
                        val b = (z / radius + 1f) / 2f
                        colorsList.add(r.coerceIn(0.2f, 1f))
                        colorsList.add(g.coerceIn(0.2f, 1f))
                        colorsList.add(b.coerceIn(0.2f, 1f))
                        colorsList.add(1.0f)
                    }

                    addVertex(x11, y1, z11)
                    addVertex(x21, y2, z21)
                    addVertex(x12, y1, z12)

                    addVertex(x12, y1, z12)
                    addVertex(x21, y2, z21)
                    addVertex(x22, y2, z22)
                }
            }

            return Triple(coordsList.toFloatArray(), normalsList.toFloatArray(), colorsList.toFloatArray())
        }

        private fun generateTorus(
            R: Float,
            r: Float,
            majorSegments: Int,
            minorSegments: Int
        ): Triple<FloatArray, FloatArray, FloatArray> {
            val coordsList = mutableListOf<Float>()
            val normalsList = mutableListOf<Float>()
            val colorsList = mutableListOf<Float>()

            val pi = Math.PI.toFloat()

            for (i in 0 until majorSegments) {
                val u1 = 2 * pi * i / majorSegments
                val u2 = 2 * pi * (i + 1) / majorSegments

                for (j in 0 until minorSegments) {
                    val v1 = 2 * pi * j / minorSegments
                    val v2 = 2 * pi * (j + 1) / minorSegments

                    fun addVertex(u: Float, v: Float) {
                        val cosU = cos(u); val sinU = sin(u)
                        val cosV = cos(v); val sinV = sin(v)

                        val x = (R + r * cosV) * cosU
                        val y = r * sinV
                        val z = (R + r * cosV) * sinU

                        coordsList.add(x); coordsList.add(y); coordsList.add(z)

                        val nx = cosV * cosU
                        val ny = sinV
                        val nz = cosV * sinU
                        normalsList.add(nx); normalsList.add(ny); normalsList.add(nz)

                        val red = (cosU * 0.5f + 0.5f).coerceIn(0.3f, 1.0f)
                        val green = (sinV * 0.5f + 0.5f).coerceIn(0.3f, 1.0f)
                        val blue = (sinU * 0.5f + 0.5f).coerceIn(0.3f, 1.0f)
                        colorsList.add(red); colorsList.add(green); colorsList.add(blue); colorsList.add(1.0f)
                    }

                    addVertex(u1, v1)
                    addVertex(u2, v1)
                    addVertex(u1, v2)

                    addVertex(u1, v2)
                    addVertex(u2, v1)
                    addVertex(u2, v2)
                }
            }

            return Triple(coordsList.toFloatArray(), normalsList.toFloatArray(), colorsList.toFloatArray())
        }
    }
}
