package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.ConfiguracionParroquial
import com.ecclesia.android.domain.models.ConfiguracionUpdateRequest

class ConfiguracionRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun obtener(): ConfiguracionParroquial = api.configuracion()

    suspend fun actualizar(body: ConfiguracionUpdateRequest): ConfiguracionParroquial =
        api.actualizarConfiguracion(body)
}
