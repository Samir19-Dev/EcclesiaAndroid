package com.ecclesia.android.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ecclesia.android.data.network.ApiClient
import com.ecclesia.android.data.network.SessionManager
import com.ecclesia.android.data.repository.AuthRepository
import com.ecclesia.android.ui.components.MainScaffold
import com.ecclesia.android.ui.components.permisosPorRuta
import com.ecclesia.android.ui.screens.auditoria.AuditoriaScreen
import com.ecclesia.android.ui.screens.certificados.CertificadosScreen
import com.ecclesia.android.ui.screens.configuracion.ConfiguracionScreen
import com.ecclesia.android.ui.screens.cursos.CursosScreen
import com.ecclesia.android.ui.screens.dashboard.DashboardScreen
import com.ecclesia.android.ui.screens.eventos.EventosScreen
import com.ecclesia.android.ui.screens.forgotpassword.ForgotPasswordScreen
import com.ecclesia.android.ui.screens.landing.LandingScreen
import com.ecclesia.android.ui.screens.login.LoginScreen
import com.ecclesia.android.ui.screens.notificaciones.NotificacionesScreen
import com.ecclesia.android.ui.screens.pagos.PagosScreen
import com.ecclesia.android.ui.screens.perfil.PerfilScreen
import com.ecclesia.android.ui.screens.personas.PersonaDetalleScreen
import com.ecclesia.android.ui.screens.personas.PersonasScreen
import com.ecclesia.android.ui.screens.register.RegisterScreen
import com.ecclesia.android.ui.screens.resetpassword.ResetPasswordScreen
import com.ecclesia.android.ui.screens.roles.RolesScreen
import com.ecclesia.android.ui.screens.sacramentos.SacramentosScreen
import com.ecclesia.android.ui.screens.solicitudes.SolicitudesScreen
import com.ecclesia.android.ui.screens.usuarios.UsuariosScreen
import com.ecclesia.android.ui.screens.verifyemail.VerifyEmailScreen
import kotlinx.coroutines.launch

private val rutasPrivadas = setOf(
    AppDestination.Dashboard.route,
    AppDestination.Personas.route,
    AppDestination.PersonaDetalle.route,
    AppDestination.Eventos.route,
    AppDestination.EventoDetalle.route,
    AppDestination.Solicitudes.route,
    AppDestination.Pagos.route,
    AppDestination.Sacramentos.route,
    AppDestination.Certificados.route,
    AppDestination.Cursos.route,
    AppDestination.Notificaciones.route,
    AppDestination.Usuarios.route,
    AppDestination.Roles.route,
    AppDestination.Auditoria.route,
    AppDestination.Configuracion.route,
    AppDestination.Perfil.route
)

@Composable
fun EcclesiaNavHost(
    navController: NavHostController = rememberNavController()
) {
    var sesionRestaurada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SessionManager.instance.restaurar()
        sesionRestaurada = true
    }

    if (!sesionRestaurada) {
        SplashPantalla()
        return
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    fun cerrarDrawer() {
        scope.launch { drawerState.close() }
    }

    fun abrirDrawer() {
        scope.launch { drawerState.open() }
    }

    fun irAlDashboard() {
        navController.navigate(AppDestination.Dashboard.route) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navegarDesdeDrawer(ruta: String) {
        cerrarDrawer()
        if (ruta != rutaActual) {
            navController.navigate(ruta) {
                popUpTo(navController.graph.id) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    var confirmarLogout by remember { mutableStateOf(false) }

    LaunchedEffect(SessionManager.instance.tieneSesion) {
        if (SessionManager.instance.tieneSesion) {
            runCatching {
                val res = ApiClient.api.misPermisos()
                SessionManager.instance.guardarPermisos(res.permisos)
            }
        }
    }

    LaunchedEffect(rutaActual, SessionManager.instance.permisos) {
        val requeridos = rutaActual?.let { permisosPorRuta[it] }
        if (requeridos != null && !SessionManager.instance.tienePermiso(*requeridos.toTypedArray())) {
            irAlDashboard()
        }
    }

    LaunchedEffect(Unit) {
        SessionManager.instance.tokens.collect { (acceso, _) ->
            if (acceso == null) {
                val ruta = navController.currentDestination?.route
                if (ruta != null && ruta in rutasPrivadas) {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = if (SessionManager.instance.tieneSesion) {
                    AppDestination.Dashboard.route
                } else {
                    AppDestination.Landing.route
                }
            ) {
        composable(AppDestination.Landing.route) {
            LandingScreen(
                onIniciarSesionClick = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(AppDestination.Landing.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestination.Login.route) {
            LoginScreen(
                onLoginClick = { irAlDashboard() },
                onRegisterClick = {
                    navController.navigate(AppDestination.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(AppDestination.ForgotPassword.route)
                }
            )
        }

        composable(AppDestination.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { correo ->
                    navController.navigate(AppDestination.VerifyEmail.crearRuta(correo)) {
                        popUpTo(AppDestination.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.ForgotPassword.route) {
            ForgotPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        composable(AppDestination.ResetPassword.route) {
            ResetPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        composable(AppDestination.VerifyEmail.route) { entry ->
            val correo = entry.arguments?.getString("correo") ?: ""
            VerifyEmailScreen(
                correo = correo,
                onLoginClick = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestination.Dashboard.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                DashboardScreen(
                    onMenuClick = ::abrirDrawer,
                    onPersonasClick = { navController.navigate(AppDestination.Personas.route) },
                    onEventosClick = { navController.navigate(AppDestination.Eventos.route) },
                    onSolicitudesClick = { navController.navigate(AppDestination.Solicitudes.route) },
                    onPagosClick = { navController.navigate(AppDestination.Pagos.route) },
                    onCertificadosClick = { navController.navigate(AppDestination.Certificados.route) },
                    onCursosClick = { navController.navigate(AppDestination.Cursos.route) }
                )
            }
        }

        composable(AppDestination.Personas.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                PersonasScreen(
                    onMenuClick = ::abrirDrawer,
                    onVerDetalle = { id ->
                        navController.navigate(AppDestination.PersonaDetalle.crearRuta(id))
                    }
                )
            }
        }

        composable(AppDestination.PersonaDetalle.route) { entry ->
            val personaId = entry.arguments?.getString("personaId")?.toLongOrNull() ?: return@composable
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                PersonaDetalleScreen(
                    personaId = personaId,
                    onMenuClick = ::abrirDrawer,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(AppDestination.Eventos.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                EventosScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Solicitudes.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                SolicitudesScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Pagos.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                PagosScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Perfil.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                PerfilScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Sacramentos.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                SacramentosScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Certificados.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                CertificadosScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Cursos.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                CursosScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Notificaciones.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                NotificacionesScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Usuarios.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                UsuariosScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Roles.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                RolesScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Auditoria.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                AuditoriaScreen(onMenuClick = ::abrirDrawer)
            }
        }

        composable(AppDestination.Configuracion.route) {
            MainScaffold(
                drawerState = drawerState,
                currentRoute = rutaActual,
                onNavigate = ::navegarDesdeDrawer,
                onLogoutClick = { confirmarLogout = true }
            ) {
                ConfiguracionScreen(onMenuClick = ::abrirDrawer)
            }
        }
            }
        }
    }

    if (confirmarLogout) {
        AlertDialog(
            onDismissRequest = { confirmarLogout = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar tu sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarLogout = false
                    scope.launch {
                        AuthRepository().logout()
                        snackbarHostState.showSnackbar("Sesión cerrada correctamente")
                    }
                }) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SplashPantalla() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
