package com.ecclesia.android.ui.screens.verifyemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    correoInicial: String = "",
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyEmailUiState(correo = correoInicial))
    val uiState: StateFlow<VerifyEmailUiState> = _uiState

    fun onTokenChange(valor: String) {
        _uiState.update { it.copy(token = valor, error = null) }
    }

    fun setCorreo(correo: String) {
        if (correo.isNotBlank() && _uiState.value.correo.isBlank()) {
            _uiState.update { it.copy(correo = correo) }
        }
    }

    fun reenviar(onExito: (String) -> Unit) {
        val correo = _uiState.value.correo
        if (correo.isBlank()) {
            _uiState.update { it.copy(error = "Falta el correo") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null, mensaje = null) }
        viewModelScope.launch {
            try {
                val r = authRepository.reenviarValidacion(correo)
                _uiState.update { it.copy(isLoading = false, mensaje = r.mensaje) }
                onExito(r.mensaje)
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun validar(onExito: () -> Unit) {
        val token = _uiState.value.token.trim()
        if (token.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa el token de validación") }
            return
        }
        _uiState.update { it.copy(validando = true, error = null, mensaje = null) }
        viewModelScope.launch {
            try {
                val r = authRepository.validarEmail(token)
                _uiState.update { it.copy(validando = false, validado = true, mensaje = r.mensaje) }
                onExito()
            } catch (t: Throwable) {
                _uiState.update { it.copy(validando = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
