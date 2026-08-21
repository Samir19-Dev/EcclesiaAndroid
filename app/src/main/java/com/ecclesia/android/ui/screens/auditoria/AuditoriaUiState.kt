package com.ecclesia.android.ui.screens.auditoria

import com.ecclesia.android.domain.models.AuditoriaLog

data class AuditoriaUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val busqueda: String = "",
    val logs: List<AuditoriaLog> = emptyList(),
    val pagina: Int = 1
)
