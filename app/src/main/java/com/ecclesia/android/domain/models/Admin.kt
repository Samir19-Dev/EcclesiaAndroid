package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rol(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    @SerialName("es_sistema") val esSistema: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class RolRequest(
    val nombre: String,
    val descripcion: String = ""
)

@Serializable
data class Permiso(
    val id: Long = 0,
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String? = null,
    @SerialName("modulo_id") val moduloId: Long? = null,
    val activo: Boolean = true
)

@Serializable
data class ReemplazarRolRequest(
    @SerialName("usuario_id") val usuarioId: Long,
    @SerialName("rol_id") val rolId: Long
)

@Serializable
data class AsignarPermisosRequest(
    val permisos: List<Long>
)

@Serializable
data class SacramentoRequest(
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean = true,
    @SerialName("genera_certificado") val generaCertificado: Boolean = false
)
