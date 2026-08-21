package com.ecclesia.android.ui.screens.sacramentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.domain.models.Sacramento
import com.ecclesia.android.domain.models.SacramentoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SacramentosViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(SacramentosUiState())
    val uiState: StateFlow<SacramentosUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val permisos = runCatching { api.misPermisos().permisos }.getOrElse { emptyList() }
            runCatching { api.sacramentos() }
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sacramentos = lista,
                            puedeEditar = permisos.any { p -> p == "sacramentos.crear" || p == "sacramentos.editar" },
                            puedeEliminar = permisos.any { p -> p == "sacramentos.eliminar" }
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(e)) }
                }
        }
    }

    fun onBusquedaChange(valor: String) = _uiState.update { it.copy(busqueda = valor) }

    fun abrirDialogoNuevo() = _uiState.update {
        it.copy(
            dialogoAbierto = true, editandoId = null, nombre = "",
            descripcion = "", activo = true, generaCertificado = false, error = null
        )
    }

    fun abrirDialogoEditar(s: Sacramento) = _uiState.update {
        it.copy(
            dialogoAbierto = true, editandoId = s.id, nombre = s.nombre,
            descripcion = s.descripcion ?: "", activo = s.activo,
            generaCertificado = s.generaCertificado, error = null
        )
    }

    fun cerrarDialogo() = _uiState.update { it.copy(dialogoAbierto = false) }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v) }
    fun onActivoChange(v: Boolean) = _uiState.update { it.copy(activo = v) }
    fun onGeneraCertificadoChange(v: Boolean) = _uiState.update { it.copy(generaCertificado = v) }

    fun guardar() {
        val s = _uiState.value
        if (s.nombre.isBlank()) return
        _uiState.update { it.copy(guardando = true, error = null) }
        viewModelScope.launch {
            val body = SacramentoRequest(
                nombre = s.nombre.trim(),
                descripcion = s.descripcion.trim().ifEmpty { null },
                activo = s.activo,
                generaCertificado = s.generaCertificado
            )
            val resultado = if (s.editandoId != null) {
                runCatching { api.actualizarSacramento(s.editandoId!!, body) }
                    .map { "Sacramento actualizado" }
            } else {
                runCatching { api.crearSacramento(body) }.map { "Sacramento registrado" }
            }
            resultado
                .onSuccess { msg ->
                    _uiState.update { it.copy(guardando = false, dialogoAbierto = false, mensaje = msg) }
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

    fun pedirConfirmacionEliminar(s: Sacramento) = _uiState.update { it.copy(eliminando = s) }

    fun cancelarEliminar() = _uiState.update { it.copy(eliminando = null) }

    fun confirmarEliminar() {
        val s = _uiState.value.eliminando ?: return
        _uiState.update { it.copy(eliminando = null, guardando = true) }
        viewModelScope.launch {
            runCatching { api.eliminarSacramento(s.id) }
                .onSuccess {
                    _uiState.update { it.copy(guardando = false, mensaje = "Sacramento desactivado") }
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

    fun consumirMensaje() = _uiState.update { it.copy(mensaje = null) }
}

