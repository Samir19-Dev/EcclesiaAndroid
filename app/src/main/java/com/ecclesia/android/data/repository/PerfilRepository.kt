package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.data.network.SessionManager
import com.ecclesia.android.domain.models.CambiarContrasenaRequest
import com.ecclesia.android.domain.models.CambiarEmailRequest
import com.ecclesia.android.domain.models.FotoPerfilResponse
import com.ecclesia.android.domain.models.MensajeResponse
import com.ecclesia.android.domain.models.PerfilRequest
import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.SesionInfo
import com.ecclesia.android.domain.models.VerificarPerfilResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PerfilRepository(
    private val api: ApiService = ApiClient.api,
    private val session: SessionManager = SessionManager.instance
) {

    suspend fun verificarPerfil(): VerificarPerfilResponse = api.verificarPerfil()

    suspend fun obtenerPerfil(): Persona = api.miPerfil()

    suspend fun crearPerfil(request: PerfilRequest): Persona {
        val persona = api.crearMiPerfil(request)
        actualizarEstadoSesion()
        return persona
    }

    suspend fun actualizarPerfil(request: PerfilRequest): Persona {
        val persona = api.actualizarMiPerfil(request)
        actualizarEstadoSesion()
        return persona
    }

    suspend fun cambiarEmail(contrasenaActual: String, nuevoCorreo: String): MensajeResponse =
        api.cambiarEmail(CambiarEmailRequest(contrasenaActual, nuevoCorreo))

    suspend fun cambiarContrasena(actual: String, nueva: String, confirmacion: String): MensajeResponse =
        api.cambiarContrasena(CambiarContrasenaRequest(actual, nueva, confirmacion))

    suspend fun sesiones(): List<SesionInfo> = api.sesiones()

    suspend fun subirFoto(file: File): FotoPerfilResponse {
        val body = file.asRequestBody("image/*".toMediaType())
        val part = MultipartBody.Part.createFormData("file", file.name, body)
        val resultado = api.subirFotoPerfil(part)
        actualizarEstadoSesion()
        return resultado
    }

    suspend fun eliminarFoto(): FotoPerfilResponse {
        val resultado = api.eliminarFotoPerfil()
        actualizarEstadoSesion()
        return resultado
    }

    private suspend fun actualizarEstadoSesion() {
        runCatching {
            val persona = api.miPerfil()
            session.actualizarPerfilCompleto(
                perfilCompleto = true,
                personaId = persona.id
            )
        }
    }
}
