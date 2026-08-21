package com.ecclesia.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.BordeTarjeta
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.TextoSuave


@Composable
fun EcclesiaCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(16.dp), ambientColor = AzulPrincipal.copy(alpha = 0.08f), spotColor = AzulPrincipal.copy(alpha = 0.06f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BordeTarjeta)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun EcclesiaCardHeader(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = AzulPrincipal
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave
                )
            }
        }
        acciones()
    }
}

@Composable
fun EcclesiaDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        color = BordeTarjeta,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun CampoLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium,
        color = AzulPrincipal,
        modifier = modifier
    )
}

@Composable
fun CruzDorada(modifier: Modifier = Modifier, tamaño: androidx.compose.ui.unit.Dp = 48.dp) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = "✝",
            color = Dorado,
            fontSize = (tamaño.value / 5f).sp
        )
    }
}
