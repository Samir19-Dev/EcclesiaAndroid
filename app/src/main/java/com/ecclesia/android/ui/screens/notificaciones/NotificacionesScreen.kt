package com.ecclesia.android.ui.screens.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.Notificacion
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.EcclesiaCardHeader
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.TextoSuave
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private data class CategoriaNotificacion(
    val clave: String,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

private val categorias = listOf(
    CategoriaNotificacion(
        clave = "solicitudes",
        titulo = "Solicitudes de sacramentos",
        descripcion = "Avísame cuando se cree o actualice una solicitud.",
        icono = Icons.AutoMirrored.Filled.Assignment
    ),
    CategoriaNotificacion(
        clave = "estados",
        titulo = "Cambios de estado",
        descripcion = "Notifica cuando una solicitud cambia de estado.",
        icono = Icons.Filled.SyncAlt
    ),
    CategoriaNotificacion(
        clave = "eventos",
        titulo = "Eventos y agenda",
        descripcion = "Recordatorios de misas, retiros y cursos.",
        icono = Icons.Filled.Event
    ),
    CategoriaNotificacion(
        clave = "auditoria",
        titulo = "Auditoría y seguridad",
        descripcion = "Alertas de acciones sensibles en el sistema.",
        icono = Icons.Filled.Security
    ),
    CategoriaNotificacion(
        clave = "cuenta",
        titulo = "Cuenta y seguridad",
        descripcion = "Inicios de sesión, cambios de contraseña y correo.",
        icono = Icons.Filled.VerifiedUser
    )
)

private fun iconoPorTipo(tipo: String?): ImageVector = when (tipo?.lowercase()) {
    "solicitud" -> Icons.Filled.Inbox
    "certificado" -> Icons.Filled.Verified
    "evento" -> Icons.Filled.CalendarMonth
    "sistema" -> Icons.Filled.Settings
    else -> Icons.Filled.Notifications
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onMenuClick: (() -> Unit)? = null,
    viewModel: NotificacionesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Notificaciones",
                onMenuClick = onMenuClick
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.cargando && uiState.notificaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = uiState.filtro == "todas",
                                onClick = { viewModel.onFiltroChange("todas") },
                                label = { Text("Todas (${uiState.notificaciones.size})") }
                            )
                            FilterChip(
                                selected = uiState.filtro == "noleidas",
                                onClick = { viewModel.onFiltroChange("noleidas") },
                                label = { Text("No leídas (${uiState.noLeidas})") }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (uiState.noLeidas > 0) {
                                TextButton(onClick = viewModel::marcarTodasLeidas) {
                                    Icon(
                                        Icons.Filled.DoneAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Marcar todas")
                                }
                            }
                        }
                    }

                    if (uiState.filtradas.isEmpty()) {
                        item {
                            EcclesiaCard(contentPadding = PaddingValues(24.dp)) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Filled.Notifications,
                                        contentDescription = null,
                                        tint = TextoSuave,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (uiState.filtro == "noleidas")
                                            "No tienes notificaciones sin leer"
                                        else "No hay notificaciones para mostrar",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextoSuave
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.filtradas, key = { it.id }) { notificacion ->
                            NotificacionCard(
                                notificacion = notificacion,
                                onClick = { viewModel.marcarLeida(notificacion) }
                            )
                        }
                    }

                    item {
                        EcclesiaCard {
                            EcclesiaCardHeader(
                                titulo = "Preferencias de notificación",
                                subtitulo = "Se aplicarán automáticamente en tu cuenta."
                            )
                        }
                    }
                    items(categorias, key = { "pref_${it.clave}" }) { categoria ->
                        CategoriaCard(
                            categoria = categoria,
                            activado = uiState.preferencias[categoria.clave] ?: true,
                            onCambiar = { viewModel.cambiar(categoria.clave, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacionCard(notificacion: Notificacion, onClick: () -> Unit) {
    val fechaFormateada = formatearFecha(notificacion.createdAt)
    EcclesiaCard(contentPadding = PaddingValues(14.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoPorTipo(notificacion.tipo),
                    contentDescription = null,
                    tint = AzulPrincipal,
                    modifier = Modifier.size(24.dp)
                )
                if (!notificacion.leida) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .background(Dorado, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificacion.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (!notificacion.leida) FontWeight.Bold else FontWeight.Normal,
                    color = AzulPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                notificacion.mensaje?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSuave,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                fechaFormateada?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSuave
                    )
                }
            }
        }
    }
}

private fun formatearFecha(fecha: String?): String? {
    if (fecha.isNullOrBlank()) return null
    return try {
        val dt = OffsetDateTime.parse(fecha)
        dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (e: Exception) {
        fecha.take(10)
    }
}

@Composable
private fun CategoriaCard(
    categoria: CategoriaNotificacion,
    activado: Boolean,
    onCambiar: (Boolean) -> Unit
) {
    EcclesiaCard(contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoria.icono,
                    contentDescription = null,
                    tint = if (activado) Dorado else TextoSuave,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoria.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    color = AzulPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = categoria.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = activado,
                onCheckedChange = onCambiar
            )
        }
    }
}
