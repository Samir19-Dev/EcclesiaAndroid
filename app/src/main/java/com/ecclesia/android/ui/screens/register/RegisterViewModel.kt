package com.ecclesia.android.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, error = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, error = null) }
    }

    fun onConfirmarChange(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, error = null) }
    }

    fun onRegistrarClick(onSuccess: () -> Unit) {
        val s = _uiState.value
        when {
            s.correo.isBlank() || s.contrasena.isBlank() -> {
                _uiState.update { it.copy(error = "Completa todos los campos") }
                return
            }
            s.contrasena != s.confirmarContrasena -> {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
                return
            }
            s.contrasena.length < 6 -> {
                _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                authRepository.register(s.correo.trim(), s.contrasena)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
