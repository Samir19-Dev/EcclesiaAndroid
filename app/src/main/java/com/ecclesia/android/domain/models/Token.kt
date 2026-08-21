package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val correo: String,
    val contrasena: String
)

@Serializable
data class RegisterRequest(
    val correo: String,
    val contrasena: String
)

@Serializable
data class LogoutRequest(
    @SerialName("cerrar_todas") val cerrarTodas: Boolean = false
)

@Serializable
data class Token(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("token_type") val tokenType: String = "bearer",
    @SerialName("expires_in") val expiresIn: Int = 1800,
    @SerialName("session_token") val sessionToken: String = "",
    @SerialName("usuario_id") val usuarioId: Int? = null,
    @SerialName("perfil_completo") val perfilCompleto: Boolean = false,
    @SerialName("correo_validado") val correoValidado: Boolean = true,
    val estado: String? = null,
    @SerialName("persona_id") val personaId: Int? = null,
    val roles: List<String> = emptyList()
)
