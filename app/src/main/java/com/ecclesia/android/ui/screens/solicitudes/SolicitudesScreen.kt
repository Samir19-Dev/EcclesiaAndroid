package com.ecclesia.android.ui.screens.solicitudes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.SolicitudSacramento
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.BadgeEstadoAuto
import com.ecclesia.android.ui.components.EcclesiaOutlineButton
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.LoadingOverlay
import com.ecclesia.android.ui.theme.EcclesiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: SolicitudesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (uiState.esAdmin) "Solicitudes (Todas)" else "Mis Solicitudes",
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LoadingOverlay(
            isLoading = uiState.isLoading,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                uiState.error?.let {
                    ErrorMessage(
                        message = it,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (uiState.solicitudes.isEmpty() && !uiState.isLoading && uiState.error == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.esAdmin) "No hay solicitudes registradas"
                            else "No tienes solicitudes registradas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(uiState.solicitudes, key = { it.id }) { solicitud ->
                            SolicitudItem(
                                solicitud = solicitud,
                                uiState = uiState,
                                onAprobar = { viewModel.cambiarEstado(solicitud, "aprobada") },
                                onRechazar = { viewModel.cambiarEstado(solicitud, "rechazada") },
                                onEnRevision = { viewModel.cambiarEstado(solicitud, "en_revision") }
                            )
                        }
                    }
                }
            }
        }
    }

    uiState.rechazando?.let { s ->
        AlertDialog(
            onDismissRequest = viewModel::cancelarRechazo,
            title = { Text("Rechazar solicitud") },
            text = {
                Column {
                    Text(
                        text = "El motivo se notificará al solicitante y quedará registrado en la auditoría.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = uiState.motivoRechazo,
                        onValueChange = viewModel::onMotivoRechazoChange,
                        label = { Text("Motivo del rechazo") },
                        placeholder = { Text("Escribe el motivo...") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::confirmarRechazo,
                    enabled = uiState.motivoRechazo.isNotBlank()
                ) {
                    Text("Rechazar solicitud", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarRechazo) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SolicitudItem(
    solicitud: SolicitudSacramento,
    uiState: SolicitudesUiState,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit,
    onEnRevision: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RequestPage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(
                    text = solicitud.sacramentoNombre ?: "Sacramento",
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.esAdmin) {
                    solicitud.usuarioCorreo?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                solicitud.personaNombre?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                solicitud.fechaPreferida?.let {
                    Text(
                        text = "Fecha preferida: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BadgeEstadoAuto(
                    estado = solicitud.estado,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (uiState.esAdmin && uiState.guardandoId != solicitud.id) {
                    val puedeAprobar = uiState.puedeTransicionar(solicitud, "aprobada")
                    val puedeRechazar = uiState.puedeTransicionar(solicitud, "rechazada")
                    val puedeRevisar = uiState.puedeTransicionar(solicitud, "en_revision")
                    if (puedeAprobar || puedeRechazar || puedeRevisar) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            if (puedeAprobar) {
                                EcclesiaOutlineButton(
                                    text = "Aprobar",
                                    onClick = onAprobar,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            if (puedeRevisar && solicitud.estado.lowercase() != "en_revision") {
                                EcclesiaOutlineButton(
                                    text = "En revisión",
                                    onClick = onEnRevision
                                )
                            }
                            if (puedeRechazar) {
                                EcclesiaOutlineButton(
                                    text = "Rechazar",
                                    onClick = onRechazar,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewSolicitudesScreen() {
    EcclesiaTheme {
        SolicitudesScreen()
    }
}
