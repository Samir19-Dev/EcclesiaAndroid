package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.theme.AmarilloAdvertencia
import com.ecclesia.android.ui.theme.RojoPeligro
import com.ecclesia.android.ui.theme.TextoSuave
import com.ecclesia.android.ui.theme.VerdeExito

fun fuerzaContrasena(pass: String): Int {
    if (pass.isEmpty()) return 0
    var score = 0
    if (pass.length >= 8) score++
    if (pass.any { it.isLetter() } && pass.any { it.isDigit() }) score++
    if (pass.any { it.isUpperCase() } && pass.any { it.isLowerCase() }) score++
    if (pass.any { !it.isLetterOrDigit() }) score++
    return score
}

@Composable
fun IndicadorFuerza(password: String, modifier: Modifier = Modifier) {
    val fuerza = fuerzaContrasena(password)
    if (password.isEmpty()) return

    val color = when (fuerza) {
        1 -> RojoPeligro
        2, 3 -> AmarilloAdvertencia
        else -> VerdeExito
    }
    val etiqueta = when (fuerza) {
        1 -> "Débil"
        2 -> "Regular"
        3 -> "Buena"
        else -> "Fuerte"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f).height(6.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(3.dp))) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fuerza / 4f)
                    .height(6.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
        Text(
            text = etiqueta,
            color = if (fuerza == 4) TextoSuave else color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RequisitosContrasena(password: String, modifier: Modifier = Modifier) {
    fun check(valido: Boolean) = if (valido) VerdeExito else TextoSuave.copy(alpha = 0.6f)

    Column(modifier = modifier.padding(top = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = check(password.length >= 8), fontSize = 12.sp)
            Text(" Mínimo 8 caracteres", color = check(password.length >= 8), fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = check(password.any { it.isLetter() } && password.any { it.isDigit() }), fontSize = 12.sp)
            Text(" Letras y números", color = check(password.any { it.isLetter() } && password.any { it.isDigit() }), fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = check(password.any { it.isUpperCase() } && password.any { it.isLowerCase() }), fontSize = 12.sp)
            Text(" Mayúsculas y minúsculas", color = check(password.any { it.isUpperCase() } && password.any { it.isLowerCase() }), fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = check(password.any { !it.isLetterOrDigit() }), fontSize = 12.sp)
            Text(" Al menos un carácter especial", color = check(password.any { !it.isLetterOrDigit() }), fontSize = 12.sp)
        }
    }
}
