package com.example.midtermgraphicsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

private val Bg = Color(0xFFF8F5FC)
private val PanelBg = Color(0xFFFEFCFF)
private val Ink = Color(0xFF534568)
private val Subtle = Color(0xFF9785AB)
private val Purple = Color(0xFF9879C9)
private val Outlined = Color(0xFFE7DEF2)

private data class AnimationItem(val label: String, val subtitle: String, val res: Int)

class LottieAnimationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Purple, onPrimary = Color.White,
                surface = PanelBg, background = Bg
            )) {
                LottieStudioScreen(onBackClick = { finish() })
            }
        }
    }
}

@Composable
private fun LottieStudioScreen(onBackClick: () -> Unit = {}) {
    // list animation
    // animation liên kết file JSON trong res/raw
    val items = remember {
        listOf(
            AnimationItem("Orbit", "Pastel planets", R.raw.pastel_orbit),
            AnimationItem("Bloom", "Rotating flower", R.raw.pastel_bloom),
            AnimationItem("Pulse", "Breathing circles", R.raw.pastel_pulse),
            AnimationItem("Spinner", "Spinning petals", R.raw.pastel_spinner)
        )
    }
    var selected by remember { mutableIntStateOf(0) }
    var playing by remember { mutableStateOf(true) }
    var looping by remember { mutableStateOf(true) }
    var reversed by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(1f) }
    var scrubbing by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }
    var resetNumber by remember { mutableIntStateOf(0) }
    var previewBackground by remember { mutableStateOf(Color(0xFFF3EDFC)) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(items[selected].res)
    )
    val anim = rememberLottieAnimatable()
    val scope = rememberCoroutineScope()
// phần logic phát animation
    LaunchedEffect(composition, selected, playing, looping, reversed, speed, scrubbing, resetNumber) {
        val current = composition ?: return@LaunchedEffect
        if (!playing || scrubbing) return@LaunchedEffect
        val sameFile = anim.composition === current
        val previous = if (sameFile) anim.progress else if (reversed) 1f else 0f
        val start = when {
            reversed && previous <= 0.0001f -> 1f
            !reversed && previous >= 0.9999f -> 0f
            else -> previous
        }
        anim.animate(
            composition = current,
            iterations = if (looping) LottieConstants.IterateForever else 1,
            speed = if (reversed) -speed else speed,
            initialProgress = start
        )
    }


    val progress = when {
        scrubbing -> scrubProgress
        composition != null && anim.composition === composition -> anim.progress
        else -> 0f
    }.coerceIn(0f, 1f)

    Column(
        modifier = Modifier.fillMaxSize().background(Bg)
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                Text("LOTTIE  /  PASTEL ANIMATION STUDIO", color = Subtle, fontSize = 11.sp)
            }
        }

        StudioPanel {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Live Preview", color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("JSON · LIVE", color = Purple, fontSize = 11.sp)
            }
            Box(
                Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(22.dp))
                    .background(previewBackground),
                contentAlignment = Alignment.Center
            ) {
                if (composition == null) {
                    CircularProgressIndicator(color = Purple)
                } else {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(232.dp)
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(items[selected].label, fontWeight = FontWeight.SemiBold, color = Ink)
                Text("${(progress * 100).roundToInt()}%", color = Purple)
            }
            Text("Preview background", color = Subtle, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Color(0xFFF3EDFC), Color(0xFFFFEFF5), Color(0xFFEAF8F2),
                    Color(0xFFEDF4FC), Color.White
                ).forEach { color ->
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(50))
                            .background(color)
                            .border(if (previewBackground == color) 2.dp else 1.dp,
                                if (previewBackground == color) Purple else Outlined,
                                RoundedCornerShape(50))
                            .clickable { previewBackground = color }
                    )
                }
            }
        }
