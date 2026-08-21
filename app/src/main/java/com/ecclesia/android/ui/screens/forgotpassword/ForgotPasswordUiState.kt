package com.ecclesia.android.ui.screens.forgotpassword

data class ForgotPasswordUiState(
    val correo: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val enviado: Boolean = false
)
