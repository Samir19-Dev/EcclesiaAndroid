package com.ecclesia.android.ui.screens.certificados

import com.ecclesia.android.domain.models.Certificado
import com.ecclesia.android.domain.models.RegistroSacramental

data class CertificadosUiState(
    val tab: String = "certificados",
    val cargando: Boolean = true,
    val error: String? = null,
    val certificados: List<Certificado> = emptyList(),
    val registros: List<RegistroSacramental> = emptyList(),
    val busqueda: String = "",
    val generandoRegistroId: Long? = null,
    val errorGenerar: String? = null,
    val mensaje: String? = null,
    val descargando: Boolean = false
) {
    val certificadosFiltrados: List<Certificado>
        get() = if (busqueda.isBlank()) certificados else certificados.filter {
            listOfNotNull(it.personaNombre, it.sacramento, it.codigoVerificacion)
                .any { texto -> texto.contains(busqueda, ignoreCase = true) }
        }
}