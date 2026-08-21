package com.ecclesia.android.ui.screens.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.EventoRepository
import com.ecclesia.android.domain.models.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventosViewModel(
    private val repository: EventoRepository = EventoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventosUiState())
    val uiState: StateFlow<EventosUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val lista: List<Evento> = repository.listar()
                _uiState.update { it.copy(isLoading = false, eventos = lista) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
