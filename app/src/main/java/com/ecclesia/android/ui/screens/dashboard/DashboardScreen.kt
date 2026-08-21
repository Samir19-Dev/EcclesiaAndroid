package com.ecclesia.android.ui.screens.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.SolicitudSacramento
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.AvatarIniciales
import com.ecclesia.android.ui.components.BadgeEstado
import com.ecclesia.android.ui.components.BadgeEstadoAuto
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.EcclesiaCardHeader
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.LoadingOverlay
import com.ecclesia.android.ui.components.rolPrincipalDe
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.EcclesiaTheme
import com.ecclesia.android.ui.theme.FontCinzel
import com.ecclesia.android.ui.theme.GrisClaro
import com.ecclesia.android.ui.theme.GrisSecundario
import com.ecclesia.android.ui.theme.StatDanger
import com.ecclesia.android.ui.theme.StatPrimary
import com.ecclesia.android.ui.theme.StatSuccess
import com.ecclesia.android.ui.theme.StatWarning
import com.ecclesia.android.ui.theme.TextoSuave
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onMenuClick: (() -> Unit)? = null,
    onPersonasClick: () -> Unit = {},
    onEventosClick: () -> Unit = {},
    onSolicitudesClick: () -> Unit = {},
    onPagosClick: () -> Unit = {},
    onCertificadosClick: () -> Unit = {},
    onCursosClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Inicio",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
        LoadingOverlay(
            isLoading = uiState.isLoading,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                CabeceraDashboard(
                    usuarioNombre = uiState.data.usuario?.persona?.nombreCompleto,
                    onActualizar = viewModel::cargar
                )

                uiState.error?.let { ErrorMessage(message = it) }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.data.esAdmin) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            titulo = "Solicitudes Activas",
                            valor = uiState.data.totalSolicitudesActivas.toString(),
                            icono = Icons.Filled.Description,
                            color = StatPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            titulo = "Sacramentos",
                            valor = uiState.data.totalSacramentos.toString(),
                            icono = Icons.Filled.MenuBook,
                            color = StatSuccess,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            titulo = "Personas Registradas",
                            valor = uiState.data.totalPersonas.toString(),
                            icono = Icons.Filled.Groups,
                            color = StatWarning,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            titulo = "Usuarios",
                            valor = uiState.data.totalUsuarios.toString(),
                            icono = Icons.Filled.Person,
                            color = StatDanger,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            titulo = "Mis Solicitudes Activas",
                            valor = uiState.data.misSolicitudesActivas.toString(),
                            icono = Icons.Filled.Description,
                            color = StatPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            titulo = "Mis Certificados",
                            valor = uiState.data.totalCertificados.toString(),
                            icono = Icons.Filled.Verified,
                            color = StatSuccess,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            titulo = "Mis Notificaciones",
                            valor = uiState.data.notificacionesNoLeidas.toString(),
                            icono = Icons.Filled.Notifications,
                            color = StatWarning,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            titulo = "Eventos Parroquiales",
                            valor = uiState.data.totalEventos.toString(),
                            icono = Icons.Filled.CalendarMonth,
                            color = StatDanger,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SolicitudesRecientesCard(
                    solicitudes = uiState.data.solicitudes.take(5),
                    onClick = onSolicitudesClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                AccesosRapidosCard(
                    onSolicitudesClick = onSolicitudesClick,
                    onCertificadosClick = onCertificadosClick,
                    onEventosClick = onEventosClick,
                    onCursosClick = onCursosClick,
                    onPersonasClick = onPersonasClick,
                    onPagosClick = onPagosClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoUsuarioCard(
                    nombre = uiState.data.usuario?.persona?.nombreCompleto,
                    correo = uiState.data.usuario?.correo,
                    fotoUrl = uiState.data.usuario?.persona?.fotoUrl,
                    roles = uiState.data.usuario?.roles?.mapNotNull { it.nombre } ?: emptyList()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "EcclesiaSys v1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CabeceraDashboard(
    usuarioNombre: String?,
    onActualizar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Panel de Control",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontCinzel,
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )
            Text(
                text = "Hola, ${usuarioNombre?.ifBlank { null } ?: "bienvenido"}",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSuave
            )
        }
        IconButton(onClick = onActualizar) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Actualizar datos",
                tint = AzulPrincipal
            )
        }
    }
}

