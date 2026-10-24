package com.example.midtermgraphicsdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.GradientDrawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.ComposeView
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

        // ==================== CARD 1: MEMBER 1 (Compose & Lottie Studio) ====================
        val card1 = createMenuCard(
            title = "1. Compose & Lottie Studio",
            description = "MotionLab Compose Canvas Studio & Lottie Vector Animation",
            tag = "Member 1",
            accentColorHex = "#A55EEA"
        ) {
            showMember1CombinedDemo()
        }

        // ==================== CARD 2: MEMBER 2 (OpenGL 3D & Vector Anim) ====================
        val card2 = createMenuCard(
            title = "2. OpenGL 3D & Vector Animation",
            description = "3D Pyramid with OpenGL ES & Bell Shake Vector Animation (AVD)",
            tag = "Member 2",
            accentColorHex = "#FFA801"
        ) {
            showMember2CombinedDemo()
        }

        // ==================== CARD 3: MEMBER 3 (Game 2D & Fractal Tree) ====================
        val card3 = createMenuCard(
            title = "3. 2D Game & Fractal Tree",
            description = "Simple 2D bouncing game & Recursive fractal tree generation",
            tag = "Member 3",
            accentColorHex = "#00D8D6"
        ) {
            showMember3CombinedDemo()
        }

        menuLayout.addView(card1)
        menuLayout.addView(card2)
        menuLayout.addView(card3)

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
        tag: String,
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

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val tvTitle = TextView(this).apply {
            text = title
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#F1F5F9"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvTag = TextView(this).apply {
            text = tag
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(accentColor)
            setPadding(20, 8, 20, 8)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f
                setColor(Color.parseColor("#0F172A"))
                setStroke(2, accentColor)
            }
        }

        topRow.addView(tvTitle)
        topRow.addView(tvTag)

        val tvDesc = TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(Color.parseColor("#94A3B8"))
            setPadding(0, 12, 0, 0)
        }

        card.addView(topRow)
        card.addView(tvDesc)

        return card
    }

    // ==================== MEMBER 1 DEMO (Compose MotionLab & Lottie Studio) ====================
    private fun showMember1CombinedDemo() {
        val member1Layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Sub-navigation bar between Compose Canvas Studio and Lottie Studio
        val subNavBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#1E293B"))
        }

        val contentContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Compose View 1: MotionLab Studio
        val composeView1 = ComposeView(this).apply {
            setContent {
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = androidx.compose.ui.graphics.Color(0xFF9A7AC9),
                        onPrimary = androidx.compose.ui.graphics.Color.White,
                        background = androidx.compose.ui.graphics.Color(0xFFF6F1FB),
                        surface = androidx.compose.ui.graphics.Color.White,
                        onSurface = androidx.compose.ui.graphics.Color(0xFF514462)
                    )
                ) {
                    MotionLabApp(onBackClick = { returnToMenu() })
                }
            }
        }

        // Compose View 2: Lottie Studio
        val composeView2 = ComposeView(this).apply {
            setContent {
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = androidx.compose.ui.graphics.Color(0xFF9879C9),
                        onPrimary = androidx.compose.ui.graphics.Color.White,
                        surface = androidx.compose.ui.graphics.Color(0xFFFEFCFF),
                        background = androidx.compose.ui.graphics.Color(0xFFF8F5FC)
                    )
                ) {
                    LottieStudioScreen(onBackClick = { returnToMenu() })
                }
            }
        }

        val btnCompose = Button(this)
        val btnLottie = Button(this)

        fun updateSubButtons(isCompose: Boolean) {
            val activeColor = Color.parseColor("#A55EEA")
            val inactiveColor = Color.parseColor("#334155")

            btnCompose.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(if (isCompose) activeColor else inactiveColor)
            }
            btnCompose.setTextColor(Color.WHITE)

            btnLottie.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(if (!isCompose) activeColor else inactiveColor)
            }
            btnLottie.setTextColor(Color.WHITE)
        }

        btnCompose.apply {
            text = "✨ 1. Compose Studio"
            isAllCaps = false
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(30, 10, 30, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener {
                updateSubButtons(true)
                contentContainer.removeAllViews()
                contentContainer.addView(composeView1)
            }
        }

        btnLottie.apply {
            text = "🌸 2. Lottie Studio"
            isAllCaps = false
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(30, 10, 30, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener {
                updateSubButtons(false)
                contentContainer.removeAllViews()
                contentContainer.addView(composeView2)
            }
        }

        subNavBar.addView(btnCompose)
        subNavBar.addView(btnLottie)

        member1Layout.addView(subNavBar)
        member1Layout.addView(contentContainer)

        // Default to Compose Studio view
        updateSubButtons(true)
        contentContainer.addView(composeView1)

        showDemoView("✨ Compose & Lottie Studio", member1Layout)
    }

    // ==================== MEMBER 2 DEMO (OpenGL 3D & Vector Anim) ====================
    @SuppressLint("ClickableViewAccessibility")
    private fun showMember2CombinedDemo() {
        val member2Layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Sub-navigation bar between OpenGL 3D and Vector Anim
        val subNavBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#1E293B"))
        }

        val contentContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Build OpenGL ES 3D View
        val glSurfaceView = GLSurfaceView(this).apply {
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

        // Build Vector Drawable Bell Animation View
        val vectorAnimView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#10141C"))
        }

        val tvHint = TextView(this).apply {
            text = "🔔 Tap the bell to trigger Animated Vector Drawable (AVD)!"
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FFC107"))
            gravity = Gravity.CENTER
            setPadding(20, 20, 20, 40)
        }

        val imageView = ImageView(this).apply {
            val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.avd_bell)
            setImageDrawable(drawable)
            isClickable = true
            setOnClickListener {
                if (drawable is AnimatedVectorDrawable) {
                    drawable.start()
                }
            }
        }

        vectorAnimView.addView(tvHint)
        vectorAnimView.addView(imageView)

        // Sub-buttons for Member 2
        val btnOpenGL = Button(this)
        val btnVector = Button(this)

        fun updateSubButtons(isOpenGL: Boolean) {
            val activeColor = Color.parseColor("#FFA801")
            val inactiveColor = Color.parseColor("#334155")

            btnOpenGL.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(if (isOpenGL) activeColor else inactiveColor)
            }
            btnOpenGL.setTextColor(if (isOpenGL) Color.BLACK else Color.WHITE)

            btnVector.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(if (!isOpenGL) activeColor else inactiveColor)
            }
            btnVector.setTextColor(if (!isOpenGL) Color.BLACK else Color.WHITE)
        }

        btnOpenGL.apply {
            text = "📐 1. OpenGL 3D Pyramid"
            isAllCaps = false
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(30, 10, 30, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener {
                updateSubButtons(true)
                contentContainer.removeAllViews()
                contentContainer.addView(glSurfaceView)
            }
        }

        btnVector.apply {
            text = "🔔 2. Vector Bell Anim"
            isAllCaps = false
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(30, 10, 30, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener {
                updateSubButtons(false)
                contentContainer.removeAllViews()
                contentContainer.addView(vectorAnimView)

                // Auto start bell animation on switch
                val d = imageView.drawable
                if (d is AnimatedVectorDrawable) {
                    d.start()
                }
            }
        }

        subNavBar.addView(btnOpenGL)
        subNavBar.addView(btnVector)

        member2Layout.addView(subNavBar)
        member2Layout.addView(contentContainer)

        // Default to OpenGL 3D view
        updateSubButtons(true)
        contentContainer.addView(glSurfaceView)

        showDemoView("📐 OpenGL 3D & Vector Anim", member2Layout)
    }

    // ==================== MEMBER 3 DEMO (Game 2D & Fractal Tree) ====================
    private fun showMember3CombinedDemo() {
        val member3Layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Sub-navigation bar between 2D Game and Fractal Tree
        val subNavBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#1E293B"))
        }

        val contentContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val gameView = Simple2DGameView(this)
        val fractalView = FractalTreeView(this)

        val btnGame = Button(this)
        val btnFractal = Button(this)

        fun updateSubButtons(isGame: Boolean) {
            val activeColor = Color.parseColor("#00D8D6")
            val inactiveColor = Color.parseColor("#334155")

            btnGame.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(if (isGame) activeColor else inactiveColor)
            }
            btnGame.setTextColor(if (isGame) Color.BLACK else Color.WHITE)

            btnFractal.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(if (!isGame) activeColor else inactiveColor)
            }
            btnFractal.setTextColor(if (!isGame) Color.BLACK else Color.WHITE)
        }

        btnGame.apply {
            text = "🎮 1. 2D Canvas Game"
            isAllCaps = false
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(30, 10, 30, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener {
                updateSubButtons(true)
                contentContainer.removeAllViews()
                contentContainer.addView(gameView)
            }
        }

        btnFractal.apply {
            text = "🌳 2. Fractal Tree"
            isAllCaps = false
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(30, 10, 30, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener {
                updateSubButtons(false)
                contentContainer.removeAllViews()
                contentContainer.addView(fractalView)
            }
        }

        subNavBar.addView(btnGame)
        subNavBar.addView(btnFractal)

        member3Layout.addView(subNavBar)
        member3Layout.addView(contentContainer)

        // Default to 2D Game view
        updateSubButtons(true)
        contentContainer.addView(gameView)

        showDemoView("🎮 2D Game & Fractal Tree", member3Layout)
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
