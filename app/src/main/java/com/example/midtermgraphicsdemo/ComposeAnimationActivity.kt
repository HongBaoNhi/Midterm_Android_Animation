package com.example.midtermgraphicsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Ink = Color(0xFF514462)
private val MutedInk = Color(0xFF917F9F)
private val Purple = Color(0xFF9A7AC9)
private val Pastels = listOf(
    Color(0xFFC8B6FF),
    Color(0xFFB8E4D5),
    Color(0xFFF7C8D8),
    Color(0xFFF8D9AC),
    Color(0xFFBBDCF5),
    Color(0xFFDCC9F0)
)
private data class SketchStroke(
    val points: List<Offset>,
    val color: Color,
    val widthDp: Float
)

class ComposeAnimationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Purple,
                    onPrimary = Color.White,
                    background = Color(0xFFF6F1FB),
                    surface = Color.White,
                    onSurface = Ink
                )
            ) {
                MotionLabApp(onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun MotionLabApp(onBackClick: () -> Unit = {}) {
    var shapeColor by remember { mutableStateOf(Pastels[0]) }
    var shapeSize by remember { mutableFloatStateOf(150f) }
    var cornerRadius by remember { mutableFloatStateOf(28f) }
    var shapeRotation by remember { mutableFloatStateOf(0f) }

    var iconType by remember { mutableStateOf("heart") }
    var iconColor by remember { mutableStateOf(Color(0xFFAB78C6)) }
    var iconSize by remember { mutableFloatStateOf(75f) }
    var iconRotation by remember { mutableFloatStateOf(0f) }
    var pulseOn by remember { mutableStateOf(false) }
    var useHandDrawing by remember { mutableStateOf(false) }

    var customText by remember { mutableStateOf("Create your own magic ✨") }
    var textColor by remember { mutableStateOf(Ink) }
    var fontSize by remember { mutableFloatStateOf(19f) }
    var fontChoice by remember { mutableStateOf("Sans") }
    var boldText by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(true) }

    val strokes = remember { mutableStateListOf<SketchStroke>() }
    var brushColor by remember { mutableStateOf(Pastels[0]) }
    var brushSize by remember { mutableFloatStateOf(6f) }

    var messageIndex by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableStateOf("Shape") }
    val messages = listOf("DESIGN IN MOTION", "MADE WITH COMPOSE", "CREATE • PLAY • INSPIRE")
    val tabs = listOf("Shape", "Icon", "Draw", "Text", "Motion")

    val animatedShapeSize by animateDpAsState(shapeSize.dp, tween(420), label = "Shape size")
    val animatedCorner by animateDpAsState(cornerRadius.dp, tween(420), label = "Corner")
    val animatedShapeColor by animateColorAsState(shapeColor, tween(520), label = "Shape color")
    val animatedRotation by animateFloatAsState(shapeRotation, tween(420), label = "Shape rotation")
    val animatedIconSize by animateDpAsState(iconSize.dp, tween(380), label = "Icon size")
    val animatedIconRotation by animateFloatAsState(iconRotation, tween(420), label = "Icon rotation")
    val animatedTextSize by animateFloatAsState(fontSize, tween(300), label = "Text size")
    val animatedTextColor by animateColorAsState(textColor, tween(380), label = "Text color")

    val pulse = rememberInfiniteTransition(label = "Pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.13f,
        animationSpec = infiniteRepeatable(
            animation = tween(780),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse factor"
    ).value

    val fontFamily = when (fontChoice) {
        "Serif" -> FontFamily.Serif
        "Mono" -> FontFamily.Monospace
        "Cursive" -> FontFamily.Cursive
        else -> FontFamily.SansSerif
    }

    fun selectPresetShapeColor(color: Color) {
        shapeColor = color
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFF6F1FB), Color(0xFFF3F6FD), Color(0xFFF8F2FA))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("←", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Ink)
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("✦ MotionLab", color = Ink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("PASTEL ANIMATION STUDIO", color = MutedInk, fontSize = 10.sp)
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF1E8F9),
                    border = BorderStroke(1.dp, Color(0xFFE4D5F2))
                ) {
                    Text("● LIVE", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Color(0xFF619B89), fontSize = 10.sp)
                }
            }

            GlassPanel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Live Preview", color = Ink, fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("01 / CANVAS", color = MutedInk, fontSize = 10.sp)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF8F4FD))
                        .border(1.dp, Color(0xFFE7DAF2), RoundedCornerShape(22.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(animatedShapeSize)
                            .graphicsLayer { rotationZ = animatedRotation }
                            .clip(RoundedCornerShape(animatedCorner))
                            .background(animatedShapeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (useHandDrawing) {
                            SketchCanvas(
                                strokes = strokes,
                                modifier = Modifier.fillMaxSize().padding(10.dp)
                            )
                        } else {
                            PresetIcon(
                                type = iconType,
                                color = iconColor,
                                modifier = Modifier
                                    .size(animatedIconSize)
                                    .graphicsLayer {
                                        rotationZ = animatedIconRotation
                                        val s = if (pulseOn) pulse else 1f
                                        scaleX = s
                                        scaleY = s
                                    }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    AnimatedVisibility(visible = showText) {
                        Text(
                            text = customText,
                            fontFamily = fontFamily,
                            fontWeight = if (boldText) FontWeight.Bold else FontWeight.Normal,
                            fontSize = animatedTextSize.sp,
                            color = animatedTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    AnimatedContent(targetState = messageIndex, label = "Message") { index ->
                        Text(messages[index], fontSize = 10.sp, color = Purple, letterSpacing = 1.sp)
                    }
                }
                Text("Customize your design below • changes animate in real time",
                    color = MutedInk, fontSize = 11.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    GlassAction(
                        title = tab,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }

            GlassPanel {
                when (selectedTab) {
                    "Shape" -> {
                        SectionTitle("◈  Shape Customizer")
                        Hint("Pick a pastel color for your shape.")
                        ColorPalette(shapeColor, ::selectPresetShapeColor)
                        ControlSlider("Shape Size", shapeSize, 60f..220f, "dp") { shapeSize = it }
                        ControlSlider("Corner Radius", cornerRadius, 0f..90f, "dp") { cornerRadius = it }
                        ControlSlider("Shape Rotation", shapeRotation, 0f..360f, "°") { shapeRotation = it }
                    }
                    "Icon" -> {
                        SectionTitle("♡  Icon Customizer")
                        Hint("Icons are drawn as vectors in Canvas, so tinting works reliably.")
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("heart", "star", "flower", "sun", "diamond", "sparkle").forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (iconType == type && !useHandDrawing)
                                            Color(0xFFEEE3FF) else Color.White.copy(alpha = .53f))
                                        .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                                        .clickable { iconType = type; useHandDrawing = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    PresetIcon(type, iconColor, Modifier.size(30.dp))
                                }
                            }
                        }
                        Hint("Selected: ${iconType.replaceFirstChar { it.uppercase() }}")
                        Text("Icon Color", fontWeight = FontWeight.SemiBold, color = Ink)
                        ColorPalette(iconColor) { iconColor = it }
                        ControlSlider("Icon Size", iconSize, 24f..120f, "dp") { iconSize = it }
                        ControlSlider("Icon Rotation", iconRotation, 0f..360f, "°") { iconRotation = it }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Icon Pulse (infinite)", color = Ink, modifier = Modifier.weight(1f))
                            Switch(checked = pulseOn, onCheckedChange = { pulseOn = it })
                        }
                        Hint("Select an icon to replace the hand-drawn preview.")
                    }
                    "Draw" -> {
                        SectionTitle("✎  Draw Your Own Icon")
                        Hint("Draw with your finger or mouse. Tap once for a dot; drag for lines.")
                        Text("Brush Color", fontWeight = FontWeight.SemiBold, color = Ink)
                        ColorPalette(brushColor) { brushColor = it }
                        ControlSlider("Brush Size", brushSize, 2f..20f, "dp") { brushSize = it }
                        DrawingEditor(strokes, brushColor, brushSize)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                GlassAction("↶ Undo", enabled = strokes.isNotEmpty()) {
                                    if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
                                }
                            }
                            Box(Modifier.weight(1f)) {
                                GlassAction("✕ Clear", enabled = strokes.isNotEmpty()) { strokes.clear() }
                            }
                        }
                        GlassAction("✓ Apply Drawing to Preview", fillWidth = true) {
                            useHandDrawing = true
                        }
                        Hint("Drawings stay available when you switch tabs; Reset clears them.")
                    }
// phần edit nội dung và front chữ
                    "Text" -> {
                        SectionTitle("Aa  Typography Editor")
                        OutlinedTextField(
                            value = customText,
                            onValueChange = { customText = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Preview text") },
                            maxLines = 2,
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = .55f),
                                unfocusedContainerColor = Color.White.copy(alpha = .40f)
                            )
                        )
                        Text("Font Family", fontWeight = FontWeight.SemiBold, color = Ink)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            listOf("Sans", "Serif", "Mono", "Cursive").forEach { name ->
                                GlassAction(name, selected = fontChoice == name) { fontChoice = name }
                            }
                        }
                        ControlSlider("Font Size", fontSize, 12f..34f, "sp") { fontSize = it }
                        Text("Text Color", fontWeight = FontWeight.SemiBold, color = Ink)
                        ColorPalette(textColor) { textColor = it }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Bold Text", modifier = Modifier.weight(1f), color = Ink)
                            Switch(checked = boldText, onCheckedChange = { boldText = it })
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Show / Hide Text", modifier = Modifier.weight(1f), color = Ink)
                            Switch(checked = showText, onCheckedChange = { showText = it })
                        }
                    }
                    else -> {
                        SectionTitle("✧  Animation Controls")
                        Hint("Try each API, then explain the transition in your report.")
                        GlassAction("Next Animated Message", fillWidth = true) {
                            messageIndex = (messageIndex + 1) % messages.size
                        }
                        GlassAction("Toggle Text Visibility", fillWidth = true) {
                            showText = !showText
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Infinite Icon Pulse", modifier = Modifier.weight(1f), color = Ink)
                            Switch(checked = pulseOn, onCheckedChange = { pulseOn = it })
                        }
                        GlassAction("Animate Size + Color", fillWidth = true) {
                            shapeSize = if (shapeSize < 180f) 205f else 130f
                            selectPresetShapeColor(if (shapeColor == Pastels[2]) Pastels[0] else Pastels[2])
                        }
                        GlassAction("Rotate Shape + Icon", fillWidth = true) {
                            shapeRotation = (shapeRotation + 90f) % 360f
                            iconRotation = (iconRotation + 90f) % 360f
                        }
                        Hint("AnimatedContent · AnimatedVisibility · animate*AsState · rememberInfiniteTransition")
                    }
                }
            }
            GlassAction("↻  Reset All Settings", fillWidth = true) {
                shapeColor = Pastels[0]; shapeSize = 150f; cornerRadius = 28f
                shapeRotation = 0f
                iconType = "heart"; iconColor = Color(0xFFAB78C6)
                iconSize = 75f; iconRotation = 0f; pulseOn = false
                useHandDrawing = false; strokes.clear()
                brushColor = Pastels[0]; brushSize = 6f
                customText = "Create your own magic ✨"; textColor = Ink
                fontSize = 19f; fontChoice = "Sans"; boldText = false; showText = true
                messageIndex = 0; selectedTab = "Shape"
            }
            Text("MOTIONLAB  •  JETPACK COMPOSE + CANVAS",
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                fontSize = 10.sp, color = MutedInk)
            Spacer(Modifier.height(10.dp))
        }
    }
}


