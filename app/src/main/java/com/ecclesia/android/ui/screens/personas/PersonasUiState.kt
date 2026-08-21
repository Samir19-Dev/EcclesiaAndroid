package com.ecclesia.android.ui.screens.personas

import com.ecclesia.android.domain.models.Persona

data class PersonasUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val busqueda: String = "",
    val personas: List<Persona> = emptyList(),
    val pagina: Int = 1,
    val guardando: Boolean = false,
    val errorGuardado: String? = null
)
