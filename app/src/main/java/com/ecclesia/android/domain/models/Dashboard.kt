package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MisPermisosResponse(
    @SerialName("usuario_id") val usuarioId: Long = 0,
    val correo: String = "",
    val permisos: List<String> = emptyList(),
    val total: Int = 0
)

@Serializable
data class PaginadoUsuarios(
    val items: List<Usuario> = emptyList(),
    val total: Int = 0
)

@Serializable
data class Notificacion(
    val id: Long = 0,
    val titulo: String = "",
    val mensaje: String? = null,
    val tipo: String? = null,
    val leida: Boolean = false,
    @SerialName("usuario_id") val usuarioId: Long = 0,
    @SerialName("created_at") val createdAt: String? = null
)
