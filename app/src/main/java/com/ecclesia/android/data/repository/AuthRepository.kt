package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.data.network.SessionManager
import com.ecclesia.android.domain.models.CorreoRequest
import com.ecclesia.android.domain.models.LoginRequest
import com.ecclesia.android.domain.models.MensajeResponse
import com.ecclesia.android.domain.models.RegisterRequest
import com.ecclesia.android.domain.models.RestablecerContrasenaRequest
import com.ecclesia.android.domain.models.Token
import com.ecclesia.android.domain.models.Usuario
import com.ecclesia.android.domain.models.ValidarEmailRequest

class AuthRepository(
    private val api: ApiService = ApiClient.api,
    private val session: SessionManager = SessionManager.instance
) {

    suspend fun login(correo: String, contrasena: String): Token {
        val token = api.login(LoginRequest(correo = correo, contrasena = contrasena))
        session.guardarToken(token)
        return token
    }

    suspend fun register(correo: String, contrasena: String): Usuario =
        api.register(RegisterRequest(correo = correo, contrasena = contrasena))

    suspend fun usuarioActual(): Usuario = api.usuarioActual()

    suspend fun forgotPassword(correo: String): MensajeResponse =
        api.forgotPassword(CorreoRequest(correo))

    suspend fun resetPassword(token: String, nueva: String, confirmacion: String): MensajeResponse =
        api.resetPassword(RestablecerContrasenaRequest(token, nueva, confirmacion))

    suspend fun reenviarValidacion(correo: String): MensajeResponse =
        api.reenviarValidacion(CorreoRequest(correo))

    suspend fun validarEmail(token: String): MensajeResponse =
        api.validarEmail(ValidarEmailRequest(token))

    suspend fun logout(cerrarTodas: Boolean = false) {
        runCatching { api.logout(com.ecclesia.android.domain.models.LogoutRequest(cerrarTodas = cerrarTodas)) }
        session.limpiar()
    }
}
