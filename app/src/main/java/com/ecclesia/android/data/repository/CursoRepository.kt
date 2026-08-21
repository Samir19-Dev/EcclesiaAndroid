package com.ecclesia.android.data.repository

import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.ApiService
import com.ecclesia.android.domain.models.Cohorte
import com.ecclesia.android.domain.models.Curso

class CursoRepository(
    private val api: ApiService = ApiClient.api
) {
    suspend fun listar(): List<Curso> = api.cursos()

    suspend fun realizados(): List<Cohorte> = api.cursosRealizados()

    suspend fun cohortes(cursoId: Long): List<Cohorte> = api.cohortesCurso(cursoId)
}
