package com.example.midtermgraphicsdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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

        // Root container
        rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#0F172A")) // Dark modern background
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
            setTextColor(Color.parseColor("#F8FAFC"))
            gravity = Gravity.CENTER
        }

        val tvHeaderSub = TextView(this).apply {
            text = "Midterm Project • Select a demo below to launch"
            textSize = 13f
            setTextColor(Color.parseColor("#94A3B8"))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 50)
        }

        menuLayout.addView(tvHeaderTitle)
        menuLayout.addView(tvHeaderSub)

        // Create 4 Menu Cards (No member tags, Full English)
        val card1 = createMenuCard(
            title = "1. 2D Canvas Game",
            description = "Simple 2D bouncing game using Android Canvas",
            accentColorHex = "#00D8D6"
        ) {
            showDemoView("🎮 2D Canvas Game", Simple2DGameView(this@MainActivity))
        }

        val card2 = createMenuCard(
            title = "2. Fractal Tree Graphic",
            description = "Recursive fractal tree generation demo",
            accentColorHex = "#2ECC71"
        ) {
            showDemoView("🌳 Fractal Tree", FractalTreeView(this@MainActivity))
        }

        val card3 = createMenuCard(
            title = "3. Compose Animation Studio",
            description = "MotionLab Studio animation & drawing with Jetpack Compose",
            accentColorHex = "#A55EEA"
        ) {
            val intent = Intent(this@MainActivity, ComposeAnimationActivity::class.java)
            startActivity(intent)
        }

        @SuppressLint("ClickableViewAccessibility")
        val card4 = createMenuCard(
            title = "4. OpenGL ES 3D Pyramid",
            description = "3D pyramid model with touch rotation & OpenGL ES",
            accentColorHex = "#FFA801"
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
            showDemoView("📐 OpenGL 3D Pyramid", glSurfaceView)
        }

        menuLayout.addView(card1)
        menuLayout.addView(card2)
        menuLayout.addView(card3)
        menuLayout.addView(card4)

        scrollView.addView(menuLayout)

        // ==================== 2. DEMO CONTAINER VIEW ====================
        demoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setBackgroundColor(Color.parseColor("#10141C"))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Top bar in demo view
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 20, 20, 20)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#1E293B"))
        }

        val btnReturn = Button(this).apply {
            text = "⬅ Back to Menu"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(Color.parseColor("#334155"))
            }
            setPadding(30, 10, 30, 10)
            setOnClickListener {
                returnToMenu()
            }
        }

        tvDemoTitle = TextView(this).apply {
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#F8FAFC"))
            setPadding(30, 0, 0, 0)
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

        // Add both layouts to root
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
                setColor(Color.parseColor("#1E293B"))
                setStroke(3, Color.parseColor("#334155"))
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
            setTextColor(Color.parseColor("#94A3B8"))
            setPadding(0, 10, 0, 0)
        }

        card.addView(tvTitle)
        card.addView(tvDesc)

        return card
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
