package com.ecclesia.android.ui.screens.perfil

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ecclesia.android.domain.models.SesionInfo
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.EmailField
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.InputField
import com.ecclesia.android.ui.components.PasswordField
import com.ecclesia.android.ui.components.PrimaryButton
import com.ecclesia.android.ui.components.SelectorField
import com.ecclesia.android.ui.theme.EcclesiaTheme
import java.io.File

private val tiposDocumento = mapOf(
    "CC" to "Cédula de ciudadanía",
    "TI" to "Tarjeta de identidad",
    "CE" to "Cédula de extranjería",
    "PA" to "Pasaporte",
    "RC" to "Registro civil",
    "sin_documento" to "Sin documento"
)

private val sexos = mapOf(
    "masculino" to "Masculino",
    "femenino" to "Femenino"
)

private val estadosCiviles = mapOf(
    "soltero" to "Soltero/a",
    "casado" to "Casado/a",
    "viudo" to "Viudo/a",
    "divorciado" to "Divorciado/a",
    "union_libre" to "Unión libre",
    "religioso_casado" to "Casado/a por la iglesia",
    "anulado" to "Anulado/a"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onMenuClick: (() -> Unit)? = null,
    viewModel: PerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            copiarAUriCache(context, it)?.let { archivo -> viewModel.subirFoto(archivo) }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Mi perfil",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = uiState.tabActual) {
                Tab(
                    selected = uiState.tabActual == 0,
                    onClick = { viewModel.onTabChange(0) },
                    text = { Text("Información") }
                )
                Tab(
                    selected = uiState.tabActual == 1,
                    onClick = { viewModel.onTabChange(1) },
                    text = { Text("Cuenta") }
                )
                Tab(
                    selected = uiState.tabActual == 2,
                    onClick = { viewModel.onTabChange(2) },
                    text = { Text("Sesiones") }
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.error?.let { ErrorMessage(message = it) }
                uiState.mensajeExito?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                when (uiState.tabActual) {
                    0 -> TabInformacion(uiState, viewModel) { launcher.launch("image/*") }
                    1 -> TabCuenta(uiState, viewModel)
                    2 -> TabSesiones(uiState, viewModel)
                }
            }
        }
    }
}

