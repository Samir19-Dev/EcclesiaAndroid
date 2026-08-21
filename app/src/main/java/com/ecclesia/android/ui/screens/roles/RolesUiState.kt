package com.ecclesia.android.ui.screens.roles

import com.ecclesia.android.domain.models.Permiso
import com.ecclesia.android.domain.models.Rol

data class RolesUiState(
    val isLoading: Boolean = true,
    val guardando: Boolean = false,
    val cargandoPermisos: Boolean = false,
    val roles: List<Rol> = emptyList(),
    val permisosDisponibles: List<Permiso> = emptyList(),
    val permisosSeleccionados: Set<Long> = emptySet(),
    val error: String? = null,
    val dialogoAbierto: Boolean = false,
    val editandoId: Long? = null,
    val nombre: String = "",
    val descripcion: String = ""
)
