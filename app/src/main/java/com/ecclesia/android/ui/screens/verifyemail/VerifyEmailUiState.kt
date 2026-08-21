package com.ecclesia.android.ui.screens.verifyemail

data class VerifyEmailUiState(
    val correo: String = "",
    val token: String = "",
    val isLoading: Boolean = false,
    val validando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val validado: Boolean = false
)
