package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal


@Composable
fun AvatarIniciales(
    fotoUrl: String?,
    nombre: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    fontSize: TextUnit = 18.sp,
    contentDescription: String? = null
) {
    val iniciales = inicialesDeAvatar(nombre)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!fotoUrl.isNullOrBlank()) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Brush.linearGradient(listOf(AzulClaro, AzulPrincipal)))
            )
            Text(
                text = iniciales,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

fun inicialesDeAvatar(nombre: String?): String {
    val partes = nombre.orEmpty().trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.isEmpty() -> "E"
        partes.size == 1 -> partes[0].take(1).uppercase()
        else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
    }
}
