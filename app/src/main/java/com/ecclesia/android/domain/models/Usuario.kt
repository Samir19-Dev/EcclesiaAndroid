package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RolSimple(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String? = null
)

@Serializable
data class Usuario(
    val id: Long = 0,
    val correo: String = "",
    @SerialName("correo_validado") val correoValidado: Boolean = false,
    @SerialName("perfil_completo") val perfilCompleto: Boolean = false,
    val estado: String = "",
    @SerialName("eliminado_at") val eliminadoAt: String? = null,
    val persona: Persona? = null,
    val roles: List<RolSimple> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class MensajeResponse(
    val mensaje: String = ""
)
