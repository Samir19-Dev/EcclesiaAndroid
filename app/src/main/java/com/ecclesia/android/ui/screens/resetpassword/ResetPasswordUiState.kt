package com.ecclesia.android.ui.screens.resetpassword

data class ResetPasswordUiState(
    val token: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false
)
