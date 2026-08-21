package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.Pago

class PagoRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun listar(): List<Pago> = api.pagos()
}
