package com.ecclesia.android.ui.screens.personas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.PersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonaDetalleViewModel(
    private val personaId: Long,
    private val repository: PersonaRepository = PersonaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonaDetalleUiState())
    val uiState: StateFlow<PersonaDetalleUiState> = _uiState

    init {
        cargarPersona()
    }

    fun cargarPersona() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val persona = repository.detalle(personaId)
                _uiState.update { it.copy(isLoading = false, persona = persona) }
                cargarSacramentos()
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    private fun cargarSacramentos() {
        _uiState.update { it.copy(cargandoSacramentos = true) }
        viewModelScope.launch {
            try {
                val sacramentos = repository.sacramentos(personaId)
                _uiState.update { it.copy(cargandoSacramentos = false, sacramentos = sacramentos) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(cargandoSacramentos = false) }
            }
        }
    }

    companion object {
        fun factory(personaId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { PersonaDetalleViewModel(personaId) }
        }
    }
}
