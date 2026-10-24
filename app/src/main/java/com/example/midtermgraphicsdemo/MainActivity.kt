package com.example.midtermgraphicsdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.GradientDrawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var rootLayout: FrameLayout
    private lateinit var menuLayout: LinearLayout
    private lateinit var demoContainer: LinearLayout
    private lateinit var demoContentArea: FrameLayout
    private lateinit var tvDemoTitle: TextView

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root container - Lavender Blush Light Pink Theme
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#FFF0F5"))
        }

        // ==================== 1. MAIN MENU DASHBOARD ====================
        val scrollView = ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        menuLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 60, 40, 60)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Header Title
        val tvHeaderTitle = TextView(this).apply {
            text = "GRAPHICS & ANIMATION"
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
            gravity = Gravity.CENTER
        }

        val tvHeaderSub = TextView(this).apply {
            text = "• Select a demo below to launch •"
            textSize = 13f
            setTextColor(Color.parseColor("#917F9F"))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 50)
        }

        menuLayout.addView(tvHeaderTitle)
        menuLayout.addView(tvHeaderSub)

        // ==================== 6 INDIVIDUAL DEMO CARDS ====================
        val card1 = createMenuCard(
            title = "1. 2D Canvas Game",
            description = "Interactive 2D bouncing ball game built with Canvas",
            accentColorHex = "#00A8A8"
        ) {
            showDemoView("🎮 1. 2D Canvas Game", Simple2DGameView(this@MainActivity))
        }

        val card2 = createMenuCard(
            title = "2. Fractal Tree Graphic",
            description = "Recursive fractal tree generation with touch control",
            accentColorHex = "#27AE60"
        ) {
            showDemoView("🌳 2. Fractal Tree Graphic", FractalTreeView(this@MainActivity))
        }

        val card3 = createMenuCard(
            title = "3. Compose Animation Studio",
            description = "MotionLab pastel animation studio built with Jetpack Compose",
            accentColorHex = "#9A7AC9"
        ) {
            val intent = Intent(this@MainActivity, ComposeAnimationActivity::class.java)
            startActivity(intent)
        }

        val card4 = createMenuCard(
            title = "4. Lottie Vector Studio",
            description = "Interactive Lottie JSON vector playback & frame scrubber",
            accentColorHex = "#E86381"
        ) {
            val intent = Intent(this@MainActivity, LottieAnimationActivity::class.java)
            startActivity(intent)
        }

        @SuppressLint("ClickableViewAccessibility")
        val card5 = createMenuCard(
            title = "5. OpenGL ES 3D Pyramid",
            description = "Rotating 3D pyramid with touch rotation & OpenGL ES",
            accentColorHex = "#D35400"
        ) {
            val glSurfaceView = GLSurfaceView(this@MainActivity).apply {
                setEGLContextClientVersion(2)
                val renderer = MyGLRenderer()
                setRenderer(renderer)

                setOnTouchListener { v, event ->
                    val x = event.x
                    val y = event.y
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        renderer.addTouchRotation((x - previousX) * 0.5f, (y - previousY) * 0.5f)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        v.performClick()
                    }
                    previousX = x
                    previousY = y
                    true
                }
            }
            showDemoView("📐 5. OpenGL ES 3D Pyramid", glSurfaceView)
        }

        val card6 = createMenuCard(
            title = "6. Vector Bell Animation",
            description = "Animated Vector Drawable (AVD) bell shake on tap",
            accentColorHex = "#C0392B"
        ) {
            showDemoView("🔔 6. Vector Bell Animation", createVectorBellView())
        }

        menuLayout.addView(card1)
        menuLayout.addView(card2)
        menuLayout.addView(card3)
        menuLayout.addView(card4)
        menuLayout.addView(card5)
        menuLayout.addView(card6)

        scrollView.addView(menuLayout)

        // ==================== DEMO CONTAINER VIEW ====================
        demoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setBackgroundColor(Color.parseColor("#FFF0F5"))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Top bar in demo view
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(30, 24, 30, 16)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.TRANSPARENT)
        }

        val btnReturn = TextView(this).apply {
            text = "←"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
            setPadding(10, 0, 30, 0)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                returnToMenu()
            }
        }

        tvDemoTitle = TextView(this).apply {
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
        }

        topBar.addView(btnReturn)
        topBar.addView(tvDemoTitle)

        demoContentArea = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        demoContainer.addView(topBar)
        demoContainer.addView(demoContentArea)

        rootLayout.addView(scrollView)
        rootLayout.addView(demoContainer)

        setContentView(rootLayout)
    }

    private fun createMenuCard(
        title: String,
        description: String,
        accentColorHex: String,
        onClick: () -> Unit
    ): LinearLayout {
        val accentColor = Color.parseColor(accentColorHex)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 32, 36, 32)
            isClickable = true
            isFocusable = true
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 32f
                setColor(Color.WHITE)
                setStroke(2, Color.parseColor("#F3D5E0"))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 28)
            }
            setOnClickListener { onClick() }
        }

        val tvTitle = TextView(this).apply {
            text = title
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(accentColor)
        }

        val tvDesc = TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(Color.parseColor("#917F9F"))
            setPadding(0, 10, 0, 0)
        }

        card.addView(tvTitle)
        card.addView(tvDesc)

        return card
    }

    private fun createVectorBellView(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#FFF0F5")) // Lavender Blush light pink
        }

        val imageView = ImageView(this).apply {
            val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.avd_bell)
            setImageDrawable(drawable)
            isClickable = true
            layoutParams = LinearLayout.LayoutParams(420, 420).apply {
                gravity = Gravity.CENTER
            }
            setOnClickListener {
                val d = drawable
                if (d is AnimatedVectorDrawable) {
                    d.start()
                }
            }
        }

        val tvHint = TextView(this).apply {
            text = "🔔 Tap the bell to trigger Animated Vector Drawable (AVD)!"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
            gravity = Gravity.CENTER
            setPadding(20, 40, 20, 0)
        }

        layout.addView(imageView)
        layout.addView(tvHint)
        return layout
    }

    private fun showDemoView(title: String, view: View) {
        tvDemoTitle.text = title
        demoContentArea.removeAllViews()
        demoContentArea.addView(view)
        menuLayout.parent?.let { (it as View).visibility = View.GONE }
        demoContainer.visibility = View.VISIBLE
    }

    private fun returnToMenu() {
        demoContentArea.removeAllViews()
        demoContainer.visibility = View.GONE
        menuLayout.parent?.let { (it as View).visibility = View.VISIBLE }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (demoContainer.visibility == View.VISIBLE) {
            returnToMenu()
        } else {
            super.onBackPressed()
        }
    }
}
