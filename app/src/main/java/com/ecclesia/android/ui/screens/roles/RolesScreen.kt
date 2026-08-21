package com.ecclesia.android.ui.screens.roles

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.BadgeEstadoAuto
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: RolesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Roles y Permisos",
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::abrirDialogoNuevo) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo rol")
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
                uiState.error?.let {
                    ErrorMessage(message = it, modifier = Modifier.padding(horizontal = 16.dp))
                }

                if (uiState.roles.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay roles registrados",
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
                        items(uiState.roles, key = { it.id }) { rol ->
                            RolItem(
                                rol = rol,
                                totalPermisos = uiState.permisosDisponibles.size,
                                onEditar = {
                                    viewModel.abrirDialogoEditar(rol.id, rol.nombre, rol.descripcion)
                                }
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
            title = { Text(if (uiState.editandoId != null) "Editar rol" else "Nuevo rol") },
            text = {
                Column {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = uiState.nombre,
                            onValueChange = viewModel::onNombreChange,
                            label = { Text("Nombre del rol") },
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
                        Text(
                            text = "Permisos (${uiState.permisosSeleccionados.size} seleccionados)",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (uiState.cargandoPermisos) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(vertical = 12.dp))
                        }
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            uiState.permisosDisponibles.forEach { permiso ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.togglePermiso(permiso.id) }
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = permiso.id in uiState.permisosSeleccionados,
                                        onCheckedChange = { viewModel.togglePermiso(permiso.id) }
                                    )
                                    Column {
                                        Text(text = permiso.nombre, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            text = permiso.codigo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::guardar,
                    enabled = uiState.nombre.trim().length >= 3 && !uiState.guardando
                ) {
                    Text(if (uiState.editandoId != null) "Guardar" else "Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cerrarDialogo) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun RolItem(
    rol: com.ecclesia.android.domain.models.Rol,
    totalPermisos: Int,
    onEditar: () -> Unit
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
                imageVector = Icons.Filled.AdminPanelSettings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(text = rol.nombre, style = MaterialTheme.typography.titleMedium)
                if (rol.descripcion.isNotBlank()) {
                    Text(
                        text = rol.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    if (rol.esSistema) {
                        BadgeEstadoAuto(estado = "sistema")
                    }
                    Text(
                        text = "ID: ${rol.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
