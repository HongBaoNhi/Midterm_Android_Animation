package com.example.midtermgraphicsdemo

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

class FractalTreeView(context: Context) : View(context) {

    private val branchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val infoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#514462")
        textSize = 36f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    private var growthTime = 0f
    private val maxDepth = 9
    private var branchAngle = 25f
    private var branchCount = 0

    private var growthAnimator: ValueAnimator? = null

    init {
        startTreeGrowth()
    }
    fun startTreeGrowth() {
        growthAnimator?.cancel()
        growthAnimator = ValueAnimator.ofFloat(0f, maxDepth.toFloat()).apply {
            duration = 6000
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                growthTime = anim.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#FFF0F5"))

        branchCount = 0

        val rootX = width / 2f
        val rootY = height * 0.85f
        val trunkLength = height * 0.18f

        drawFractalBranch(canvas, rootX, rootY, trunkLength, -90f, 1)

        val currentStage = growthTime.toInt().coerceAtMost(maxDepth)
        canvas.drawText("TOPIC: SIMULATING A FRACTAL TREE", 40f, 70f, infoPaint)
        canvas.drawText("• Recursion Depth: $currentStage / $maxDepth", 40f, 125f, infoPaint)
        canvas.drawText("• Active Branches: $branchCount", 40f, 175f, infoPaint)
        canvas.drawText("• Branch Angle: ${branchAngle.toInt()}° (Swipe horizontally to adjust)", 40f, 225f, infoPaint)
        canvas.drawText("👉 Tap the bottom half of the screen to regrow", 40f, height - 60f, infoPaint)
    }

    private fun drawFractalBranch(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        maxLength: Float,
        angleDeg: Float,
        depth: Int
    ) {
        if (depth > growthTime + 1 || depth > maxDepth) return

        val progress = (growthTime - (depth - 1)).coerceIn(0f, 1f)
        val currentLength = maxLength * progress

        val rad = Math.toRadians(angleDeg.toDouble())
        val x2 = x1 + (currentLength * cos(rad)).toFloat()
        val y2 = y1 + (currentLength * sin(rad)).toFloat()

        branchPaint.strokeWidth = (maxDepth - depth + 1) * 3f

        branchPaint.color = if (depth <= 5) {
            Color.rgb(160, 102, 50)
        } else {
            Color.rgb(76, 209, 55)
        }

        canvas.drawLine(x1, y1, x2, y2, branchPaint)
        branchCount++

        if (progress >= 1f) {
            val childLength = maxLength * 0.75f
            drawFractalBranch(canvas, x2, y2, childLength, angleDeg - branchAngle, depth + 1)
            drawFractalBranch(canvas, x2, y2, childLength, angleDeg + branchAngle, depth + 1)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                branchAngle = (15f + (event.x / width) * 35f).coerceIn(15f, 50f)
                invalidate()
            }
            MotionEvent.ACTION_DOWN -> {
                if (event.y > height * 0.65f) {
                    startTreeGrowth()
                }
            }
        }
        return true
    }
}