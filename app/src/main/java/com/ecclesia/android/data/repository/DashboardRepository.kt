package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.domain.models.Evento
import com.ecclesia.android.domain.models.Notificacion
import com.ecclesia.android.domain.models.PaginatedSolicitudes
import com.ecclesia.android.domain.models.Pago
import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.SolicitudSacramento
import com.ecclesia.android.domain.models.Usuario
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class DashboardData(
    val esAdmin: Boolean = false,
    val usuario: Usuario? = null,
    val solicitudes: List<SolicitudSacramento> = emptyList(),
    val totalSolicitudesActivas: Int = 0,
    val totalSacramentos: Int = 0,
    val totalPersonas: Int = 0,
    val totalUsuarios: Int = 0,
    val misSolicitudesActivas: Int = 0,
    val totalCertificados: Int = 0,
    val notificacionesNoLeidas: Int = 0,
    val totalEventos: Int = 0
)

class DashboardRepository(
    private val auth: AuthRepository = AuthRepository(),
    private val personas: PersonaRepository = PersonaRepository(),
    private val eventos: EventoRepository = EventoRepository(),
    private val solicitudes: SolicitudRepository = SolicitudRepository(),
    private val certificados: CertificadoRepository = CertificadoRepository()
) {
    private val api = ApiClient.api

    suspend fun cargar(): DashboardData = coroutineScope {
        val usuario = async { runCatching { auth.usuarioActual() }.getOrNull() }
        val permisos = runCatching { api.misPermisos().permisos }.getOrElse { emptyList() }
        val esAdmin = permisos.any { it == "solicitudes.ver_todas" }

        if (esAdmin) {
            val todas = async {
                runCatching { api.todasSolicitudes(pagina = 1, porPagina = 50) }
                    .getOrElse { PaginatedSolicitudes() }
            }
            val sacramentos = async { runCatching { solicitudes.sacramentos() }.getOrElse { emptyList() } }
            val listaPersonas = async { runCatching { personas.listar() }.getOrElse { emptyList() } }
            val usuarios = async { runCatching { api.usuariosAdmin() }.getOrNull() }

            val resTodas = todas.await()
            DashboardData(
                esAdmin = true,
                usuario = usuario.await(),
                solicitudes = resTodas.items,
                totalSolicitudesActivas = if (resTodas.total > 0) resTodas.total else resTodas.items.size,
                totalSacramentos = sacramentos.await().size,
                totalPersonas = listaPersonas.await().size,
                totalUsuarios = usuarios.await()?.total ?: 0
            )
        } else {
            val mias = async {
                runCatching { solicitudes.misSolicitudes() }
                    .getOrElse { PaginatedSolicitudes() }
            }
            val certificados = async { runCatching { certificados.listarCertificados() }.getOrElse { emptyList() } }
            val notificaciones = async { runCatching { api.notificaciones() }.getOrElse { emptyList<Notificacion>() } }
            val listaEventos = async { runCatching { eventos.listar() }.getOrElse { emptyList<Evento>() } }

            val resMias = mias.await()
            val activas = resMias.items.count {
                it.estado.equals("pendiente", ignoreCase = true) ||
                    it.estado.equals("en_revision", ignoreCase = true)
            }

            DashboardData(
                esAdmin = false,
                usuario = usuario.await(),
                solicitudes = resMias.items,
                misSolicitudesActivas = activas.takeIf { it > 0 } ?: resMias.items.size,
                totalCertificados = certificados.await().size,
                notificacionesNoLeidas = notificaciones.await().count { !it.leida },
                totalEventos = listaEventos.await().size
            )
        }
    }
}
