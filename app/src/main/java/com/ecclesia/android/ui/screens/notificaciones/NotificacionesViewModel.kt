package com.ecclesia.android.ui.screens.notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.repository.NotificacionesRepository
import com.ecclesia.android.domain.models.Notificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificacionesViewModel(
    private val repository: NotificacionesRepository = NotificacionesRepository.instance
) : ViewModel() {

    private val api = ApiClient.api

    private val _uiState = MutableStateFlow(NotificacionesUiState())
    val uiState: StateFlow<NotificacionesUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.preferencias.collect { prefs ->
                _uiState.update { it.copy(cargando = false, preferencias = prefs) }
            }
        }
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(error = null) }
        viewModelScope.launch {
            runCatching { api.notificaciones() }
                .onSuccess { lista ->
                    _uiState.update { it.copy(notificaciones = lista) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = "No se pudieron cargar las notificaciones") }
                }
        }
    }

    fun onFiltroChange(filtro: String) = _uiState.update { it.copy(filtro = filtro) }

    fun marcarLeida(notificacion: Notificacion) {
        if (notificacion.leida) return
        viewModelScope.launch {
            runCatching { api.marcarNotificacionLeida(notificacion.id) }
                .onSuccess {
                    _uiState.update { st ->
                        st.copy(
                            notificaciones = st.notificaciones.map {
                                if (it.id == notificacion.id) it.copy(leida = true) else it
                            }
                        )
                    }
                }
        }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            runCatching { api.marcarTodasNotificacionesLeidas() }
                .onSuccess {
                    _uiState.update { st ->
                        st.copy(notificaciones = st.notificaciones.map { it.copy(leida = true) })
                    }
                }
        }
    }

    fun cambiar(clave: String, valor: Boolean) {
        _uiState.update {
            it.copy(preferencias = it.preferencias + (clave to valor))
        }
        viewModelScope.launch {
            runCatching { repository.guardar(clave, valor) }
        }
    }
}
