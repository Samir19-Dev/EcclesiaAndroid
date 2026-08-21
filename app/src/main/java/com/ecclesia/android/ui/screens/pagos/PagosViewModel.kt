package com.ecclesia.android.ui.screens.pagos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.PagoRepository
import com.ecclesia.android.domain.models.Pago
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PagosViewModel(
    private val repository: PagoRepository = PagoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PagosUiState())
    val uiState: StateFlow<PagosUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val lista: List<Pago> = repository.listar()
                _uiState.update { it.copy(isLoading = false, pagos = lista) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
