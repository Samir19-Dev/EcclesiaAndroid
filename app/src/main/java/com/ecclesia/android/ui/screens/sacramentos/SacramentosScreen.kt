package com.ecclesia.android.ui.screens.sacramentos

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.Sacramento
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.BadgeEstadoAuto
import com.ecclesia.android.ui.components.ConfirmDialog
import com.ecclesia.android.ui.components.EcclesiaSearchField
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SacramentosScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: SacramentosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { viewModel.consumirMensaje() }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Sacramentos",
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (uiState.puedeEditar) {
                FloatingActionButton(onClick = viewModel::abrirDialogoNuevo) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo sacramento")
                }
            }
        }
    ) { innerPadding ->
        LoadingOverlay(
            isLoading = uiState.isLoading,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    uiState.error?.let {
                        ErrorMessage(message = it)
                    }
                    EcclesiaSearchField(
                        valor = uiState.busqueda,
                        onCambio = viewModel::onBusquedaChange,
                        placeholder = "Buscar sacramento…"
                    )
                }

                if (uiState.filtrados.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.busqueda.isBlank()) "No hay sacramentos registrados"
                            else "Sin resultados para \"${uiState.busqueda}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filtrados, key = { it.id }) { sacramento ->
                            SacramentoItem(
                                sacramento = sacramento,
                                puedeEditar = uiState.puedeEditar,
                                puedeEliminar = uiState.puedeEliminar,
                                onEditar = { viewModel.abrirDialogoEditar(sacramento) },
                                onEliminar = { viewModel.pedirConfirmacionEliminar(sacramento) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.dialogoAbierto) {
        AlertDialog(
            onDismissRequest = viewModel::cerrarDialogo,
            title = { Text(if (uiState.editandoId != null) "Editar sacramento" else "Nuevo sacramento") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = viewModel::onNombreChange,
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.descripcion,
                        onValueChange = viewModel::onDescripcionChange,
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activo", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = uiState.activo, onCheckedChange = viewModel::onActivoChange)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Genera certificado", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = uiState.generaCertificado,
                            onCheckedChange = viewModel::onGeneraCertificadoChange
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::guardar,
                    enabled = uiState.nombre.isNotBlank() && !uiState.guardando
                ) {
                    Text(if (uiState.editandoId != null) "Guardar" else "Registrar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cerrarDialogo) { Text("Cancelar") }
            }
        )
    }

    uiState.eliminando?.let { s ->
        ConfirmDialog(
            titulo = "Desactivar sacramento",
            mensaje = "¿Desactivar \"${s.nombre}\"? El registro dejará de mostrarse en el catálogo.",
            confirmarTexto = "Desactivar",
            peligro = true,
            onConfirmar = viewModel::confirmarEliminar,
            onCancelar = viewModel::cancelarEliminar
        )
    }
}

@Composable
private fun SacramentoItem(
    sacramento: Sacramento,
    puedeEditar: Boolean,
    puedeEliminar: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.AutoStories,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(text = sacramento.nombre, style = MaterialTheme.typography.titleMedium)
                sacramento.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    BadgeEstadoAuto(estado = if (sacramento.activo) "activo" else "inactivo")
                    Text(
                        text = "${sacramento.requisitos} requisitos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (sacramento.generaCertificado) {
                        Text(
                            text = "· Certificado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (puedeEditar) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (puedeEliminar) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Desactivar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
