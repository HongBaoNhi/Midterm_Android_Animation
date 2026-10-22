package com.example.midtermgraphicsdemo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root layout containing the top navigation bar and the demo container below
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        // Top navigation bar with 2 buttons for Member 3's demos
        val buttonBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val btnGame2D = Button(this).apply {
            text = "1. 2D Canvas Game"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener {
                container.removeAllViews()
                container.addView(Simple2DGameView(this@MainActivity))
            }
        }

        val btnFractal = Button(this).apply {
            text = "2. Fractal Tree"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener {
                container.removeAllViews()
                container.addView(FractalTreeView(this@MainActivity))
            }
        }

        buttonBar.addView(btnGame2D)
        buttonBar.addView(btnFractal)

        rootLayout.addView(buttonBar)
        rootLayout.addView(container)

        // Display the 2D Game demo by default when the app launches
        container.addView(Simple2DGameView(this))

        setContentView(rootLayout)
    }
}