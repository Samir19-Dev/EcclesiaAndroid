package com.ecclesia.android.ui.screens.auditoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuditoriaRepository
import com.ecclesia.android.domain.models.AuditoriaLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuditoriaViewModel(
    private val repository: AuditoriaRepository = AuditoriaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuditoriaUiState())
    val uiState: StateFlow<AuditoriaUiState> = _uiState

    init {
        cargar()
    }

    fun onBuscarChange(valor: String) {
        _uiState.update { it.copy(busqueda = valor, pagina = 1) }
    }

    fun cambiarPagina(pagina: Int) {
        _uiState.update { it.copy(pagina = pagina) }
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val lista: List<AuditoriaLog> = repository.listar()
                _uiState.update { it.copy(isLoading = false, logs = lista) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