// phần danh sách
        // khi user chọn mục khác -> thì sẽ nạp JSON tương ứng với mục đó
        StudioPanel {
            SectionHeading("Animation Library")
            Text("Four original local Lottie JSON files", color = Subtle, fontSize = 12.sp)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEachIndexed { index, item ->
                    OutlinedButton(
                        onClick = {
                            if (selected != index) {
                                selected = index
                                scrubbing = false
                                playing = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, if (selected == index) Purple else Outlined
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected == index)
                                Color(0xFFF0E8FB) else Color.White,
                            contentColor = Ink
                        )
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.label, fontWeight = FontWeight.SemiBold)
                            Text(item.subtitle, color = Subtle, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
// phần pause
        StudioPanel {
            SectionHeading("Playback Controller")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SoftButton(
                    label = if (playing) "Ⅱ Pause" else "▶ Play",
                    modifier = Modifier.weight(1f)
                ) { playing = !playing }
                SoftButton("↺ Reset", modifier = Modifier.weight(1f)) {
                    playing = false
                    scrubbing = false
                    scope.launch {
                        anim.snapTo(composition, if (reversed) 1f else 0f)
                        resetNumber++
                    }
                }
            }
            // phần chạy lặp với đổi chiều
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Infinite Loop", color = Ink, modifier = Modifier.weight(1f))
                Switch(checked = looping, onCheckedChange = { looping = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reverse direction", color = Ink, modifier = Modifier.weight(1f))
                Switch(checked = reversed, onCheckedChange = { reversed = it })
            }
// phần tốc độ phát
            Text("Speed: ${format(speed)}×", color = Ink, fontWeight = FontWeight.SemiBold)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0.5f, 1f, 1.5f, 2f).forEach { option ->
                    SoftButton("${format(option)}×", active = speed == option) { speed = option }
                }
            }
        }
// phần tua animation
        StudioPanel {
            SectionHeading("Timeline / Frame Scrubber")
            Text("Drag to seek, release to resume from that frame", color = Subtle, fontSize = 12.sp)
            Slider(
                value = progress,
                onValueChange = { position ->
                    playing = false
                    scrubbing = true
                    scrubProgress = position
                },
                onValueChangeFinished = {
                    val seekTo = scrubProgress
                    scope.launch {
                        anim.snapTo(composition, seekTo) // chuyển đến vtri vừa chọn
                        scrubbing = false
                        playing = true
                    }
                },
                valueRange = 0f..1f
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0%", color = Subtle, fontSize = 12.sp)
                Text("${(progress * 100).roundToInt()}%", color = Purple, fontWeight = FontWeight.Bold)
                Text("100%", color = Subtle, fontSize = 12.sp)
            }
        }
// phần show in4
        StudioPanel {
            SectionHeading("Animation Information")
            val c: LottieComposition? = composition
            InfoRow("Name", items[selected].label)
            InfoRow("Duration", if (c == null) "Loading..." else "${format(c.duration / 1000f)} s")
            InfoRow("Frame rate", if (c == null) "Loading..." else "${format(c.frameRate)} FPS")
            InfoRow("Total frames", if (c == null) "Loading..." else
                "${(c.endFrame - c.startFrame).roundToInt()}")
            InfoRow("Current frame", if (c == null) "—" else
                "${(c.startFrame + (c.endFrame - c.startFrame) * progress).roundToInt()}")
            InfoRow("Playback", if (reversed) "Reverse" else "Forward")
        }
        Text("MotionLab • Kotlin + Jetpack Compose + Lottie", fontSize = 11.sp,
            color = Subtle, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
    }
}

private fun format(value: Float): String = String.format(Locale.US, "%.1f", value)

@Composable
private fun StudioPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
            .background(PanelBg)
            .border(1.dp, Outlined, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        content = content
    )
}

@Composable
private fun SectionHeading(title: String) {
    Text(title, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun SoftButton(
    label: String,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (active) Purple else Outlined
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (active) Color(0xFFEDE2FA) else Color(0xFFF9F5FE),
            contentColor = Ink
        )
    ) { Text(label, fontSize = 13.sp) }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Subtle, fontSize = 13.sp)
        Text(value, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
