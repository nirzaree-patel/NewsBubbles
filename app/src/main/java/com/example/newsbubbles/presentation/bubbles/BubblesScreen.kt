package com.example.newsbubbles.presentation.bubbles

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.newsbubbles.domain.model.NewsCategory
import kotlin.math.roundToInt

private val BackgroundTop = Color(0xFF060B18)
private val BackgroundBottom = Color(0xFF14213D)

@Composable
fun BubblesScreen(
    onCategoryTapped: (NewsCategory) -> Unit,
    viewModel: BubblesViewModel = hiltViewModel()
) {
    val bubbles by viewModel.bubbles.collectAsState()
    val density = LocalDensity.current
    val topInsetPx = with(density) { 110.dp.toPx() }

    var canvasWidthPx by remember { mutableFloatStateOf(0f) }
    var canvasHeightPx by remember { mutableFloatStateOf(0f) }
    var lastFrameMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withInfiniteAnimationFrameMillis { frameMs ->
                val delta = if (lastFrameMs == 0L) 0f
                            else ((frameMs - lastFrameMs) / 1000f).coerceAtMost(0.032f)
                lastFrameMs = frameMs
                viewModel.step(canvasWidthPx, canvasHeightPx, delta, topInsetPx)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundTop, BackgroundBottom)))
            .onSizeChanged { size ->
                canvasWidthPx = size.width.toFloat()
                canvasHeightPx = size.height.toFloat()
                viewModel.initialize(size.width.toFloat(), size.height.toFloat(), topInsetPx)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = size.minDimension * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.02f),
                radius = size.minDimension * 0.4f,
                center = Offset(size.width * 0.85f, size.height * 0.9f)
            )
        }

        bubbles.forEach { bubble ->
            BubbleItem(bubble = bubble, onTap = { onCategoryTapped(bubble.uiModel.category) })
        }

        Text(
            text = "News Bubbles",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap a bubble to explore",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 78.dp),
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun BubbleItem(bubble: BubbleState, onTap: () -> Unit) {
    val density = LocalDensity.current
    val diameterDp = with(density) { (bubble.radius * 2f).toDp() }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "bubblePressScale"
    )

    val bubbleColor = bubble.uiModel.color
    val gradientBrush = remember(bubbleColor) {
        object : ShaderBrush() {
            override fun createShader(size: androidx.compose.ui.geometry.Size): Shader {
                val r = size.minDimension / 2f
                return RadialGradientShader(
                    center = Offset(size.width * 0.35f, size.height * 0.3f),
                    radius = size.maxDimension * 0.9f,
                    colors = listOf(
                        lighten(bubbleColor, 0.35f),
                        bubbleColor,
                        darken(bubbleColor, 0.25f)
                    ),
                    colorStops = listOf(0f, 0.55f, 1f)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset((bubble.x - bubble.radius).roundToInt(), (bubble.y - bubble.radius).roundToInt()) }
            .size(diameterDp)
            .scale(pressScale)
            .shadow(elevation = 14.dp, shape = CircleShape, ambientColor = bubbleColor, spotColor = bubbleColor)
            .clip(CircleShape)
            .background(gradientBrush)
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.25f), shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        val diameterValue = diameterDp.value
        val name = bubble.uiModel.category.displayName
        val iconSize = (diameterValue * 0.36f).coerceIn(26f, 72f).sp
        val diameterBasedLabelSize = diameterValue * 0.155f
        val availableWidth = diameterValue - 12f
        val widthFitLabelSize = availableWidth / (name.length * 0.62f)
        val labelSize = diameterBasedLabelSize.coerceAtMost(widthFitLabelSize).coerceIn(10f, 24f).sp

        Column(
            modifier = Modifier.padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = bubble.uiModel.icon,
                fontSize = iconSize,
                textAlign = TextAlign.Center
            )
            Text(
                text = name,
                color = Color.White,
                fontSize = labelSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun lighten(color: Color, amount: Float): Color = Color(
    red = color.red + (1f - color.red) * amount,
    green = color.green + (1f - color.green) * amount,
    blue = color.blue + (1f - color.blue) * amount,
    alpha = color.alpha
)

private fun darken(color: Color, amount: Float): Color = Color(
    red = color.red * (1f - amount),
    green = color.green * (1f - amount),
    blue = color.blue * (1f - amount),
    alpha = color.alpha
)
