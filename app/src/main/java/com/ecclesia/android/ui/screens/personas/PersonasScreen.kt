package com.ecclesia.android.ui.screens.personas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.PersonaRequest
import com.ecclesia.android.ui.components.AppTopBar
import com.ecclesia.android.ui.components.AvatarIniciales
import com.ecclesia.android.ui.components.BadgeEstado
import com.ecclesia.android.ui.components.EcclesiaButton
import com.ecclesia.android.ui.components.EcclesiaCard
import com.ecclesia.android.ui.components.EcclesiaOutlineButton
import com.ecclesia.android.ui.components.EcclesiaSearchField
import com.ecclesia.android.ui.components.ErrorMessage
import com.ecclesia.android.ui.components.PaginacionBarra
import com.ecclesia.android.ui.components.SelectorField
import com.ecclesia.android.ui.components.SkeletonTarjeta
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.BordeInput
import com.ecclesia.android.ui.theme.ErrorLogin
import com.ecclesia.android.ui.theme.GrisClaro
import com.ecclesia.android.ui.theme.Oscuro
import com.ecclesia.android.ui.theme.TextoSuave
import com.ecclesia.android.ui.theme.VerdeExito

private const val POR_PAGINA = 10

private val TIPOS_DOCUMENTO = listOf("CC", "TI", "CE", "PA")
private val ESTADOS_CIVILES = listOf("soltero", "casado", "union_libre", "divorciado", "viudo")
private val SEXOS = listOf("masculino", "femenino")

data class FormaPersona(
    val primerNombre: String = "",
    val segundoNombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val tipoDocumento: String = "CC",
    val numeroDocumento: String = "",
    val estadoCivil: String = "soltero",
    val sexo: String = "masculino",
    val fechaNacimiento: String = "",
    val lugarNacimiento: String = ""
)

