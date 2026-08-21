package com.ecclesia.android.ui.screens.solicitudes

import com.ecclesia.android.domain.models.SolicitudSacramento

data class SolicitudesUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val solicitudes: List<SolicitudSacramento> = emptyList(),
    val esAdmin: Boolean = false,
    val guardandoId: Long? = null,
    val rechazando: SolicitudSacramento? = null,
    val motivoRechazo: String = ""
) {
    fun transicionesPermitidas(estado: String): List<String> {
        val e = estado.lowercase()
        return when (e) {
            "pendiente" -> listOf("en_revision", "aprobada", "rechazada", "documentacion_incompleta", "cancelada")
            "en_revision" -> listOf("aprobada", "rechazada", "documentacion_incompleta", "pendiente", "cancelada")
            "documentacion_incompleta" -> listOf("en_revision", "aprobada", "rechazada", "pendiente", "cancelada")
            "aprobada" -> listOf("en_revision", "rechazada", "pendiente")
            "rechazada" -> listOf("en_revision", "aprobada", "pendiente")
            "cancelada" -> listOf("pendiente", "en_revision", "aprobada")
            else -> emptyList()
        }
    }

    fun puedeTransicionar(s: SolicitudSacramento, destino: String): Boolean =
        transicionesPermitidas(s.estado).contains(destino.lowercase())
}