@Composable
private fun StatCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    EcclesiaCard(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextoSuave
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = valor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun SolicitudesRecientesCard(
    solicitudes: List<SolicitudSacramento>,
    onClick: () -> Unit
) {
    EcclesiaCard {
        EcclesiaCardHeader(
            titulo = "Solicitudes Recientes",
            subtitulo = "Últimos trámites y movimientos registrados"
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (solicitudes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Inbox,
                    contentDescription = null,
                    tint = TextoSuave,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No hay solicitudes recientes para mostrar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            solicitudes.forEachIndexed { index, solicitud ->
                SolicitudRow(solicitud = solicitud)
                if (index < solicitudes.lastIndex) {
                    com.ecclesia.android.ui.components.EcclesiaDivider()
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ver todas las solicitudes",
            style = MaterialTheme.typography.labelMedium,
            color = AzulPrincipal,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SolicitudRow(solicitud: SolicitudSacramento) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = solicitud.personaNombre.orEmpty().ifBlank { "Trámite" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = AzulPrincipal,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            BadgeEstado(
                texto = solicitud.sacramentoNombre.orEmpty().ifBlank { "Sacramento" },
                color = GrisClaro,
                textoColor = GrisSecundario
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            BadgeEstadoAuto(estado = solicitud.estado)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatearFechaSolicitud(solicitud),
                style = MaterialTheme.typography.labelSmall,
                color = TextoSuave
            )
        }
    }
}

@Composable
private fun AccesosRapidosCard(
    onSolicitudesClick: () -> Unit,
    onCertificadosClick: () -> Unit,
    onEventosClick: () -> Unit,
    onCursosClick: () -> Unit,
    onPersonasClick: () -> Unit,
    onPagosClick: () -> Unit
) {
    EcclesiaCard {
        EcclesiaCardHeader(
            titulo = "Accesos Rápidos",
            subtitulo = "Módulos principales del sistema"
        )
        Spacer(modifier = Modifier.height(12.dp))

        AccesoRapido(
            icono = Icons.Filled.Description,
            color = StatPrimary,
            texto = "Solicitudes",
            onClick = onSolicitudesClick
        )
        AccesoRapido(
            icono = Icons.Filled.Verified,
            color = StatSuccess,
            texto = "Certificados",
            onClick = onCertificadosClick
        )
        AccesoRapido(
            icono = Icons.Filled.CalendarMonth,
            color = Color(0xFF0DCAF0),
            texto = "Eventos",
            onClick = onEventosClick
        )
        AccesoRapido(
            icono = Icons.Filled.School,
            color = GrisSecundario,
            texto = "Cursos de Catequesis",
            onClick = onCursosClick
        )
        AccesoRapido(
            icono = Icons.Filled.Groups,
            color = StatWarning,
            texto = "Personas",
            onClick = onPersonasClick
        )
        AccesoRapido(
            icono = Icons.Filled.Payments,
            color = StatDanger,
            texto = "Pagos",
            onClick = onPagosClick
        )
    }
}

@Composable
private fun AccesoRapido(
    icono: ImageVector,
    color: Color,
    texto: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = AzulPrincipal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = TextoSuave,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun InfoUsuarioCard(
    nombre: String?,
    correo: String?,
    fotoUrl: String?,
    roles: List<String>
) {
    val rol = rolPrincipalDe(roles) ?: "Usuario"

    EcclesiaCard {
        EcclesiaCardHeader(
            titulo = "Información de Usuario",
            subtitulo = "Estado de la cuenta activa"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(
                fotoUrl = fotoUrl,
                nombre = nombre,
                size = 48.dp,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = nombre.orEmpty().ifBlank { correo.orEmpty().ifBlank { "Usuario" } },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrincipal,
                    maxLines = 1
                )
                Text(
                    text = rol.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = Dorado
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ItemInfoUsuario(
            icono = Icons.Filled.Verified,
            color = StatSuccess,
            texto = "Sesión activa correctamente."
        )
        ItemInfoUsuario(
            icono = Icons.Filled.Groups,
            color = StatPrimary,
            texto = "Rol: $rol"
        )
        ItemInfoUsuario(
            icono = Icons.Filled.Description,
            color = StatSuccess,
            texto = "Conexión segura al servidor parroquial."
        )
    }
}

@Composable
private fun ItemInfoUsuario(
    icono: ImageVector,
    color: Color,
    texto: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave,
            modifier = Modifier.weight(1f)
        )
    }
}

private val formatoFechaSolicitud: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.of("America/Bogota"))

private fun parsearFechaSolicitud(fecha: String?): ZonedDateTime? {
    if (fecha.isNullOrBlank()) return null
    return try {
        ZonedDateTime.parse(fecha)
    } catch (_: Exception) {
        try {
            OffsetDateTime.parse(fecha).toZonedDateTime()
        } catch (_: Exception) {
            try {
                LocalDateTime.parse(fecha).atZone(ZoneId.of("America/Bogota"))
            } catch (_: Exception) {
                null
            }
        }
    }
}

private fun formatearFechaSolicitud(solicitud: SolicitudSacramento): String {
    val fecha = solicitud.fechaPreferida ?: solicitud.createdAt
    return parsearFechaSolicitud(fecha)?.format(formatoFechaSolicitud) ?: "—"
}

@Composable
fun PreviewDashboardScreen() {
    EcclesiaTheme {
        DashboardScreen()
    }
}