package com.ecclesia.android.data.network

import com.ecclesia.android.domain.models.RefreshTokenRequest
import com.ecclesia.android.domain.models.Token
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit


class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val session = SessionManager.instance
        val refreshToken = session.refreshToken ?: return null
        if (refreshToken.isBlank()) return null

        val tokenQueFalló = extraerBearer(response.request.header("Authorization"))

        return synchronized(LOCK) {
            if (session.accessToken != null && session.accessToken != tokenQueFalló) {
                return@synchronized requestConToken(response, session.accessToken!!)
            }

            val tokenNuevo = refresh(refreshToken)
            if (tokenNuevo == null) {
                runBlocking { session.limpiar() }
                return@synchronized null
            }
            runBlocking { session.guardarToken(tokenNuevo) }
            requestConToken(response, tokenNuevo.accessToken)
        }
    }

    private fun requestConToken(response: Response, accessToken: String): Request =
        response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

    private fun refresh(refreshToken: String): Token? {
        return try {
            val body = Json.encodeToString(
                RefreshTokenRequest.serializer(),
                RefreshTokenRequest(refreshToken)
            ).toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(ApiClient.BASE_URL + "auth/refresh")
                .post(body)
                .build()

            clienteRefresh.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val cuerpo = resp.body?.string() ?: return null
                val obj = Json.parseToJsonElement(cuerpo).jsonObject

                val acceso = obj["access_token"]?.jsonPrimitive?.content ?: return null
                val refresco = obj["refresh_token"]?.jsonPrimitive?.contentOrNull ?: refreshToken
                val tipo = obj["token_type"]?.jsonPrimitive?.contentOrNull ?: "bearer"
                val expira = obj["expires_in"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 1800
                val sesionToken = obj["session_token"]?.jsonPrimitive?.contentOrNull ?: ""
                val usuarioId = obj["usuario_id"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
                val perfil = obj["perfil_completo"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: false
                val validado = obj["correo_validado"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: true
                val estado = obj["estado"]?.jsonPrimitive?.contentOrNull
                val personaId = obj["persona_id"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
                val roles = obj["roles"]?.jsonArray
                    ?.mapNotNull { it.jsonPrimitive.contentOrNull }
                    ?: emptyList()

                Token(
                    accessToken = acceso,
                    refreshToken = refresco,
                    tokenType = tipo,
                    expiresIn = expira,
                    sessionToken = sesionToken,
                    usuarioId = usuarioId,
                    perfilCompleto = perfil,
                    correoValidado = validado,
                    estado = estado,
                    personaId = personaId,
                    roles = roles
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extraerBearer(header: String?): String? {
        if (header.isNullOrBlank()) return null
        return header.removePrefix("Bearer ").ifBlank { null }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    companion object {
        private val LOCK = Any()

        private val clienteRefresh: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }
    }
}
