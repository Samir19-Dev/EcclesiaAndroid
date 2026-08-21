package com.ecclesia.android.ui.screens.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.domain.models.AsignarPermisosRequest
import com.ecclesia.android.domain.models.RolRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RolesViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(RolesUiState())
    val uiState: StateFlow<RolesUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching { api.permisos() }
                .onSuccess { lista ->
                    _uiState.update { it.copy(permisosDisponibles = lista) }
                }
            runCatching { api.roles() }
                .onSuccess { lista ->
                    _uiState.update { it.copy(isLoading = false, roles = lista) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(e)) }
                }
        }
    }

    fun abrirDialogoNuevo() = _uiState.update {
        it.copy(
            dialogoAbierto = true, editandoId = null, nombre = "",
            descripcion = "", permisosSeleccionados = emptySet(), error = null
        )
    }

    fun abrirDialogoEditar(rolId: Long, nombre: String, descripcion: String) {
        _uiState.update {
            it.copy(
                dialogoAbierto = true, editandoId = rolId, nombre = nombre,
                descripcion = descripcion, permisosSeleccionados = emptySet(),
                cargandoPermisos = true, error = null
            )
        }
        viewModelScope.launch {
            runCatching { api.permisosDeRol(rolId) }
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            cargandoPermisos = false,
                            permisosSeleccionados = lista.map { p -> p.id }.toSet()
                        )
                    }
                }
                .onFailure {
                    _uiState.update { st -> st.copy(cargandoPermisos = false) }
                }
        }
    }

    fun cerrarDialogo() = _uiState.update {
        it.copy(dialogoAbierto = false, permisosSeleccionados = emptySet())
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v) }

    fun togglePermiso(id: Long) = _uiState.update {
        val nuevos = if (id in it.permisosSeleccionados) {
            it.permisosSeleccionados - id
        } else {
            it.permisosSeleccionados + id
        }
        it.copy(permisosSeleccionados = nuevos)
    }

    fun guardar() {
        val s = _uiState.value
        if (s.nombre.trim().length < 3) return

        _uiState.update { it.copy(guardando = true, error = null) }
        viewModelScope.launch {
            val body = RolRequest(nombre = s.nombre.trim(), descripcion = s.descripcion.trim())
            val editandoId = s.editandoId

            val resultado = if (editandoId != null) {
                runCatching { api.actualizarRol(editandoId, body) }.map { editandoId }
            } else {
                runCatching { api.crearRol(body) }.map { it.id }
            }

            resultado
                .onSuccess { rolId ->
                    if (s.permisosSeleccionados.isNotEmpty()) {
                        runCatching {
                            api.asignarPermisosARol(rolId, AsignarPermisosRequest(s.permisosSeleccionados.toList()))
                        }
                    }
                    _uiState.update {
                        it.copy(guardando = false, dialogoAbierto = false)
                    }
                    cargar()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(guardando = false, error = ApiErrorParser.mensaje(e))
                    }
                }
        }
    }
}
