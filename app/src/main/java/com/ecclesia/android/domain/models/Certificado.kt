package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Certificado(
    val id: Long = 0,
    val codigo: String? = null,
    @SerialName("codigo_verificacion") val codigoVerificacion: String? = null,
    @SerialName("hash_verificacion") val hashVerificacion: String? = null,
    @SerialName("qr_data") val qrData: String? = null,
    val tipo: String? = null,
    val sacramento: String? = null,
    @SerialName("persona_nombre") val personaNombre: String? = null,
    val solicitante: String? = null,
    @SerialName("registro_sacramental_id") val registroSacramentalId: Long? = null,
    @SerialName("archivo_id") val archivoId: Long? = null,
    val estado: String = "emitido",
    @SerialName("fecha_emision") val fechaEmision: String? = null,
    @SerialName("usuario_emisor_id") val usuarioEmisorId: Long? = null,
    @SerialName("solicitante_id") val solicitanteId: Long? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class RegistroSacramental(
    val id: Long = 0,
    @SerialName("sacramento_id") val sacramentoId: Long? = null,
    val sacramento: String? = null,
    @SerialName("solicitud_sacramento_id") val solicitudSacramentoId: Long? = null,
    @SerialName("fecha_realizacion") val fechaRealizacion: String? = null,
    val lugar: String? = null,
    val estado: String = "realizado",
    @SerialName("sacerdote_nombre") val sacerdoteNombre: String? = null,
    @SerialName("numero_acta") val numeroActa: String? = null,
    @SerialName("libro_fisico") val libroFisico: String? = null,
    @SerialName("pagina_libro") val paginaLibro: String? = null,
    val titular: String? = null,
    @SerialName("tiene_certificado") val tieneCertificado: Boolean = false
)

@Serializable
data class CertificadoCreateRequest(
    @SerialName("registro_sacramental_id") val registroSacramentalId: Long
)