package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Evento(
    val id: Long = 0,
    val titulo: String = "",
    @SerialName("tipo_evento") val tipoEvento: String? = null,
    @SerialName("subtipo_evento") val subtipoEvento: String? = null,
    val descripcion: String? = null,
    @SerialName("fecha_inicio") val fechaInicio: String? = null,
    @SerialName("fecha_fin") val fechaFin: String? = null,
    @SerialName("hora_inicio") val horaInicio: String? = null,
    @SerialName("hora_fin") val horaFin: String? = null,
    val lugar: String? = null,
    @SerialName("responsable_id") val responsableId: Long? = null,
    @SerialName("cupo_maximo") val cupoMaximo: Int? = null,
    @SerialName("requiere_inscripcion") val requiereInscripcion: Boolean = false,
    @SerialName("requiere_pago") val requierePago: Boolean = false,
    @SerialName("monto_sugerido") val montoSugerido: Double? = null,
    val estado: String = "borrador",
    val inscritos: Int = 0,
    val inscrito: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
