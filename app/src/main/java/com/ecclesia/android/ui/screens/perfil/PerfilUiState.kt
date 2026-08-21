package com.ecclesia.android.ui.screens.perfil

import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.SesionInfo
import com.ecclesia.android.domain.models.Usuario

data class PerfilUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val mensajeExito: String? = null,
    val usuario: Usuario? = null,
    val perfil: Persona? = null,
    val tienePerfil: Boolean = false,

    val primerNombre: String = "",
    val segundoNombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val tipoDocumento: String = "CC",
    val numeroDocumento: String = "",
    val fechaNacimiento: String = "",
    val sexo: String = "",
    val estadoCivil: String = "soltero",
    val municipio: String = "",
    val departamento: String = "",
    val region: String = "",
    val lugarNacimiento: String = "",

    val emailActual: String = "",
    val emailContrasenaActual: String = "",
    val nuevoCorreo: String = "",
    val passContrasenaActual: String = "",
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",

    val guardando: Boolean = false,
    val cambiandoEmail: Boolean = false,
    val cambiandoPass: Boolean = false,
    val subiendoFoto: Boolean = false,

    val sesiones: List<SesionInfo> = emptyList(),
    val cargandoSesiones: Boolean = false,

    val tabActual: Int = 0
)
