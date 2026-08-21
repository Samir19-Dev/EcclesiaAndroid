package com.ecclesia.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.FontCinzel


@Composable
fun AppTopBar(
    titulo: String,
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onNotificacionesClick: (() -> Unit)? = null,
    notificacionesNoLeidas: Int = 0,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    onMenuClick != null -> IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint = AzulPrincipal)
                    }
                    onBackClick != null -> IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulPrincipal)
                    }
                }

                Text("✝", color = Dorado, fontSize = 17.sp, fontFamily = FontCinzel)
            }

            Text(
                text = titulo,
                color = AzulPrincipal,
                fontFamily = FontCinzel,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.4.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 80.dp)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onNotificacionesClick != null) {
                    Box {
                        IconButton(
                            onClick = onNotificacionesClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = AzulPrincipal)
                        }
                        if (notificacionesNoLeidas > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .background(Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$notificacionesNoLeidas",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                actions()
            }
        }

        androidx.compose.material3.HorizontalDivider(
            color = com.ecclesia.android.ui.theme.BordeTarjeta,
            modifier = Modifier.padding(top = 0.dp)
        )
    }
}
