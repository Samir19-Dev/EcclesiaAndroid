package com.ecclesia.android.ui.screens.pagos

import com.ecclesia.android.domain.models.Pago

data class PagosUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val pagos: List<Pago> = emptyList()
)
