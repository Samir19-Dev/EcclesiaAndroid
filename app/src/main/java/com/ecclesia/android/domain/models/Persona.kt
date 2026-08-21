package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Persona(
    val id: Long = 0,
    @SerialName("primer_nombre") val primerNombre: String = "",
    @SerialName("segundo_nombre") val segundoNombre: String? = null,
    @SerialName("primer_apellido") val primerApellido: String = "",
    @SerialName("segundo_apellido") val segundoApellido: String? = null,
    @SerialName("fecha_nacimiento") val fechaNacimiento: String? = null,
    val sexo: String? = null,
    @SerialName("lugar_nacimiento") val lugarNacimiento: String? = null,
    val region: String? = null,
    val departamento: String? = null,
    val municipio: String? = null,
    @SerialName("tipo_documento") val tipoDocumento: String = "",
    @SerialName("numero_documento") val numeroDocumento: String? = null,
    @SerialName("estado_civil") val estadoCivil: String? = null,
    @SerialName("tiene_usuario") val tieneUsuario: Boolean = false,
    @SerialName("foto_url") val fotoUrl: String? = null,
    @SerialName("foto_public_id") val fotoPublicId: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val sacramentos: Int? = null
) {
    val nombreCompleto: String
        get() = listOf(primerNombre, segundoNombre, primerApellido, segundoApellido)
            .filterNotNull().joinToString(" ").trim()
}
