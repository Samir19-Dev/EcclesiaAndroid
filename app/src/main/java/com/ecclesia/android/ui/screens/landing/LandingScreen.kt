package com.ecclesia.android.ui.screens.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.components.EcclesiaButton
import com.ecclesia.android.ui.components.EcclesiaOutlineButton
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.FontCinzel
import com.ecclesia.android.ui.theme.TextoSuave

private val NavyOscuro = Color(0xFF07182E)
private val NavyMedio = Color(0xFF0F2540)

private data class FeatureLanding(
    val titulo: String,
    val desc: String,
    val icono: ImageVector,
    val colorFondo: Color,
    val colorIcono: Color
)

private data class StatLanding(val num: String, val label: String)

private val featuresLanding = listOf(
    FeatureLanding(
        titulo = "Gestión Sacramental",
        desc = "Registra y administra Bautismo, Confirmación, Matrimonio, Primera Comunión y más con flujos personalizados por sacramento.",
        icono = Icons.AutoMirrored.Filled.ReceiptLong,
        colorFondo = Color(0x1F2A5298), colorIcono = AzulClaro
    ),
    FeatureLanding(
        titulo = "Certificados Digitales",
        desc = "Generación automática para sacramentos de bajo riesgo. Validación humana para casos sensibles. Todo con QR verificable.",
        icono = Icons.Filled.Verified,
        colorFondo = Color(0x26C9A84C), colorIcono = Color(0xFFB8960C)
    ),
    FeatureLanding(
        titulo = "Agenda y Eventos",
        desc = "Gestiona misas, retiros, cursos y celebraciones. Control de cupos, inscripciones y conflictos de horario automático.",
        icono = Icons.Filled.Event,
        colorFondo = Color(0x1F48C78E), colorIcono = Color(0xFF2D8F60)
    ),
    FeatureLanding(
        titulo = "Gestión de Personas",
        desc = "Base de datos parroquial completa. Relaciones familiares, historial sacramental y documentos en la nube.",
        icono = Icons.Filled.PeopleAlt,
        colorFondo = Color(0x1F9F5AFD), colorIcono = Color(0xFF7C3AED)
    ),
    FeatureLanding(
        titulo = "Seguridad y Auditoría",
        desc = "Trazabilidad completa de cada acción. Sin eliminación de datos. Historial inmutable con quién, qué y cuándo.",
        icono = Icons.Filled.Security,
        colorFondo = Color(0x1FEF4444), colorIcono = Color(0xFFDC2626)
    ),
    FeatureLanding(
        titulo = "Notificaciones",
        desc = "Alertas por correo y Telegram para usuarios y secretaría. Cada solicitud tiene seguimiento en tiempo real.",
        icono = Icons.Filled.Notifications,
        colorFondo = Color(0x1F14B8A6), colorIcono = Color(0xFF0F766E)
    )
)

private val statsLanding = listOf(
    StatLanding("100%", "Trazabilidad de registros"),
    StatLanding("0", "Registros eliminados"),
    StatLanding("∞", "Histórico sacramental"),
    StatLanding("24/7", "Acceso a certificados")
)

private val sacramentosLanding = listOf(
    "Bautismo", "Primera Comunión", "Confirmación", "Matrimonio", "Unción de Enfermos", "Penitencia"
)

@Composable
fun LandingScreen(
    onIniciarSesionClick: () -> Unit = {},
    onVerFuncionalidadesClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NavLanding(onIniciarSesionClick)
        HeroLanding(onIniciarSesionClick, onVerFuncionalidadesClick)
        FeaturesSection()
        StatsSection()
        CtaSection(onIniciarSesionClick)
        FooterLanding(onIniciarSesionClick)
    }
}

@Composable
private fun NavLanding(onIniciarSesionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyOscuro.copy(alpha = 0.92f))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("✝", color = Dorado, fontSize = 22.sp, fontFamily = FontCinzel)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "EcclesiaSys",
                color = Color.White,
                fontFamily = FontCinzel,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "GESTIÓN SACRAMENTAL",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 8.sp,
                letterSpacing = 1.6.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        EcclesiaOutlineButton(
            text = "Iniciar sesión",
            onClick = onIniciarSesionClick,
            color = Dorado,
            icon = Icons.AutoMirrored.Filled.Login
        )
    }
}

