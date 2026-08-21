package com.ecclesia.android.ui.screens.sacramentos

import com.ecclesia.android.domain.models.Sacramento

data class SacramentosUiState(
    val isLoading: Boolean = true,
    val guardando: Boolean = false,
    val sacramentos: List<Sacramento> = emptyList(),
    val busqueda: String = "",
    val error: String? = null,
    val mensaje: String? = null,
    val puedeEditar: Boolean = false,
    val puedeEliminar: Boolean = false,
    val dialogoAbierto: Boolean = false,
    val editandoId: Long? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val activo: Boolean = true,
    val generaCertificado: Boolean = false,
    val eliminando: Sacramento? = null
) {
    val filtrados: List<Sacramento>
        get() {
            val t = busqueda.trim().lowercase()
            return if (t.isEmpty()) sacramentos
            else sacramentos.filter { it.nombre.lowercase().contains(t) }
        }
}
