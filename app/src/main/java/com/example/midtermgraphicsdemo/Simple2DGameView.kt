package com.example.midtermgraphicsdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class Simple2DGameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var score = 0
    private var isGameOver = false

    // 1. Ball properties
    private var ballX = 400f
    private var ballY = 400f
    private val ballRadius = 32f
    private var ballVx = 12f
    private var ballVy = 14f

    // 2. Player paddle properties
    private var paddleX = 500f
    private val paddleWidth = 260f
    private val paddleHeight = 34f

    // 3. Target item with Pulse Scaling Animation
    private var targetX = 500f
    private var targetY = 450f
    private var animTick = 0f

    // 4. Expanding ripple wave animation on collision
    data class Ripple(var x: Float, var y: Float, var radius: Float, var alpha: Int, val color: Int)
    private val ripples = mutableListOf<Ripple>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#1E272E"))

        val paddleY = height - 180f

        if (!isGameOver && width > 0 && height > 0) {
            updateGame(paddleY)
        }

        // --- ANIMATION 1: Draw expanding Ripple Waves ---
        val iterator = ripples.iterator()
        while (iterator.hasNext()) {
            val r = iterator.next()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.color = r.color
            paint.alpha = r.alpha
            canvas.drawCircle(r.x, r.y, r.radius, paint)

            r.radius += 8f // Expand circle radius
            r.alpha -= 12  // Fade out over time
            if (r.alpha <= 0) iterator.remove()
        }
        paint.style = Paint.Style.FILL
        paint.alpha = 255

        // --- ANIMATION 2: Draw Target with Pulse Scaling Animation ---
        animTick += 0.12f
        val pulseRadius = 45f + (sin(animTick) * 10f) // Radius oscillates between 35 and 55
        paint.color = Color.parseColor("#FFDD59")
        canvas.drawCircle(targetX, targetY, pulseRadius, paint)

        // --- ANIMATION 3: Draw Ball & Player Paddle ---
        paint.color = Color.parseColor("#FF5E57")
        canvas.drawCircle(ballX, ballY, ballRadius, paint)

        paint.color = Color.parseColor("#00D8D6")
        canvas.drawRoundRect(
            paddleX - paddleWidth / 2, paddleY,
            paddleX + paddleWidth / 2, paddleY + paddleHeight,
            18f, 18f, paint
        )

        // Display topic info & current score
        paint.color = Color.WHITE
        paint.textSize = 38f
        canvas.drawText("TOPIC: SIMPLE 2D GAME WITH CANVAS", 40f, 70f, paint)
        canvas.drawText("• Score: $score", 40f, 125f, paint)
        canvas.drawText("• Control: Swipe horizontally to bounce & hit the yellow target", 40f, 175f, paint)

        if (isGameOver) {
            paint.textSize = 60f
            paint.color = Color.YELLOW
            canvas.drawText("GAME OVER! Tap to restart", width / 6f, height / 2f, paint)
        } else {
            // Maintain 60 FPS animation loop
            postInvalidateOnAnimation()
        }
    }

    private fun updateGame(paddleY: Float) {
        ballX += ballVx
        ballY += ballVy

        // Bounce off left and right walls
        if (ballX - ballRadius <= 0 || ballX + ballRadius >= width) {
            ballVx = -ballVx
            addRipple(ballX, ballY, Color.WHITE)
        }

        // Bounce off top ceiling
        if (ballY - ballRadius <= 200f) {
            ballVy = -ballVy
            addRipple(ballX, ballY, Color.WHITE)
        }

        // Check collision with the Player Paddle
        if (ballY + ballRadius >= paddleY && ballY - ballRadius <= paddleY + paddleHeight) {
            if (ballX >= paddleX - paddleWidth / 2 && ballX <= paddleX + paddleWidth / 2 && ballVy > 0) {
                ballVy = -ballVy
                score += 10
                addRipple(ballX, paddleY, Color.parseColor("#00D8D6"))
            }
        }

        // Check collision with the Yellow Target (Euclidean distance between two circle centers)
        val distance = hypot(ballX - targetX, ballY - targetY)
        if (distance <= ballRadius + 45f) {
            score += 50
            addRipple(targetX, targetY, Color.parseColor("#FFDD59"))
            // Respawn target at a random position
            targetX = Random.nextInt(150, (width - 150).coerceAtLeast(200)).toFloat()
            targetY = Random.nextInt(300, (height / 2).coerceAtLeast(350)).toFloat()
        }

        // Ball falls below the bottom edge -> Game Over
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