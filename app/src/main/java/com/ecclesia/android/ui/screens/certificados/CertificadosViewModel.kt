package com.ecclesia.android.ui.screens.certificados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.CertificadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CertificadosViewModel(
    private val repository: CertificadoRepository = CertificadoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CertificadosUiState())
    val uiState: StateFlow<CertificadosUiState> = _uiState

    init {
        cargar()
    }

    fun cambiarTab(tab: String) {
        _uiState.update { it.copy(tab = tab) }
    }

    fun onBuscarChange(valor: String) {
        _uiState.update { it.copy(busqueda = valor) }
    }

    fun descartarMensaje() {
        _uiState.update { it.copy(mensaje = null, errorGenerar = null) }
    }

    fun cargar() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                val certificados = repository.listarCertificados()
                val registros = repository.listarRegistros()
                _uiState.update {
                    it.copy(cargando = false, certificados = certificados, registros = registros)
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(cargando = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun generar(registroId: Long) {
        _uiState.update { it.copy(generandoRegistroId = registroId, errorGenerar = null) }
        viewModelScope.launch {
            try {
                repository.generar(registroId)
                _uiState.update {
                    it.copy(
                        generandoRegistroId = null,
                        mensaje = "Certificado generado correctamente"
                    )
                }
                cargar()
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        generandoRegistroId = null,
                        errorGenerar = ApiErrorParser.mensaje(t)
                    )
                }
            }
        }
    }

    fun descargar(certificadoId: Long, onBytes: (ByteArray) -> Unit) {
        _uiState.update { it.copy(descargando = true) }
        viewModelScope.launch {
            try {
                val body = repository.descargar(certificadoId)
                val bytes = body.bytes()
                _uiState.update { it.copy(descargando = false) }
                onBytes(bytes)
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(descargando = false, mensaje = "No se pudo descargar el certificado")
                }
            }
        }
    }
}