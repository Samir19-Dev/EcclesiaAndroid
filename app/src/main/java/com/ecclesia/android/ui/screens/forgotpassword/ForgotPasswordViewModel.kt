package com.ecclesia.android.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, error = null) }
    }

    fun enviar(onExito: () -> Unit) {
        val correo = _uiState.value.correo.trim()
        if (correo.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu correo electrónico") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                authRepository.forgotPassword(correo)
                _uiState.update { it.copy(isLoading = false, enviado = true) }
                onExito()
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
