package com.ecclesia.android.ui.screens.login

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
