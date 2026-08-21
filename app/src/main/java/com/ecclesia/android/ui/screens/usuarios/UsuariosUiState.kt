package com.ecclesia.android.ui.screens.usuarios

import com.ecclesia.android.domain.models.Rol
import com.ecclesia.android.domain.models.Usuario

data class UsuarioFila(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String,
    val rolId: Long?,
    val estado: String
)

data class UsuariosUiState(
    val isLoading: Boolean = true,
    val guardando: Boolean = false,
    val usuarios: List<UsuarioFila> = emptyList(),
    val roles: List<Rol> = emptyList(),
    val busqueda: String = "",
    val error: String? = null,
    val mensaje: String? = null,
    val dialogoAbierto: Boolean = false,
    val editandoId: Long? = null,
    val correo: String = "",
    val contrasena: String = "Ecclesia2026*",
    val rolSeleccionadoId: Long? = null,
    val estadoSeleccionado: String = "Activo",
    val eliminando: UsuarioFila? = null
) {
    val filtrados: List<UsuarioFila>
        get() {
            val t = busqueda.trim().lowercase()
            return if (t.isEmpty()) usuarios
            else usuarios.filter {
                it.nombre.lowercase().contains(t) || it.correo.lowercase().contains(t)
            }
        }
}

internal fun Usuario.aFila(): UsuarioFila {
    val prioridad = listOf(
        "superadmin", "admin del sitio", "administrador parroquial",
        "párroco", "parroco", "secretario", "secretaria", "catequista"
    )
    var rolPrincipal = "Usuario Fiel"
    var rolId: Long? = roles.firstOrNull()?.id

    for (prio in prioridad) {
        val r = roles.firstOrNull { it.nombre.lowercase().trim() == prio }
        if (r != null) {
            rolPrincipal = r.nombre
            rolId = r.id
            break
        }
    }

    if (rolPrincipal == "Usuario Fiel" && roles.isNotEmpty()) {
        val r = roles.firstOrNull {
            it.nombre.lowercase().trim() !in listOf("usuario", "usuario fiel")
        }
        if (r != null) {
            rolPrincipal = r.nombre
            rolId = r.id
        } else {
            rolPrincipal = roles[0].nombre.ifEmpty { "Usuario Fiel" }
        }
    }

    return UsuarioFila(
        id = id,
        nombre = correo.substringBefore("@").ifEmpty { "Usuario" },
        correo = correo,
        rol = rolPrincipal,
        rolId = rolId,
        estado = if (estado.equals("activo", ignoreCase = true)) "Activo" else "Inactivo"
    )
}
