package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.AuditoriaLog

class AuditoriaRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun listar(buscar: String? = null): List<AuditoriaLog> = api.auditoria(buscar)
}
