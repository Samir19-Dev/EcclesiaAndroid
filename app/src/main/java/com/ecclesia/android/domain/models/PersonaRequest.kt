package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PersonaRequest(
    @SerialName("primer_nombre") val primerNombre: String? = null,
    @SerialName("segundo_nombre") val segundoNombre: String? = null,
    @SerialName("primer_apellido") val primerApellido: String? = null,
    @SerialName("segundo_apellido") val segundoApellido: String? = null,
    @SerialName("tipo_documento") val tipoDocumento: String? = null,
    @SerialName("numero_documento") val numeroDocumento: String? = null,
    @SerialName("fecha_nacimiento") val fechaNacimiento: String? = null,
    val sexo: String? = null,
    @SerialName("lugar_nacimiento") val lugarNacimiento: String? = null,
    @SerialName("estado_civil") val estadoCivil: String? = null,
    val region: String? = null,
    val departamento: String? = null,
    val municipio: String? = null
)

@Serializable
data class SacramentoRegistrado(
    val id: Long = 0,
    @SerialName("sacramento_id") val sacramentoId: Long? = null,
    val sacramento: String = "",
    val fecha: String? = null,
    val parroquia: String? = null,
    @SerialName("libro_fisico") val libroFisico: String? = null,
    @SerialName("pagina_libro") val paginaLibro: String? = null,
    @SerialName("numero_acta") val numeroActa: String? = null,
    @SerialName("tiene_certificado") val tieneCertificado: Boolean = false
)
