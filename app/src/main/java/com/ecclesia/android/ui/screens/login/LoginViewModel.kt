package com.ecclesia.android.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuthRepository
import com.ecclesia.android.domain.models.Token
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, error = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, error = null) }
    }

    fun onLoginClick(onSuccess: (Token) -> Unit) {
        val state = _uiState.value
        if (state.correo.isBlank() || state.contrasena.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu correo y contraseña") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val token = authRepository.login(state.correo.trim(), state.contrasena)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(token)
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, error = ApiErrorParser.mensaje(t))
                }
            }
        }
    }
}
