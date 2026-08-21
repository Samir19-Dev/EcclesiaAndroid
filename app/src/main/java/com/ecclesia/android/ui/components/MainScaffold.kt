package com.ecclesia.android.ui.components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ecclesia.android.ui.theme.FondoSidebar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    drawerState: DrawerState,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = FondoSidebar,
                drawerContentColor = Color.White
            ) {
                DrawerContent(
                    currentRoute = currentRoute,
                    onNavigate = { onNavigate(it) },
                    onLogoutClick = onLogoutClick
                )
            }
        }
    ) {
        content()
    }
}
