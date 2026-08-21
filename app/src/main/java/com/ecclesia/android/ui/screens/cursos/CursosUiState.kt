package com.ecclesia.android.ui.screens.cursos

import com.ecclesia.android.domain.models.Curso

data class CursosUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val cursos: List<Curso> = emptyList()
)
