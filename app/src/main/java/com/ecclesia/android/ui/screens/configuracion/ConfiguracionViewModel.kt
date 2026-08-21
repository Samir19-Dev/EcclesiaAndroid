package com.ecclesia.android.ui.screens.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecclesia.android.data.network.ApiErrorParser
import com.ecclesia.android.data.repository.ConfiguracionRepository
import com.ecclesia.android.domain.models.ConfiguracionUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfiguracionViewModel(
    private val repository: ConfiguracionRepository = ConfiguracionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val config = repository.obtener()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nombreParroquia = config.nombreParroquia ?: "",
                        parrocoActual = config.parrocoActual ?: "",
                        telefono = config.telefono ?: "",
                        emailParroquia = config.emailParroquia ?: "",
                        direccion = config.direccion ?: "",
                        plantillaActiva = config.plantillaActiva ?: "clasica",
                        incluirQr = config.incluirQr,
                        incluirSello = config.incluirSello,
                        diasRetencionDocs = (config.diasRetencionDocs ?: 1825).toString(),
                        notifEmail = config.notifEmail,
                        notifTelegram = config.notifTelegram,
                        telegramBotToken = config.telegramBotToken ?: ""
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }

    fun onTabChange(tab: Int) = _uiState.update { it.copy(tabActual = tab, error = null, mensajeExito = null) }

    fun onNombreParroquia(v: String) = _uiState.update { it.copy(nombreParroquia = v, error = null) }
    fun onParrocoActual(v: String) = _uiState.update { it.copy(parrocoActual = v, error = null) }
    fun onTelefono(v: String) = _uiState.update { it.copy(telefono = v, error = null) }
    fun onEmailParroquia(v: String) = _uiState.update { it.copy(emailParroquia = v, error = null) }
    fun onDireccion(v: String) = _uiState.update { it.copy(direccion = v, error = null) }
    fun onPlantillaActiva(v: String) = _uiState.update { it.copy(plantillaActiva = v) }
    fun onIncluirQr(v: Boolean) = _uiState.update { it.copy(incluirQr = v) }
    fun onIncluirSello(v: Boolean) = _uiState.update { it.copy(incluirSello = v) }
    fun onDiasRetencion(v: String) = _uiState.update { it.copy(diasRetencionDocs = v.filter { c -> c.isDigit() }, error = null) }
    fun onNotifEmail(v: Boolean) = _uiState.update { it.copy(notifEmail = v) }
    fun onNotifTelegram(v: Boolean) = _uiState.update { it.copy(notifTelegram = v) }
    fun onTelegramBotToken(v: String) = _uiState.update { it.copy(telegramBotToken = v) }

    fun guardarTab(tab: Int) {
        val s = _uiState.value
        val request = when (tab) {
            0 -> {
                when {
                    s.nombreParroquia.isBlank() -> {
                        _uiState.update { it.copy(error = "El nombre de la parroquia es obligatorio") }
                        return
                    }
                    s.parrocoActual.isBlank() -> {
                        _uiState.update { it.copy(error = "El párroco actual es obligatorio") }
                        return
                    }
                    s.telefono.isBlank() -> {
                        _uiState.update { it.copy(error = "El teléfono de contacto es obligatorio") }
                        return
                    }
                    s.emailParroquia.isBlank() || !s.emailParroquia.contains("@") -> {
                        _uiState.update { it.copy(error = "Ingresa un email institucional válido") }
                        return
                    }
                    s.direccion.isBlank() -> {
                        _uiState.update { it.copy(error = "La dirección es obligatoria") }
                        return
                    }
                }
                ConfiguracionUpdateRequest(
                    nombreParroquia = s.nombreParroquia.trim(),
                    parrocoActual = s.parrocoActual.trim(),
                    telefono = s.telefono.trim(),
                    emailParroquia = s.emailParroquia.trim(),
                    direccion = s.direccion.trim()
                )
            }
            1 -> ConfiguracionUpdateRequest(
                plantillaActiva = s.plantillaActiva,
                incluirQr = s.incluirQr,
                incluirSello = s.incluirSello
            )
            2 -> {
                val dias = s.diasRetencionDocs.toIntOrNull()
                if (dias == null || dias < 365) {
                    _uiState.update { it.copy(error = "Debe ser al menos 365 días") }
                    return
                }
                ConfiguracionUpdateRequest(diasRetencionDocs = dias)
            }
            else -> {
                if (s.notifTelegram && s.telegramBotToken.isBlank()) {
                    _uiState.update { it.copy(error = "Ingresa el token del bot de Telegram") }
                    return
                }
                ConfiguracionUpdateRequest(
                    notifEmail = s.notifEmail,
                    notifTelegram = s.notifTelegram,
                    telegramBotToken = if (s.notifTelegram) s.telegramBotToken.trim() else null
                )
            }
        }

        _uiState.update { it.copy(guardando = true, error = null, mensajeExito = null) }
        viewModelScope.launch {
            try {
                repository.actualizar(request)
                _uiState.update { it.copy(guardando = false, mensajeExito = "Configuración guardada correctamente") }
            } catch (t: Throwable) {
                _uiState.update { it.copy(guardando = false, error = ApiErrorParser.mensaje(t)) }
            }
        }
    }
}
