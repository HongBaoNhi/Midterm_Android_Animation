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
        color = Color.WHITE
        textSize = 38f
    }

    // Variable simulating tree growth over time (from 0.0 to 9.0)
    private var growthTime = 0f
    private val maxDepth = 9
    private var branchAngle = 25f // Default branching angle: 25 degrees
    private var branchCount = 0   // Counter for active branches drawn on Canvas

    private var growthAnimator: ValueAnimator? = null

    init {
        startTreeGrowth()
    }

    // Simulate tree growth over time using ValueAnimator
    fun startTreeGrowth() {
        growthAnimator?.cancel()
        growthAnimator = ValueAnimator.ofFloat(0f, maxDepth.toFloat()).apply {
            duration = 6000 // Tree grows through 9 levels over 6 seconds
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                growthTime = anim.animatedValue as Float
                invalidate() // Request Canvas redraw on each frame
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#181C24")) // Dark background to highlight Canvas strokes

        branchCount = 0

        // Tree root coordinates located at the bottom-center of the screen
        val rootX = width / 2f
        val rootY = height * 0.85f
        val trunkLength = height * 0.18f

        // Start recursive drawing from the trunk (-90 degrees points straight up)
        drawFractalBranch(canvas, rootX, rootY, trunkLength, -90f, 1)

        // Display technical parameters on screen for the Demo Video
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
        // Base case: stop recursion if depth exceeds current growth time or maximum depth
        if (depth > growthTime + 1 || depth > maxDepth) return

        // Calculate the growth ratio of the current branch level (from 0.0 to 1.0)
        val progress = (growthTime - (depth - 1)).coerceIn(0f, 1f)
        val currentLength = maxLength * progress

        // Trigonometric formula to calculate the branch endpoint coordinates (x2, y2)
        val rad = Math.toRadians(angleDeg.toDouble())
        val x2 = x1 + (currentLength * cos(rad)).toFloat()
        val y2 = y1 + (currentLength * sin(rad)).toFloat()

        // Stroke width decreases as recursion depth increases
        branchPaint.strokeWidth = (maxDepth - depth + 1) * 3f

        // Color transition: Brown for trunk/main branches, green for upper twigs and leaves
        branchPaint.color = if (depth <= 5) {
            Color.rgb(160, 102, 50)
        } else {
            Color.rgb(76, 209, 55)
        }

        // Draw the current branch onto the Canvas
        canvas.drawLine(x1, y1, x2, y2, branchPaint)
        branchCount++

        // Once the parent branch is fully grown (100%), recursively spawn 2 child branches
        if (progress >= 1f) {
            val childLength = maxLength * 0.75f
            drawFractalBranch(canvas, x2, y2, childLength, angleDeg - branchAngle, depth + 1)
            drawFractalBranch(canvas, x2, y2, childLength, angleDeg + branchAngle, depth + 1)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // Allow horizontal swipe to dynamically adjust the branching angle
                branchAngle = (15f + (event.x / width) * 35f).coerceIn(15f, 50f)
                invalidate()
            }
            MotionEvent.ACTION_DOWN -> {
                if (event.y > height * 0.65f) {
                    startTreeGrowth() // Tap the lower area to restart the growth animation
                }
            }
        }
        return true
    }
}