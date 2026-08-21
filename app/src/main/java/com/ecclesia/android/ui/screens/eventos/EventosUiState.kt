package com.ecclesia.android.ui.screens.eventos

import com.ecclesia.android.domain.models.Evento

data class EventosUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val eventos: List<Evento> = emptyList()
)
