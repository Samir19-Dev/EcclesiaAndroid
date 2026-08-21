package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.Evento

class EventoRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun listar(estado: String? = null): List<Evento> = api.eventos(estado)

    suspend fun detalle(id: Long): Evento = api.evento(id)
}
