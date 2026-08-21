package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.CambiarEstadoRequest
import com.ecclesia.android.domain.models.PaginatedSolicitudes
import com.ecclesia.android.domain.models.Sacramento

class SolicitudRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun misSolicitudes(estado: String? = null): PaginatedSolicitudes =
        api.misSolicitudes(pagina = 1, porPagina = 50, estado = estado)

    suspend fun todasLasSolicitudes(): PaginatedSolicitudes =
        api.todasSolicitudes(pagina = 1, porPagina = 50)

    suspend fun cambiarEstado(id: Long, estado: String, observaciones: String? = null) =
        api.cambiarEstadoSolicitud(id, CambiarEstadoRequest(estado, observaciones))

    suspend fun sacramentos(): List<Sacramento> = api.sacramentos()
}
