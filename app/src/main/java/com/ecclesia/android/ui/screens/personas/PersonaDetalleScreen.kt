package com.ecclesia.android.ui.screens.personas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.SacramentoRegistrado
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.AvatarIniciales
import com.ecclesia.android.ui.components.BadgeEstado
import com.ecclesia.android.ui.components.EcclesiaButton
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.SkeletonTarjeta
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.GrisClaro
import com.ecclesia.android.ui.theme.GrisSecundario
import com.ecclesia.android.ui.theme.Oscuro
import com.ecclesia.android.ui.theme.TextoSuave
import com.ecclesia.android.ui.theme.VerdeExito

@Composable
fun PersonaDetalleScreen(
    personaId: Long,
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    viewModel: PersonaDetalleViewModel = viewModel(
        factory = PersonaDetalleViewModel.factory(personaId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Detalle de Persona",
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
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SkeletonTarjeta(lineas = 3)
                        SkeletonTarjeta(lineas = 4)
                    }
                }
                uiState.error != null && uiState.persona == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ErrorMessage(message = uiState.error.orEmpty(), modifier = Modifier.fillMaxWidth())
                        EcclesiaButton(
                            text = "Reintentar",
                            onClick = viewModel::cargarPersona,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                uiState.persona != null -> {
                    val persona = uiState.persona ?: return@Box
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarIniciales(
                                fotoUrl = persona.fotoUrl,
                                nombre = persona.nombreCompleto,
                                size = 80.dp,
                                fontSize = 28.sp
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = persona.nombreCompleto,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${persona.tipoDocumento}: ${persona.numeroDocumento ?: "—"}",
                                    color = TextoSuave,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                BadgeEstado(
                                    texto = "Estado Civil: ${persona.estadoCivil ?: "—"}",
                                    color = GrisClaro,
                                    textoColor = Oscuro,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        EcclesiaCard(modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                text = "Datos Personales",
                                style = MaterialTheme.typography.titleMedium,
                                color = AzulPrincipal,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0xFFE8EDF5), modifier = Modifier.padding(vertical = 12.dp))
                            FilaDato("Fecha de Nacimiento", persona.fechaNacimiento ?: "No registrada")
                            FilaDato("Lugar de Nacimiento", persona.lugarNacimiento ?: "No registrado")
                            FilaDato("Sexo", persona.sexo?.replaceFirstChar { it.uppercase() } ?: "No especificado")
                            FilaDato(
                                "Cuenta de Usuario Vinculada",
                                if (persona.tieneUsuario) "Sí (Activa)" else "No vinculada"
                            )
                            FilaDato("Ubicación", listOf(persona.departamento, persona.municipio).filter { !it.isNullOrBlank() }.joinToString(", ").ifBlank { "No registrada" })
                        }

                        EcclesiaCard(modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                text = "Sacramentos Recibidos",
                                style = MaterialTheme.typography.titleMedium,
                                color = AzulPrincipal,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(color = androidx.compose.ui.graphics.Color(0xFFE8EDF5), modifier = Modifier.padding(vertical = 12.dp))
                            when {
                                uiState.cargandoSacramentos -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = AzulPrincipal,
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = "  Cargando registros sacramentales...",
                                            color = TextoSuave,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                uiState.sacramentos.isEmpty() -> {
                                    Text(
                                        text = "No hay registros sacramentales guardados.",
                                        color = TextoSuave,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                                else -> {
                                    uiState.sacramentos.forEach { sac ->
                                        SacramentoTarjeta(sac)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDato(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = TextoSuave,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SacramentoTarjeta(sac: SacramentoRegistrado) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sac.sacramento.ifBlank { "Sacramento" },
                style = MaterialTheme.typography.titleSmall,
                color = AzulPrincipal,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            BadgeEstado(
                texto = if (sac.tieneCertificado) "Emitido" else "No emitido",
                color = if (sac.tieneCertificado) VerdeExito else GrisSecundario
            )
        }
        Text(
            text = "Fecha: ${sac.fecha ?: "—"}",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Parroquia: ${sac.parroquia ?: "—"}",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSuave
        )
        if (!sac.libroFisico.isNullOrBlank() || !sac.paginaLibro.isNullOrBlank()) {
            Text(
                text = "Registro: Libro: ${sac.libroFisico ?: "—"} - Folio: ${sac.paginaLibro ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSuave
            )
        }
        HorizontalDivider(
            color = Color(0xFFE8EDF5),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
