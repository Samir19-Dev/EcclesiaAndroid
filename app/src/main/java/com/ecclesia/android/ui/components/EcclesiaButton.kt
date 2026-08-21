package com.ecclesia.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulOscuro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.TextoSuave


@Composable
fun EcclesiaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = false,
    icon: ImageVector? = null,
    colors: List<Color> = listOf(AzulClaro, AzulPrincipal),
    contentColor: Color = Color.White,
    tonalElevation: androidx.compose.ui.unit.Dp = 0.dp
) {
    val gradiente = Brush.linearGradient(colors)
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(gradiente, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContentColor = contentColor.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        ContenidoBotón(text, loading, icon, contentColor, Modifier)
    }
}

@Composable
private fun RowScope.ContenidoBotón(
    text: String,
    loading: Boolean,
    icon: ImageVector?,
    contentColor: Color,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        }
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
fun EcclesiaOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = false,
    color: Color = AzulPrincipal,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = color,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun EcclesiaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = AzulClaro
) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = modifier) {
        Text(text, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PageTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = AzulPrincipal,
        modifier = modifier
    )
}

@Composable
fun PageSubtitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextoSuave,
        modifier = modifier
    )
}
