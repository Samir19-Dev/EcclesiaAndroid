package com.ecclesia.android.ui.screens.configuracion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.InputField
import com.ecclesia.android.ui.components.PrimaryButton
import com.ecclesia.android.ui.components.SelectorField

private val plantillas = mapOf(
    "clasica" to "Clásica (Tradicional Eclesiástica)",
    "moderna" to "Moderna Elegante",
    "minimalista" to "Minimalista"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: ConfiguracionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Configuración",
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "Configuración del Sistema",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Parámetros globales, certificados, firmas y notificaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TabRow(selectedTabIndex = uiState.tabActual) {
                Tab(
                    selected = uiState.tabActual == 0,
                    onClick = { viewModel.onTabChange(0) },
                    text = { Text("Parroquia") }
                )
                Tab(
                    selected = uiState.tabActual == 1,
                    onClick = { viewModel.onTabChange(1) },
                    text = { Text("Certificados") }
                )
                Tab(
                    selected = uiState.tabActual == 2,
                    onClick = { viewModel.onTabChange(2) },
                    text = { Text("Retención") }
                )
                Tab(
                    selected = uiState.tabActual == 3,
                    onClick = { viewModel.onTabChange(3) },
                    text = { Text("Notificaciones") }
                )
            }

            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.error?.let { ErrorMessage(message = it, modifier = Modifier.padding(horizontal = 16.dp)) }
                uiState.mensajeExito?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                when (uiState.tabActual) {
                    0 -> TabDatosParroquiales(uiState, viewModel)
                    1 -> TabCertificados(uiState, viewModel)
                    2 -> TabRetencion(uiState, viewModel)
                    else -> TabNotificaciones(uiState, viewModel)
                }
            }
        }
    }
}

@Composable
private fun TarjetaForm(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            contenido()
        }
    }
}

@Composable
private fun FilaSwitch(texto: String, activo: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = activo, onCheckedChange = onChange)
    }
}

@Composable
private fun TabDatosParroquiales(uiState: ConfiguracionUiState, viewModel: ConfiguracionViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TarjetaForm(titulo = "Información Parroquial") {
            InputField(
                value = uiState.nombreParroquia,
                onValueChange = viewModel::onNombreParroquia,
                label = "Nombre de la Parroquia *",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            InputField(
                value = uiState.parrocoActual,
                onValueChange = viewModel::onParrocoActual,
                label = "Párroco Actual *",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            InputField(
                value = uiState.telefono,
                onValueChange = viewModel::onTelefono,
                label = "Teléfono de Contacto *",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            InputField(
                value = uiState.emailParroquia,
                onValueChange = viewModel::onEmailParroquia,
                label = "Email Institucional *",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            InputField(
                value = uiState.direccion,
                onValueChange = viewModel::onDireccion,
                label = "Dirección Parroquial *",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = if (uiState.guardando) "Guardando..." else "Guardar Cambios",
                onClick = { viewModel.guardarTab(0) },
                enabled = !uiState.guardando
            )
        }
    }
}

@Composable
private fun TabCertificados(uiState: ConfiguracionUiState, viewModel: ConfiguracionViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TarjetaForm(titulo = "Plantillas y Formato") {
            SelectorField(
                value = plantillas[uiState.plantillaActiva] ?: uiState.plantillaActiva,
                opciones = plantillas.values.toList(),
                onSelect = { seleccion ->
                    val clave = plantillas.entries.firstOrNull { it.value == seleccion }?.key ?: seleccion
                    viewModel.onPlantillaActiva(clave)
                },
                label = "Estilo de Plantilla"
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilaSwitch(
                texto = "Incluir Código QR de Validación Verificable",
                activo = uiState.incluirQr,
                onChange = viewModel::onIncluirQr
            )
            FilaSwitch(
                texto = "Incluir Sello Digital Parroquial",
                activo = uiState.incluirSello,
                onChange = viewModel::onIncluirSello
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = if (uiState.guardando) "Guardando..." else "Guardar Cambios",
                onClick = { viewModel.guardarTab(1) },
                enabled = !uiState.guardando
            )
        }
    }
}

@Composable
private fun TabRetencion(uiState: ConfiguracionUiState, viewModel: ConfiguracionViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TarjetaForm(titulo = "Políticas de Conservación") {
            OutlinedTextField(
                value = uiState.diasRetencionDocs,
                onValueChange = viewModel::onDiasRetencion,
                label = { Text("Días de retención de documentos (mín. 365)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Los libros y fe de bautismos se conservan indefinidamente según el derecho canónico.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = if (uiState.guardando) "Guardando..." else "Guardar Configuración",
                onClick = { viewModel.guardarTab(2) },
                enabled = !uiState.guardando
            )
        }
    }
}

@Composable
private fun TabNotificaciones(uiState: ConfiguracionUiState, viewModel: ConfiguracionViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TarjetaForm(titulo = "Canales de Notificación") {
            FilaSwitch(
                texto = "Enviar Notificaciones por Correo Electrónico",
                activo = uiState.notifEmail,
                onChange = viewModel::onNotifEmail
            )
            FilaSwitch(
                texto = "Enviar Alertas por Telegram Bot",
                activo = uiState.notifTelegram,
                onChange = viewModel::onNotifTelegram
            )
            if (uiState.notifTelegram) {
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    value = uiState.telegramBotToken,
                    onValueChange = viewModel::onTelegramBotToken,
                    label = "Token Bot de Telegram",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = if (uiState.guardando) "Guardando..." else "Guardar Preferencias",
                onClick = { viewModel.guardarTab(3) },
                enabled = !uiState.guardando
            )
        }
    }
}
