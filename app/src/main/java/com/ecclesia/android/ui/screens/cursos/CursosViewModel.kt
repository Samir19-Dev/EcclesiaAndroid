package com.ecclesia.android.ui.screens.cursos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.CursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CursosViewModel(
    private val repository: CursoRepository = CursoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CursosUiState())
    val uiState: StateFlow<CursosUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val lista = repository.listar()
                _uiState.update { it.copy(isLoading = false, cursos = lista) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
