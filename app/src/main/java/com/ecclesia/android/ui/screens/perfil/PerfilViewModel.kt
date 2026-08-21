package com.ecclesia.android.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.AuthRepository
import com.ecclesia.android.data.repository.PerfilRepository
import com.ecclesia.android.domain.models.PerfilRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class PerfilViewModel(
    private val perfilRepository: PerfilRepository = PerfilRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val usuario = authRepository.usuarioActual()
                val tienePerfil = usuario.persona != null
                val perfil = usuario.persona
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usuario = usuario,
                        perfil = perfil,
                        tienePerfil = tienePerfil,
                        emailActual = usuario.correo,
                        primerNombre = perfil?.primerNombre ?: "",
                        segundoNombre = perfil?.segundoNombre ?: "",
                        primerApellido = perfil?.primerApellido ?: "",
                        segundoApellido = perfil?.segundoApellido ?: "",
                        tipoDocumento = perfil?.tipoDocumento ?: "CC",
                        numeroDocumento = perfil?.numeroDocumento ?: "",
                        fechaNacimiento = perfil?.fechaNacimiento ?: "",
                        sexo = perfil?.sexo ?: "",
                        estadoCivil = perfil?.estadoCivil ?: "soltero",
                        municipio = perfil?.municipio ?: "",
                        departamento = perfil?.departamento ?: "",
                        region = perfil?.region ?: "",
                        lugarNacimiento = perfil?.lugarNacimiento ?: ""
                    )
                }
                cargarSesiones()
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun onTabChange(tab: Int) {
        _uiState.update { it.copy(tabActual = tab) }
    }

    fun onPrimerNombre(v: String) = _uiState.update { it.copy(primerNombre = v, error = null) }
    fun onSegundoNombre(v: String) = _uiState.update { it.copy(segundoNombre = v) }
    fun onPrimerApellido(v: String) = _uiState.update { it.copy(primerApellido = v, error = null) }
    fun onSegundoApellido(v: String) = _uiState.update { it.copy(segundoApellido = v) }
    fun onTipoDocumento(v: String) = _uiState.update { it.copy(tipoDocumento = v) }
    fun onNumeroDocumento(v: String) = _uiState.update { it.copy(numeroDocumento = v) }
    fun onFechaNacimiento(v: String) = _uiState.update { it.copy(fechaNacimiento = v) }
    fun onSexo(v: String) = _uiState.update { it.copy(sexo = v) }
    fun onEstadoCivil(v: String) = _uiState.update { it.copy(estadoCivil = v) }
    fun onMunicipio(v: String) = _uiState.update { it.copy(municipio = v) }
    fun onDepartamento(v: String) = _uiState.update { it.copy(departamento = v) }
    fun onRegion(v: String) = _uiState.update { it.copy(region = v) }
    fun onLugarNacimiento(v: String) = _uiState.update { it.copy(lugarNacimiento = v) }

    fun onEmailContrasenaActual(v: String) = _uiState.update { it.copy(emailContrasenaActual = v, mensajeExito = null) }
    fun onNuevoCorreo(v: String) = _uiState.update { it.copy(nuevoCorreo = v) }
    fun onPassContrasenaActual(v: String) = _uiState.update { it.copy(passContrasenaActual = v, mensajeExito = null) }
    fun onNuevaContrasena(v: String) = _uiState.update { it.copy(nuevaContrasena = v) }
    fun onConfirmarContrasena(v: String) = _uiState.update { it.copy(confirmarContrasena = v) }

    fun guardarPerfil() {
        val s = _uiState.value
        when {
            s.primerNombre.isBlank() || s.primerApellido.isBlank() -> {
                _uiState.update { it.copy(error = "El nombre y el apellido son obligatorios") }
                return
            }
            s.tipoDocumento != "sin_documento" && s.numeroDocumento.isBlank() -> {
                _uiState.update { it.copy(error = "Debes proporcionar el número de documento") }
                return
            }
        }
        val request = PerfilRequest(
            primerNombre = s.primerNombre.trim(),
            segundoNombre = s.segundoNombre.trim().ifBlank { null },
            primerApellido = s.primerApellido.trim(),
            segundoApellido = s.segundoApellido.trim().ifBlank { null },
            fechaNacimiento = s.fechaNacimiento.trim().ifBlank { null },
            sexo = s.sexo.trim().ifBlank { null },
            lugarNacimiento = s.lugarNacimiento.trim().ifBlank { null },
            region = s.region.trim().ifBlank { null },
            departamento = s.departamento.trim().ifBlank { null },
            municipio = s.municipio.trim().ifBlank { null },
            tipoDocumento = s.tipoDocumento,
            numeroDocumento = s.numeroDocumento.trim().ifBlank { null },
            estadoCivil = s.estadoCivil
        )
        _uiState.update { it.copy(guardando = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                val persona = if (s.tienePerfil) {
                    perfilRepository.actualizarPerfil(request)
                } else {
                    perfilRepository.crearPerfil(request)
                }
                _uiState.update {
                    it.copy(
                        guardando = false,
                        tienePerfil = true,
                        perfil = persona,
                        mensajeExito = "Perfil guardado correctamente"
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(guardando = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun cambiarEmail() {
        val s = _uiState.value
        if (s.emailContrasenaActual.isBlank() || s.nuevoCorreo.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu contraseña y el nuevo correo") }
            return
        }
        _uiState.update { it.copy(cambiandoEmail = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                val respuesta = perfilRepository.cambiarEmail(s.emailContrasenaActual, s.nuevoCorreo.trim())
                _uiState.update {
                    it.copy(
                        cambiandoEmail = false,
                        emailContrasenaActual = "",
                        nuevoCorreo = "",
                        mensajeExito = respuesta.mensaje
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(cambiandoEmail = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun cambiarContrasena() {
        val s = _uiState.value
        when {
            s.passContrasenaActual.isBlank() || s.nuevaContrasena.isBlank() || s.confirmarContrasena.isBlank() -> {
                _uiState.update { it.copy(error = "Completa todos los campos de contraseña") }
                return
            }
            s.nuevaContrasena != s.confirmarContrasena -> {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
                return
            }
        }
        _uiState.update { it.copy(cambiandoPass = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                val respuesta = perfilRepository.cambiarContrasena(
                    s.passContrasenaActual,
                    s.nuevaContrasena,
                    s.confirmarContrasena
                )
                _uiState.update {
                    it.copy(
                        cambiandoPass = false,
                        passContrasenaActual = "",
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        mensajeExito = respuesta.mensaje
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(cambiandoPass = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun cargarSesiones() {
        _uiState.update { it.copy(cargandoSesiones = true, error = null) }
        viewModelScope.launch {
            try {
                val sesiones = perfilRepository.sesiones()
                _uiState.update { it.copy(cargandoSesiones = false, sesiones = sesiones) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(cargandoSesiones = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun subirFoto(archivo: File) {
        _uiState.update { it.copy(subiendoFoto = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                val resultado = perfilRepository.subirFoto(archivo)
                val perfil = _uiState.value.perfil?.copy(fotoUrl = resultado.fotoUrl)
                _uiState.update {
                    it.copy(
                        subiendoFoto = false,
                        perfil = perfil,
                        mensajeExito = resultado.mensaje
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(subiendoFoto = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun eliminarFoto() {
        _uiState.update { it.copy(subiendoFoto = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                val resultado = perfilRepository.eliminarFoto()
                val perfil = _uiState.value.perfil?.copy(fotoUrl = null)
                _uiState.update {
                    it.copy(
                        subiendoFoto = false,
                        perfil = perfil,
                        mensajeExito = resultado.mensaje
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(subiendoFoto = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
