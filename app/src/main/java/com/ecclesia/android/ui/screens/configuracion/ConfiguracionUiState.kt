package com.ecclesia.android.ui.screens.configuracion

data class ConfiguracionUiState(
    val isLoading: Boolean = true,
    val guardando: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null,
    val tabActual: Int = 0,
    val nombreParroquia: String = "",
    val parrocoActual: String = "",
    val telefono: String = "",
    val emailParroquia: String = "",
    val direccion: String = "",
    val plantillaActiva: String = "clasica",
    val incluirQr: Boolean = true,
    val incluirSello: Boolean = true,
    val diasRetencionDocs: String = "1825",
    val notifEmail: Boolean = true,
    val notifTelegram: Boolean = false,
    val telegramBotToken: String = ""
)
