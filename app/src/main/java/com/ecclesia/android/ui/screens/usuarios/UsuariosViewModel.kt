package com.ecclesia.android.ui.screens.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.domain.models.ReemplazarRolRequest
import com.ecclesia.android.domain.models.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuariosViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(UsuariosUiState())
    val uiState: StateFlow<UsuariosUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val rolesRes = runCatching { api.roles() }.getOrElse { emptyList() }
            runCatching { api.usuariosAdmin() }
                .onSuccess { paginado ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            usuarios = paginado.items.map { u -> u.aFila() },
                            roles = rolesRes,
                            rolSeleccionadoId = it.rolSeleccionadoId ?: rolesRes.firstOrNull()?.id
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = ApiErrorParser.mensaje(e))
                    }
                }
        }
    }

    fun onBusquedaChange(valor: String) = _uiState.update { it.copy(busqueda = valor) }

    fun abrirDialogoNuevo() = _uiState.update {
        it.copy(
            dialogoAbierto = true, editandoId = null, correo = "",
            contrasena = "Ecclesia2026*", rolSeleccionadoId = it.roles.firstOrNull()?.id,
            estadoSeleccionado = "Activo", error = null
        )
    }

    fun abrirDialogoEditar(fila: UsuarioFila) = _uiState.update {
        it.copy(
            dialogoAbierto = true, editandoId = fila.id, correo = fila.correo,
            contrasena = "", rolSeleccionadoId = fila.rolId ?: it.roles.firstOrNull()?.id,
            estadoSeleccionado = fila.estado, error = null
        )
    }

    fun cerrarDialogo() = _uiState.update { it.copy(dialogoAbierto = false) }

    fun onCorreoChange(v: String) = _uiState.update { it.copy(correo = v) }
    fun onContrasenaChange(v: String) = _uiState.update { it.copy(contrasena = v) }
    fun onRolChange(id: Long) = _uiState.update { it.copy(rolSeleccionadoId = id) }
    fun onEstadoChange(v: String) = _uiState.update { it.copy(estadoSeleccionado = v) }

    fun guardar() {
        val s = _uiState.value
        val editandoId = s.editandoId
        if (editandoId == null && (s.correo.isBlank() || !s.correo.contains("@"))) return
        if (s.rolSeleccionadoId == null) return

        _uiState.update { it.copy(guardando = true, error = null) }
        viewModelScope.launch {
            if (editandoId != null) {
                var fallo: Throwable? = null
                s.rolSeleccionadoId?.let { rolId ->
                    runCatching { api.reemplazarRolUsuario(ReemplazarRolRequest(editandoId, rolId)) }
                        .onFailure { fallo = it }
                }
                if (fallo == null) {
                    runCatching { api.cambiarEstadoUsuario(editandoId, s.estadoSeleccionado) }
                        .onFailure { fallo = it }
                }
                if (fallo != null) {
                    _uiState.update {
                        it.copy(guardando = false, error = ApiErrorParser.mensaje(fallo!!))
                    }
                } else {
                    _uiState.update {
                        it.copy(guardando = false, dialogoAbierto = false, mensaje = "Usuario actualizado")
                    }
                    cargar()
                }
            } else {
                runCatching {
                    api.register(RegisterRequest(correo = s.correo.trim(), contrasena = s.contrasena.ifBlank { "Ecclesia2026*" }))
                }
                    .onSuccess { nuevo ->
                        s.rolSeleccionadoId?.let { rolId ->
                            runCatching {
                                api.reemplazarRolUsuario(ReemplazarRolRequest(nuevo.id, rolId))
                            }
                        }
                        runCatching { api.cambiarEstadoUsuario(nuevo.id, s.estadoSeleccionado) }
                        _uiState.update {
                            it.copy(guardando = false, dialogoAbierto = false, mensaje = "Usuario registrado")
                        }
                        cargar()
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                guardando = false,
                                error = ApiErrorParser.mensaje(e)
                            )
                        }
                    }
            }
        }
    }

    fun toggleEstado(fila: UsuarioFila) {
        val nuevo = if (fila.estado == "Activo") "Inactivo" else "Activo"
        viewModelScope.launch {
            runCatching { api.cambiarEstadoUsuario(fila.id, nuevo) }
                .onSuccess {
                    _uiState.update { st ->
                        st.copy(
                            usuarios = st.usuarios.map {
                                if (it.id == fila.id) it.copy(estado = nuevo) else it
                            },
                            mensaje = "Estado actualizado"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = ApiErrorParser.mensaje(e)) }
                }
        }
    }

    fun pedirConfirmacionEliminar(fila: UsuarioFila) = _uiState.update { it.copy(eliminando = fila) }

    fun cancelarEliminar() = _uiState.update { it.copy(eliminando = null) }

    fun confirmarEliminar() {
        val fila = _uiState.value.eliminando ?: return
        _uiState.update { it.copy(eliminando = null, guardando = true) }
        viewModelScope.launch {
            runCatching { api.eliminarUsuario(fila.id) }
                .onSuccess {
                    _uiState.update { it.copy(guardando = false, mensaje = "Usuario eliminado") }
                    cargar()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(guardando = false, error = ApiErrorParser.mensaje(e))
                    }
                    cargar()
                }
        }
    }

    fun consumirMensaje() = _uiState.update { it.copy(mensaje = null) }
}