@Composable
private fun TabInformacion(
    uiState: PerfilUiState,
    viewModel: PerfilViewModel,
    elegirFoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FotoPerfil(
                fotoUrl = uiState.perfil?.fotoUrl,
                nombre = uiState.perfil?.nombreCompleto ?: "E",
                size = 72.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                OutlinedButton(onClick = elegirFoto, enabled = !uiState.subiendoFoto) {
                    if (uiState.subiendoFoto) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Subir foto")
                    }
                }
                if (uiState.perfil?.fotoUrl != null) {
                    OutlinedButton(onClick = viewModel::eliminarFoto) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quitar foto")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            value = uiState.primerNombre,
            onValueChange = viewModel::onPrimerNombre,
            label = "Primer nombre"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.segundoNombre,
            onValueChange = viewModel::onSegundoNombre,
            label = "Segundo nombre"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.primerApellido,
            onValueChange = viewModel::onPrimerApellido,
            label = "Primer apellido"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.segundoApellido,
            onValueChange = viewModel::onSegundoApellido,
            label = "Segundo apellido"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorField(
            value = tiposDocumento[uiState.tipoDocumento] ?: uiState.tipoDocumento,
            opciones = tiposDocumento.values.toList(),
            onSelect = { etiqueta ->
                viewModel.onTipoDocumento(
                    tiposDocumento.entries.firstOrNull { it.value == etiqueta }?.key ?: etiqueta
                )
            },
            label = "Tipo de documento"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.numeroDocumento,
            onValueChange = viewModel::onNumeroDocumento,
            label = "Número de documento"
        )

        Spacer(modifier = Modifier.height(12.dp))

        InputField(
            value = uiState.fechaNacimiento,
            onValueChange = viewModel::onFechaNacimiento,
            label = "Fecha de nacimiento (AAAA-MM-DD)"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorField(
            value = sexos[uiState.sexo] ?: "",
            opciones = sexos.values.toList(),
            onSelect = { etiqueta ->
                viewModel.onSexo(sexos.entries.firstOrNull { it.value == etiqueta }?.key ?: etiqueta)
            },
            label = "Sexo"
        )
        Spacer(modifier = Modifier.height(8.dp))
        SelectorField(
            value = estadosCiviles[uiState.estadoCivil] ?: uiState.estadoCivil,
            opciones = estadosCiviles.values.toList(),
            onSelect = { etiqueta ->
                viewModel.onEstadoCivil(
                    estadosCiviles.entries.firstOrNull { it.value == etiqueta }?.key ?: etiqueta
                )
            },
            label = "Estado civil"
        )

        Spacer(modifier = Modifier.height(12.dp))

        InputField(
            value = uiState.municipio,
            onValueChange = viewModel::onMunicipio,
            label = "Municipio"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.departamento,
            onValueChange = viewModel::onDepartamento,
            label = "Departamento"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.region,
            onValueChange = viewModel::onRegion,
            label = "Región"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = uiState.lugarNacimiento,
            onValueChange = viewModel::onLugarNacimiento,
            label = "Lugar de nacimiento"
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = if (uiState.guardando) "Guardando..." else
                if (uiState.tienePerfil) "Actualizar perfil" else "Crear perfil",
            onClick = viewModel::guardarPerfil,
            enabled = !uiState.guardando
        )
    }
}

@Composable
private fun TabCuenta(uiState: PerfilUiState, viewModel: PerfilViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cambiar correo electrónico",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Correo actual: ${uiState.emailActual}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                PasswordField(
                    value = uiState.emailContrasenaActual,
                    onValueChange = viewModel::onEmailContrasenaActual,
                    label = "Contraseña actual"
                )
                Spacer(modifier = Modifier.height(8.dp))
                EmailField(
                    value = uiState.nuevoCorreo,
                    onValueChange = viewModel::onNuevoCorreo,
                    label = "Nuevo correo electrónico"
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                    text = if (uiState.cambiandoEmail) "Cambiando..." else "Cambiar correo",
                    onClick = viewModel::cambiarEmail,
                    enabled = !uiState.cambiandoEmail
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cambiar contraseña",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                PasswordField(
                    value = uiState.passContrasenaActual,
                    onValueChange = viewModel::onPassContrasenaActual,
                    label = "Contraseña actual"
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordField(
                    value = uiState.nuevaContrasena,
                    onValueChange = viewModel::onNuevaContrasena,
                    label = "Nueva contraseña"
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordField(
                    value = uiState.confirmarContrasena,
                    onValueChange = viewModel::onConfirmarContrasena,
                    label = "Confirmar nueva contraseña"
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                    text = if (uiState.cambiandoPass) "Cambiando..." else "Cambiar contraseña",
                    onClick = viewModel::cambiarContrasena,
                    enabled = !uiState.cambiandoPass
                )
            }
        }
    }
}

@Composable
private fun TabSesiones(uiState: PerfilUiState, viewModel: PerfilViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sesiones activas (${uiState.sesiones.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = viewModel::cargarSesiones) {
                Text("Actualizar")
            }
        }

        if (uiState.cargandoSesiones) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.sesiones.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay sesiones activas")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.sesiones, key = { it.id }) { sesion ->
                    SesionCard(sesion)
                }
            }
        }
    }
}

@Composable
private fun SesionCard(sesion: SesionInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Devices, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sesion.dispositivo ?: sesion.userAgent ?: "Dispositivo",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sesion.ipAddress ?: "IP desconocida",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                sesion.fechaUltimoUso?.let { fecha ->
                    Text(
                        text = "Último uso: $fecha",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!sesion.activa) {
                Text(
                    text = "Inactiva",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun FotoPerfil(fotoUrl: String?, nombre: String, size: androidx.compose.ui.unit.Dp) {
    val iniciales = nombre.split(" ").filter { it.isNotBlank() }.let { partes ->
        when {
            partes.isEmpty() -> "E"
            partes.size == 1 -> partes[0].take(1).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }
    if (!fotoUrl.isNullOrBlank()) {
        AsyncImage(
            model = fotoUrl,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun copiarAUriCache(context: Context, uri: Uri): File? {
    return try {
        val tipo = context.contentResolver.getType(uri) ?: "image/jpeg"
        val ext = when {
            tipo.contains("png") -> ".png"
            tipo.contains("webp") -> ".webp"
            else -> ".jpg"
        }
        val archivo = File(context.cacheDir, "foto_perfil$ext")
        context.contentResolver.openInputStream(uri)?.use { input ->
            archivo.outputStream().use { output -> input.copyTo(output) }
        }
        archivo
    } catch (e: Exception) {
        null
    }
}