@Composable
private fun GlassPanel(content: @Composable ColumnScope.() -> Unit) {
    val radius = RoundedCornerShape(26.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, radius, ambientColor = Color(0x226F5489), spotColor = Color(0x226F5489))
            .clip(radius)
            .background(Color(0xFFFEFCFF))
            .border(1.dp, Color(0xFFE7DEF0), radius)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        content = content
    )
}

@Composable
private fun GlassAction(
    title: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    fillWidth: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier,
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (selected) Purple.copy(alpha = .65f) else Color(0xFFE8DDF2)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Color(0xFFEDE3FA)
            else Color(0xFFF9F6FD),
            contentColor = Ink
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun Hint(text: String) {
    Text(text, color = MutedInk, fontSize = 12.sp)
}

@Composable
private fun ColorPalette(selected: Color, onSelect: (Color) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        (Pastels + listOf(Ink, Color.White)).forEach { color ->
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .border(2.dp, if (selected == color) Purple else Color.White, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onSelect(color) },
                contentAlignment = Alignment.Center
            ) {
                if (selected == color) {
                    Text("✓", color = if (color == Color.White) Ink else Color.White,
                        fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ControlSlider(
    name: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    unit: String,
    onChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = Ink, fontSize = 13.sp)
            Text("${value.toInt()}$unit", color = Purple, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold)
        }
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            colors = SliderDefaults.colors(thumbColor = Purple, activeTrackColor = Purple)
        )
    }
}

@Composable
private fun PresetIcon(type: String, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val d = size.minDimension
        val cx = size.width / 2f
        val cy = size.height / 2f
        when (type) {
            "heart" -> {
                val p = Path().apply {
                    moveTo(cx, cy + d * .37f)
                    cubicTo(cx - d * .62f, cy + d * .02f,
                        cx - d * .39f, cy - d * .48f, cx, cy - d * .20f)
                    cubicTo(cx + d * .39f, cy - d * .48f,
                        cx + d * .62f, cy + d * .02f, cx, cy + d * .37f)
                    close()
                }
                drawPath(p, color)
            }
            "star" -> {
                val p = Path()
                for (i in 0 until 10) {
                    val angle = -PI / 2 + i * PI / 5
                    val r = if (i % 2 == 0) d * .46f else d * .20f
                    val x = cx + cos(angle).toFloat() * r
                    val y = cy + sin(angle).toFloat() * r
                    if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                p.close()
                drawPath(p, color)
            }
            "flower" -> {
                for (i in 0 until 6) {
                    val angle = i * PI / 3
                    drawCircle(color, radius = d * .18f,
                        center = Offset(cx + cos(angle).toFloat() * d * .27f,
                            cy + sin(angle).toFloat() * d * .27f))
                }
                drawCircle(Color.White.copy(alpha = .90f), radius = d * .13f,
                    center = Offset(cx, cy))
            }
            "sun" -> {
                for (i in 0 until 8) {
                    val angle = i * PI / 4
                    val dx = cos(angle).toFloat()
                    val dy = sin(angle).toFloat()
                    drawLine(color,
                        Offset(cx + dx * d * .33f, cy + dy * d * .33f),
                        Offset(cx + dx * d * .47f, cy + dy * d * .47f),
                        strokeWidth = d * .06f, cap = StrokeCap.Round)
                }
                drawCircle(color, radius = d * .25f, center = Offset(cx, cy))
            }
            "diamond" -> {
                val p = Path().apply {
                    moveTo(cx, cy - d * .47f)
                    lineTo(cx + d * .40f, cy)
                    lineTo(cx, cy + d * .47f)
                    lineTo(cx - d * .40f, cy)
                    close()
                }
                drawPath(p, color)
            }
            else -> {
                val p = Path().apply {
                    moveTo(cx, cy - d * .48f)
                    lineTo(cx + d * .13f, cy - d * .13f)
                    lineTo(cx + d * .48f, cy)
                    lineTo(cx + d * .13f, cy + d * .13f)
                    lineTo(cx, cy + d * .48f)
                    lineTo(cx - d * .13f, cy + d * .13f)
                    lineTo(cx - d * .48f, cy)
                    lineTo(cx - d * .13f, cy - d * .13f)
                    close()
                }
                drawPath(p, color)
            }
        }
    }
}

private fun normalized(point: Offset, width: Int, height: Int): Offset = Offset(
    (point.x / width.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f),
    (point.y / height.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
)

@Composable
private fun DrawingEditor(
    strokes: SnapshotStateList<SketchStroke>,
    brushColor: Color,
    brushSize: Float
) {
    Canvas(
        modifier = Modifier
            .size(270.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = .7f))
            .border(1.dp, Color(0xFFE5D8F1), RoundedCornerShape(18.dp))
            .pointerInput(brushColor, brushSize) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    val index = strokes.size
                    strokes.add(SketchStroke(
                        points = listOf(normalized(down.position, size.width, size.height)),
                        color = brushColor,
                        widthDp = brushSize
                    ))
                    var pointerIsDown = true
                    while (pointerIsDown) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change != null && change.pressed) {
                            val p = normalized(change.position, size.width, size.height)
                            val old = strokes[index]
                            if (old.points.lastOrNull() != p) {
                                strokes[index] = old.copy(points = old.points + p)
                            }
                            change.consume()
                        } else {
                            pointerIsDown = false
                        }
                    }
                }
            }
    ) {
        renderStrokes(strokes)
    }
}

@Composable
private fun SketchCanvas(strokes: List<SketchStroke>, modifier: Modifier = Modifier) {
    Canvas(modifier) { renderStrokes(strokes) }
}

private fun DrawScope.renderStrokes(strokes: List<SketchStroke>) {
    strokes.forEach { stroke ->
        if (stroke.points.size == 1) {
            val p = stroke.points.first()
            drawCircle(stroke.color, radius = stroke.widthDp.dp.toPx() / 2f,
                center = Offset(p.x * size.width, p.y * size.height))
        } else if (stroke.points.size > 1) {
            val path = Path()
            val first = stroke.points.first()
            path.moveTo(first.x * size.width, first.y * size.height)
            stroke.points.drop(1).forEach { p ->
                path.lineTo(p.x * size.width, p.y * size.height)
            }
            drawPath(path, stroke.color,
                style = Stroke(width = stroke.widthDp.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}
