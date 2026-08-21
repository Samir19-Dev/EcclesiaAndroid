package com.ecclesia.android.ui.screens.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.ui.components.AuthField
import com.ecclesia.android.ui.components.EcclesiaButton
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.ErrorLogin
import com.ecclesia.android.ui.theme.ErrorLoginBorde
import com.ecclesia.android.ui.theme.ErrorLoginFondo
import com.ecclesia.android.ui.theme.FontCinzel
import com.ecclesia.android.ui.theme.TextoSuave
import com.ecclesia.android.ui.theme.VerdeExito

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✝", color = Dorado, fontSize = 38.sp, fontFamily = FontCinzel)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Recuperar contraseña",
                color = AzulPrincipal,
                fontFamily = FontCinzel,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Te enviaremos un enlace de recuperación a tu correo",
                color = TextoSuave,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.enviado) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerdeExito.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                        .border(1.5.dp, VerdeExito.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = VerdeExito, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Revisa tu correo. Si la cuenta existe, recibirás un enlace para restablecer tu contraseña.",
                        color = VerdeExito,
                        fontSize = 13.sp
                    )
                }
            } else {
                uiState.error?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ErrorLoginFondo, RoundedCornerShape(6.dp))
                            .border(1.5.dp, ErrorLoginBorde, RoundedCornerShape(6.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = ErrorLogin, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(it, color = ErrorLogin, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                AuthField(
                    value = uiState.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = "Correo electrónico",
                    placeholder = "admin@ecclesiasys.com",
                    icono = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(20.dp))

                EcclesiaButton(
                    text = if (uiState.isLoading) "Enviando..." else "Enviar enlace",
                    onClick = { viewModel.enviar {} },
                    enabled = !uiState.isLoading,
                    loading = uiState.isLoading,
                    fullWidth = true,
                    modifier = Modifier.height(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Volver al inicio de sesión",
                color = AzulClaro,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
