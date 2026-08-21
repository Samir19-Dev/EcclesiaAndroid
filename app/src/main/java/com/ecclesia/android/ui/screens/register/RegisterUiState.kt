package com.ecclesia.android.ui.screens.register

data class RegisterUiState(
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
