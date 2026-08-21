package com.ecclesia.android.ui.screens.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.AvatarIniciales
import com.ecclesia.android.ui.components.BadgeEstadoAuto
import com.ecclesia.android.ui.components.ConfirmDialog
import com.ecclesia.android.ui.components.EcclesiaSearchField
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: UsuariosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { viewModel.consumirMensaje() }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Usuarios",
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::abrirDialogoNuevo) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo usuario")
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
                        placeholder = "Buscar por nombre o correo…"
                    )
                }

                if (uiState.filtrados.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.busqueda.isBlank()) "No hay usuarios registrados"
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
                        items(uiState.filtrados, key = { it.id }) { fila ->
                            UsuarioItem(
                                fila = fila,
                                onEditar = { viewModel.abrirDialogoEditar(fila) },
                                onToggleEstado = { viewModel.toggleEstado(fila) },
                                onEliminar = { viewModel.pedirConfirmacionEliminar(fila) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.dialogoAbierto) {
        UsuarioDialog(viewModel = viewModel, uiState = uiState)
    }

    uiState.eliminando?.let { fila ->
        ConfirmDialog(
            titulo = "Eliminar usuario",
            mensaje = "¿Está seguro de eliminar al usuario ${fila.correo}?",
            confirmarTexto = "Eliminar",
            peligro = true,
            onConfirmar = viewModel::confirmarEliminar,
            onCancelar = viewModel::cancelarEliminar
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuarioDialog(viewModel: UsuariosViewModel, uiState: UsuariosUiState) {
    val esEdicion = uiState.editandoId != null
    var menuRolesAbierto by remember { mutableStateOf(false) }
    val rolSeleccionado = uiState.roles.firstOrNull { it.id == uiState.rolSeleccionadoId }

    AlertDialog(
        onDismissRequest = viewModel::cerrarDialogo,
        title = { Text(if (esEdicion) "Editar usuario" else "Nuevo usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = uiState.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    enabled = !esEdicion,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!esEdicion) {
                    OutlinedTextField(
                        value = uiState.contrasena,
                        onValueChange = viewModel::onContrasenaChange,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = menuRolesAbierto,
                    onExpandedChange = { menuRolesAbierto = it }
                ) {
                    OutlinedTextField(
                        value = rolSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuRolesAbierto) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = menuRolesAbierto,
                        onDismissRequest = { menuRolesAbierto = false }
                    ) {
                        uiState.roles.forEach { rol ->
                            DropdownMenuItem(
                                text = { Text(rol.nombre) },
                                onClick = {
                                    viewModel.onRolChange(rol.id)
                                    menuRolesAbierto = false
                                }
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Activo", "Inactivo").forEach { estado ->
                        val seleccionado = uiState.estadoSeleccionado == estado
                        TextButton(onClick = { viewModel.onEstadoChange(estado) }) {
                            Text(
                                text = if (seleccionado) "● $estado" else estado,
                                color = if (seleccionado) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = viewModel::guardar,
                enabled = !uiState.guardando &&
                    (esEdicion || (uiState.correo.isNotBlank() && uiState.correo.contains("@")))
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (esEdicion) "Guardar" else "Registrar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::cerrarDialogo) { Text("Cancelar") }
        }
    )
}

@Composable
private fun UsuarioItem(
    fila: UsuarioFila,
    onEditar: () -> Unit,
    onToggleEstado: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarIniciales(fotoUrl = null, nombre = fila.nombre, size = 40.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(text = fila.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = fila.correo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    BadgeEstadoAuto(estado = fila.rol)
                    BadgeEstadoAuto(estado = fila.estado)
                }
            }
            IconButton(onClick = onToggleEstado) {
                Icon(
                    Icons.Filled.PowerSettingsNew,
                    contentDescription = "Cambiar estado",
                    tint = if (fila.estado == "Activo") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
