package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class CorreoRequest(
    val correo: String
)

@Serializable
data class ValidarEmailRequest(
    val token: String
)

@Serializable
data class RestablecerContrasenaRequest(
    val token: String,
    @SerialName("contrasena_nueva") val contrasenaNueva: String,
    @SerialName("confirmar_contrasena") val confirmarContrasena: String
)

@Serializable
data class CambiarEmailRequest(
    @SerialName("contrasena_actual") val contrasenaActual: String,
    @SerialName("nuevo_correo") val nuevoCorreo: String
)

@Serializable
data class CambiarContrasenaRequest(
    @SerialName("contrasena_actual") val contrasenaActual: String,
    @SerialName("contrasena_nueva") val contrasenaNueva: String,
    @SerialName("confirmar_contrasena") val confirmarContrasena: String
)

@Serializable
data class PerfilRequest(
    @SerialName("primer_nombre") val primerNombre: String,
    @SerialName("segundo_nombre") val segundoNombre: String? = null,
    @SerialName("primer_apellido") val primerApellido: String,
    @SerialName("segundo_apellido") val segundoApellido: String? = null,
    @SerialName("fecha_nacimiento") val fechaNacimiento: String? = null,
    val sexo: String? = null,
    @SerialName("lugar_nacimiento") val lugarNacimiento: String? = null,
    val region: String? = null,
    val departamento: String? = null,
    val municipio: String? = null,
    @SerialName("tipo_documento") val tipoDocumento: String = "CC",
    @SerialName("numero_documento") val numeroDocumento: String? = null,
    @SerialName("estado_civil") val estadoCivil: String = "soltero"
)

@Serializable
data class SesionInfo(
    val id: Long = 0,
    @SerialName("session_token") val sessionToken: String = "",
    @SerialName("ip_address") val ipAddress: String? = null,
    @SerialName("user_agent") val userAgent: String? = null,
    val dispositivo: String? = null,
    @SerialName("fecha_creacion") val fechaCreacion: String? = null,
    @SerialName("fecha_ultimo_uso") val fechaUltimoUso: String? = null,
    @SerialName("fecha_expiracion") val fechaExpiracion: String? = null,
    val activa: Boolean = true
)

@Serializable
data class FotoPerfilResponse(
    val ok: Boolean = false,
    @SerialName("foto_url") val fotoUrl: String? = null,
    val mensaje: String = ""
)

@Serializable
data class VerificarPerfilResponse(
    @SerialName("perfil_completo") val perfilCompleto: Boolean = false,
    @SerialName("tiene_perfil") val tienePerfil: Boolean = false,
    @SerialName("correo_validado") val correoValidado: Boolean = false,
    val mensaje: String = ""
)
