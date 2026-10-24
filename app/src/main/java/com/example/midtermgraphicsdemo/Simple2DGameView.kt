package com.example.midtermgraphicsdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class Simple2DGameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var score = 0
    private var isGameOver = false

    private var ballX = 400f
    private var ballY = 400f
    private val ballRadius = 32f
    private var ballVx = 12f
    private var ballVy = 14f

    private var paddleX = 500f
    private val paddleWidth = 260f
    private val paddleHeight = 34f

    private var targetX = 500f
    private var targetY = 450f
    private var animTick = 0f

    data class Ripple(var x: Float, var y: Float, var radius: Float, var alpha: Int, val color: Int)
    private val ripples = mutableListOf<Ripple>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#F8F5FC"))

        val paddleY = height - 180f

        if (!isGameOver && width > 0 && height > 0) {
            updateGame(paddleY)
        }

        val iterator = ripples.iterator()
        while (iterator.hasNext()) {
            val r = iterator.next()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.color = r.color
            paint.alpha = r.alpha
            canvas.drawCircle(r.x, r.y, r.radius, paint)

            r.radius += 8f
            r.alpha -= 12
            if (r.alpha <= 0) iterator.remove()
        }
        paint.style = Paint.Style.FILL
        paint.alpha = 255

        animTick += 0.12f
        val pulseRadius = 45f + (sin(animTick) * 10f) // Radius oscillates between 35 and 55
        paint.color = Color.parseColor("#FFA801")
        canvas.drawCircle(targetX, targetY, pulseRadius, paint)

        paint.color = Color.parseColor("#E86381")
        canvas.drawCircle(ballX, ballY, ballRadius, paint)

        paint.color = Color.parseColor("#9A7AC9")
        canvas.drawRoundRect(
            paddleX - paddleWidth / 2, paddleY,
            paddleX + paddleWidth / 2, paddleY + paddleHeight,
            18f, 18f, paint
        )

        paint.color = Color.parseColor("#514462")
        paint.textSize = 36f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("TOPIC: SIMPLE 2D GAME WITH CANVAS", 40f, 70f, paint)
        canvas.drawText("• Score: $score", 40f, 120f, paint)
        canvas.drawText("• Control: Swipe horizontally to bounce & hit target", 40f, 165f, paint)

        if (isGameOver) {
            paint.textSize = 55f
            paint.color = Color.parseColor("#C0392B")
            canvas.drawText("GAME OVER! Tap to restart", width / 6f, height / 2f, paint)
        } else {
            postInvalidateOnAnimation()
        }
    }

    private fun updateGame(paddleY: Float) {
        ballX += ballVx
        ballY += ballVy

        if (ballX - ballRadius <= 0 || ballX + ballRadius >= width) {
            ballVx = -ballVx
            addRipple(ballX, ballY, Color.WHITE)
        }

        if (ballY - ballRadius <= 200f) {
            ballVy = -ballVy
            addRipple(ballX, ballY, Color.WHITE)
        }

        if (ballY + ballRadius >= paddleY && ballY - ballRadius <= paddleY + paddleHeight) {
            if (ballX >= paddleX - paddleWidth / 2 && ballX <= paddleX + paddleWidth / 2 && ballVy > 0) {
                ballVy = -ballVy
                score += 10
                addRipple(ballX, paddleY, Color.parseColor("#00D8D6"))
            }
        }

        val distance = hypot(ballX - targetX, ballY - targetY)
        if (distance <= ballRadius + 45f) {
            score += 50
            addRipple(targetX, targetY, Color.parseColor("#FFDD59"))
            targetX = Random.nextInt(150, (width - 150).coerceAtLeast(200)).toFloat()
            targetY = Random.nextInt(300, (height / 2).coerceAtLeast(350)).toFloat()
        }

        if (ballY - ballRadius > height) {
            isGameOver = true
        }
    }

    private fun addRipple(x: Float, y: Float, color: Int) {
        ripples.add(Ripple(x, y, 20f, 255, color))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (isGameOver && event.action == MotionEvent.ACTION_DOWN) {
                    ballX = width / 2f
                    ballY = 300f
                    ballVx = 12f
                    ballVy = 14f
                    score = 0
                    isGameOver = false
                    invalidate()
                } else {
                    paddleX = event.x.coerceIn(paddleWidth / 2, width - paddleWidth / 2)
                }
            }
        }
        return true
    }
}