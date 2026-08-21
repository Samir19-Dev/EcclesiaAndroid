package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.BordeTarjeta
import com.ecclesia.android.ui.theme.TextoSuave


@Composable
fun PaginacionBarra(
    paginaActual: Int,
    totalPaginas: Int,
    onPagina: (Int) -> Unit,
    modifier: Modifier = Modifier,
    filas: Int? = null,
    totalItems: Int? = null
) {
    if (totalPaginas <= 1) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotonPagina(icono = Icons.Filled.ChevronLeft, activo = paginaActual > 1) {
            onPagina(paginaActual - 1)
        }
        val inicio = ((paginaActual - 1) / 5) * 5 + 1
        val fin = minOf(inicio + 4, totalPaginas)
        for (p in inicio..fin) {
            BotonPagina(
                texto = p.toString(),
                seleccionado = p == paginaActual,
                activo = true
            ) { onPagina(p) }
        }
        BotonPagina(icono = Icons.Filled.ChevronRight, activo = paginaActual < totalPaginas) {
            onPagina(paginaActual + 1)
        }
        if (filas != null && totalItems != null) {
            Text(
                text = "Mostrando $filas de $totalItems",
                color = TextoSuave,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun BotonPagina(
    activo: Boolean,
    texto: String? = null,
    icono: androidx.compose.ui.graphics.vector.ImageVector? = null,
    seleccionado: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        seleccionado -> AzulPrincipal
        else -> Color.White
    }
    val fg = if (seleccionado) Color.White else AzulClaro
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (activo) bg else Color.Transparent)
            .border(1.dp, if (activo) BordeTarjeta else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(enabled = activo, interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = if (activo) fg else TextoSuave.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        } else {
            Text(text = texto ?: "", color = if (activo) fg else TextoSuave.copy(alpha = 0.4f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}
