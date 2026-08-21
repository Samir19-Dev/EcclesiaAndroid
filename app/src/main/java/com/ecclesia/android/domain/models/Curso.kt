package com.ecclesia.android.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Curso(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String? = null,
    @SerialName("duracion_horas") val duracionHoras: Int? = null,
    @SerialName("sacramento_relacionado_id") val sacramentoRelacionadoId: Long? = null,
    val sacramento: String? = null,
    val activo: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("mi_estado") val miEstado: String? = null
)

@Serializable
data class PersonaEnCurso(
    @SerialName("persona_id") val personaId: Long = 0,
    val nombre: String = "",
    @SerialName("tipo_documento") val tipoDocumento: String? = null,
    @SerialName("numero_documento") val numeroDocumento: String? = null,
    @SerialName("asistencia_porcentaje") val asistenciaPorcentaje: Double? = null,
    val aprobado: Boolean = false
)

@Serializable
data class Cohorte(
    val id: Long = 0,
    @SerialName("curso_id") val cursoId: Long = 0,
    @SerialName("curso_nombre") val cursoNombre: String? = null,
    @SerialName("fecha_inicio") val fechaInicio: String? = null,
    @SerialName("fecha_fin") val fechaFin: String? = null,
    val estado: String? = null,
    val observaciones: String? = null,
    val personas: List<PersonaEnCurso> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null
)
