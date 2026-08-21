package com.ecclesia.android.ui.screens.personas

import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.SacramentoRegistrado

data class PersonaDetalleUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val persona: Persona? = null,
    val cargandoSacramentos: Boolean = false,
    val sacramentos: List<SacramentoRegistrado> = emptyList()
)
