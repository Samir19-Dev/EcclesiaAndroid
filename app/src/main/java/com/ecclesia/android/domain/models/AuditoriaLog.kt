package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AuditoriaLog(
    val id: Long = 0,
    val usuario: String = "Sistema",
    @SerialName("usuario_id") val usuarioId: Long? = null,
    val accion: String = "",
    val modulo: String = "",
    val entidad: String? = null,
    @SerialName("entidad_id") val entidadId: Long? = null,
    val detalle: String = "",
    val descripcion: String? = null,
    val ip: String = "",
    @SerialName("ip_address") val ipAddress: String? = null,
    @SerialName("user_agent") val userAgent: String? = null,
    val fecha: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("datos_anteriores") val datosAnteriores: Map<String, JsonElement> = emptyMap(),
    @SerialName("datos_nuevos") val datosNuevos: Map<String, JsonElement> = emptyMap()
)
