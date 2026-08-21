package com.ecclesia.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.RojoPeligro
import com.ecclesia.android.ui.theme.TextoSuave


@Composable
fun ConfirmDialog(
    titulo: String,
    mensaje: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    confirmarTexto: String = "Confirmar",
    cancelarTexto: String = "Cancelar",
    peligro: Boolean = false,
    loading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = null,
                tint = if (peligro) RojoPeligro else Dorado,
                modifier = Modifier.size(44.dp)
            )
        },
        title = {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulPrincipal,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = mensaje,
                color = TextoSuave,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            EcclesiaButton(
                text = confirmarTexto,
                onClick = onConfirmar,
                loading = loading,
                colors = if (peligro) listOf(RojoPeligro, RojoPeligro) else listOf(Dorado, Dorado)
            )
        },
        dismissButton = {
            EcclesiaOutlineButton(
                text = cancelarTexto,
                onClick = onCancelar,
                enabled = !loading,
                color = AzulPrincipal
            )
        }
    )
}
