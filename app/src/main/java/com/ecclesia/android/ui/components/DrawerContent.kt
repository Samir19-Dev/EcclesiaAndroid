package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.SessionManager
import com.ecclesia.android.domain.models.Usuario
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.DoradoClaro
import com.ecclesia.android.ui.theme.FondoSidebar
import com.ecclesia.android.ui.theme.FontCinzel
import com.ecclesia.android.ui.theme.TextoSidebar

private data class ItemDrawer(
    val route: String,
    val titulo: String,
    val icono: ImageVector,
    val permisos: List<String>? = null
)

private val seccionPrincipal = listOf(
    ItemDrawer("dashboard", "Dashboard", Icons.Filled.Dashboard),
    ItemDrawer("solicitudes", "Solicitudes", Icons.Filled.Inbox),
    ItemDrawer("notificaciones", "Notificaciones", Icons.Filled.Notifications),
    ItemDrawer("perfil", "Perfil", Icons.Filled.Person)
)

private val seccionGestion = listOf(
    ItemDrawer("sacramentos", "Sacramentos", Icons.Filled.MenuBook, listOf("sacramentos.listar")),
    ItemDrawer("personas", "Personas", Icons.Filled.Groups, listOf("personas.listar")),
    ItemDrawer("eventos", "Eventos", Icons.Filled.CalendarMonth, listOf("eventos.listar")),
    ItemDrawer(
        "certificados", "Certificados", Icons.Filled.Verified,
        listOf("certificados.ver_todos", "certificados.generar", "certificados.ver_propios")
    ),
    ItemDrawer("cursos", "Cursos", Icons.Filled.School, listOf("cursos.listar")),
    ItemDrawer("pagos", "Pagos", Icons.Filled.Payments, listOf("pagos.listar"))
)

private val seccionAdministracion = listOf(
    ItemDrawer("usuarios", "Usuarios", Icons.Filled.Person, listOf("usuarios.listar")),
    ItemDrawer("roles", "Roles", Icons.Filled.Security, listOf("roles.listar")),
    ItemDrawer("auditoria", "Auditoría", Icons.Filled.Assignment, listOf("auditoria.ver")),
    ItemDrawer("configuracion", "Configuración", Icons.Filled.Settings, listOf("configuracion.ver"))
)

val permisosPorRuta: Map<String, List<String>> = (seccionGestion + seccionAdministracion)
    .filter { it.permisos != null }
    .associate { it.route to it.permisos!! }

@Composable
fun DrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val session = SessionManager.instance

    LaunchedEffect(Unit) {
        runCatching {
            val res = ApiClient.api.misPermisos()
            session.guardarPermisos(res.permisos)
        }
    }

    val solicitudesPendientes by produceState<Int>(initialValue = 0) {
        value = runCatching {
            if (session.tienePermiso("solicitudes.ver_todas")) {
                val todas = ApiClient.api.todasSolicitudes(pagina = 1, porPagina = 50)
                todas.items.count {
                    it.estado.equals("pendiente", true) || it.estado.equals("en_revision", true)
                }
            } else {
                ApiClient.api.misSolicitudes(estado = "pendiente").total
            }
        }.getOrDefault(0)
    }
    val badgeSolicitudes = if (solicitudesPendientes > 0) solicitudesPendientes else null

    val gestionVisible = seccionGestion.filter { item ->
        item.permisos == null || session.tienePermiso(*item.permisos.toTypedArray())
    }
    val administracionVisible = seccionAdministracion.filter { item ->
        item.permisos == null || session.tienePermiso(*item.permisos.toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoSidebar)
    ) {
        CabezaDrawer()

        UsuarioHeader()

        HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SeccionDrawer(
                etiqueta = "Principal",
                items = seccionPrincipal,
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                badges = mapOf("solicitudes" to badgeSolicitudes)
            )
            if (gestionVisible.isNotEmpty()) {
                SeccionDrawer(
                    etiqueta = "Gestión Parroquial",
                    items = gestionVisible,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate
                )
            }
            if (administracionVisible.isNotEmpty()) {
                SeccionDrawer(
                    etiqueta = "Administración",
                    items = administracionVisible,
                    currentRoute = currentRoute,
                    onNavigate = onNavigate
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center) {
            EcclesiaOutlineButton(
                text = "Cerrar sesión",
                onClick = onLogoutClick,
                color = Color(0xFFDC3545),
                icon = Icons.Filled.Logout
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SeccionDrawer(
    etiqueta: String,
    items: List<ItemDrawer>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    badges: Map<String, Int?> = emptyMap()
) {
    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(
            text = etiqueta.uppercase(),
            color = Color.White.copy(alpha = 0.28f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        items.forEach { item ->
            ItemDrawerComposable(
                item = item,
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                badge = badges[item.route]
            )
        }
    }
}

@Composable
private fun ItemDrawerComposable(
    item: ItemDrawer,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    badge: Int? = null
) {
    val activo = currentRoute == item.route
    val iconoColor = if (activo) Dorado else Color.White.copy(alpha = 0.55f)
    val textoColor = if (activo) DoradoClaro else Color.White.copy(alpha = 0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (activo) Color.White.copy(alpha = 0.08f) else Color.Transparent)
            .clickable { onNavigate(item.route) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icono,
            contentDescription = null,
            tint = iconoColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = item.titulo,
            color = textoColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (badge != null && badge > 0) {
            Text(
                text = "$badge",
                color = AzulPrincipal,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Dorado, RoundedCornerShape(10.dp))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
private fun CabezaDrawer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Dorado.copy(alpha = 0.10f), RoundedCornerShape(6.dp))
                .padding(1.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(Color.Transparent, RoundedCornerShape(5.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✝", color = Dorado, fontSize = 19.sp, fontFamily = FontCinzel)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "EcclesiaSys",
                color = Color.White,
                fontFamily = FontCinzel,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Sistema Parroquial",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun UsuarioHeader() {
    val usuario by produceState<Usuario?>(initialValue = null) {
        value = runCatching { ApiClient.api.usuarioActual() }.getOrNull()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarIniciales(
            fotoUrl = usuario?.persona?.fotoUrl,
            nombre = usuario?.persona?.nombreCompleto,
            size = 72.dp,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = usuario?.persona?.nombreCompleto.orEmpty()
                .ifBlank { usuario?.correo.orEmpty().ifBlank { "Usuario" } },
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        val rol = rolPrincipalDe(usuario?.roles?.mapNotNull { it.nombre } ?: emptyList())
            ?: "Usuario"
        Text(
            text = rol.replaceFirstChar { it.uppercase() },
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun PreviewDrawerContent() {
    MaterialTheme {
        DrawerContent(
            currentRoute = "dashboard",
            onNavigate = {},
            onLogoutClick = {}
        )
    }
}
