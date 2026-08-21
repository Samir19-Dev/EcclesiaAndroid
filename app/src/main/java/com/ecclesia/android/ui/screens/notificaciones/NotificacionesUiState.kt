package com.ecclesia.android.ui.screens.notificaciones

import com.ecclesia.android.domain.models.Notificacion

data class NotificacionesUiState(
    val cargando: Boolean = true,
    val notificaciones: List<Notificacion> = emptyList(),
    val filtro: String = "todas",
    val preferencias: Map<String, Boolean> = emptyMap(),
    val error: String? = null
) {
    val filtradas: List<Notificacion>
        get() = if (filtro == "noleidas") notificaciones.filter { !it.leida } else notificaciones

    val noLeidas: Int get() = notificaciones.count { !it.leida }
}
