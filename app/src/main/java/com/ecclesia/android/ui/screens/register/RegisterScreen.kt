package com.ecclesia.android.ui.screens.register

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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecclesia.android.ui.components.AuthField
import com.ecclesia.android.ui.components.EcclesiaButton
import com.ecclesia.android.ui.components.IndicadorFuerza
import com.ecclesia.android.ui.components.RequisitosContrasena
import com.ecclesia.android.ui.theme.AzulClaro
import com.ecclesia.android.ui.theme.AzulPrincipal
import com.ecclesia.android.ui.theme.Dorado
import com.ecclesia.android.ui.theme.EcclesiaTheme
import com.ecclesia.android.ui.theme.ErrorLogin
import com.ecclesia.android.ui.theme.ErrorLoginBorde
import com.ecclesia.android.ui.theme.ErrorLoginFondo
import com.ecclesia.android.ui.theme.FontCinzel
import com.ecclesia.android.ui.theme.TextoSuave
import com.ecclesia.android.ui.theme.VerdeExito

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
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
                text = "Crear cuenta",
                color = AzulPrincipal,
                fontFamily = FontCinzel,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Completa los datos para registrarte",
                color = TextoSuave,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            uiState.error?.let { error ->
                Alerta(error = error, exito = false)
                Spacer(modifier = Modifier.height(16.dp))
            }

            AuthField(
                value = uiState.correo,
                onValueChange = viewModel::onCorreoChange,
                label = "Correo electrónico",
                placeholder = "ejemplo@gmail.com",
                icono = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthField(
                value = uiState.contrasena,
                onValueChange = viewModel::onContrasenaChange,
                label = "Contraseña",
                placeholder = "Mínimo 8 caracteres",
                icono = Icons.Filled.Lock,
                esContrasena = true
            )

            if (uiState.contrasena.isNotEmpty()) {
                IndicadorFuerza(password = uiState.contrasena, modifier = Modifier.padding(top = 8.dp))
                RequisitosContrasena(password = uiState.contrasena, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            AuthField(
                value = uiState.confirmarContrasena,
                onValueChange = viewModel::onConfirmarChange,
                label = "Confirmar contraseña",
                placeholder = "Repite tu contraseña",
                icono = Icons.Filled.Lock,
                esContrasena = true,
                error = if (uiState.confirmarContrasena.isNotEmpty() && uiState.confirmarContrasena != uiState.contrasena) {
                    "Las contraseñas no coinciden"
                } else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            EcclesiaButton(
                text = if (uiState.isLoading) "Registrando..." else "Crear cuenta",
                onClick = { viewModel.onRegistrarClick { onRegisterSuccess(uiState.correo.trim()) } },
                enabled = !uiState.isLoading,
                loading = uiState.isLoading,
                fullWidth = true,
                modifier = Modifier.height(50.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = TextoSuave,
                    fontSize = 13.sp
                )
                Text(
                    text = "Iniciar sesión",
                    color = AzulClaro,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Alerta(error: String, exito: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (exito) VerdeExito.copy(alpha = 0.08f) else ErrorLoginFondo, RoundedCornerShape(6.dp))
            .border(1.5.dp, if (exito) VerdeExito.copy(alpha = 0.4f) else ErrorLoginBorde, RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = if (exito) VerdeExito else ErrorLogin,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = error,
            color = if (exito) VerdeExito else ErrorLogin,
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    EcclesiaTheme {
        RegisterScreen()
    }
}
