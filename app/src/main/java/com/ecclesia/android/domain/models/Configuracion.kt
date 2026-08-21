package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfiguracionParroquial(
    val id: Long = 0,
    @SerialName("nombre_parroquia") val nombreParroquia: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    @SerialName("email_parroquia") val emailParroquia: String? = null,
    @SerialName("parroco_actual") val parrocoActual: String? = null,
    @SerialName("plantilla_activa") val plantillaActiva: String? = null,
    @SerialName("incluir_qr") val incluirQr: Boolean = true,
    @SerialName("incluir_sello") val incluirSello: Boolean = true,
    @SerialName("dias_retencion_docs") val diasRetencionDocs: Int? = null,
    @SerialName("notif_email") val notifEmail: Boolean = true,
    @SerialName("notif_telegram") val notifTelegram: Boolean = false,
    @SerialName("telegram_bot_token") val telegramBotToken: String? = null
)

@Serializable
data class ConfiguracionUpdateRequest(
    @SerialName("nombre_parroquia") val nombreParroquia: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    @SerialName("email_parroquia") val emailParroquia: String? = null,
    @SerialName("parroco_actual") val parrocoActual: String? = null,
    @SerialName("plantilla_activa") val plantillaActiva: String? = null,
    @SerialName("incluir_qr") val incluirQr: Boolean? = null,
    @SerialName("incluir_sello") val incluirSello: Boolean? = null,
    @SerialName("dias_retencion_docs") val diasRetencionDocs: Int? = null,
    @SerialName("notif_email") val notifEmail: Boolean? = null,
    @SerialName("notif_telegram") val notifTelegram: Boolean? = null,
    @SerialName("telegram_bot_token") val telegramBotToken: String? = null
)
