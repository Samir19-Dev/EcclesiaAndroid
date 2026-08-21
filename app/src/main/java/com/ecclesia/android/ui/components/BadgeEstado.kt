package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.theme.AmarilloAdvertencia
import com.ecclesia.android.ui.theme.AzulBootstrap
import com.ecclesia.android.ui.theme.AzulInfo
import com.ecclesia.android.ui.theme.EstadoCancelado
import com.ecclesia.android.ui.theme.EstadoDocIncompleta
import com.ecclesia.android.ui.theme.EstadoPendiente
import com.ecclesia.android.ui.theme.EstadoRechazado
import com.ecclesia.android.ui.theme.EstadoRevision
import com.ecclesia.android.ui.theme.GrisClaro
import com.ecclesia.android.ui.theme.GrisSecundario
import com.ecclesia.android.ui.theme.RojoPeligro
import com.ecclesia.android.ui.theme.VerdeExito


@Composable
fun BadgeEstado(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier,
    textoColor: Color = Color.White
) {
    val colorTexto = if (color == AmarilloAdvertencia || color == AzulInfo || color == GrisClaro) Color(0xFF212529) else textoColor
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = colorTexto,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

private fun colorPorEstado(estado: String?): Color {
    val e = (estado ?: "").lowercase()
    return when {
        e.contains("aprob") -> VerdeExito
        e.contains("recha") || e.contains("anul") || e.contains("cancelado") -> RojoPeligro
        e.contains("revis") -> AzulInfo
        e.contains("incomp") || e.contains("doc") || e.contains("borrador") || e.contains("inactivo") -> GrisSecundario
        e.contains("cancel") -> EstadoCancelado
        e.contains("pend") -> EstadoPendiente
        e.contains("pagado") || e.contains("completado") || e.contains("publicado") || e.contains("confirmado") -> VerdeExito
        e.contains("activo") -> VerdeExito
        e.contains("finalizado") || e.contains("cerrado") || e.contains("programado") -> AzulBootstrap
        else -> GrisSecundario
    }
}

@Composable
fun BadgeEstadoAuto(
    estado: String?,
    modifier: Modifier = Modifier
) {
    BadgeEstado(texto = estado ?: "—", color = colorPorEstado(estado), modifier = modifier)
}

@Composable
fun BadgeAccionAuditoria(
    accion: String,
    modifier: Modifier = Modifier
) {
    val color = when (accion.uppercase()) {
        "CREAR" -> VerdeExito
        "EDITAR" -> AmarilloAdvertencia
        "ELIMINAR" -> RojoPeligro
        "EMISION_CERTIFICADO" -> AzulBootstrap
        "LOGIN" -> AzulInfo
        else -> GrisSecundario
    }
    BadgeEstado(texto = accion, color = color, modifier = modifier)
}
