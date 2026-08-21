package com.ecclesia.android.data.network

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

object ApiErrorParser {

    fun mensaje(t: Throwable): String = when (t) {
        is CancellationException -> throw t
        is HttpException -> extraerDetail(t) ?: "Error del servidor (${t.code()})"
        is java.net.ConnectException -> "No se pudo conectar con el servidor. Verifica que el backend esté corriendo."
        is java.net.SocketTimeoutException -> "El servidor tardó demasiado en responder."
        is java.net.UnknownHostException -> "No se encontró el servidor. Revisa la dirección de conexión."
        else -> t.message ?: "Error inesperado"
    }

    private fun extraerDetail(e: HttpException): String? {
        return try {
            val cuerpo = e.response()?.errorBody()?.string() ?: return null
            val obj = Json.parseToJsonElement(cuerpo).jsonObject
            val detail = obj["detail"] ?: return null
            parseDetail(detail)
        } catch (ex: Exception) {
            null
        }
    }

    private fun parseDetail(detail: JsonElement): String = when {
        detail.jsonPrimitive.isString -> detail.jsonPrimitive.content
        else -> try {
            detail.jsonArray.joinToString("\n") { item ->
                item.jsonObject["msg"]?.jsonPrimitive?.content ?: ""
            }
        } catch (ex: Exception) {
            try {
                detail.jsonObject["mensaje"]?.jsonPrimitive?.content
                    ?: detail.jsonObject["codigo"]?.jsonPrimitive?.content
                    ?: detail.toString()
            } catch (ex2: Exception) {
                detail.toString()
            }
        }
    }
}
