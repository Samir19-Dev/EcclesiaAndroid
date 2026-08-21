package com.ecclesia.android.ui.screens.personas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.PersonaRepository
import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.PersonaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonasViewModel(
    private val repository: PersonaRepository = PersonaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonasUiState())
    val uiState: StateFlow<PersonasUiState> = _uiState

    init {
        cargar()
    }

    fun onBuscarChange(valor: String) {
        _uiState.update { it.copy(busqueda = valor, pagina = 1) }
    }

    fun cambiarPagina(pagina: Int) {
        _uiState.update { it.copy(pagina = pagina) }
    }

    fun descartarErrorGuardado() {
        _uiState.update { it.copy(errorGuardado = null) }
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val lista: List<Persona> = repository.listar()
                _uiState.update { it.copy(isLoading = false, personas = lista) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun guardar(editarId: Long?, request: PersonaRequest, onExito: () -> Unit) {
        _uiState.update { it.copy(guardando = true, errorGuardado = null) }
        viewModelScope.launch {
            try {
                if (editarId != null) {
                    repository.actualizar(editarId, request)
                } else {
                    repository.crear(request)
                }
                _uiState.update { it.copy(guardando = false) }
                cargar()
                onExito()
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(guardando = false, errorGuardado = ApiErrorParser.mensaje(t))
                }
            }
        }
    }
}