@Composable
fun PersonasScreen(
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onVerDetalle: (Long) -> Unit = {},
    viewModel: PersonasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var mostrarModal by remember { mutableStateOf(false) }
    var editandoId by remember { mutableStateOf<Long?>(null) }
    var forma by remember { mutableStateOf(FormaPersona()) }
    var errores by remember { mutableStateOf(mapOf<String, String>()) }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Personas",
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
                if (uiState.isLoading && uiState.personas.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(5) { SkeletonTarjeta(lineas = 2) }
                    }
                } else {
                    PersonasContenido(
                        uiState = uiState,
                        onBuscarChange = viewModel::onBuscarChange,
                        onRefrescar = viewModel::cargar,
                        onNuevaPersona = {
                            forma = FormaPersona()
                            errores = emptyMap()
                            editandoId = null
                            mostrarModal = true
                        },
                        onVerDetalle = onVerDetalle,
                        onEditar = { p ->
                            forma = FormaPersona(
                                primerNombre = p.primerNombre.orEmpty(),
                                segundoNombre = p.segundoNombre.orEmpty(),
                                primerApellido = p.primerApellido.orEmpty(),
                                segundoApellido = p.segundoApellido.orEmpty(),
                                tipoDocumento = p.tipoDocumento.ifBlank { "CC" },
                                numeroDocumento = p.numeroDocumento.orEmpty(),
                                estadoCivil = p.estadoCivil?.ifBlank { "soltero" } ?: "soltero",
                                sexo = p.sexo?.ifBlank { "masculino" } ?: "masculino",
                                fechaNacimiento = p.fechaNacimiento.orEmpty(),
                                lugarNacimiento = p.lugarNacimiento.orEmpty()
                            )
                            errores = emptyMap()
                            editandoId = p.id
                            mostrarModal = true
                        },
                        onCambiarPagina = viewModel::cambiarPagina
                    )
                }
            }

            if (mostrarModal) {
                PersonaFormDialog(
                    editandoId = editandoId,
                    guardando = uiState.guardando,
                    error = uiState.errorGuardado,
                    forma = forma,
                    errores = errores,
                    onFormaChange = { forma = it },
                    onErrores = { errores = it },
                    onGuardar = { request ->
                        viewModel.guardar(editandoId, request) {
                            mostrarModal = false
                        }
                    },
                    onCancelar = {
                        if (!uiState.guardando) {
                            mostrarModal = false
                            viewModel.descartarErrorGuardado()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonasContenido(
    uiState: PersonasUiState,
    onBuscarChange: (String) -> Unit,
    onRefrescar: () -> Unit,
    onNuevaPersona: () -> Unit,
    onVerDetalle: (Long) -> Unit,
    onEditar: (Persona) -> Unit,
    onCambiarPagina: (Int) -> Unit
) {
    val filtradas = remember(uiState.personas, uiState.busqueda) {
        val termino = uiState.busqueda.trim().lowercase()
        if (termino.isEmpty()) uiState.personas
        else uiState.personas.filter { p ->
            "${p.primerNombre} ${p.primerApellido}".lowercase().contains(termino) ||
                p.numeroDocumento.orEmpty().lowercase().contains(termino)
        }
    }

    val totalPaginas = ((filtradas.size + POR_PAGINA - 1) / POR_PAGINA).coerceAtLeast(1)
    val pagina = uiState.pagina.coerceIn(1, totalPaginas)
    val visibles = filtradas.drop((pagina - 1) * POR_PAGINA).take(POR_PAGINA)

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
                    text = "Registro de Personas",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filtradas.size} personas registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSuave
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
            EcclesiaButton(
                text = "Nueva Persona",
                onClick = onNuevaPersona,
                icon = Icons.Filled.PersonAdd,
                modifier = Modifier.weight(1f)
            )
        }

        EcclesiaSearchField(
            valor = uiState.busqueda,
            onCambio = onBuscarChange,
            placeholder = "Buscar por nombre o documento...",
            modifier = Modifier.padding(top = 16.dp)
        )

        uiState.error?.let {
            ErrorMessage(message = it, modifier = Modifier.padding(top = 12.dp))
        }

        if (filtradas.isEmpty() && !uiState.isLoading && uiState.error == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron personas registradas.",
                    color = TextoSuave,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            visibles.forEach { persona ->
                PersonaItem(
                    persona = persona,
                    onVerDetalle = { onVerDetalle(persona.id) },
                    onEditar = { onEditar(persona) },
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            PaginacionBarra(
                paginaActual = pagina,
                totalPaginas = totalPaginas,
                onPagina = onCambiarPagina,
                filas = visibles.size,
                totalItems = filtradas.size,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun PersonaItem(
    persona: Persona,
    onVerDetalle: () -> Unit,
    onEditar: () -> Unit,
    modifier: Modifier = Modifier
) {
    EcclesiaCard(modifier = modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarIniciales(
                fotoUrl = persona.fotoUrl,
                nombre = persona.nombreCompleto,
                size = 42.dp,
                fontSize = 14.sp,
                modifier = Modifier.width(42.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = persona.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${persona.tipoDocumento}: ${persona.numeroDocumento ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSuave
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeEstado(
                        texto = persona.estadoCivil ?: "—",
                        color = GrisClaro,
                        textoColor = Oscuro
                    )
                    BadgeEstado(
                        texto = "${persona.sacramentos ?: 0}",
                        color = GrisClaro,
                        textoColor = Oscuro
                    )
                    if (persona.tieneUsuario) {
                        BadgeEstado(texto = "Sí", color = VerdeExito)
                    } else {
                        Text(
                            text = "No",
                            color = TextoSuave,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            Column {
                IconButton(onClick = onVerDetalle) {
                    Icon(Icons.Filled.Visibility, contentDescription = "Ver Detalle", tint = TextoSuave)
                }
                IconButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = AzulClaro)
                }
            }
        }
    }
}

@Composable
private fun PersonaFormDialog(
    editandoId: Long?,
    guardando: Boolean,
    error: String?,
    forma: FormaPersona,
    errores: Map<String, String>,
    onFormaChange: (FormaPersona) -> Unit,
    onErrores: (Map<String, String>) -> Unit,
    onGuardar: (PersonaRequest) -> Unit,
    onCancelar: () -> Unit
) {
    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (editandoId != null) "Editar Persona" else "Registrar Nueva Persona",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Bold
                )

                error?.let {
                    ErrorMessage(message = it, modifier = Modifier.padding(top = 12.dp))
                }

                CampoForma(
                    valor = forma.primerNombre,
                    onCambio = { onFormaChange(forma.copy(primerNombre = it)) },
                    label = "Primer Nombre *",
                    error = errores["primerNombre"],
                    modifier = Modifier.padding(top = 12.dp)
                )
                CampoForma(
                    valor = forma.segundoNombre,
                    onCambio = { onFormaChange(forma.copy(segundoNombre = it)) },
                    label = "Segundo Nombre",
                    modifier = Modifier.padding(top = 10.dp)
                )
                CampoForma(
                    valor = forma.primerApellido,
                    onCambio = { onFormaChange(forma.copy(primerApellido = it)) },
                    label = "Primer Apellido *",
                    error = errores["primerApellido"],
                    modifier = Modifier.padding(top = 10.dp)
                )
                CampoForma(
                    valor = forma.segundoApellido,
                    onCambio = { onFormaChange(forma.copy(segundoApellido = it)) },
                    label = "Segundo Apellido",
                    modifier = Modifier.padding(top = 10.dp)
                )

                SelectorField(
                    value = forma.tipoDocumento,
                    opciones = TIPOS_DOCUMENTO,
                    onSelect = { onFormaChange(forma.copy(tipoDocumento = it)) },
                    label = "Tipo Documento *",
                    modifier = Modifier.padding(top = 10.dp)
                )
                CampoForma(
                    valor = forma.numeroDocumento,
                    onCambio = { onFormaChange(forma.copy(numeroDocumento = it)) },
                    label = "Número Documento *",
                    error = errores["numeroDocumento"],
                    modifier = Modifier.padding(top = 10.dp)
                )

                SelectorField(
                    value = forma.estadoCivil,
                    opciones = ESTADOS_CIVILES,
                    onSelect = { onFormaChange(forma.copy(estadoCivil = it)) },
                    label = "Estado Civil",
                    modifier = Modifier.padding(top = 10.dp)
                )
                SelectorField(
                    value = forma.sexo,
                    opciones = SEXOS,
                    onSelect = { onFormaChange(forma.copy(sexo = it)) },
                    label = "Sexo",
                    modifier = Modifier.padding(top = 10.dp)
                )
                CampoForma(
                    valor = forma.fechaNacimiento,
                    onCambio = { onFormaChange(forma.copy(fechaNacimiento = it)) },
                    label = "Fecha Nacimiento",
                    placeholder = "AAAA-MM-DD",
                    modifier = Modifier.padding(top = 10.dp)
                )
                CampoForma(
                    valor = forma.lugarNacimiento,
                    onCambio = { onFormaChange(forma.copy(lugarNacimiento = it)) },
                    label = "Lugar Nacimiento",
                    placeholder = "Ciudad, Departamento",
                    modifier = Modifier.padding(top = 10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancelar, enabled = !guardando) {
                        Text("Cancelar", color = TextoSuave)
                    }
                    EcclesiaButton(
                        text = "Guardar Persona",
                        onClick = {
                            val nuevos = validarForma(forma)
                            if (nuevos.isNotEmpty()) {
                                onErrores(nuevos)
                                return@EcclesiaButton
                            }
                            onErrores(emptyMap())
                            onGuardar(forma.toRequest())
                        },
                        loading = guardando,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CampoForma(
    valor: String,
    onCambio: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (error != null) ErrorLogin else AzulClaro,
            unfocusedBorderColor = if (error != null) ErrorLogin else BordeInput,
            cursorColor = AzulClaro,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = if (error != null) ErrorLogin else AzulClaro,
            errorLabelColor = ErrorLogin
        )
    )
}

private fun validarForma(forma: FormaPersona): Map<String, String> {
    val errores = mutableMapOf<String, String>()
    if (forma.primerNombre.isBlank()) errores["primerNombre"] = "El primer nombre es obligatorio."
    if (forma.primerApellido.isBlank()) errores["primerApellido"] = "El primer apellido es obligatorio."
    if (forma.numeroDocumento.isBlank()) errores["numeroDocumento"] = "El número de documento es obligatorio."
    return errores
}

private fun FormaPersona.toRequest(): PersonaRequest = PersonaRequest(
    primerNombre = primerNombre.trim(),
    segundoNombre = segundoNombre.trim().ifEmpty { null },
    primerApellido = primerApellido.trim(),
    segundoApellido = segundoApellido.trim().ifEmpty { null },
    tipoDocumento = tipoDocumento,
    numeroDocumento = numeroDocumento.trim(),
    estadoCivil = estadoCivil,
    sexo = sexo,
    fechaNacimiento = fechaNacimiento.trim().ifEmpty { null },
    lugarNacimiento = lugarNacimiento.trim().ifEmpty { null }
)
