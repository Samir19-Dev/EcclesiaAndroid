package com.ecclesia.android.ui.screens.auditoria

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ecclesia.android.domain.models.AuditoriaLog
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.BadgeAccionAuditoria
import com.ecclesia.android.ui.components.BadgeEstado
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.EcclesiaSearchField
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.PaginacionBarra
import com.ecclesia.android.ui.components.SkeletonTarjeta
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.AzulBootstrap
import com.ecclesia.android.ui.theme.FondoClaro
import com.ecclesia.android.ui.theme.GrisClaro
import com.ecclesia.android.ui.theme.Oscuro
import com.ecclesia.android.ui.theme.TextoSuave
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val POR_PAGINA = 10

private const val GRAFANA_URL =
    "http://localhost:3000/public-dashboards/6319c9ed3e3c4f47a17556448dcc9dec"

@Composable
fun AuditoriaScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: AuditoriaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Auditoría",
                onMenuClick = onMenuClick,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.logs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(5) { SkeletonTarjeta(lineas = 3) }
                    }
                } else {
                    AuditoriaContenido(
                        uiState = uiState,
                        onBuscarChange = viewModel::onBuscarChange,
                        onRefrescar = viewModel::cargar,
                        onCambiarPagina = viewModel::cambiarPagina
                    )
                }
            }
        }
    }
}

@Composable
private fun AuditoriaContenido(
    uiState: AuditoriaUiState,
    onBuscarChange: (String) -> Unit,
    onRefrescar: () -> Unit,
    onCambiarPagina: (Int) -> Unit
) {
    val filtrados = remember(uiState.logs, uiState.busqueda) {
        val termino = uiState.busqueda.trim().lowercase()
        if (termino.isEmpty()) uiState.logs
        else uiState.logs.filter { l ->
            l.usuario.lowercase().contains(termino) ||
                l.modulo.lowercase().contains(termino) ||
                l.accion.lowercase().contains(termino) ||
                l.detalle.lowercase().contains(termino)
        }
    }

    val totalPaginas = ((filtrados.size + POR_PAGINA - 1) / POR_PAGINA).coerceAtLeast(1)
    val pagina = uiState.pagina.coerceIn(1, totalPaginas)
    val visibles = filtrados.drop((pagina - 1) * POR_PAGINA).take(POR_PAGINA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bitácora de Auditoría y Analítica",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Registro inmutable de actividades, trazabilidad e informes Power BI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSuave
                )
            }
            IconButton(
                onClick = onRefrescar,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AzulClaro, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = TextoSuave)
                }
            }
        }

        PanelGrafana(modifier = Modifier.padding(top = 16.dp))

        EcclesiaSearchField(
            valor = uiState.busqueda,
            onCambio = onBuscarChange,
            placeholder = "Buscar por usuario, módulo o acción...",
            modifier = Modifier.padding(top = 16.dp)
        )

        uiState.error?.let {
            ErrorMessage(message = it, modifier = Modifier.padding(top = 12.dp))
        }

        if (filtrados.isEmpty() && !uiState.isLoading && uiState.error == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay registros de auditoría.",
                    color = TextoSuave,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            visibles.forEach { log ->
                AuditoriaItem(log = log, modifier = Modifier.padding(top = 12.dp))
            }
            PaginacionBarra(
                paginaActual = pagina,
                totalPaginas = totalPaginas,
                onPagina = onCambiarPagina,
                filas = visibles.size,
                totalItems = filtrados.size,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun PanelGrafana(modifier: Modifier = Modifier) {
    EcclesiaCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoClaro)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Insights,
                    contentDescription = null,
                    tint = AzulBootstrap,
                    modifier = Modifier.size(28.dp)
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = "Informe de Actividad y Métricas Parroquiales",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Panel analítico interactivo en tiempo real",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSuave
                    )
                }
            }
            GrafanaWebView(
                url = GRAFANA_URL,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun GrafanaWebView(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    var altoContenidoPx by remember { mutableStateOf(700) }

    val webView = remember {
        WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.postDelayed({
                        view.evaluateJavascript(
                            "(function(){return Math.max(document.documentElement.scrollHeight, document.body.scrollHeight);})()"
                        ) { resultado ->
                            val px = resultado.trim().trim('"').toIntOrNull()
                            if (px != null && px > 0) {
                                altoContenidoPx = px
                            }
                        }
                    }, 500)
                }
            }
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            loadUrl(url)
        }
    }
    AndroidView(
        factory = { webView },
        modifier = modifier.height(with(density) { altoContenidoPx.dp })
    )
}

@Composable
private fun AuditoriaItem(
    log: AuditoriaLog,
    modifier: Modifier = Modifier
) {
    EcclesiaCard(modifier = modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                tint = AzulClaro,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.usuario,
                        style = MaterialTheme.typography.titleSmall,
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    BadgeAccionAuditoria(accion = log.accion)
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeEstado(
                        texto = log.modulo,
                        color = GrisClaro,
                        textoColor = Oscuro
                    )
                }

                Text(
                    text = formatearFecha(log.fecha) + " · " + formatearHora(log.fecha),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = log.detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Oscuro,
                    modifier = Modifier.padding(top = 6.dp)
                )

                if (tieneCambios(log)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(GrisClaro, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        cambiosFormateados(log).forEach { cambio ->
                            Text(
                                text = cambio,
                                style = MaterialTheme.typography.bodySmall,
                                color = Oscuro,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "IP: ${log.ip.ifBlank { "—" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSuave,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun tieneCambios(log: AuditoriaLog): Boolean =
    log.datosAnteriores.isNotEmpty()

private fun cambiosFormateados(log: AuditoriaLog): List<String> {
    val antes = log.datosAnteriores
    val nuevo = log.datosNuevos
    return antes.map { (clave, valorAntes) ->
        val vAntes = textoValor(valorAntes)
        val vNuevo = textoValor(nuevo[clave])
        "$clave: $vAntes → $vNuevo"
    }
}

private fun textoValor(elemento: Any?): String {
    return when (elemento) {
        is JsonPrimitive -> elemento.content
        null -> "—"
        else -> elemento.toString()
    }
}

private fun parsearFecha(fecha: String?): ZonedDateTime? {
    if (fecha.isNullOrBlank()) return null
    return try {
        ZonedDateTime.parse(fecha)
    } catch (_: Exception) {
        try {
            OffsetDateTime.parse(fecha).toZonedDateTime()
        } catch (_: Exception) {
            try {
                LocalDateTime.parse(fecha).atZone(ZoneId.of("America/Bogota"))
            } catch (_: Exception) {
                null
            }
        }
    }
}

private val formatoFecha: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.of("America/Bogota"))

private val formatoHora: DateTimeFormatter =
    DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.of("America/Bogota"))

private fun formatearFecha(fecha: String?): String {
    val zdt = parsearFecha(fecha) ?: return "—"
    return zdt.format(formatoFecha)
}

private fun formatearHora(fecha: String?): String {
    val zdt = parsearFecha(fecha) ?: return "—"
    return zdt.format(formatoHora)
}
