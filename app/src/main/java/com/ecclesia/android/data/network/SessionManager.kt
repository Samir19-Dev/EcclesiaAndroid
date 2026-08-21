package com.ecclesia.android.data.network

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    companion object {
        @Volatile
        lateinit var instance: SessionManager

        fun init(context: Context) {
            instance = SessionManager(context.applicationContext)
        }
    }

    @Volatile
    var accessToken: String? = null
        private set
    @Volatile
    var refreshToken: String? = null
        private set
    @Volatile
    var sessionToken: String? = null
        private set
    @Volatile
    var usuarioId: Long? = null
        private set
    @Volatile
    var personaId: Long? = null
        private set
    @Volatile
    var perfilCompleto: Boolean = false
        private set
    @Volatile
    var correoValidado: Boolean = true
        private set
    @Volatile
    var estado: String? = null
        private set
    @Volatile
    var roles: List<String> = emptyList()
        private set
    var permisos: Set<String> by mutableStateOf(emptySet())
        private set

    private val keyAccess = stringPreferencesKey("access_token")
    private val keyRefresh = stringPreferencesKey("refresh_token")
    private val keySession = stringPreferencesKey("session_token")
    private val keyUserId = stringPreferencesKey("usuario_id")
    private val keyPersonaId = stringPreferencesKey("persona_id")
    private val keyPerfilCompleto = booleanPreferencesKey("perfil_completo")
    private val keyCorreoValidado = booleanPreferencesKey("correo_validado")
    private val keyEstado = stringPreferencesKey("estado")
    private val keyRoles = stringPreferencesKey("roles")
    private val keyPermisos = stringSetPreferencesKey("permisos")

    val tokens: Flow<Pair<String?, String?>> = context.dataStore.data.map { prefs ->
        prefs[keyAccess] to prefs[keyRefresh]
    }

    val tieneSesion: Boolean
        get() = !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()

    fun esAdmin(): Boolean = roles.any { rol ->
        rol.contains("superadmin", ignoreCase = true) ||
            rol.contains("admin", ignoreCase = true)
    }

    fun tienePermiso(vararg codigos: String): Boolean {
        if (codigos.isEmpty()) return true
        val set = permisos.map { it.lowercase().trim() }.toSet()
        return codigos.any { set.contains(it.lowercase().trim()) }
    }

    suspend fun guardarPermisos(lista: List<String>) {
        permisos = lista.toSet()
        context.dataStore.edit { prefs -> prefs[keyPermisos] = permisos }
    }

    suspend fun guardarToken(token: com.ecclesia.android.domain.models.Token) {
        accessToken = token.accessToken
        refreshToken = token.refreshToken
        sessionToken = token.sessionToken.ifBlank { sessionToken }
        usuarioId = token.usuarioId?.toLong() ?: usuarioId
        personaId = token.personaId?.toLong() ?: personaId
        perfilCompleto = token.perfilCompleto
        correoValidado = token.correoValidado
        estado = token.estado
        roles = token.roles.ifEmpty { roles }

        context.dataStore.edit { prefs ->
            prefs[keyAccess] = token.accessToken
            prefs[keyRefresh] = token.refreshToken
            token.sessionToken.takeIf { it.isNotBlank() }?.let { prefs[keySession] = it }
            token.usuarioId?.let { prefs[keyUserId] = it.toString() }
            token.personaId?.let { prefs[keyPersonaId] = it.toString() }
            prefs[keyPerfilCompleto] = token.perfilCompleto
            prefs[keyCorreoValidado] = token.correoValidado
            token.estado?.let { prefs[keyEstado] = it }
            if (token.roles.isNotEmpty()) prefs[keyRoles] = token.roles.joinToString(",")
        }
    }

    suspend fun actualizarAcceso(nuevoAccessToken: String) {
        accessToken = nuevoAccessToken
        context.dataStore.edit { prefs -> prefs[keyAccess] = nuevoAccessToken }
    }

    suspend fun actualizarPerfilCompleto(perfilCompleto: Boolean, personaId: Long? = null) {
        this.perfilCompleto = perfilCompleto
        personaId?.let { this.personaId = it }
        context.dataStore.edit { prefs ->
            prefs[keyPerfilCompleto] = perfilCompleto
            personaId?.let { prefs[keyPersonaId] = it.toString() }
        }
    }

    suspend fun limpiar() {
        accessToken = null
        refreshToken = null
        sessionToken = null
        usuarioId = null
        personaId = null
        perfilCompleto = false
        correoValidado = true
        estado = null
        roles = emptyList()
        permisos = emptySet()
        context.dataStore.edit { it.clear() }
    }

    suspend fun restaurar() {
        val prefs = context.dataStore.data.firstOrNull() ?: return
        accessToken = prefs[keyAccess]
        refreshToken = prefs[keyRefresh]
        sessionToken = prefs[keySession]
        usuarioId = prefs[keyUserId]?.toLongOrNull()
        personaId = prefs[keyPersonaId]?.toLongOrNull()
        perfilCompleto = prefs[keyPerfilCompleto] ?: false
        correoValidado = prefs[keyCorreoValidado] ?: true
        estado = prefs[keyEstado]
        roles = prefs[keyRoles]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        permisos = prefs[keyPermisos] ?: emptySet()
    }
}