@Composable
private fun HeroLanding(
    onIniciarSesionClick: () -> Unit,
    onVerFuncionalidadesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF07182E), NavyMedio, AzulPrincipal)
                )
            )
            .padding(horizontal = 20.dp)
            .padding(top = 40.dp, bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Dorado.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, Dorado.copy(alpha = 0.3f), CircleShape)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Dorado, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PLATAFORMA DE GESTIÓN PARROQUIAL",
                color = Dorado,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "EcclesiaSys",
            color = Color.White,
            fontFamily = FontCinzel,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            lineHeight = 44.sp
        )
        Text(
            text = "tecnología digna",
            color = Color.Transparent,
            fontFamily = FontCinzel,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            style = TextStyle(brush = Brush.linearGradient(listOf(Dorado, Color(0xFFE8C860))))
        )

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "EcclesiaSys digitaliza la gestión sacramental de tu parroquia. " +
                "Certificados automáticos, trazabilidad completa y control seguro de cada sacramento.",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(22.dp))
        EcclesiaButton(
            text = "Ingresar a la plataforma",
            onClick = onIniciarSesionClick,
            icon = Icons.AutoMirrored.Filled.Login
        )
        Spacer(modifier = Modifier.height(10.dp))
        EcclesiaOutlineButton(
            text = "Ver funcionalidades",
            onClick = onVerFuncionalidadesClick,
            color = Color.White.copy(alpha = 0.8f),
            icon = Icons.Filled.ArrowDownward
        )

        Spacer(modifier = Modifier.height(40.dp))
        TarjetaHeroPrincipal()
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaHeroSecundaria(
            icono = Icons.Filled.PeopleAlt,
            titulo = "Solicitud de Matrimonio",
            sub = "En revisión por secretaría",
            badge = "Revisión",
            badgeColor = Color(0xFFFFB700)
        )
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaHeroSecundaria(
            icono = Icons.Filled.CalendarMonth,
            titulo = "Misa de Acción de Gracias",
            sub = "Dom 15 · 10:00 AM",
            badge = "Próximo",
            badgeColor = Color(0xFF209CEE)
        )
    }
}

@Composable
private fun TarjetaHeroPrincipal() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CajaIconoHero(Icons.Filled.Shield, 44.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Certificado de Bautismo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(
                "Generado automáticamente · Verificable con QR",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
        Text(
            text = "Emitido",
            color = Color(0xFF48C78E),
            modifier = Modifier
                .background(Color(0x2648C78E), CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TarjetaHeroSecundaria(
    icono: ImageVector,
    titulo: String,
    sub: String,
    badge: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CajaIconoHero(icono, 36.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(sub, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
        }
        Text(
            text = badge,
            color = badgeColor,
            modifier = Modifier
                .background(badgeColor.copy(alpha = 0.15f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CajaIconoHero(icono: ImageVector, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Dorado.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .border(1.dp, Dorado.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = Dorado, modifier = Modifier.size(size / 2))
    }
}

@Composable
private fun FeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FC))
            .padding(horizontal = 20.dp, vertical = 40.dp)
    ) {
        Text(
            text = "FUNCIONALIDADES",
            color = AzulPrincipal,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier
                .background(AzulPrincipal.copy(alpha = 0.08f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Todo lo que tu parroquia necesita",
            color = AzulPrincipal,
            fontFamily = FontCinzel,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Un sistema integral diseñado para la realidad de las comunidades religiosas",
            color = TextoSuave,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
        featuresLanding.forEach { f ->
            TarjetaFeature(f)
            Spacer(modifier = Modifier.height(14.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE8EDF5), RoundedCornerShape(14.dp))
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SACRAMENTOS SOPORTADOS",
                color = TextoSuave,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sacramentosLanding.take(3).forEach { s ->
                    SacramentoTag(s, Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sacramentosLanding.drop(3).forEach { s ->
                    SacramentoTag(s, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TarjetaFeature(f: FeatureLanding) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE8EDF5), RoundedCornerShape(14.dp))
            .padding(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(f.colorFondo, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(f.icono, contentDescription = null, tint = f.colorIcono, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(f.titulo, color = AzulPrincipal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(f.desc, color = TextoSuave, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun SacramentoTag(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = "✝ $texto",
        color = AzulPrincipal,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(AzulPrincipal.copy(alpha = 0.06f), CircleShape)
            .border(1.dp, AzulPrincipal.copy(alpha = 0.12f), CircleShape)
            .padding(vertical = 7.dp)
    )
}

@Composable
private fun StatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(AzulPrincipal, NavyMedio)))
            .padding(horizontal = 20.dp, vertical = 36.dp)
    ) {
        statsLanding.chunked(2).forEach { fila ->
            Row(modifier = Modifier.fillMaxWidth()) {
                fila.forEach { s ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = s.num,
                            color = Dorado,
                            fontFamily = FontCinzel,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp
                        )
                        Text(
                            text = s.label,
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun CtaSection(onIniciarSesionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FC))
            .padding(horizontal = 20.dp, vertical = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("✝", color = Dorado, fontSize = 40.sp, fontFamily = FontCinzel)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "¿Lista tu parroquia para el futuro?",
            color = AzulPrincipal,
            fontFamily = FontCinzel,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Digitaliza tu gestión sacramental hoy mismo.",
            color = TextoSuave,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        EcclesiaButton(
            text = "Acceder a la plataforma",
            onClick = onIniciarSesionClick,
            icon = Icons.AutoMirrored.Filled.ArrowRight
        )
    }
}

@Composable
private fun FooterLanding(onIniciarSesionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyOscuro)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("✝", color = Dorado, fontSize = 20.sp, fontFamily = FontCinzel)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("EcclesiaSys", color = Color.White, fontFamily = FontCinzel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    "PLATAFORMA DE GESTIÓN SACRAMENTAL",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 8.sp,
                    letterSpacing = 1.2.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Diseñado para la comunidad. Construido con propósito.",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.clickable { onIniciarSesionClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
                tint = Dorado,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Acceder al sistema", color = Dorado, fontSize = 11.sp)
        }
    }
}
