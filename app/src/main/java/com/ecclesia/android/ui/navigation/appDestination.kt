package com.ecclesia.android.ui.navigation

sealed class AppDestination(val route: String) {
    data object Landing : AppDestination("landing")
    data object Login : AppDestination("login")
    data object Register : AppDestination("register")
    data object ForgotPassword : AppDestination("forgot-password")
    data object ResetPassword : AppDestination("reset-password")
    data object VerifyEmail : AppDestination("verify-email/{correo}") {
        fun crearRuta(correo: String) = "verify-email/$correo"
    }

    data object Dashboard : AppDestination("dashboard")
    data object Personas : AppDestination("personas")
    data object PersonaDetalle : AppDestination("personas/{personaId}") {
        fun crearRuta(personaId: Long) = "personas/$personaId"
    }
    data object Eventos : AppDestination("eventos")
    data object EventoDetalle : AppDestination("eventos/{eventoId}") {
        fun crearRuta(eventoId: Long) = "eventos/$eventoId"
    }
    data object Solicitudes : AppDestination("solicitudes")
    data object Pagos : AppDestination("pagos")
    data object Sacramentos : AppDestination("sacramentos")
    data object Certificados : AppDestination("certificados")
    data object Cursos : AppDestination("cursos")
    data object Notificaciones : AppDestination("notificaciones")
    data object Usuarios : AppDestination("usuarios")
    data object Roles : AppDestination("roles")
    data object Auditoria : AppDestination("auditoria")
    data object Configuracion : AppDestination("configuracion")
    data object Perfil : AppDestination("perfil")
}
