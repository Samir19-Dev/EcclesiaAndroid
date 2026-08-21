package com.ecclesia.android.ui.screens.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState

    fun onTokenChange(valor: String) {
        _uiState.update { it.copy(token = valor, error = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, error = null) }
    }

    fun onConfirmarChange(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, error = null) }
    }

    fun restablecer(onExito: () -> Unit) {
        val s = _uiState.value
        when {
            s.token.isBlank() -> {
                _uiState.update { it.copy(error = "Ingresa el token de recuperación") }
                return
            }
            s.contrasena.isBlank() || s.confirmarContrasena.isBlank() -> {
                _uiState.update { it.copy(error = "Completa todos los campos") }
                return
            }
            s.contrasena != s.confirmarContrasena -> {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
                return
            }
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                authRepository.resetPassword(s.token.trim(), s.contrasena, s.confirmarContrasena)
                _uiState.update { it.copy(isLoading = false, exito = true) }
                onExito()
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
