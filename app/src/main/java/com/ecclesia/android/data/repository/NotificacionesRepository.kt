package com.ecclesia.android.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStoreNotificaciones by preferencesDataStore(name = "notificaciones")

class NotificacionesRepository(private val context: Context) {

    companion object {
        @Volatile
        lateinit var instance: NotificacionesRepository

        fun init(context: Context) {
            instance = NotificacionesRepository(context.applicationContext)
        }
    }

    private val claves = listOf("solicitudes", "estados", "eventos", "auditoria", "cuenta")

    private fun key(clave: String) = booleanPreferencesKey("notif_$clave")

    val preferencias = context.dataStoreNotificaciones.data.map { prefs ->
        claves.associateWith { prefs[key(it)] ?: true }
    }

    suspend fun guardar(clave: String, valor: Boolean) {
        context.dataStoreNotificaciones.edit { prefs ->
            prefs[key(clave)] = valor
        }
    }

    suspend fun obtener(): Map<String, Boolean> =
        preferencias.firstOrNull() ?: claves.associateWith { true }
}