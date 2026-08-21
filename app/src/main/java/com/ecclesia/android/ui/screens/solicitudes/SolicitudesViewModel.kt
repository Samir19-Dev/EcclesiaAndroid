package com.ecclesia.android.ui.screens.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.network.SessionManager
import com.ecclesia.android.data.repository.SolicitudRepository
import com.ecclesia.android.domain.models.Sacramento
import com.ecclesia.android.domain.models.SolicitudSacramento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolicitudesViewModel(
    private val repository: SolicitudRepository = SolicitudRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudesUiState())
    val uiState: StateFlow<SolicitudesUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val esAdmin = SessionManager.instance.tienePermiso("solicitudes.ver_todas")
                val items = if (esAdmin) {
                    repository.todasLasSolicitudes().items
                } else {
                    repository.misSolicitudes().items
                }
                _uiState.update { it.copy(isLoading = false, solicitudes = items, esAdmin = esAdmin) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun cargarSacramentos(onResult: (Result<List<Sacramento>>) -> Unit) {
        viewModelScope.launch {
            runCatching { repository.sacramentos() }.let(onResult)
        }
    }

    fun cambiarEstado(s: SolicitudSacramento, nuevoEstado: String) {
        val st = _uiState.value
        if (!st.puedeTransicionar(s, nuevoEstado)) return

        if (nuevoEstado == "rechazada" || nuevoEstado == "documentacion_incompleta") {
            _uiState.update { it.copy(rechazando = s, motivoRechazo = "") }
            return
        }
        ejecutarCambio(s, nuevoEstado, null)
    }

    fun onMotivoRechazoChange(v: String) = _uiState.update { it.copy(motivoRechazo = v) }

    fun cancelarRechazo() = _uiState.update { it.copy(rechazando = null, motivoRechazo = "") }

    fun confirmarRechazo() {
        val st = _uiState.value
        val s = st.rechazando ?: return
        if (st.motivoRechazo.isBlank()) return
        _uiState.update { it.copy(rechazando = null) }
        ejecutarCambio(s, "rechazada", st.motivoRechazo.trim())
    }

    private fun ejecutarCambio(s: SolicitudSacramento, nuevoEstado: String, observaciones: String?) {
        _uiState.update { it.copy(guardandoId = s.id, error = null) }
        viewModelScope.launch {
            runCatching { repository.cambiarEstado(s.id, nuevoEstado, observaciones) }
                .onSuccess {
                    _uiState.update { it.copy(guardandoId = null) }
                    cargar()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(guardandoId = null, error = ApiErrorParser.mensaje(e))
                    }
                }
        }
    }
}
