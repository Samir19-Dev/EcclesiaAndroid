package com.ecclesia.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ecclesia.android.ui.theme.BordeTarjeta


@Composable
fun SkeletonShimmer(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val x by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFEEF1F6),
            Color(0xFFF8FAFC),
            Color(0xFFEEF1F6)
        ),
        start = Offset(x - 300f, 0f),
        end = Offset(x, 200f)
    )
    Box(
        modifier = modifier.clip(shape).background(brush)
    )
}

@Composable
fun SkeletonTexto(ancho: Dp, alto: Dp = 14.dp, modifier: Modifier = Modifier) {
    SkeletonShimmer(modifier = modifier.height(alto).fillMaxWidth().then(Modifier.padding(vertical = 2.dp)), shape = RoundedCornerShape(6.dp))
}

@Composable
fun SkeletonAvatar(tamano: Dp = 40.dp, modifier: Modifier = Modifier) {
    SkeletonShimmer(modifier = modifier.size(tamano), shape = CircleShape)
}

@Composable
fun SkeletonTarjeta(modifier: Modifier = Modifier, lineas: Int = 3) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                SkeletonAvatar(36.dp)
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    SkeletonTexto(ancho = 140.dp, alto = 16.dp)
                    SkeletonTexto(ancho = 90.dp, alto = 12.dp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            repeat(lineas) {
                SkeletonTexto(ancho = if (it % 2 == 0) 220.dp else 180.dp, alto = 12.dp)
            }
        }
    }
}
