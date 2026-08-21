package com.ecclesia.android.ui.screens.certificados

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.Certificado
import com.ecclesia.android.domain.models.RegistroSacramental
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.AvatarIniciales
import com.ecclesia.android.ui.components.BadgeEstadoAuto
import com.ecclesia.android.ui.components.EcclesiaButton
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.EcclesiaSearchField
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.SnackbarMessage
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.TextoSuave
import java.io.File

private fun abrirPdf(context: Context, bytes: ByteArray, nombreArchivo: String) {
    val archivo = File(context.cacheDir, nombreArchivo)
    archivo.writeBytes(bytes)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificadosScreen(
    onMenuClick: (() -> Unit)? = null,
    viewModel: CertificadosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var certificadoSeleccionado by remember { mutableStateOf<Certificado?>(null) }

    SnackbarMessage(
        message = uiState.mensaje,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::descartarMensaje
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(titulo = "Certificados", onMenuClick = onMenuClick)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = if (uiState.tab == "certificados") 0 else 1,
                containerColor = Color.White
            ) {
                Tab(
                    selected = uiState.tab == "certificados",
                    onClick = { viewModel.cambiarTab("certificados") },
                    text = { Text("Certificados (${uiState.certificados.size})") }
                )
                Tab(
                    selected = uiState.tab == "registros",
                    onClick = { viewModel.cambiarTab("registros") },
                    text = { Text("Registros (${uiState.registros.size})") }
                )
            }

            when {
                uiState.cargando && uiState.certificados.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null && uiState.certificados.isEmpty() -> {
                    ErrorMessage(
                        message = uiState.error.orEmpty(),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    if (uiState.tab == "certificados") {
                        TabCertificados(
                            uiState = uiState,
                            onBuscarChange = viewModel::onBuscarChange,
                            onVerDetalle = { certificadoSeleccionado = it },
                            onDescargar = { id ->
                                viewModel.descargar(id) { bytes ->
                                    abrirPdf(context, bytes, "certificado_$id.pdf")
                                }
                            },
                            onReintentar = viewModel::cargar
                        )
                    } else {
                        TabRegistros(
                            uiState = uiState,
                            onGenerar = viewModel::generar,
                            onReintentar = viewModel::cargar
                        )
                    }
                }
            }
        }
    }

    certificadoSeleccionado?.let { certificado ->
        DialogDetalleCertificado(
            certificado = certificado,
            descargando = uiState.descargando,
            onDescargar = {
                viewModel.descargar(certificado.id) { bytes ->
                    abrirPdf(context, bytes, "certificado_${certificado.id}.pdf")
                }
            },
            onDismiss = { certificadoSeleccionado = null }
        )
    }
}

@Composable
private fun TabCertificados(
    uiState: CertificadosUiState,
    onBuscarChange: (String) -> Unit,
    onVerDetalle: (Certificado) -> Unit,
    onDescargar: (Long) -> Unit,
    onReintentar: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            EcclesiaSearchField(
                valor = uiState.busqueda,
                onCambio = onBuscarChange,
                placeholder = "Buscar por persona, sacramento o código…",
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (uiState.error != null) {
            item { ErrorMessage(message = uiState.error.orEmpty()) }
        }
        if (uiState.certificadosFiltrados.isEmpty()) {
            item {
                EcclesiaCard {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = Dorado,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No hay certificados emitidos",
                            fontWeight = FontWeight.SemiBold,
                            color = AzulPrincipal
                        )
                    }
                }
            }
        } else {
            items(uiState.certificadosFiltrados, key = { it.id }) { certificado ->
                TarjetaCertificado(
                    certificado = certificado,
                    onClick = { onVerDetalle(certificado) },
                    onDescargar = { onDescargar(certificado.id) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaCertificado(
    certificado: Certificado,
    onClick: () -> Unit,
    onDescargar: () -> Unit
) {
    EcclesiaCard(contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarIniciales(fotoUrl = null, nombre = certificado.personaNombre, size = 46.dp, fontSize = 17.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = certificado.personaNombre ?: "—",
                    style = MaterialTheme.typography.titleSmall,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = certificado.sacramento ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave
                )
                certificado.codigoVerificacion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Dorado,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                BadgeEstadoAuto(estado = certificado.estado)
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    Icons.Filled.Download,
                    contentDescription = "Descargar",
                    tint = AzulPrincipal,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = onDescargar)
                )
            }
        }
    }
}

@Composable
private fun TabRegistros(
    uiState: CertificadosUiState,
    onGenerar: (Long) -> Unit,
    onReintentar: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.errorGenerar != null) {
            item { ErrorMessage(message = uiState.errorGenerar.orEmpty()) }
        }
        if (uiState.registros.isEmpty()) {
            item {
                EcclesiaCard {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = Dorado,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No hay registros pendientes de certificado",
                            fontWeight = FontWeight.SemiBold,
                            color = AzulPrincipal
                        )
                    }
                }
            }
        } else {
            items(uiState.registros, key = { it.id }) { registro ->
                TarjetaRegistro(
                    registro = registro,
                    generando = uiState.generandoRegistroId == registro.id,
                    onGenerar = { onGenerar(registro.id) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaRegistro(
    registro: RegistroSacramental,
    generando: Boolean,
    onGenerar: () -> Unit
) {
    EcclesiaCard(contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(fotoUrl = null, nombre = registro.titular, size = 46.dp, fontSize = 17.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = registro.titular ?: "—",
                    style = MaterialTheme.typography.titleSmall,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = registro.sacramento ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave
                )
                registro.lugar?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSuave
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            EcclesiaButton(
                text = "Generar",
                onClick = onGenerar,
                loading = generando,
                icon = Icons.Filled.Description,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun DialogDetalleCertificado(
    certificado: Certificado,
    descargando: Boolean,
    onDescargar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalle del certificado") },
        text = {
            Column {
                FilaDetalle("Titular", certificado.personaNombre)
                FilaDetalle("Sacramento", certificado.sacramento)
                FilaDetalle("Código de verificación", certificado.codigoVerificacion)
                FilaDetalle("Estado", certificado.estado)
                FilaDetalle("Fecha de emisión", certificado.fechaEmision)
                certificado.solicitante?.let { FilaDetalle("Solicitante", it) }
            }
        },
        confirmButton = {
            EcclesiaButton(
                text = "Descargar PDF",
                onClick = onDescargar,
                loading = descargando,
                icon = Icons.Filled.Download
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String?) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = "$etiqueta:",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave,
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = valor ?: "—",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}