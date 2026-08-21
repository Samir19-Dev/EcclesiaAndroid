package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.Certificado
import com.ecclesia.android.domain.models.CertificadoCreateRequest
import com.ecclesia.android.domain.models.RegistroSacramental
import okhttp3.ResponseBody

class CertificadoRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun listarCertificados(): List<Certificado> = api.certificados()

    suspend fun listarRegistros(): List<RegistroSacramental> = api.registrosCertificados()

    suspend fun generar(registroId: Long): Certificado =
        api.generarCertificado(CertificadoCreateRequest(registroSacramentalId = registroId))

    suspend fun descargar(id: Long): ResponseBody = api.descargarCertificado(id)
}