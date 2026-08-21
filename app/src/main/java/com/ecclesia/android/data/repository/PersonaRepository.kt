package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.PersonaRequest
import com.ecclesia.android.domain.models.SacramentoRegistrado

class PersonaRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun listar(buscar: String? = null): List<Persona> = api.personas(buscar)

    suspend fun detalle(id: Long): Persona = api.persona(id)

    suspend fun sacramentos(id: Long): List<SacramentoRegistrado> = api.sacramentosPersona(id)

    suspend fun crear(request: PersonaRequest): Persona = api.crearPersona(request)

    suspend fun actualizar(id: Long, request: PersonaRequest): Persona = api.actualizarPersona(id, request)
}
