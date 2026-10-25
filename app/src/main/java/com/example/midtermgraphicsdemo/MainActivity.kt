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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
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

        rootLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#FFF0F5"))
        }

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

        val card5 = createMenuCard(
            title = "5. OpenGL ES 3D Studio",
            description = "Interactive 3D Pyramid, Cube & Sphere with touch rotation",
            accentColorHex = "#9A7AC9"
        ) {
            showDemoView("📐 5. OpenGL ES 3D Studio", createGL3DStudioView())
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

        demoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            setBackgroundColor(Color.parseColor("#FFF0F5"))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

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

    @SuppressLint("ClickableViewAccessibility")
    private fun createGL3DStudioView(): View {
        val density = resources.displayMetrics.density
        fun dp(value: Int): Int = (value * density).toInt()

        val rootContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FFF0F5"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val renderer = MyGLRenderer()

        val viewportArea = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.9f
            )
        }

        val glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(2)
            setRenderer(renderer)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

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

        val tvHint = TextView(this).apply {
            text = "💡 Drag anywhere to rotate in 3D space"
            textSize = 12f
            setTextColor(Color.parseColor("#917F9F"))
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }
        }

        viewportArea.addView(glSurfaceView)
        viewportArea.addView(tvHint)

        val controlScrollView = ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.1f
            )
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadii = floatArrayOf(
                    dp(32).toFloat(), dp(32).toFloat(),
                    dp(32).toFloat(), dp(32).toFloat(),
                    0f, 0f, 0f, 0f
                )
                setColor(Color.WHITE)
                setStroke(2, Color.parseColor("#F3D5E0"))
            }
        }

        val controlPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(24))
        }

        val tvColorTitle = TextView(this).apply {
            text = "🎨 Surface Color"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
            setPadding(0, 0, 0, dp(10))
        }

        val colorScrollView = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            setPadding(0, 0, 0, dp(14))
        }

        val colorRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        data class ColorOption(val name: String, val glColor: FloatArray?, val hexColor: String)
        val colorList = listOf(
            ColorOption("Rainbow", null, "#9A7AC9"),
            ColorOption("Pink", floatArrayOf(1.0f, 0.70f, 0.75f, 1.0f), "#FFB3C1"),
            ColorOption("Purple", floatArrayOf(0.75f, 0.60f, 0.90f, 1.0f), "#C7CEEA"),
            ColorOption("Blue", floatArrayOf(0.50f, 0.75f, 0.98f, 1.0f), "#A0E7E5"),
            ColorOption("Mint", floatArrayOf(0.50f, 0.88f, 0.72f, 1.0f), "#B5EAD7"),
            ColorOption("Yellow", floatArrayOf(0.98f, 0.88f, 0.50f, 1.0f), "#E2F0CB"),
            ColorOption("Orange", floatArrayOf(0.98f, 0.68f, 0.50f, 1.0f), "#FFDAC1"),
            ColorOption("Silver", floatArrayOf(0.95f, 0.95f, 0.98f, 1.0f), "#F8F9FA")
        )

        val colorButtons = mutableListOf<Button>()
        fun updateColorStyles(activeIndex: Int) {
            colorButtons.forEachIndexed { idx, btn ->
                val active = idx == activeIndex
                val opt = colorList[idx]
                btn.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor(opt.hexColor))
                    setStroke(if (active) dp(3) else dp(1), if (active) Color.parseColor("#514462") else Color.parseColor("#E0D0E8"))
                }
                btn.text = if (active) "✓" else ""
                val isLightHex = opt.hexColor == "#F8F9FA" || opt.hexColor == "#E2F0CB" || opt.hexColor == "#B5EAD7"
                btn.setTextColor(if (isLightHex) Color.parseColor("#333333") else Color.WHITE)
            }
        }

        colorList.forEachIndexed { idx, option ->
            val btn = Button(this).apply {
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(dp(44), dp(44)).apply {
                    setMargins(dp(4), 0, dp(12), 0)
                }
                setOnClickListener {
                    glSurfaceView.queueEvent {
                        renderer.setSurfaceColor(option.glColor)
                    }
                    glSurfaceView.requestRender()
                    updateColorStyles(idx)
                }
            }
            colorButtons.add(btn)
            colorRow.addView(btn)
        }
        updateColorStyles(0)
        colorScrollView.addView(colorRow)

        val tvMaterialTitle = TextView(this).apply {
            text = "✨ Material & Lighting"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
            setPadding(0, dp(4), 0, dp(8))
        }

        var currentRoughness = 0.3f
        var currentShininess = 0.8f

        val roughnessHeaderRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(2), 0, dp(2))
        }
        val tvRoughnessTitle = TextView(this).apply {
            text = "Roughness"
            textSize = 12f
            setTextColor(Color.parseColor("#514462"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvRoughnessVal = TextView(this).apply {
            text = "30%"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#9A7AC9"))
        }
        roughnessHeaderRow.addView(tvRoughnessTitle)
        roughnessHeaderRow.addView(tvRoughnessVal)

        val sbRoughness = SeekBar(this).apply {
            max = 100
            progress = 30
            setPadding(0, dp(4), 0, dp(4))
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = (progress.coerceAtLeast(1)).toFloat() / 100f
                    currentRoughness = value
                    tvRoughnessVal.text = "${progress}%"
                    glSurfaceView.queueEvent {
                        renderer.setMaterialProperties(currentRoughness, currentShininess)
                    }
                    glSurfaceView.requestRender()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        val roughnessSubRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, dp(10))
        }
        val tvSmoothHint = TextView(this).apply {
            text = "Smooth"
            textSize = 10f
            setTextColor(Color.parseColor("#917F9F"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvRoughHint = TextView(this).apply {
            text = "Rough"
            textSize = 10f
            setTextColor(Color.parseColor("#917F9F"))
            gravity = Gravity.END
        }
        roughnessSubRow.addView(tvSmoothHint)
        roughnessSubRow.addView(tvRoughHint)

        val shininessHeaderRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(2), 0, dp(2))
        }
        val tvShininessTitle = TextView(this).apply {
            text = "Shininess"
            textSize = 12f
            setTextColor(Color.parseColor("#514462"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvShininessVal = TextView(this).apply {
            text = "80%"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#9A7AC9"))
        }
        shininessHeaderRow.addView(tvShininessTitle)
        shininessHeaderRow.addView(tvShininessVal)

        val sbShininess = SeekBar(this).apply {
            max = 100
            progress = 80
            setPadding(0, dp(4), 0, dp(4))
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value = progress.toFloat() / 100f
                    currentShininess = value
                    tvShininessVal.text = "${progress}%"
                    glSurfaceView.queueEvent {
                        renderer.setMaterialProperties(currentRoughness, currentShininess)
                    }
                    glSurfaceView.requestRender()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        val shininessSubRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, dp(14))
        }
        val tvMatteHint = TextView(this).apply {
            text = "Matte"
            textSize = 10f
            setTextColor(Color.parseColor("#917F9F"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvGlossyHint = TextView(this).apply {
            text = "Glossy"
            textSize = 10f
            setTextColor(Color.parseColor("#917F9F"))
            gravity = Gravity.END
        }
        shininessSubRow.addView(tvMatteHint)
        shininessSubRow.addView(tvGlossyHint)

        val tvShapeTitle = TextView(this).apply {
            text = "📐 3D Shapes"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#514462"))
            setPadding(0, dp(4), 0, dp(10))
        }

        val shapeGrid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(8))
        }

        val shapeRow1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, dp(10))
        }

        val shapeRow2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, dp(8))
        }

        val shapeButtons = mutableListOf<Button>()
        val shapeList = listOf(
            Pair("Pyramid", ShapeType.PYRAMID),
            Pair("Cube", ShapeType.CUBE),
            Pair("Sphere", ShapeType.SPHERE),
            Pair("Torus", ShapeType.TORUS)
        )

        fun updateShapeBtnStyles(activeIndex: Int) {
            shapeButtons.forEachIndexed { idx, btn ->
                val active = idx == activeIndex
                btn.background = GradientDrawable().apply {
                    if (active) {
                        setColor(Color.parseColor("#9A7AC9"))
                    } else {
                        setColor(Color.WHITE)
                        setStroke(dp(2), Color.parseColor("#9A7AC9"))
                    }
                    cornerRadius = dp(16).toFloat()
                }
                btn.setTextColor(if (active) Color.WHITE else Color.parseColor("#514462"))
            }
        }

        shapeList.forEachIndexed { idx, pair ->
            val btn = Button(this).apply {
                text = pair.first
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                isAllCaps = false
                layoutParams = LinearLayout.LayoutParams(0, dp(58), 1f).apply {
                    if (idx % 2 == 1) leftMargin = dp(12)
                }
                setOnClickListener {
                    glSurfaceView.queueEvent {
                        renderer.setShape(pair.second)
                    }
                    glSurfaceView.requestRender()
                    updateShapeBtnStyles(idx)
                }
            }
            shapeButtons.add(btn)
            if (idx < 2) {
                shapeRow1.addView(btn)
            } else {
                shapeRow2.addView(btn)
            }
        }
        updateShapeBtnStyles(0)

        shapeGrid.addView(shapeRow1)
        shapeGrid.addView(shapeRow2)

        controlPanel.addView(tvColorTitle)
        controlPanel.addView(colorScrollView)
        controlPanel.addView(tvMaterialTitle)
        controlPanel.addView(roughnessHeaderRow)
        controlPanel.addView(sbRoughness)
        controlPanel.addView(roughnessSubRow)
        controlPanel.addView(shininessHeaderRow)
        controlPanel.addView(sbShininess)
        controlPanel.addView(shininessSubRow)
        controlPanel.addView(tvShapeTitle)
        controlPanel.addView(shapeGrid)

        controlScrollView.addView(controlPanel)

        rootContainer.addView(viewportArea)
        rootContainer.addView(controlScrollView)

        return rootContainer
    }

    private fun createVectorBellView(): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#FFF0F5"))
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
            setPadding(0, 40, 0, 0)
        }

        layout.addView(imageView)
        layout.addView(tvHint)

        return layout
    }

    private fun showDemoView(title: String, demoView: View) {
        tvDemoTitle.text = title
        demoContentArea.removeAllViews()
        demoContentArea.addView(demoView)

        demoContainer.visibility = View.VISIBLE
    }

    private fun returnToMenu() {
        demoContentArea.removeAllViews()
        demoContainer.visibility = View.GONE
    }
}