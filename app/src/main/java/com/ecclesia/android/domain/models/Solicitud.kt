package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolicitudSacramento(
    val id: Long = 0,
    @SerialName("sacramento_id") val sacramentoId: Long = 0,
    @SerialName("sacramento_nombre") val sacramentoNombre: String? = null,
    @SerialName("usuario_correo") val usuarioCorreo: String? = null,
    @SerialName("persona_nombre") val personaNombre: String? = null,
    val estado: String = "pendiente",
    @SerialName("requiere_validacion_manual") val requiereValidacionManual: Boolean = true,
    @SerialName("fecha_preferida") val fechaPreferida: String? = null,
    val motivo: String? = null,
    @SerialName("observaciones_secretario") val observacionesSecretario: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PaginatedSolicitudes(
    val total: Int = 0,
    val pagina: Int = 1,
    @SerialName("por_pagina") val porPagina: Int = 10,
    val items: List<SolicitudSacramento> = emptyList()
)

@Serializable
data class CambiarEstadoRequest(
    val estado: String,
    @SerialName("observaciones_secretario") val observacionesSecretario: String? = null
)

@Serializable
data class Sacramento(
    val id: Long = 0,
    val nombre: String = "",
    @SerialName("requiere_pago") val requierePago: Boolean = false,
    @SerialName("monto_sugerido") val montoSugerido: Double? = null,
    @SerialName("nivel_riesgo") val nivelRiesgo: String? = null,
    @SerialName("genera_certificado") val generaCertificado: Boolean = false,
    val activo: Boolean = true,
    val descripcion: String? = null,
    val requisitos: Int = 0
)
