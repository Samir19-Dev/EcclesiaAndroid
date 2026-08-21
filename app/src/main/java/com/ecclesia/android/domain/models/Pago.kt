package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pago(
    val id: Long = 0,
    val referencia: String = "",
    val concepto: String? = null,
    val fiel: String? = null,
    @SerialName("fiel_nombre") val fielNombre: String? = null,
    val monto: Double = 0.0,
    val moneda: String = "COP",
    @SerialName("fecha_pago") val fechaPago: String? = null,
    val metodo: String? = null,
    val estado: String = "completado",
    @SerialName("solicitud_sacramento_id") val solicitudSacramentoId: Long? = null,
    @SerialName("evento_id") val eventoId: Long? = null,
    @SerialName("curso_id") val cursoId: Long? = null,
    @SerialName("registrado_por") val registradoPor: Long? = null,
    val observaciones: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
