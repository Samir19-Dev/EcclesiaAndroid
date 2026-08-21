package com.ecclesia.android.ui.components

private fun String.normalizado(): String =
    lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i")
        .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
        .replace("_", " ")

fun rolPrincipalDe(roles: List<String>): String? {
    val jerarquia = listOf(
        "superadmin", "admin del sitio", "administrador parroquial",
        "parroco", "secretario", "catequista", "usuario"
    )
    val rolesNorm = roles.map { it.normalizado() }
    return jerarquia.firstOrNull { jerarquia ->
        rolesNorm.any { rol -> rol.contains(jerarquia) }
    } ?: roles.firstOrNull()
}
